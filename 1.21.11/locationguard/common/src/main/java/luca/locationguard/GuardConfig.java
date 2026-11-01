package luca.locationguard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Konfiguration in config/locationguard.json. Die Datei wird im laufenden Spiel
 * etwa jede Sekunde auf Aenderungen geprueft - du kannst sie also live bearbeiten.
 */
public final class GuardConfig {
	public static final String FILE_NAME = "locationguard.json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	// ---- Allgemein ----
	/** Hauptschalter fuer alles. */
	public boolean enabled = true;
	/** Serveradresse -> Schutz aktiv. Eine Regel ueberschreibt enabled nicht. */
	public Map<String, Boolean> serverRules = new LinkedHashMap<>();
	/** Zeigt den internen Teststatus mit F8 an. */
	public boolean debugOverlay = false;

	// ---- F3-Debug-Bildschirm ----
	/** Komplettes F3-Overlay leeren (strengste Option). */
	public boolean hideEntireF3 = false;
	/** XYZ, Block-, Chunk-Koordinaten ("position"-Eintraege). */
	public boolean hideCoordinates = true;
	/** Zeigt feste, verschobene F3-Koordinaten statt sie auszublenden. */
	public boolean scrambleCoordinates = true;
	/** Chunk-/Section-Infos und Chunk-Grenzen. */
	public boolean hideChunkInfo = true;
	public boolean hideBiome = true;
	/** Angeschauter Block/Fluessigkeit/Entity inkl. Koordinaten. */
	public boolean hideLookingAt = true;
	public boolean hideLightLevels = true;
	public boolean hideHeightmap = true;
	public boolean hideLocalDifficulty = true;
	/** Weitere Debug-Eintraege: Teil des Namens, z. B. "sound_mood". */
	public List<String> extraHiddenDebugEntries = new ArrayList<>();

	// ---- Welt-Muster (Fingerabdruecke, aus denen man Position/Seed ableiten kann) ----
	// Hinweis: Block-Optionen wirken auf neu gebaute Chunks -> nach Aenderung F3+A druecken.
	/** Zufaellige Rotation/Variante von Bloecken (Gras, Erde, Stein ...) abschalten - alle sehen gleich aus. */
	public boolean hideBlockRotation = true;
	/** Zufaelligen Versatz von Blumen, Gras, Bambus, Tropfstein ... abschalten (Bambus-Kollision kann minimal abweichen). */
	public boolean hideBlockOffset = false;
	/** Bedrock-Muster verstecken: Bedrock wird als anderer Block dargestellt. */
	public boolean hideBedrockPattern = true;
	/** Block, als der Bedrock erscheint, z. B. "minecraft:deepslate". */
	public String bedrockRenderAs = "minecraft:deepslate";
	/** Erz-Muster verstecken: Erze werden als Gestein dargestellt (Spielbarkeit leidet!). */
	public boolean hideOrePattern = false;
	/** "auto" (Stein/Tiefenschiefer/Netherrack passend zum Erz) oder eine Block-ID. */
	public String oreRenderAs = "auto";
	/** Wolken nicht rendern (Wolkenmuster verraet Zeit/Position). */
	public boolean hideClouds = true;
	/** Herumliegende Item-Entities (und damit ihre Flugbahn) nicht rendern. */
	public boolean hideDroppedItems = false;

	// ---- Chat ----
	/** Koordinaten in Chat-/Systemnachrichten ersetzen (/tp, /locate, /data, ...). */
	public boolean scrubChatCoordinates = true;
	/** Seed-Ausgaben (/seed) ersetzen. */
	public boolean scrubChatSeed = true;
	public String replacement = "[hidden]";
	/** Eigene Regex-Muster, die im Chat ersetzt werden sollen. */
	public List<String> customChatPatterns = new ArrayList<>();

	// ---- intern (nicht in der JSON) ----
	private transient List<Pattern> compiledCustom = List.of();

	private static volatile GuardConfig instance = new GuardConfig();
	private static long lastCheckMs = 0;
	private static long lastModified = -1;
	private static boolean everLoaded = false;

	public static GuardConfig get() {
		long now = System.currentTimeMillis();
		if (!everLoaded || now - lastCheckMs > 1000) {
			lastCheckMs = now;
			refresh();
		}
		return instance;
	}

	private static Path path() {
		Path dir;
		try {
			dir = Minecraft.getInstance().gameDirectory.toPath().resolve("config");
		} catch (Throwable t) {
			dir = Path.of("config");
		}
		return dir.resolve(FILE_NAME);
	}

