package net.cadrian.nwcscore.lybuilder;

public class LyMultiRest extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyMultiRest node);
	}

	private final int bars;
	private final boolean showRests;

	LyMultiRest(final int bars, final boolean showRests) {
		this.bars = bars;
		this.showRests = showRests;
	}

	public int getBars() {
		return bars;
	}

	public boolean isShowRests() {
		return showRests;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
