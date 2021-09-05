package net.cadrian.nwcscore.lybuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cadrian.nwcscore.ast.AbstractNode;
import net.cadrian.nwcscore.ast.Bar;
import net.cadrian.nwcscore.ast.Chord;
import net.cadrian.nwcscore.ast.Clef;
import net.cadrian.nwcscore.ast.Converter;
import net.cadrian.nwcscore.ast.Key;
import net.cadrian.nwcscore.ast.Lyric;
import net.cadrian.nwcscore.ast.Note;
import net.cadrian.nwcscore.ast.Rest;
import net.cadrian.nwcscore.ast.RestMultiBar;
import net.cadrian.nwcscore.ast.Song;
import net.cadrian.nwcscore.ast.SongInfo;
import net.cadrian.nwcscore.ast.Staff;
import net.cadrian.nwcscore.ast.StaffInstrument;
import net.cadrian.nwcscore.ast.TimeSig;
import net.cadrian.nwcscore.ast.Visitors;
import net.cadrian.nwcscore.music.KeySignature;

class NwcVisitor implements Visitors {

	private String songTitle;
	private String songAuthor;
	private String songLyricist;
	private String songCopyright1;
	private String songCopyright2;

	private final List<LyStaffGroup> staffGroups = new ArrayList<>();
	private LyStaff currentStaff;
	private LyStaffGroup currentGroup;
	private net.cadrian.nwcscore.music.Clef currentClef;
	private KeySignature currentKeySignature;

