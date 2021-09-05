package net.cadrian.nwcscore.lybuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LyTimeSignature extends LyNode {

	static interface Visitor {
		void visit(LyTimeSignature node);
	}

	private static final Pattern SIG_PATTERN = Pattern.compile("(?<n>[0-9]+)/(?<d>[0-9]+)");

	private final String sig;
	private final int barLength;

	LyTimeSignature(final String nwcsig) {
		switch (nwcsig) {
		case "AllaBreve":
			sig = "2/2";
			barLength = 256;
			break;
		case "Common":
			sig = "4/4";
			barLength = 256;
			break;
		default:
			sig = nwcsig;
			final Matcher sigMatcher = SIG_PATTERN.matcher(sig);
			if (!sigMatcher.matches()) {
				throw new RuntimeException("Invalid time signature: " + sig);
			}
			final int n = Integer.parseInt(sigMatcher.group("n"));
			final int d = Integer.parseInt(sigMatcher.group("d"));
			barLength = n * 256 / d;
		}
	}

	public String getSig() {
		return sig;
	}

	@Override
	public void accept(final LyNode.Visitor v) {
		((Visitor) v).visit(this);
	}

	public int getBarLength() {
		return barLength;
	}

}
