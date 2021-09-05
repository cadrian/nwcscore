package net.cadrian.nwcscore.ast;

public class Clef extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(Clef node);
	}

	public Clef() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
