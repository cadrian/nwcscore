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
