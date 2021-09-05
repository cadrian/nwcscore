package net.cadrian.nwcscore.ast;

public class Note extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(Note node);
	}

	public Note() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
