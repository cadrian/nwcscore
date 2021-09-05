package net.cadrian.nwcscore.ast;

public class Rest extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(Rest node);
	}

	public Rest() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
