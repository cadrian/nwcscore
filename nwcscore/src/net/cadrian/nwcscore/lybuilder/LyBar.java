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
package net.cadrian.nwcscore.lybuilder;

import net.cadrian.nwcscore.music.Bar;

public class LyBar extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyBar node);
	}

	private final Bar bar;
	private final boolean sysBreak;

	LyBar(final Bar bar, final boolean sysBreak) {
		this.bar = bar;
		this.sysBreak = sysBreak;
	}

	public Bar getBar() {
		return bar;
	}

	public boolean isSysBreak() {
		return sysBreak;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