	private static void refresh() {
		Path file = path();
		try {
			if (!Files.exists(file)) {
				GuardConfig fresh = new GuardConfig();
				Files.createDirectories(file.getParent());
				Files.writeString(file, GSON.toJson(fresh), StandardCharsets.UTF_8);
				fresh.compile();
				instance = fresh;
				lastModified = Files.getLastModifiedTime(file).toMillis();
				everLoaded = true;
				return;
			}
			long mod = Files.getLastModifiedTime(file).toMillis();
			if (everLoaded && mod == lastModified) return;
			String json = Files.readString(file, StandardCharsets.UTF_8);
			GuardConfig loaded = GSON.fromJson(json, GuardConfig.class);
			if (loaded == null) loaded = new GuardConfig();
			if (loaded.extraHiddenDebugEntries == null) loaded.extraHiddenDebugEntries = new ArrayList<>();
			if (loaded.customChatPatterns == null) loaded.customChatPatterns = new ArrayList<>();
			if (loaded.serverRules == null) loaded.serverRules = new LinkedHashMap<>();
			if (loaded.replacement == null) loaded.replacement = "[hidden]";
			if (loaded.bedrockRenderAs == null) loaded.bedrockRenderAs = "minecraft:deepslate";
			if (loaded.oreRenderAs == null) loaded.oreRenderAs = "auto";
			loaded.compile();
			instance = loaded;
			lastModified = mod;
			everLoaded = true;
			// Datei mit neuen Feldern auffuellen, damit neue Optionen sichtbar werden
			Files.writeString(file, GSON.toJson(loaded), StandardCharsets.UTF_8);
			lastModified = Files.getLastModifiedTime(file).toMillis();
		} catch (IOException | RuntimeException e) {
			if (!everLoaded) {
				LocationGuard.LOGGER.error("Konfiguration nicht lesbar, verwende sichere Standardwerte", e);
				instance.compile();
				everLoaded = true;
			}
			// Bei Fehlern nach dem ersten Laden bleibt die letzte gueltige Konfiguration aktiv.
		}
	}

	private void compile() {
		List<Pattern> list = new ArrayList<>();
		for (String s : customChatPatterns) {
			Pattern p = TextScrubber.safeCompile(s);
			if (p != null) list.add(p);
		}
		compiledCustom = list;
	}

	public boolean isActive() {
		if (!enabled) return false;
		ServerData server = Minecraft.getInstance().getCurrentServer();
		if (server == null || server.ip == null || server.ip.isBlank()) return true;

		String address = normalizeAddress(server.ip);
		Boolean exactRule = findServerRule(address);
		if (exactRule != null) return exactRule;

		String host = withoutPort(address);
		Boolean hostRule = findServerRule(host);
		return hostRule == null || hostRule;
	}

	private Boolean findServerRule(String address) {
		for (Map.Entry<String, Boolean> rule : serverRules.entrySet()) {
			if (rule.getKey() != null && normalizeAddress(rule.getKey()).equals(address)) {
				return rule.getValue();
			}
		}
		return null;
	}

	private static String normalizeAddress(String address) {
		return address.trim().toLowerCase(Locale.ROOT);
	}

	private static String withoutPort(String address) {
		if (address.startsWith("[")) {
			int closingBracket = address.indexOf(']');
			return closingBracket >= 0 ? address.substring(0, closingBracket + 1) : address;
		}
		int colon = address.lastIndexOf(':');
		if (colon > 0 && address.indexOf(':') == colon && address.substring(colon + 1).matches("\\d+")) {
			return address.substring(0, colon);
		}
		return address;
	}

	/** @param fullId z. B. "minecraft:player_position" */
	public boolean isDebugEntryHidden(String fullId) {
		if (!isActive()) return false;
		if (hideEntireF3) return true;
		int i = fullId.indexOf(':');
		String path = i >= 0 ? fullId.substring(i + 1) : fullId;
		if (hideCoordinates && !scrambleCoordinates && (path.contains("position") || path.contains("coord"))) return true;
		if (hideChunkInfo && (path.contains("chunk") || path.contains("section"))) return true;
		if (hideBiome && path.contains("biome")) return true;
		if (hideLookingAt && path.contains("looking_at")) return true;
		if (hideLightLevels && path.contains("light")) return true;
		if (hideHeightmap && path.contains("heightmap")) return true;
		if (hideLocalDifficulty && path.contains("difficulty")) return true;
		for (String extra : extraHiddenDebugEntries) {
			if (extra != null && !extra.isBlank() && path.contains(extra)) return true;
		}
		return false;
	}

	public String scrubText(String text) {
		return TextScrubber.scrub(text, scrubChatCoordinates, scrubChatSeed, replacement, compiledCustom);
	}
}
