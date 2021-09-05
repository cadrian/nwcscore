package net.cadrian.nwcscore.lybuilder;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class LyStaffGroup {

	private final Set<String> bracket;
	private final List<LyStaff> staves = new ArrayList<>();

	LyStaffGroup(final Set<String> bracket) {
		this.bracket = bracket;
	}

	void addStaff(final LyStaff staff) {
		staves.add(staff);
	}

	public String getName() {
		String result = null;
		for (final LyStaff staff : staves) {
			final String name = staff.getName();
			if (name != null && !name.isEmpty()) {
				if (result != null) {
					return null;
				}
				result = name;
			}
		}
		return result;
	}

	public String getShortName() {
		String result = null;
		for (final LyStaff staff : staves) {
			final String name = staff.getShortName();
			if (name != null) {
				if (result == null) {
					result = name;
				} else {
					return null;
				}
			}
		}
		return result;
	}

	public List<LyStaff> getStaves() {
		return Collections.unmodifiableList(staves);
	}

	public int size() {
		return staves.size();
	}

	void output(final PrintWriter out) {
		if (staves.size() > 1 && getName() != null && hasBracket("Brace")) {
			out.println("  \\new PianoStaff \\with {");
			out.println("    instrumentName = #\"" + getName() + "\"");
			out.println("    shortInstrumentName = #\"" + getShortName() + "\"");
			out.println("    \\remove \"System_start_delimiter_engraver\"");
			out.println("    \\override InstrumentName.self-alignment-X = #RIGHT");
			out.println("  } { <<");
			for (final LyStaff staff : staves) {
				staff.output(out, false);
			}
			out.println("  >> }");
		} else {
			for (final LyStaff staff : staves) {
				staff.output(out, true);
			}
		}
	}

	public boolean isBracket(final Set<String> bracket) {
		return Objects.equals(this.bracket, bracket);
	}

	public boolean hasBracket(final String string) {
		return bracket != null && bracket.contains(string);
	}

	public boolean nestBracket(final Set<String> bracket) {
		return bracket.containsAll(this.bracket);
	}

}
