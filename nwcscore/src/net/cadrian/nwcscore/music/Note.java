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

import java.util.HashMap;
import java.util.Map;

public enum Note {
	A("A"), B("B"), C("C"), D("D"), E("E"), F("F"), G("G"), As("A♯"), Bf("B♭"), Cs("C♯"), Df("D♭"), Ds("D♯"), Ef("E♭"),
	Fs("F♯"), Gf("G♭"), Gs("G♯"), Af("A♭"), Cf("C♭"), Ff("F♭"), Bs("B♯"), Es("E♯"), REST("R"), SKIP("S");

	public final String id;
	final String nwcid;

	private Note(final String id) {
		this.id = id;
		this.nwcid = id.replace('♯', '#').replace('♭', 'b');
	}

	private static final Map<String, Note> MAP = new HashMap<>();
	private static final Map<String, Note> MAPNWC = new HashMap<>();

	static {
		for (final Note note : values()) {
			MAP.put(note.id, note);
			MAPNWC.put(note.nwcid, note);
		}
	}

	public static Note as(final String id) {
		return MAP.get(id);
	}

	public static Note nwc(final String nwcid) {
		return MAPNWC.get(nwcid);
	}

	public boolean isNatural() {
		return id.length() == 1;
	}

	public Note getNatural() {
		return isNatural() ? this : MAP.get(id.substring(0, 1));
	}

	public boolean isSharp() {
		return id.length() == 2 && id.charAt(1) == '♯';
	}

	public Note getSharp() {
		return isSharp() ? this : MAP.get(id.substring(0, 1) + "♯");
	}

	public boolean isFlat() {
		return id.length() == 2 && id.charAt(1) == '♭';
	}

	public Note getFlat() {
		return isFlat() ? this : MAP.get(id.substring(0, 1) + "♭");
	}

	public Note getPrevious() {
		// must be natural
		switch (id) {
		case "A":
			return G;
		case "B":
			return A;
		case "C":
			return B;
		case "D":
			return C;
		case "E":
			return D;
		case "F":
			return E;
		case "G":
			return F;
		default:
			return null;
		}
	}

	public Note getNext() {
		// must be natural
		switch (id) {
		case "A":
			return B;
		case "B":
			return C;
		case "C":
			return D;
		case "D":
			return E;
		case "E":
			return F;
		case "F":
			return G;
		case "G":
			return A;
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return id;
	}
}
