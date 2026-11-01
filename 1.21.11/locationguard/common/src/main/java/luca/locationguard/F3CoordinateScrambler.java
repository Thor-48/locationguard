package luca.locationguard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class F3CoordinateScrambler {
	private static final int OFFSET_X = ThreadLocalRandom.current().nextInt(-2_000_000, 2_000_001);
	private static final int OFFSET_Y = ThreadLocalRandom.current().nextInt(-2_000_000, 2_000_001);
	private static final int OFFSET_Z = ThreadLocalRandom.current().nextInt(-2_000_000, 2_000_001);
	private static final Pattern XYZ = Pattern.compile("^XYZ: (-?\\d+(?:\\.\\d+)?) / (-?\\d+(?:\\.\\d+)?) / (-?\\d+(?:\\.\\d+)?)$");
	private static final Pattern BLOCK = Pattern.compile("^Block: (-?\\d+) (-?\\d+) (-?\\d+)$");
	private static final Pattern CHUNK = Pattern.compile("^Chunk: (-?\\d+) (-?\\d+) (-?\\d+) \\[(-?\\d+) (-?\\d+) in r\\.(-?\\d+)\\.(-?\\d+)\\.mca]$");
	private static final Pattern SECTION = Pattern.compile("^Section-relative: (\\d+) (\\d+) (\\d+)$");

	private F3CoordinateScrambler() {}

	public static Collection<String> scramble(Collection<String> lines) {
		List<String> result = new ArrayList<>(lines.size());
		for (String line : lines) {
			result.add(scrambleLine(line));
		}
		return result;
	}

	public static String scrambleLine(String line) {
		Matcher xyz = XYZ.matcher(line);
		if (xyz.matches()) {
			double x = Double.parseDouble(xyz.group(1)) + OFFSET_X;
			double y = Double.parseDouble(xyz.group(2)) + OFFSET_Y;
			double z = Double.parseDouble(xyz.group(3)) + OFFSET_Z;
			return String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", x, y, z);
		}

		Matcher block = BLOCK.matcher(line);
		if (block.matches()) {
			return String.format(Locale.ROOT, "Block: %d %d %d",
					Integer.parseInt(block.group(1)) + OFFSET_X,
					Integer.parseInt(block.group(2)) + OFFSET_Y,
					Integer.parseInt(block.group(3)) + OFFSET_Z);
		}

		Matcher chunk = CHUNK.matcher(line);
		if (chunk.matches()) {
			int offsetChunkX = Math.floorDiv(OFFSET_X, 16);
			int offsetSectionY = Math.floorDiv(OFFSET_Y, 16);
			int offsetChunkZ = Math.floorDiv(OFFSET_Z, 16);
			int chunkX = Integer.parseInt(chunk.group(1)) + offsetChunkX;
			int sectionY = Integer.parseInt(chunk.group(2)) + offsetSectionY;
			int chunkZ = Integer.parseInt(chunk.group(3)) + offsetChunkZ;
			return String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]",
					chunkX,
					sectionY,
					chunkZ,
					Math.floorMod(Integer.parseInt(chunk.group(4)) + offsetChunkX, 32),
					Math.floorMod(Integer.parseInt(chunk.group(5)) + offsetChunkZ, 32),
					Math.floorDiv(Integer.parseInt(chunk.group(6)) + offsetChunkX, 32),
					Math.floorDiv(Integer.parseInt(chunk.group(7)) + offsetChunkZ, 32));
		}

		Matcher section = SECTION.matcher(line);
		if (section.matches()) {
			return String.format(Locale.ROOT, "Section-relative: %02d %02d %02d",
					Math.floorMod(Integer.parseInt(section.group(1)) + OFFSET_X, 16),
					Math.floorMod(Integer.parseInt(section.group(2)) + OFFSET_Y, 16),
					Math.floorMod(Integer.parseInt(section.group(3)) + OFFSET_Z, 16));
		}
		return line;
	}
}
