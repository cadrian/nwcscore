package net.cadrian.nwcscore.ast;

public class Lyric extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(Lyric node);
	}

	private final int num;

	public Lyric(final int num) {
		this.num = num;
	}

	public int getNum() {
		return num;
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
