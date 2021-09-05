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

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNode {

	public static interface Visitor {
		// empty interface
	}

	private final Map<String, String> properties = new HashMap<>();

	protected AbstractNode() {
	}

	public void addProperty(final String prop, final String value) {
		properties.put(prop, value);
	}

	public String getProperty(final String prop) {
		return properties.get(prop);
	}

	public <T> T getProperty(final String prop, final Converter<T> converter) {
		final String p = properties.get(prop);
		return converter.convert(p);
	}

	public String getType() {
		return getClass().getSimpleName();
	}

	public abstract void accept(Visitor v);

}
