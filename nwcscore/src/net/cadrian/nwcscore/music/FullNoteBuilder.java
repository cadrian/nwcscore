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
package net.cadrian.nwcscore.music;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FullNoteBuilder {

	private static final Pattern NWCPOS_PATTERN = Pattern
			.compile("(?<alt>[#bn])?(?<pos>-?[0-9]+)(?<tie>\\^)?(?<cautionary>\\))?");
	private static final Pattern NWCDUR_PATTERN = Pattern
			.compile("(?<dur>(Whole|Half|4th|8th|16th|32nd|64th))(,(?<properties>.*))?");

	private Note note;
	private int octave;
	private final Duration duration;
	private final int dots;
	private final boolean slur;
	private boolean tie;
	private boolean cautionary;
	private final Triplet triplet;

	private FullNoteBuilder(final String nwcdur) {
		final Matcher nwcdurMatcher;

		nwcdurMatcher = NWCDUR_PATTERN.matcher(nwcdur);
		if (!nwcdurMatcher.matches()) {
			throw new RuntimeException("Invalid nwc dur: " + nwcdur);
		}

		final Map<String, String> properties = getProperties(nwcdurMatcher);
		duration = getDuration(nwcdurMatcher);
		dots = getDots(properties);
		slur = isSlur(properties);
		triplet = getTriplet(properties);
	}

	FullNoteBuilder(final String nwcdur, final boolean visible) {
		this(nwcdur);
		note = visible ? Note.REST : Note.SKIP;
		if (triplet != null) {
			System.out.println("REST IN TRIPLET: "+triplet);
		}
	}

	FullNoteBuilder(final String nwcpos, final String nwcdur, final Note note0, final int octave0,
			final KeySignature sig) {
		this(nwcdur);

		final Matcher nwcposMatcher;
		nwcposMatcher = NWCPOS_PATTERN.matcher(nwcpos);
		if (!nwcposMatcher.matches()) {
			throw new RuntimeException("Invalid nwc pos: " + nwcpos);
		}

		final char alteration = getAlteration(nwcposMatcher);

		final int pos = getPos(nwcposMatcher);
		tie = isTie(nwcposMatcher);
		cautionary = isCautionary(nwcposMatcher);

		note = note0;
		octave = octave0;
		// dumb algorithm :-)
		if (pos < 0) {
			for (int i = pos; i < 0; i++) {
				if (note == Note.C) {
					octave--;
				}
				note = note.getPrevious();
			}
		} else if (pos > 0) {
			for (int i = 0; i < pos; i++) {
				note = note.getNext();
				if (note == Note.C) {
					octave++;
				}
			}
		}
		note = sig.getAlteredNote(note);
		switch (alteration) {
		case ' ':
			// do nothing
			break;
		case 'n':
			note = note.getNatural();
			break;
		case '#':
			note = note.getSharp();
			break;
		case 'b':
			note = note.getFlat();
			break;
		default:
			throw new RuntimeException("BUG");
		}
	}

	private static Map<String, String> getProperties(final Matcher nwcdurMatcher) {
		final String properties = nwcdurMatcher.group("properties");
		if (properties == null || properties.isEmpty()) {
			return Collections.emptyMap();
		}
		final Map<String, String> result = new HashMap<>();
		for (final String property : properties.split(",")) {
			final int eq = property.indexOf('=');
			if (eq == -1) {
				result.put(property, "");
			} else {
				result.put(property.substring(0, eq), property.substring(eq + 1));
			}
		}
		return result;
	}

	private Triplet getTriplet(final Map<String, String> properties) {
		final String triplet = properties.get("Triplet");
		if (triplet == null) {
			return null;
		}
		switch (triplet) {
		case "":
			return Triplet.INSIDE;
		case "First":
			return Triplet.START;
		case "End":
			return Triplet.END;
		}
		throw new RuntimeException("Invalid triplet value: " + triplet);
	}

	private boolean isCautionary(final Matcher nwcposMatcher) {
		final String cautionary = nwcposMatcher.group("cautionary");
		return cautionary != null && !cautionary.isEmpty();
	}

	private int getDots(final Map<String, String> properties) {
		if (properties.containsKey("Dotted")) {
			return 1;
		}
		return 0;
	}

	private Duration getDuration(final Matcher nwcdurMatcher) {
		return Duration.nwc(nwcdurMatcher.group("dur"));
	}

	private boolean isSlur(final Map<String, String> properties) {
		return properties.containsKey("Slur");
	}

	private boolean isTie(final Matcher nwcposMatcher) {
		final String tie = nwcposMatcher.group("tie");
		return tie != null && !tie.isEmpty();
	}

	private int getPos(final Matcher nwcposMatcher) {
		return Integer.parseInt(nwcposMatcher.group("pos"));
	}

	private char getAlteration(final Matcher nwcposMatcher) {
		final char alteration;
		final String alt = nwcposMatcher.group("alt");
		if (alt == null || alt.isEmpty()) {
			alteration = ' ';
		} else {
			switch (alt) {
			case "#":
			case "b":
			case "n":
				alteration = alt.charAt(0);
				break;
			default:
				throw new RuntimeException("BUG");
			}
		}
		return alteration;
	}

	FullNote build() {
		// TODO: add ignored properties (Accent, Staccato, ???)
		return new FullNote(note, octave, duration, dots, slur, tie, cautionary, triplet);
	}

}
