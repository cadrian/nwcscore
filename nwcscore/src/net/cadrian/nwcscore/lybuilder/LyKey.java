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

import java.util.Set;

import net.cadrian.nwcscore.music.KeySignature;
import net.cadrian.nwcscore.music.Mode;
import net.cadrian.nwcscore.music.Note;

public class LyKey extends LyNode {

	static interface Visitor extends LyNode.Visitor {
		void visit(LyKey node);
	}

	private final KeySignature signature;
	private final boolean visible;

	LyKey(final String signature, final String tonic, final boolean visible) {
		this.visible = visible;
		Note root = Note.nwc(tonic);
		KeySignature sig = null;
		Set<KeySignature> standardSignature = KeySignature.get(signature);
		if (standardSignature == null) {
			System.err.println("WARNING: ad-hoc signature: " + signature);
			sig = KeySignature.adhoc(signature);
		} else {
			for (final KeySignature s : standardSignature) {
				if (s.getRoot() == root) {
					sig = s;
					break;
				}
			}
			if (sig == null) {
				// looks like NWC sets strange tonics (without alterations)
				root = Note.nwc(tonic.substring(0, 1));
				for (final KeySignature s : standardSignature) {
					if (s.getRoot() == root) {
						sig = s;
						break;
					}
				}
				if (sig == null) {
					for (final KeySignature s : standardSignature) {
						if (s.getMode() == Mode.MAJOR) {
							sig = s;
							break;
						}
					}
					if (sig == null) {
						System.err.println("WARNING: ad-hoc signature: " + signature);
						sig = KeySignature.adhoc(signature);
					}
				}
			}
		}
		this.signature = sig;
	}

	public boolean isVisible() {
		return visible;
	}

	public KeySignature getSignature() {
		return signature;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

}
