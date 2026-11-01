package luca.locationguard;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Reine Textlogik (keine Minecraft-Abhaengigkeit, daher einzeln testbar). */
public final class TextScrubber {
	/** Zahl oder Tilde-Koordinate (z. B. "-300", "12.5", "~", "~5"). */
	private static final String NUM = "(?:-?\\d+(?:\\.\\d+)?|~(?:-?\\d+(?:\\.\\d+)?)?)";
	private static final String SEP = "(?:\\s*[,;]\\s*|\\s+)";
	/** Drei Zahlen hintereinander, z. B. "12 64 -300", "12.5, 64.0, -300.7", "[12, 64, -300]". */
	public static final Pattern COORD_TRIPLE = Pattern.compile(
			"(?<![\\w.\\-~])" + NUM + SEP + NUM + SEP + NUM + "(?!\\w)(?!\\.\\d)");
	/** Beschriftete Werte, z. B. "x=12", "Y: 64", "z = -300". */
	public static final Pattern COORD_LABELLED = Pattern.compile(
			"(?i)(?<!\\w)[xyz]\\s*[:=]\\s*-?\\d+(?:\\.\\d+)?");
	/** Seed-Ausgaben, z. B. "Seed: [-8841234567890]". */
	public static final Pattern SEED = Pattern.compile("(?i)(seed[^\\d\\-]{0,8})-?\\d{6,}");

	private TextScrubber() {}

	public static String scrub(String text, boolean coords, boolean seed, String replacement, List<Pattern> custom) {
		String out = text;
		String rep = Matcher.quoteReplacement(replacement);
		if (coords) {
			out = COORD_TRIPLE.matcher(out).replaceAll(rep);
			out = COORD_LABELLED.matcher(out).replaceAll(rep);
		}
		if (seed) {
			out = SEED.matcher(out).replaceAll("$1" + rep);
		}
		if (custom != null) {
			for (Pattern p : custom) {
				out = p.matcher(out).replaceAll(rep);
			}
		}
		return out;
	}

	public static Pattern safeCompile(String regex) {
		try {
			return Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			LocationGuard.LOGGER.warn("Ungueltiges Regex in customChatPatterns ignoriert: {}", regex);
			return null;
		}
	}
}
