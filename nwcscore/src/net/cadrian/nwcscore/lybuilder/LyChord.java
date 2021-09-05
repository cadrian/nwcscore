package net.cadrian.nwcscore.lybuilder;

import java.util.List;
import java.util.Map;

import net.cadrian.nwcscore.music.FullNote;

public class LyChord extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyChord node);
	}

	private final List<FullNote> shortNotes;
	private final List<FullNote> longNotes;
	private final Map<String, String> opts;

	LyChord(final List<FullNote> shortNotes, List<FullNote> longNotes, final Map<String, String> opts) {
		this.shortNotes = shortNotes;
		this.longNotes = longNotes;
		this.opts = opts;
	}

	public List<FullNote> getShortNotes() {
		return shortNotes;
	}

	public List<FullNote> getLongNotes() {
		return longNotes;
	}

	public String getOpt(final String opt) {
		return opts.get(opt);
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
