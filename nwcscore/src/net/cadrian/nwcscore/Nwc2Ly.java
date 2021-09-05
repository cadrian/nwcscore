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
package net.cadrian.nwcscore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cadrian.nwcscore.lybuilder.LilypondBuilder;
import net.cadrian.nwcscore.parser.ParseException;
import net.cadrian.nwcscore.parser.Parser;
import net.cadrian.nwcscore.parser.ast.Song;

public class Nwc2Ly {

	private static final Pattern FILENAME_PATTERN = Pattern.compile("^(?<name>.*?)\\.nwctxt$");

	public static void main(final String[] args) throws Exception {
		final String inputFilename = args[0];
		final Matcher inputFilenameMatcher = FILENAME_PATTERN.matcher(inputFilename);
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
