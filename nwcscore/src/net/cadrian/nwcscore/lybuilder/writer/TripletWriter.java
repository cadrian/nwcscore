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
import net.cadrian.nwcscore.music.Triplet;

class TripletWriter extends AbstractNoteWriter {

	TripletWriter(final NotesWriterContext context, final List<FullNote> chord) {
		super(context);
		addChord(chord, false);
	}

	@Override
	public void addChord(final List<FullNote> chord) {
		addChord(chord, chord.get(0).triplet == Triplet.END);
	}

	@Override
	protected void output() {
		context.out.println(context.indent + "\\tuplet 3/2 { " + chords2str(chords) + " }");
	}
}