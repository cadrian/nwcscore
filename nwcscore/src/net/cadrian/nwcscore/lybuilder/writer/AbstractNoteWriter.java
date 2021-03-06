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

import java.util.ArrayList;
import java.util.List;

import net.cadrian.nwcscore.music.FullNote;

abstract class AbstractNoteWriter implements NotesWriter {

	protected final NotesWriterContext context;
	private final NotesWriter previous;

	AbstractNoteWriter(final NotesWriterContext context) {
		this.context = context;
		this.previous = context.notesWriter;
	}

	protected final List<List<FullNote>> chords = new ArrayList<>();

	@Override
	public abstract void addChord(final List<FullNote> chord);

	protected final void addChord(final List<FullNote> chord, final boolean full) {
		chords.add(chord);
		if (full) {
			output();
			context.notesWriter = previous;
		}
	}

	protected abstract void output();

	protected final String chords2str(final List<List<FullNote>> chords) {
		final StringBuilder result = new StringBuilder();
		final int n = chords.size();
		for (int i = 0; i < n; i++) {
			if (i > 0) {
				result.append(' ');
			}
			result.append(chord2str(chords.get(i)));
		}
		return result.toString();
	}

	protected final String chord2str(final List<FullNote> chord) {
		final StringBuilder result = new StringBuilder();
		final FullNote nodeFirstFullNote = chord.get(0);
		if (chord.size() == 1) {
			result.append(context.note2str(nodeFirstFullNote));
			result.append(context.duration2str(nodeFirstFullNote));
			result.append(context.slur2str(nodeFirstFullNote));
			result.append(context.tie2str(nodeFirstFullNote));
		} else {
			result.append('<');
			final int n = chord.size();
			for (int i = 0; i < n; i++) {
				if (i > 0) {
					result.append(' ');
				}
				final FullNote nodeFullNote = chord.get(i);
				result.append(context.note2str(nodeFullNote));
				result.append(context.slur2str(nodeFullNote));
				result.append(context.tie2str(nodeFullNote));
			}
			result.append('>');
			result.append(context.duration2str(nodeFirstFullNote));
		}
		return result.toString();
	}
}