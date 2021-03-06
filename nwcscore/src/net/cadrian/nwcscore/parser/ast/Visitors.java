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
package net.cadrian.nwcscore.parser.ast;

public interface Visitors extends Bar.Visitor, Chord.Visitor, Clef.Visitor, Key.Visitor, Lyric.Visitor, Note.Visitor,
		PgSetup.Visitor, Rest.Visitor, RestChord.Visitor, RestMultiBar.Visitor, Song.Visitor, SongInfo.Visitor,
		Staff.Visitor, StaffInstrument.Visitor, SustainPedal.Visitor, TimeSig.Visitor, User.Visitor {

}
