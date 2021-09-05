package net.cadrian.nwcscore.lybuilder;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class LyStaff {

	private final List<List<LyNode>> nodes = new ArrayList<>();
	private List<LyNode> layer;

	private String name;
	private String shortName;
	private final Set<String> bracket;
	private LyStaffGroup group;

	LyStaff(final Set<String> bracket) {
		this.bracket = bracket;
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
			outputLayer(out, "    ", nodes.get(0));
			out.println("  }");
			break;
		default:
			out.println("  {");
			outputInstructions(out, instructions);
			out.println("  <<");
			final int n = nodes.size();
			final int n2 = n / 2;
			for (int i = 0; i < n; i++) {
				out.print("    \\new Voice { ");
				if (i < n2) {
					out.println("\\voiceOne");
				} else {
					out.println("\\voiceTwo");
				}
				outputLayer(out, "      ", nodes.get(i));
				out.println("    }");
			}
			out.println("  >> }");
			break;
		}
	}

	private void outputInstructions(final PrintWriter out, final String... instructions) {
		for (final String instruction : instructions) {
			out.println("    " + instruction);
		}
	}

	private void outputLayer(final PrintWriter out, final String indent, final List<LyNode> layer) {
		final LyVisitor v = new LyVisitor(out, indent);
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
