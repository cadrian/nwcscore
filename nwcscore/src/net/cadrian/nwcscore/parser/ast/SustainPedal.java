package net.cadrian.nwcscore.parser.ast;

public class SustainPedal extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(SustainPedal node);
	}

	public SustainPedal() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
