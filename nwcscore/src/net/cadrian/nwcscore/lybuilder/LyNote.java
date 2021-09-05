package net.cadrian.nwcscore.lybuilder;

import net.cadrian.nwcscore.music.FullNote;

public class LyNote extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyNote node);
	}

	private final FullNote fullNote;

	LyNote(final FullNote fullNote) {
		this.fullNote = fullNote;
	}

	public FullNote getFullNote() {
		return fullNote;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
