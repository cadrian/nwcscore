package net.cadrian.nwcscore.parser.ast;

public class Chord extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(Chord node);
	}

	public Chord() {
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
