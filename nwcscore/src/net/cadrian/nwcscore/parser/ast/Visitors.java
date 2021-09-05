package net.cadrian.nwcscore.parser.ast;

public interface Visitors extends Bar.Visitor, Chord.Visitor, Clef.Visitor, Key.Visitor, Lyric.Visitor, Note.Visitor,
		Rest.Visitor, RestMultiBar.Visitor, Song.Visitor, SongInfo.Visitor, Staff.Visitor, StaffInstrument.Visitor,
		SustainPedal.Visitor, TimeSig.Visitor, User.Visitor {

}
