package net.cadrian.nwcscore.lybuilder;

import net.cadrian.nwcscore.music.Pedal;

public class LyPedal extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyPedal node);
	}

	private final Pedal pedal;
	private final boolean on;

	LyPedal(final Pedal pedal, final boolean on) {
		this.pedal = pedal;
		this.on = on;
	}

	public Pedal getPedal() {
		return pedal;
	}

	public boolean isOn() {
		return on;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
