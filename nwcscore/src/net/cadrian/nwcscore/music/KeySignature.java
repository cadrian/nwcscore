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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeySignature {

	private static final Map<String, Set<KeySignature>> MAP = new HashMap<>();
	static {
		addSignature(Note.Cf, Mode.MAJOR, Note.Bf, Note.Ef, Note.Af, Note.Df, Note.Gf, Note.Cf, Note.Ff);
		addSignature(Note.Gf, Mode.MAJOR, Note.Bf, Note.Ef, Note.Af, Note.Df, Note.Gf, Note.Cf);
		addSignature(Note.Df, Mode.MAJOR, Note.Bf, Note.Ef, Note.Af, Note.Df, Note.Gf);
		addSignature(Note.Af, Mode.MAJOR, Note.Bf, Note.Ef, Note.Af, Note.Df);
		addSignature(Note.Ef, Mode.MAJOR, Note.Bf, Note.Ef, Note.Af);
		addSignature(Note.Bf, Mode.MAJOR, Note.Bf, Note.Ef);
		addSignature(Note.F, Mode.MAJOR, Note.Bf);
		addSignature(Note.C, Mode.MAJOR);
		addSignature(Note.C, Mode.MAJOR, Note.C); // Yes, NWC notes the empty signature as "C"
		addSignature(Note.G, Mode.MAJOR, Note.Fs);
		addSignature(Note.D, Mode.MAJOR, Note.Fs, Note.Cs);
		addSignature(Note.A, Mode.MAJOR, Note.Fs, Note.Cs, Note.Gs);
		addSignature(Note.E, Mode.MAJOR, Note.Fs, Note.Cs, Note.Gs, Note.Ds);
		addSignature(Note.B, Mode.MAJOR, Note.Fs, Note.Cs, Note.Gs, Note.Ds, Note.As);
		addSignature(Note.Fs, Mode.MAJOR, Note.Fs, Note.Cs, Note.Gs, Note.Ds, Note.As, Note.Es);
		addSignature(Note.Cs, Mode.MAJOR, Note.Fs, Note.Cs, Note.Gs, Note.Ds, Note.As, Note.Es, Note.Bs);

		addSignature(Note.Af, Mode.MAJOR, Note.Bf, Note.Ef, Note.Af, Note.Df, Note.Gf, Note.Cf, Note.Ff);
		addSignature(Note.Ef, Mode.MAJOR, Note.Bf, Note.Ef, Note.Af, Note.Df, Note.Gf, Note.Cf);
		addSignature(Note.Bf, Mode.MINOR, Note.Bf, Note.Ef, Note.Af, Note.Df, Note.Gf);
		addSignature(Note.F, Mode.MINOR, Note.Bf, Note.Ef, Note.Af, Note.Df);
		addSignature(Note.C, Mode.MINOR, Note.Bf, Note.Ef, Note.Af);
		addSignature(Note.G, Mode.MINOR, Note.Bf, Note.Ef);
		addSignature(Note.D, Mode.MINOR, Note.Bf);
		addSignature(Note.A, Mode.MINOR);
		addSignature(Note.E, Mode.MINOR, Note.Fs);
		addSignature(Note.B, Mode.MINOR, Note.Fs, Note.Cs);
		addSignature(Note.Fs, Mode.MINOR, Note.Fs, Note.Cs, Note.Gs);
		addSignature(Note.Cs, Mode.MINOR, Note.Fs, Note.Cs, Note.Gs, Note.Ds);
		addSignature(Note.Gs, Mode.MINOR, Note.Fs, Note.Cs, Note.Gs, Note.Ds, Note.As);
		addSignature(Note.Ds, Mode.MAJOR, Note.Fs, Note.Cs, Note.Gs, Note.Ds, Note.As, Note.Es);
		addSignature(Note.As, Mode.MAJOR, Note.Fs, Note.Cs, Note.Gs, Note.Ds, Note.As, Note.Es, Note.Bs);
	}

	private static void addSignature(final Note root, final Mode mode, final Note... signature) {
		final String sigstr = sig2str(signature);
		final Set<KeySignature> sigs = MAP.computeIfAbsent(sigstr, v -> new HashSet<>());
		sigs.add(new KeySignature(root, mode, signature));
	}

	private static String sig2str(final Note... signature) {
		final StringBuilder result = new StringBuilder();
		for (final Note n : signature) {
			if (result.length() != 0) {
				result.append(',');
			}
			result.append(n.nwcid);
		}
		return result.toString();
	}

	public static Set<KeySignature> get(final String nwcsig) {
		final Set<KeySignature> result = MAP.get(nwcsig);
		return result == null ? null : Collections.unmodifiableSet(result);
	}

	private final Note root;
	private final Mode mode;
	private final Note[] signature;
	private final Map<Note, Note> alterations = new HashMap<>();

	private KeySignature(final Note root, final Mode mode, final Note... signature) {
		this.root = root;
		this.mode = mode;
		this.signature = signature;
		for (final Note n : signature) {
			alterations.put(n.getNatural(), n);
		}
	}

	public Note getRoot() {
		return root;
	}

	public Mode getMode() {
		return mode;
	}

	public List<Note> getSignature() {
		return Collections.unmodifiableList(Arrays.asList(signature));
	}

	public Note getAlteredNote(final Note note) {
		final Note result = alterations.get(note.getNatural());
		return result == null ? note : result;
	}

	@Override
	public String toString() {
		return root + " " + mode + ": " + sig2str(signature);
	}

	public static KeySignature adhoc(final String signature) {
		final List<Note> sig = new ArrayList<>();
		for (final String s : signature.split(",")) {
			sig.add(Note.nwc(s));
		}
		return new KeySignature(null, null, sig.toArray(new Note[sig.size()]));
	}

}
