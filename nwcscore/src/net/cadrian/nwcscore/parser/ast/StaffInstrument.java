package net.cadrian.nwcscore.parser.ast;

public class StaffInstrument extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(StaffInstrument node);
	}

	public StaffInstrument() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
