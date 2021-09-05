package net.cadrian.nwcscore.ast;

public class TimeSig extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(TimeSig node);
	}

	public TimeSig() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
