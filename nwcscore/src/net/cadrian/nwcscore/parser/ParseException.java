package net.cadrian.nwcscore.parser;

public class ParseException extends Exception {

	private static final long serialVersionUID = -6240896329633947613L;

	ParseException() {
		super();
	}

	ParseException(final String msg) {
		super(msg);
	}

	ParseException(final Throwable t) {
		super(t);
	}

	ParseException(final String msg, final Throwable t) {
		super(msg, t);
	}

}
