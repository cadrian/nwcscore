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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Converter<T> {

	public static final Converter<Integer> INTEGER = new Converter<>() {
		@Override
		public Integer convert(final String value) {
			if (value == null) {
				return 0;
			}
			return Integer.decode(value);
		}
	};

	public static final Converter<String> NWC_STRING = new Converter<>() {
		public String convert(final String value) {
			if (value == null) {
				return null;
			}
			final StringBuilder result = new StringBuilder();
			int s = 0;
			for (final char c : value.toCharArray()) {
				switch (s) {
				case 0:
					if (c != '"') {
						throw new RuntimeException("invalid NWC string");
					}
					s = 1;
					break;
				case 1:
					switch (c) {
					case '\\':
						s = 10;
						break;
					case '"':
						s = 2;
						break;
					default:
						result.append(c);
					}
					break;
				case 2:
					throw new RuntimeException("invalid NWC string");
				case 10:
					switch (c) {
					case 'n':
						result.append('\n');
						break;
					default:
						result.append(c);
					}
					s = 1;
					break;
				}
			}
			return result.toString();
		}
	};

	public static final Converter<Set<String>> NWC_STRINGSET = new Converter<>() {
		@Override
		public Set<String> convert(final String value) {
			if (value == null) {
				return null;
			}
			final Set<String> result = new HashSet<>();
			final String[] split = value.split(",");
			result.addAll(Arrays.asList(split));
			return result;
		}
	};

	public static final Converter<Map<String, String>> NWC_STRINGMAP = new Converter<>() {
		@Override
		public Map<String, String> convert(final String value) {
			if (value == null) {
				return null;
			}
			final Map<String, String> result = new HashMap<>();
			for (final String s : value.split(",")) {
				final int i = s.indexOf('=');
				if (i < 0) {
					result.put(s, "");
				} else {
					result.put(s.substring(0, i), s.substring(i + 1));
				}
			}
			return result;
		}
	};

	T convert(String value);

}
