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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Staff extends AbstractNode {

	public static interface Visitor extends AbstractNode.Visitor {
		void visit(Staff node);
	}

	private StaffInstrument staffInstrument;
	private final Map<Integer, Lyric> lyrics = new HashMap<>();
	private final List<AbstractNode> nodes = new ArrayList<>();

	public Staff() {
	}

	public StaffInstrument getStaffInstrument() {
		return staffInstrument;
	}

	public void setStaffInstrument(final StaffInstrument staffInstrument) {
		this.staffInstrument = staffInstrument;
	}

	public void setLyric(final Lyric lyric) {
		lyrics.put(lyric.getNum(), lyric);
	}

	public Lyric getLyric(final int num) {
		return lyrics.get(num);
	}

	public List<AbstractNode> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public void addNode(final AbstractNode node) {
		nodes.add(node);
	}

	@Override
	public void accept(final AbstractNode.Visitor v) {
		((Visitor) v).visit(this);
	}

	public boolean isLayer() {
		final Set<String> property = getWithNextStaff();
		return property != null && property.contains("Layer");
	}

	public boolean isConnectBars() {
		final Set<String> property = getWithNextStaff();
		return property != null && property.contains("ConnectBars");
	}

	public Set<String> getBracket() {
		final Set<String> result = getWithNextStaff();
		if (result == null) {
			return Collections.emptySet();
		}
		result.remove("Layer");
		result.remove("ConnectBars");
		return Collections.unmodifiableSet(result);
	}

	private Set<String> getWithNextStaff() {
		return getProperty("WithNextStaff", Converter.NWC_STRINGSET);
	}

}
