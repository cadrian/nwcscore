/*
 * This file is part of NWCScore.
 *
 * NWCScore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * NWCScore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NWCScore.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.cadrian.nwcscore.lybuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cadrian.nwcscore.music.KeySignature;
import net.cadrian.nwcscore.music.Pedal;
import net.cadrian.nwcscore.parser.ast.AbstractNode;
import net.cadrian.nwcscore.parser.ast.Bar;
import net.cadrian.nwcscore.parser.ast.Chord;
import net.cadrian.nwcscore.parser.ast.Clef;
import net.cadrian.nwcscore.parser.ast.Converter;
import net.cadrian.nwcscore.parser.ast.Key;
import net.cadrian.nwcscore.parser.ast.Lyric;
import net.cadrian.nwcscore.parser.ast.Note;
import net.cadrian.nwcscore.parser.ast.PgSetup;
import net.cadrian.nwcscore.parser.ast.Rest;
import net.cadrian.nwcscore.parser.ast.RestChord;
import net.cadrian.nwcscore.parser.ast.RestMultiBar;
import net.cadrian.nwcscore.parser.ast.Song;
import net.cadrian.nwcscore.parser.ast.SongInfo;
import net.cadrian.nwcscore.parser.ast.Staff;
import net.cadrian.nwcscore.parser.ast.StaffInstrument;
import net.cadrian.nwcscore.parser.ast.SustainPedal;
import net.cadrian.nwcscore.parser.ast.TimeSig;
import net.cadrian.nwcscore.parser.ast.User;
import net.cadrian.nwcscore.parser.ast.Visitors;

class NwcVisitor implements Visitors {

	private String songTitle;
	private String songAuthor;
	private String songLyricist;
	private String songCopyright1;
	private String songCopyright2;

	private int firstBar = 1;

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
	public void visit(final RestChord node) {
		final String visibility = node.getProperty("Visibility");
		if (visibility == null || visibility.equals("Y")) {
			final String nwcdur = node.getProperty("Dur"); // rest
			final String nwcdur2 = node.getProperty("Dur2"); // note
			final String nwcpos2 = node.getProperty("Pos2");
			final Map<String, String> opts = node.getProperty("Opts", Converter.NWC_STRINGMAP);
			currentStaff.add(new LyChord(Collections.singletonList(currentClef.getRest(nwcdur, true)),
					currentClef.getChord(nwcpos2, nwcdur2, currentKeySignature), opts));
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
		case "Alto":
			if (shift == null) {
				currentClef = net.cadrian.nwcscore.music.Clef.ALTO;
			} else {
				throw new RuntimeException("clef not implemented: alto " + shift);
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
		final String signature = node.getProperty("Signature");
		final LyKey lyKey = new LyKey(signature, node.getProperty("Tonic"),
				visibility == null || visibility.equals("Y"));
		currentStaff.add(lyKey);
		currentKeySignature = lyKey.getSignature();
	}

	@Override
	public void visit(final Lyric node) {
		currentStaff.setLyrics(node.getNum(), node.getProperty("Text", Converter.NWC_STRING));
	}

	@Override
	public void visit(final Song node) {
		node.getSongInfo().accept(this);
		node.getPgSetup().accept(this);
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
	public void visit(final PgSetup node) {
		final int bar = node.getProperty("StartingBar", Converter.INTEGER);
		if (bar > 0) {
			firstBar = bar;
		}
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
				currentStaff = new LyStaff(bracket, firstBar, node.isConnectBars());
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

	@Override
	public void visit(final User node) {
		if (!node.getName().equals("Pianoteq_Pedals.nw")) {
			System.err.println("Ignored user object: " + node.getName());
		} else {
			final Pedal pedal;
			switch (node.getProperty("Type")) {
			case "Sustain":
				pedal = Pedal.SUSTAIN;
				break;
			case "Sustenuto":
				pedal = Pedal.SOSTENUTO;
				break;
			case "Soft":
				pedal = Pedal.UNA_CORDA;
				break;
			default:
				throw new RuntimeException("Pedal type not supported: " + node.getProperty("Type"));
			}
			final int value = node.getProperty("Value", Converter.INTEGER);
			currentStaff.add(new LyPedal(pedal, value > 0));
		}
	}

	@Override
	public void visit(final SustainPedal node) {
		final String status = node.getProperty("Status");
		final boolean on = status == null || !status.equals("Released");
		currentStaff.add(new LyPedal(Pedal.SUSTAIN, on));
	}

}
