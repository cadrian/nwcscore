package net.cadrian.nwcscore.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Song extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(Song node);
	}

	private final SongInfo songInfo;
	private final List<Staff> staves = new ArrayList<>();

	public Song(final SongInfo songInfo) {
		this.songInfo = songInfo;
	}

	public SongInfo getSongInfo() {
		return songInfo;
	}

	public void addStaff(final Staff staff) {
		staves.add(staff);
	}

	public List<Staff> getStaves() {
		return Collections.unmodifiableList(staves);
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
