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

import java.io.IOException;

import net.cadrian.nwcscore.parser.ast.Song;

public class LilypondBuilder {

	private final Song song;

	public LilypondBuilder(final Song song) {
		this.song = song;
	}

	public void output(final String filename) throws IOException {
		final NwcVisitor visitor = new NwcVisitor();
		song.accept(visitor);
		visitor.output(filename);
	}

}
