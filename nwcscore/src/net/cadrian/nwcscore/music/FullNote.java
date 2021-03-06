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
package net.cadrian.nwcscore.music;

import java.util.Objects;

public final class FullNote {

	public final Note note;
	public final int octave;
	public final Duration duration;
	public final int dots;
	public final boolean slur;
	public final boolean tie;
	public final boolean grace;
	public final boolean cautionary;
	public final Triplet triplet;

	FullNote(final Note note, final int octave, final Duration duration, final int dots, final boolean slur,
			final boolean tie, final boolean grace, final boolean cautionary, final Triplet triplet) {
		if (note == null) {
			throw new RuntimeException("BUG");
		}
		this.note = note;
		this.octave = octave;
		this.duration = duration;
		this.dots = dots;
		this.slur = slur;
		this.tie = tie;
		this.grace = grace;
		this.cautionary = cautionary;
		this.triplet = triplet;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cautionary, dots, duration, grace, note, octave, slur, tie, triplet);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final FullNote other = (FullNote) obj;
		return cautionary == other.cautionary && dots == other.dots && duration == other.duration
				&& grace == other.grace && note == other.note && octave == other.octave && slur == other.slur
				&& tie == other.tie && triplet == other.triplet;
	}

}
