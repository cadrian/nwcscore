package net.cadrian.nwcscore.ast;

public class RestMultiBar extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(RestMultiBar node);
	}

	public RestMultiBar() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
