package net.cadrian.nwcscore.lybuilder.writer;

import java.util.List;

import net.cadrian.nwcscore.music.FullNote;

public interface NotesWriter {
	void addChord(final List<FullNote> chord);
}