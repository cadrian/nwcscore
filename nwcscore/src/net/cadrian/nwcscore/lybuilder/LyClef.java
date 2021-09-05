package net.cadrian.nwcscore.lybuilder;

import net.cadrian.nwcscore.music.Clef;

public class LyClef extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyClef node);
	}

	private final Clef clef;

	LyClef(final Clef clef) {
		this.clef = clef;
	}

	public Clef getClef() {
		return clef;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
