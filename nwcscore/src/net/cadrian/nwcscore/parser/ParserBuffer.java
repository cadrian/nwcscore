package net.cadrian.nwcscore.parser;

class ParserBuffer {

	private final char[] input;
	private int index;

	ParserBuffer(final String input) {
		this.input = input.toCharArray();
		this.index = 0;
	}

	public ParserBuffer(final StringBuilder input) {
		final int n = input.length();
		this.input = new char[n];
		input.getChars(0, n, this.input, 0);
		this.index = 0;
	}

	boolean isOff() {
		return index >= input.length;
	}

	char current() {
		return input[index];
	}

	int position() {
		return index;
	}

	int size() {
		return input.length;
	}

	void next() {
		index++;
	}

	boolean check(final char c) {
		return index < input.length && input[index] == c;
	}

	String readWord() {
		final StringBuilder b = new StringBuilder();
		if (index < input.length && Character.isJavaIdentifierStart(input[index])) {
			b.append(input[index++]);
			while (index < input.length && Character.isJavaIdentifierPart(input[index])) {
				b.append(input[index++]);
			}
		}
		return b.toString();
	}

	private boolean in(final char... c) {
		final char cur = input[index];
		for (final char ch : c) {
			if (ch == cur) {
				return true;
			}
		}
		return false;
	}

	String readUntil(final char... c) {
		final StringBuilder b = new StringBuilder();
		while (index < input.length && !in(c)) {
			b.append(input[index++]);
		}
		return b.toString();
	}

	void skipLine() {
		while (index < input.length && input[index] != '\n') {
			index++;
		}
		index++; // skip EOL
	}

}
