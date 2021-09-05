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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.cadrian.nwcscore.music.FullNote;

public class LyChord extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyChord node);
	}

	private final List<FullNote> shortNotes;
	private final List<FullNote> longNotes;
	private final Map<String, String> opts;

	LyChord(final List<FullNote> shortNotes, final List<FullNote> longNotes, final Map<String, String> opts) {
		if (longNotes != null && longNotes.get(0).duration == shortNotes.get(0).duration) {
			List<FullNote> notes = new ArrayList<>(shortNotes);
			notes.addAll(longNotes);
			this.shortNotes = Collections.unmodifiableList(notes);
			this.longNotes = null;
		} else {
			this.shortNotes = shortNotes;
			this.longNotes = longNotes;
		}
		this.opts = opts;
	}

	public List<FullNote> getShortNotes() {
		return shortNotes;
	}

	public List<FullNote> getLongNotes() {
		return longNotes;
	}

	public String getOpt(final String opt) {
		return opts.get(opt);
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
