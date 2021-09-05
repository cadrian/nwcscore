package net.cadrian.nwcscore.lybuilder;

import java.io.IOException;

import net.cadrian.nwcscore.parser.ast.Song;

public class LilypondBuilder {

	private final Song song;

	public LilypondBuilder(final Song song) {
		this.song = song;
	}

	public void output(final String filename) throws IOException {
		final NwcVisitor visitor = new NwcVisitor();
		song.accept(visitor);
		visitor.output(filename);
	}

}
