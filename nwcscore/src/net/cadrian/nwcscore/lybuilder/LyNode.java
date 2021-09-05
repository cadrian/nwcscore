package net.cadrian.nwcscore.lybuilder;

abstract class LyNode {

	static interface Visitor {
		// empty interface
	}

	public abstract void accept(Visitor v);

}
