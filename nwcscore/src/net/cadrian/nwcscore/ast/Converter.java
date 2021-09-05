package net.cadrian.nwcscore.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Converter<T> {

	public static final Converter<Integer> INTEGER = new Converter<>() {
		@Override
		public Integer convert(final String value) {
			return Integer.decode(value);
		}
	};

	public static final Converter<String> NWC_STRING = new Converter<>() {
		public String convert(final String value) {
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
			final Set<String> result = new HashSet<>();
			final String[] split = value.split(",");
			result.addAll(Arrays.asList(split));
			return result;
		}
	};

	public static final Converter<Map<String, String>> NWC_STRINGMAP = new Converter<>() {
		@Override
		public Map<String, String> convert(final String value) {
			final Map<String, String> result = new HashMap<>();
			for (final String s : value.split(",")) {
				final int i = s.indexOf(',');
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
