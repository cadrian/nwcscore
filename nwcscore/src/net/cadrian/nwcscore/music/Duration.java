package net.cadrian.nwcscore.music;

import java.util.HashMap;
import java.util.Map;

public enum Duration {

	WHOLE("Whole", 1), HALF("Half", 2), QUARTER("4th", 4), EIGHTH("8th", 8), SIXTEENTH("16th", 16),
	THIRTYSECOND("32nd", 32), SIXTYFOURTH("64th", 64);

	public final String nwcid;
	public final int division;

	private Duration(final String nwcid, final int division) {
		this.nwcid = nwcid;
		this.division = division;
	}

	private static final Map<String, Duration> MAP = new HashMap<>();
	static {
		for (final Duration duration : values()) {
			MAP.put(duration.nwcid, duration);
		}
	}

	public static Duration nwc(final String nwcid) {
		return MAP.get(nwcid);
	}

}
