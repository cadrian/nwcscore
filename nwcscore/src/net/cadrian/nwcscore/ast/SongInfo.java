package net.cadrian.nwcscore.ast;

public class SongInfo extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(SongInfo node);
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
