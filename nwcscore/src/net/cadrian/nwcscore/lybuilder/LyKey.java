package net.cadrian.nwcscore.lybuilder;

import net.cadrian.nwcscore.music.KeySignature;
import net.cadrian.nwcscore.music.Mode;
import net.cadrian.nwcscore.music.Note;

public class LyKey extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyKey node);
	}

	private final KeySignature signature;

	LyKey(final String signature, final String tonic) {
		Note root = Note.nwc(tonic);
		KeySignature sig = null;
		for (final KeySignature s : KeySignature.get(signature)) {
			if (s.getRoot() == root) {
				sig = s;
				break;
			}
		}
		if (sig == null) {
			// looks like NWC sets strange tonics (without alterations)
			root = Note.nwc(tonic.substring(0, 1));
			for (final KeySignature s : KeySignature.get(signature)) {
				if (s.getRoot() == root) {
					sig = s;
					break;
				}
			}
			if (sig == null) {
				for (final KeySignature s : KeySignature.get(signature)) {
					if (s.getMode() == Mode.MAJOR) {
						sig = s;
						break;
					}
				}
			}
		}
		this.signature = sig;
	}

	public KeySignature getSignature() {
		return signature;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
