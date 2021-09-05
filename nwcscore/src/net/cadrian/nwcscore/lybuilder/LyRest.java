package net.cadrian.nwcscore.lybuilder;

import net.cadrian.nwcscore.music.FullNote;

public class LyRest extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyRest node);
	}

	private final FullNote fullNote;

	LyRest(final FullNote fullNote) {
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
