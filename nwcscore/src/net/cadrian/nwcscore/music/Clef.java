package net.cadrian.nwcscore.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Clef {
	// TODO check values for Alto and Tenor keys
	TREBLE(Note.B, 1), TREBLE8(Note.B, 0), BASS(Note.D, 0), ALTO(Note.A, 1), TENOR(Note.C, 0);

	// notes at line 0 (NWC-style: 0 is the 3rd line...)
	public final Note note0;
	public final int octave0;

	private Clef(final Note note0, final int octave0) {
		this.note0 = note0;
		this.octave0 = octave0;
	}

	public FullNote getRest(final String nwcdur, final boolean visible) {
		return new FullNoteBuilder(nwcdur, visible).build();
	}

	public FullNote getNote(final String nwcpos, final String nwcdur, final KeySignature sig) {
		return new FullNoteBuilder(nwcpos, nwcdur, note0, octave0, sig).build();
	}

	public List<FullNote> getChord(final String nwcpos, final String nwcdur, final KeySignature sig) {
		final List<FullNote> result = new ArrayList<>();

		for (final String pos : nwcpos.split(",")) {
			result.add(new FullNoteBuilder(pos, nwcdur, note0, octave0, sig).build());
		}

		return Collections.unmodifiableList(result);
	}
}
