package net.cadrian.nwcscore.parser;

import net.cadrian.nwcscore.ast.AbstractNode;
import net.cadrian.nwcscore.ast.Bar;
import net.cadrian.nwcscore.ast.Chord;
import net.cadrian.nwcscore.ast.Clef;
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

public class Parser {

	private final ParserBuffer buffer;
	private int line = 0;

	private Song song;
	private Staff staff;

	public Parser(final String input) throws ParseException {
		this.buffer = new ParserBuffer(input);
		parseLines();
	}

	public Parser(final StringBuilder input) throws ParseException {
		this.buffer = new ParserBuffer(input);
		parseLines();
	}

	public Song getSong() {
		return song;
	}

	private void parseLines() throws ParseException {
		parseHeaderFooter();
		while (parseLine()) {
			// continue
		}
		parseHeaderFooter();
		if (!buffer.isOff()) {
			throw new ParseException("stray data line " + line);
		}
	}

	private void parseHeaderFooter() throws ParseException {
		line++;
		if (!buffer.check('!')) {
			throw new ParseException("Missing '!' at line " + line + " start");
		}
		buffer.next();
		final String s = buffer.readWord();
		if (!s.equals("NoteWorthyComposer")) {
			throw new ParseException("Invalid NoteWorthyComposer file");
		}
		buffer.skipLine();
	}

	private boolean parseLine() throws ParseException {
		if (!buffer.check('|')) {
			return false;
		}
		line++;
		buffer.next();
		final String type = buffer.readWord();
		switch (type) {
		case "SongInfo":
			parseSongInfo();
			break;
		case "AddStaff":
			parseAddStaff();
			break;
		case "StaffProperties":
			parseProperties(staff);
			break;
		case "StaffInstrument":
			parseStaffInstrument();
			break;
		case "Lyric1":
			parseLyric1();
			break;
		case "Clef":
			parseClef();
			break;
		case "Key":
			parseKey();
			break;
		case "TimeSig":
			parseTimeSig();
			break;
		case "RestMultiBar":
			parseRestMultiBar();
			break;
		case "Bar":
			parseBar();
			break;
		case "Note":
			parseNote();
			break;
		case "Chord":
			parseChord();
			break;
		case "Rest":
			parseRest();
			break;
		default:
			buffer.skipLine();
		}
		return true;
	}

	private void parseProperties(final AbstractNode currentNode) throws ParseException {
		while (!buffer.check('\n') && !buffer.check('\r')) {
			if (!buffer.check('|')) {
				throw new ParseException("Missing '|' at line " + line + " after " + currentNode.getType());
			}
			buffer.next();
			final String prop = buffer.readUntil(':', '\r', '\n');
			if (!buffer.check(':')) {
				currentNode.addProperty(prop, null);
				buffer.skipLine();
			} else {
				buffer.next();
				final String val = buffer.readUntil('|', '\r', '\n');
				currentNode.addProperty(prop, val);
			}
		}
		buffer.skipLine();
	}

	private void parseSongInfo() throws ParseException {
		final SongInfo songInfo = new SongInfo();
		parseProperties(songInfo);
		song = new Song(songInfo);
	}

	private void parseAddStaff() throws ParseException {
		staff = new Staff();
		song.addStaff(staff);
		parseProperties(staff);
	}

	private void parseStaffInstrument() throws ParseException {
		final StaffInstrument inst = new StaffInstrument();
		parseProperties(inst);
		staff.setStaffInstrument(inst);
	}

	private void parseLyric1() throws ParseException {
		final Lyric lyric = new Lyric(1);
		parseProperties(lyric);
		staff.setLyric(lyric);
	}

	private void parseClef() throws ParseException {
		final Clef clef = new Clef();
		parseProperties(clef);
		staff.addNode(clef);
	}

	private void parseKey() throws ParseException {
		final Key key = new Key();
		parseProperties(key);
		staff.addNode(key);
	}

	private void parseTimeSig() throws ParseException {
		final TimeSig ts = new TimeSig();
		parseProperties(ts);
		staff.addNode(ts);
	}

	private void parseRestMultiBar() throws ParseException {
		final RestMultiBar rmb = new RestMultiBar();
		parseProperties(rmb);
		staff.addNode(rmb);
	}

	private void parseBar() throws ParseException {
		final Bar bar = new Bar();
		parseProperties(bar);
		staff.addNode(bar);
	}

	private void parseNote() throws ParseException {
		final Note note = new Note();
		parseProperties(note);
		staff.addNode(note);
	}

	private void parseChord() throws ParseException {
		final Chord chord = new Chord();
		parseProperties(chord);
		staff.addNode(chord);
	}

	private void parseRest() throws ParseException {
		final Rest rest = new Rest();
		parseProperties(rest);
		staff.addNode(rest);
	}

}
