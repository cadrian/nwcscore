package net.cadrian.nwcscore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cadrian.nwcscore.ast.Song;
import net.cadrian.nwcscore.lybuilder.LilypondBuilder;
import net.cadrian.nwcscore.parser.ParseException;
import net.cadrian.nwcscore.parser.Parser;

public class Nwc2Ly {
	
	private static final Pattern FILENAME_PATTERN = Pattern.compile("^(?<name>.*?)\\.nwctxt$");

	public static void main(final String[] args) throws Exception {
		String inputFilename = args[0];
		Matcher inputFilenameMatcher = FILENAME_PATTERN.matcher(inputFilename);
		if (!inputFilenameMatcher.matches()) {
			throw new Exception("invalid file name");
		}
		final Song s = readInput(inputFilename).getSong();
		new LilypondBuilder(s).output(inputFilenameMatcher.group("name") + ".ly");
	}

	private static Parser readInput(final String name) throws IOException, ParseException {
		final StringBuilder result = new StringBuilder();
		final File input = new File(name);
		try (Reader in = new BufferedReader(new FileReader(input))) {
			final char[] buf = new char[4096];
			int n;
			while ((n = in.read(buf)) != -1) {
				result.append(buf, 0, n);
			}
		}
		return new Parser(result);
	}

}
