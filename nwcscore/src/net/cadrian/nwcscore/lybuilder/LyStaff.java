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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class LyStaff {

	private final List<List<LyNode>> nodes = new ArrayList<>();
	private List<LyNode> layer;

	private String name;
	private String shortName;
	private final Set<String> bracket;
	private LyStaffGroup group;

	private final int firstBar;

	private final Map<Integer, String> lyrics = new TreeMap<>();

	LyStaff(final Set<String> bracket, final int firstBar) {
		this.bracket = bracket;
		this.firstBar = firstBar;
		nextLayer();
	}

	void setGroup(final LyStaffGroup group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	void setName(final String name) {
		if (this.name == null) {
			this.name = name;
		}
	}

	public String getShortName() {
		return shortName;
	}

	void setShortName(final String shortName) {
		if (this.shortName == null) {
			this.shortName = shortName;
		}
	}

	void add(final LyNode node) {
		layer.add(node);
	}

	void setLyrics(final int num, final String lyrics) {
		this.lyrics.put(num, lyrics);
	}

	public void output(final PrintWriter out, final boolean withNames, final String... instructions) {
		out.print("  \\new Staff ");
		if (withNames) {
			out.println("\\with {");
			if (name != null) {
				out.println("    instrumentName = #\"" + name + "\"");
			}
			if (shortName != null) {
				out.println("    shortInstrumentName = #\"" + shortName + "\"");
			}
			out.println("    \\override InstrumentName.self-alignment-X = #RIGHT");
			if (group == null || !group.hasBracket("ConnectBars")) { // TODO always false, get property from Staff
				out.println("    \\override Staff.BarLine.allow-span-bar = ##f");
			}
			out.print("  }");
		}
		switch (nodes.size()) {
		case 1:
			out.println("  {");
			outputInstructions(out, instructions);
			out.println("    \\new Voice {");
			if (firstBar != 1) {
				out.println("    \\set Score.currentBarNumber = #" + firstBar);
				out.println("    \\set Score.barNumberVisibility = #all-bar-numbers-visible");
				out.println("    \\bar \"\" % bar#" + firstBar);
			}
			outputLayer(out, "      ", nodes.get(0));
			out.println("    }");
			outputLyrics(out);
			out.println("  }");
			break;
		default:
			out.println("  {");
			outputInstructions(out, instructions);
			out.println("    \\set Staff.pedalSustainStyle = #'mixed");
			out.println("    <<");
			final int n = nodes.size();
			final int n2 = n / 2;
			for (int i = 0; i < n; i++) {
				out.print("      \\new Voice { ");
				if (firstBar != 1) {
					out.println("    \\set Score.currentBarNumber = #" + firstBar);
					out.println("    \\set Score.barNumberVisibility = #all-bar-numbers-visible");
					out.println("    \\bar \"\" % bar#" + firstBar);
				}
				if (i < n2) {
					out.println("\\voiceOne");
				} else {
					out.println("\\voiceTwo");
				}
				outputLayer(out, "        ", nodes.get(i));
				out.println("      }");
			}
			out.println("    >>");
			outputLyrics(out);
			out.println("  }");
			break;
		}
	}

	private void outputInstructions(final PrintWriter out, final String... instructions) {
		for (final String instruction : instructions) {
			out.println("    " + instruction);
		}
	}

	private void outputLyrics(final PrintWriter out) {
		for (final String lyrics : this.lyrics.values()) {
			out.println("    \\addlyrics {");
			for (final String line : lyrics.split("\n")) {
				if (line.isBlank()) {
					out.println();
				} else {
					out.print("      ");
					out.println(line.replace("-", " -- "));
				}
			}
			out.println("    }");
		}
	}

	private void outputLayer(final PrintWriter out, final String indent, final List<LyNode> layer) {
		final LyVisitor v = new LyVisitor(out, indent, firstBar);
		for (final LyNode node : layer) {
			node.accept(v);
		}
		out.println(indent + "\\bar \"|.\" % bar#end");
	}

	void nextLayer() {
		nodes.add(layer = new ArrayList<>());
	}

	public boolean hasBracket(final String string) {
		return bracket.contains(string);
	}

}
