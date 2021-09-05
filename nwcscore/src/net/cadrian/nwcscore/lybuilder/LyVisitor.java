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
import java.util.Collections;

import net.cadrian.nwcscore.lybuilder.writer.NotesWriterContext;
import net.cadrian.nwcscore.music.Clef;
import net.cadrian.nwcscore.music.FullNote;
import net.cadrian.nwcscore.music.KeySignature;

class LyVisitor implements LyBar.Visitor, LyChord.Visitor, LyClef.Visitor, LyKey.Visitor, LyMultiRest.Visitor,
		LyNote.Visitor, LyPedal.Visitor, LyRest.Visitor, LyTimeSignature.Visitor, LyTransposition.Visitor {

	private final PrintWriter out;
	private final String indent;
	private final NotesWriterContext noteWriterContext;

	// TODO maybe useless? (that's an NWC value used for transposing during
	// playback, not during score printing)
	@SuppressWarnings("unused")
	private int transposition = 0;

	private Clef clef;
	private KeySignature keySignature;
	private LyTimeSignature timeSignature;

	LyVisitor(final PrintWriter out, final String indent) {
		this.out = out;
		this.indent = indent;
		this.noteWriterContext = new NotesWriterContext(out, indent);
	}

	@Override
	public void visit(final LyTransposition node) {
		transposition = node.getValue();
	}

	@Override
	public void visit(final LyTimeSignature node) {
		timeSignature = node;
		out.println(indent + "\\time " + node.getSig());
	}

	@Override
	public void visit(final LyKey node) {
		keySignature = node.getSignature();
		out.println(indent + "\\key " + keySignature.getRoot().toString().toLowerCase() + " \\"
				+ keySignature.getMode().toString().toLowerCase());
	}

	@Override
	public void visit(final LyClef node) {
		out.print(indent);
		clef = node.getClef();
		switch (clef) {
		case TREBLE:
			out.println("\\clef \"treble\"");
			break;
		case TREBLE8:
			out.println("\\clef \"treble_8\"");
			break;
		case BASS:
			out.println("\\clef \"bass\"");
			break;
		case TENOR:
			out.println("\\clef \"tenor\"");
			break;
		case ALTO:
			out.println("\\clef \"alto\"");
			break;
		}
	}

	private int bar = 1;

	@Override
	public void visit(final LyBar node) {
		out.print(indent);
		switch (node.getBar()) {
		case SIMPLE:
			out.print("|");
			break;
		case DOUBLE:
			out.print("\\bar \"||\"");
			break;
		case END:
			out.print("\\bar \"|.\"");
			break;
		case REPEAT_OPEN:
			out.print("\\bar \".|:-||\"");
			break;
		case REPEAT_CLOSE:
			out.print("\\bar \":|.\"");
			break;
		}
		if (node.isSysBreak()) {
			out.print(" \\break");
		}
		out.println(" % bar#" + bar);
		bar++;
	}

	@Override
	public void visit(final LyMultiRest node) {
		if (node.isShowRests()) {
			out.println(indent + "R1*" + timeSignature.getSig() + "*" + +node.getBars());
		} else {
			final String skip = getSkip();

			for (int i = 0; i < node.getBars(); i++) {
				out.print(indent);
				if (i > 0) {
					out.print("| ");
				}
				out.println(skip);
			}
		}
		bar += node.getBars();
	}

	private String getSkip() {
		final StringBuilder result = new StringBuilder();
		int n = timeSignature.getBarLength();
		n = appendSkip(result, n, 256, "s1");
		n = appendSkip(result, n, 128, "s2");
		n = appendSkip(result, n, 64, "s4");
		n = appendSkip(result, n, 32, "s8");
		n = appendSkip(result, n, 16, "s16");
		n = appendSkip(result, n, 8, "s32");
		n = appendSkip(result, n, 4, "s64");
		if (n != 0) {
			throw new RuntimeException("BUG: remaining length: " + n);
		}
		return result.toString();
	}

	private static int appendSkip(final StringBuilder result, int n, final int len, final String s) {
		while (n >= len) {
			result.append(s);
			n -= len;
		}
		return n;
	}

	@Override
	public void visit(final LyNote node) {
		final FullNote nodeFullNote = node.getFullNote();
		if (noteWriterContext.isStarted()) {
			noteWriterContext.addChord(Collections.singletonList(nodeFullNote));
		} else if (nodeFullNote.triplet != null) {
			noteWriterContext.startTriplet(Collections.singletonList(nodeFullNote));
		} else {
			noteWriterContext.singleChord(Collections.singletonList(nodeFullNote));
		}
	}

	@Override
	public void visit(final LyChord node) {
		if (noteWriterContext.isStarted()) {
			if (node.getLongNotes() != null) {
				throw new RuntimeException("Invalid nwc file: nested chords????");
			}
			noteWriterContext.addChord(node.getShortNotes());
		} else if (node.getLongNotes() == null) {
			if (node.getShortNotes().get(0).triplet != null) {
				noteWriterContext.startTriplet(node.getShortNotes());
			} else {
				noteWriterContext.singleChord(node.getShortNotes());
			}
		} else {
			if (node.getShortNotes().get(0).triplet != null) {
				throw new RuntimeException("Triplet on chord: not supported");
			}
			noteWriterContext.startChord(node.getLongNotes(), node.getOpt("Stem"));
			noteWriterContext.addChord(node.getShortNotes());
		}
	}

	@Override
	public void visit(final LyRest node) {
		final FullNote nodeFullNote = node.getFullNote();
		if (noteWriterContext.isStarted()) {
			noteWriterContext.addChord(Collections.singletonList(nodeFullNote));
		} else {
			noteWriterContext.singleChord(Collections.singletonList(nodeFullNote));
		}
	}

	@Override
	public void visit(final LyPedal node) {
		switch (node.getPedal()) {
		case SUSTAIN:
			out.println(indent + (node.isOn() ? "\\sustainOn" : "\\sustainOff"));
			break;
		case SOSTENUTO:
			out.println(indent + (node.isOn() ? "\\sostenutoOn" : "\\sostenutoOff"));
			break;
		case UNA_CORDA:
			out.println(indent + (node.isOn() ? "\\unaCorda" : "\\treCorde"));
			break;
		}
	}

}
