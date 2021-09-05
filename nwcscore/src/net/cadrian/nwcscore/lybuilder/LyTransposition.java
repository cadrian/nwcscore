package net.cadrian.nwcscore.lybuilder;

public class LyTransposition extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyTransposition node);
	}

	private final int value;

	LyTransposition(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
