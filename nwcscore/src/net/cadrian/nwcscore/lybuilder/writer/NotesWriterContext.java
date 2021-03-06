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
package net.cadrian.nwcscore.lybuilder.writer;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cadrian.nwcscore.music.FullNote;

public class NotesWriterContext {

	final PrintWriter out;
	final String indent;

	private boolean slur;
	private final Set<FullNote> ties = new HashSet<>();
	NotesWriter notesWriter;

	public NotesWriterContext(final PrintWriter out, final String indent) {
		this.out = out;
		this.indent = indent;
	}

	public void singleChord(final List<FullNote> chord) {
		if (notesWriter != null) {
			throw new RuntimeException("Invalid nwc file: nested note writers");
		}
		new ChordWriter(this, chord, null).output();
	}

	public void singleRest(final FullNote fullNote, final String s) {
		out.println(indent + s + duration2str(fullNote));
	}

	public void startChord(final List<FullNote> chord, final String stem) {
		if (notesWriter != null) {
			throw new RuntimeException("Nested note writers not supported");
		}
		notesWriter = new ChordWriter(this, chord, stem);
	}

	public void startTriplet(final List<FullNote> chord) {
		if (notesWriter != null) {
			throw new RuntimeException("Nested note writers not supported");
		}
		notesWriter = new TripletWriter(this, chord);
	}

	public void addChord(final List<FullNote> chord) {
		if (notesWriter == null) {
			throw new RuntimeException("Invalid nwc file: missing note writer");
		}
		notesWriter.addChord(chord);
	}

	public boolean isStarted() {
		return notesWriter != null;
	}

	FullNote getNoteOrTie(final FullNote fullNote) {
		for (final FullNote tie : ties) {
			if (fullNote.note.isNatural()) {
				if (fullNote.note == tie.note.getNatural() && fullNote.octave == tie.octave) {
					ties.remove(tie);
					return tie;
				}
			} else {
				if (fullNote.note == tie.note && fullNote.octave == tie.octave) {
					ties.remove(tie);
					return tie;
				}
			}
		}
		return fullNote;
	}

	String note2str(final FullNote nodeFullNote) {
		final StringBuilder result = new StringBuilder();
		final FullNote tieFullNote = getNoteOrTie(nodeFullNote);
		if (nodeFullNote.tie) {
			ties.add(nodeFullNote);
		}
		if (nodeFullNote.grace) {
			result.append("\\grace ");
		}
		result.append(tieFullNote.note.id.toLowerCase().replace('???', 's').replace('???', 'f'));
		result.append(octave2str(tieFullNote.octave));
		result.append(cautionary2str(nodeFullNote.cautionary));
		return result.toString();
	}

	String slur2str(final FullNote fullNote) {
		if (fullNote.slur && !slur) {
			slur = true;
			return "(";
		} else if (!fullNote.slur && slur) {
			slur = false;
			return ")";
		}
		return "";
	}

	String tie2str(final FullNote fullNote) {
		return fullNote.tie ? "~" : "";
	}

	private String dots2str(final int dots) {
		switch (dots) {
		case 0:
			return "";
		case 1:
			return ".";
		case 2:
			return "..";
		default:
			throw new RuntimeException("BUG");
		}
	}

	private static String octave2str(final int octave) {
		switch (octave) {
		case -2:
			return ",,";
		case -1:
			return ",";
		case 0:
			return "";
		case 1:
			return "'";
		case 2:
			return "''";
		case 3:
			return "'''";
		case 4:
			return "''''";
		default:
			throw new RuntimeException("invalid octave: " + octave);
		}
	}

	String duration2str(final FullNote fullNote) {
		return fullNote.duration.division + dots2str(fullNote.dots);
	}

	private String cautionary2str(final boolean cautionary) {
		return cautionary ? "?" : "";
	}

}
