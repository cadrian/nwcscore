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

import java.util.List;

import net.cadrian.nwcscore.music.FullNote;

public class ChordWriter extends AbstractNoteWriter {

	static enum Direction {
		UP, DOWN;
	}

	private final Direction direction;
	private final List<FullNote> longChord;

	private final int chordLength;
	private int chordFill;

	ChordWriter(final NotesWriterContext context, final List<FullNote> longChord, final String stem) {
		super(context);
		this.longChord = longChord;
		this.chordLength = chordDuration(longChord.get(0));
		if (stem == null) {
			direction = null;
		} else {
			switch (stem) {
			case "Up":
				direction = Direction.UP;
				break;
			case "Down":
				direction = Direction.DOWN;
				break;
			default:
				throw new RuntimeException("Invalid stem direction: " + stem);
			}
		}
	}

	@Override
	public void addChord(final List<FullNote> chord) {
		chordFill += chordDuration(chord.get(0));
		addChord(chord, chordFill >= chordLength);
	}

	@Override
	protected void output() {
		if (chordFill == 0) {
			context.out.println(context.indent + chord2str(longChord));
		} else {
			switch (direction) {
			case UP:
				context.out.println(
						context.indent + "<< { " + chords2str(chords) + " } \\\\ " + chord2str(longChord) + ">>");
				break;
			case DOWN:
				context.out.println(
						context.indent + "<< " + chord2str(longChord) + " \\\\ { " + chords2str(chords) + " } >>");
				break;
			}
		}
	}

	private static int chordDuration(final FullNote fullNote) {
		if (fullNote.grace) {
			return 0;
		}
		final int base = 256 / fullNote.duration.division;
		switch (fullNote.dots) {
		case 0:
			return base;
		case 1:
			return base + base / 2;
		case 2:
			return base + base / 2 + base / 4;
		default:
			throw new RuntimeException("BUG");
		}
	}
}