package net.cadrian.nwcscore.ast;

public class Key extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(Key node);
	}

	public Key() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
