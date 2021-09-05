/*
 * This file is part of NWCScore.
 *
 * NWCScore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * NWCScore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NWCScore.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
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
