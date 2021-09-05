package net.cadrian.nwcscore.parser.ast;

public class User extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(User node);
	}

	private final String name;

	public User(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