	public void output(final String filename) throws IOException {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
			out.println("\\version \"2.22.0\"");
			out.println("\\language \"english\"");
			out.println("\\header {");
			if (songTitle != null && !songTitle.isEmpty()) {
				out.println("  title = \"" + songTitle + "\"");
			}
			if (songAuthor != null && !songAuthor.isEmpty()) {
				out.println("  composer = \"" + songAuthor + "\"");
			}
			if (songLyricist != null && !songLyricist.isEmpty()) {
				out.println("  poet = \"" + songLyricist + "\"");
			}
			out.println("  copyright = \"" + songCopyright1 + " - " + songCopyright2 + "\"");
			out.println("}");
			out.println("\\score { \\new StaffGroup {");
			out.println("  \\set StaffGroup.systemStartDelimiterHierarchy = #'" + getSystemStartDelimiterHierarchy());
			out.println("  <<");
			for (final LyStaffGroup staffGroup : staffGroups) {
				staffGroup.output(out);
			}
			out.println(">> } }");
		}
	}

	private String getSystemStartDelimiterHierarchy() {
		final StringBuilder result = new StringBuilder();
		char staffnum = 'a';
		boolean bracket = false;
		boolean brace = false;
		for (final LyStaffGroup group : staffGroups) {
			for (final LyStaff staff : group.getStaves()) {
				if (!bracket && staff.hasBracket("Bracket")) {
					if (result.length() > 0) {
						result.append(' ');
					}
					result.append("(SystemStartBracket");
					bracket = true;
				}
				if (!brace && staff.hasBracket("Brace")) {
					if (result.length() > 0) {
						result.append(' ');
					}
					result.append("(SystemStartBrace");
					brace = true;
				}
				if (result.length() > 0) {
					result.append(' ');
				}
				result.append(staffnum++);
				if (brace && !staff.hasBracket("Brace")) {
					result.append(")");
					brace = false;
				}
				if (bracket && !staff.hasBracket("Bracket")) {
					result.append(")");
					bracket = false;
				}
			}
		}
		if (bracket) {
			result.append(")");
		}
		if (brace) {
			result.append(")");
		}
		return result.toString();
	}

	@Override
	public void visit(final Bar node) {
		final String visibility = node.getProperty("Visibility");
		if (visibility == null || visibility.equals("Y")) {
			final String type = node.getProperty("Type");
			final net.cadrian.nwcscore.music.Bar bar;
			final String sysbreak = node.getProperty("SysBreak");
			if (type == null) {
				bar = net.cadrian.nwcscore.music.Bar.SIMPLE;
			} else {
				switch (type) {
				case "Double":
					bar = net.cadrian.nwcscore.music.Bar.DOUBLE;
					break;
				default:
					throw new RuntimeException("bar not implemented: " + type);
				}
			}
			currentStaff.add(new LyBar(bar, sysbreak != null && sysbreak.equals("Y")));
		}
	}

	@Override
	public void visit(final Note node) {
		final String visibility = node.getProperty("Visibility");
		if (visibility == null || visibility.equals("Y")) {
			final String nwcdur = node.getProperty("Dur");
			final String nwcpos = node.getProperty("Pos");
			currentStaff.add(new LyNote(currentClef.getNote(nwcpos, nwcdur, currentKeySignature)));
		}
	}

	@Override
	public void visit(final Chord node) {
		final String visibility = node.getProperty("Visibility");
		if (visibility == null || visibility.equals("Y")) {
			final String nwcdur = node.getProperty("Dur");
			final String nwcpos = node.getProperty("Pos");
			final String nwcdur2 = node.getProperty("Dur2");
			final String nwcpos2 = node.getProperty("Pos2");
			final Map<String, String> opts = node.getProperty("Opts", Converter.NWC_STRINGMAP);
			currentStaff.add(new LyChord(currentClef.getChord(nwcpos, nwcdur, currentKeySignature),
					nwcpos2 == null ? null : currentClef.getChord(nwcpos2, nwcdur2, currentKeySignature), opts));
		}
	}

	@Override
	public void visit(final Rest node) {
		final String visibility = node.getProperty("Visibility");
		final String nwcdur = node.getProperty("Dur");
		currentStaff.add(new LyRest(currentClef.getRest(nwcdur, visibility == null || visibility.equals("Y"))));
	}

	@Override
	public void visit(final RestMultiBar node) {
		final String visibility = node.getProperty("Visibility");
		final int num = node.getProperty("NumBars", Converter.INTEGER);
		final String whenhidden = node.getProperty("WhenHidden");// ShowBars,ShowRests
		currentStaff.add(new LyMultiRest(num, (visibility == null || visibility.equals("Y")) && whenhidden != null
				&& whenhidden.contains("ShowRests")));
	}

	@Override
	public void visit(final Clef node) {
		final String type = node.getProperty("Type");
		final String shift = node.getProperty("OctaveShift");
		switch (type) {
		case "Treble":
			if (shift == null) {
				currentClef = net.cadrian.nwcscore.music.Clef.TREBLE;
			} else {
				switch (shift) {
				case "Octave Down":
					currentClef = net.cadrian.nwcscore.music.Clef.TREBLE8;
					break;
				default:
					throw new RuntimeException("clef not implemented: treble " + shift);
				}
			}
			break;
		case "Bass":
			if (shift == null) {
				currentClef = net.cadrian.nwcscore.music.Clef.BASS;
			} else {
				throw new RuntimeException("clef not implemented: bass " + shift);
			}
			break;
		default:
			throw new RuntimeException("clef not implemented: " + type);
		}
		currentStaff.add(new LyClef(currentClef));
	}

	@Override
	public void visit(final Key node) {
		final String visibility = node.getProperty("Visibility");
		if (visibility == null || visibility.equals("Y")) {
			final String signature = node.getProperty("Signature");
			if (KeySignature.get(signature) != null) {
				final LyKey lyKey = new LyKey(signature, node.getProperty("Tonic"));
				currentStaff.add(lyKey);
				currentKeySignature = lyKey.getSignature();
			} else {
				// TODO some kind of ad-hoc signature???
				System.err.println("Ignore unknown signature: " + signature);
			}
		}
	}

	@Override
	public void visit(final Lyric node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(final Song node) {
		node.getSongInfo().accept(this);
		for (final Staff staff : node.getStaves()) {
			staff.accept(this);
		}
	}

	@Override
	public void visit(final SongInfo node) {
		songTitle = node.getProperty("Title", Converter.NWC_STRING);
		songAuthor = node.getProperty("Author", Converter.NWC_STRING);
		songLyricist = node.getProperty("Lyricist", Converter.NWC_STRING);
		songCopyright1 = node.getProperty("Copyright1", Converter.NWC_STRING);
		songCopyright2 = node.getProperty("Copyright2", Converter.NWC_STRING);
	}

	@Override
	public void visit(final Staff node) {
		if (node.getProperty("Visible").equals("Y")) {
			final Set<String> bracket = node.getBracket();

			if (currentGroup == null || currentGroup.nestBracket(bracket) && !currentGroup.isBracket(bracket)) {
				currentGroup = new LyStaffGroup(bracket);
				staffGroups.add(currentGroup);
			}

			if (currentStaff == null) {
				currentStaff = new LyStaff(bracket);
			}

			currentStaff.setName(node.getProperty("Label", Converter.NWC_STRING));
			currentStaff.setShortName(node.getProperty("LabelAbbr", Converter.NWC_STRING));
			node.getStaffInstrument().accept(this);
			int num = 0;
			do {
				final Lyric lyric = node.getLyric(++num);
				if (lyric != null) {
					lyric.accept(this);
				} else {
					num = -1;
				}
			} while (num > 0);
			for (final AbstractNode n : node.getNodes()) {
				n.accept(this);
			}
			if (node.isLayer()) {
				currentStaff.nextLayer();
			} else {
				currentGroup.addStaff(currentStaff);
				currentStaff = null;
				if (!currentGroup.nestBracket(bracket)) {
					currentGroup = null;
				}
			}
		}
	}

	@Override
	public void visit(final StaffInstrument node) {
		final int trans = node.getProperty("Trans", Converter.INTEGER);
		currentStaff.add(new LyTransposition(trans));
	}

	@Override
	public void visit(final TimeSig node) {
		currentStaff.add(new LyTimeSignature(node.getProperty("Signature")));
	}

}
