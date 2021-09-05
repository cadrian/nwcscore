package net.cadrian.nwcscore.music;

import java.util.Objects;

public final class FullNote {

	public final Note note;
	public final int octave;
	public final Duration duration;
	public final int dots;
	public final boolean slur;
	public final boolean tie;
	public final boolean cautionary;
	public final Triplet triplet;

	FullNote(final Note note, final int octave, final Duration duration, final int dots, final boolean slur,
			final boolean tie, final boolean cautionary, final Triplet triplet) {
		if (note == null) {
			throw new RuntimeException("BUG");
		}
		this.note = note;
		this.octave = octave;
		this.duration = duration;
		this.dots = dots;
		this.slur = slur;
		this.tie = tie;
		this.cautionary = cautionary;
		this.triplet = triplet;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cautionary, dots, duration, note, triplet, octave, slur, tie);
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
		return cautionary == other.cautionary && dots == other.dots && duration == other.duration && note == other.note
				&& triplet == other.triplet && octave == other.octave && slur == other.slur && tie == other.tie;
	}

}
