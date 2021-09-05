package net.cadrian.nwcscore.ast;

public class Bar extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(Bar node);
	}

	public Bar() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
