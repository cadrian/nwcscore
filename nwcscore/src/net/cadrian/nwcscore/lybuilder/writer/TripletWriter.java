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