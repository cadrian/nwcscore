package net.cadrian.nwcscore.lybuilder;

import net.cadrian.nwcscore.music.Bar;

public class LyBar extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyBar node);
	}

	private final Bar bar;
	private final boolean sysBreak;

	LyBar(final Bar bar, final boolean sysBreak) {
		this.bar = bar;
		this.sysBreak = sysBreak;
	}

	public Bar getBar() {
		return bar;
	}

	public boolean isSysBreak() {
		return sysBreak;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
