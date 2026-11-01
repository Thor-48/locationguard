package luca.locationguard;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocationGuard {
	public static final String MOD_ID = "locationguard";
	public static final Logger LOGGER = LoggerFactory.getLogger("Location Guard");
	private static final AtomicLong BLOCK_SEED_OVERRIDES = new AtomicLong();
	private static final AtomicLong OFFSET_OVERRIDES = new AtomicLong();
	private static final AtomicLong COORDINATE_COPIES_BLOCKED = new AtomicLong();
	private static final AtomicLong HIDDEN_DEBUG_ENTRIES = new AtomicLong();
	private static final AtomicLong DISGUISED_BLOCKS = new AtomicLong();
	private static final AtomicLong HIDDEN_CLOUD_FRAMES = new AtomicLong();
	private static final AtomicLong HIDDEN_ITEM_FRAMES = new AtomicLong();
	private static final AtomicLong SCRUBBED_CHAT_MESSAGES = new AtomicLong();
	private static volatile Boolean debugOverlayOverride;

	private LocationGuard() {}

	/** Wird von den Loader-Einstiegspunkten aufgerufen. */
	public static void init() {
		LOGGER.info("Location Guard geladen. Konfiguration: config/{}", GuardConfig.FILE_NAME);
	}

	public static void toggleDebugOverlay() {
		debugOverlayOverride = !isDebugOverlayVisible();
		BLOCK_SEED_OVERRIDES.set(0);
		OFFSET_OVERRIDES.set(0);
		COORDINATE_COPIES_BLOCKED.set(0);
		HIDDEN_DEBUG_ENTRIES.set(0);
		DISGUISED_BLOCKS.set(0);
		HIDDEN_CLOUD_FRAMES.set(0);
		HIDDEN_ITEM_FRAMES.set(0);
		SCRUBBED_CHAT_MESSAGES.set(0);
	}

	public static boolean isDebugOverlayVisible() {
		Boolean override = debugOverlayOverride;
		return override != null ? override : GuardConfig.get().debugOverlay;
	}

	public static void recordBlockSeedOverride() {
		BLOCK_SEED_OVERRIDES.incrementAndGet();
	}

	public static void recordOffsetOverride() {
		OFFSET_OVERRIDES.incrementAndGet();
	}

	public static void recordCoordinateCopyBlocked() {
		COORDINATE_COPIES_BLOCKED.incrementAndGet();
	}

	public static long blockSeedOverrides() {
		return BLOCK_SEED_OVERRIDES.get();
	}

	public static long offsetOverrides() {
		return OFFSET_OVERRIDES.get();
	}

	public static long coordinateCopiesBlocked() {
		return COORDINATE_COPIES_BLOCKED.get();
	}

	public static void setHiddenDebugEntries(long count) {
		HIDDEN_DEBUG_ENTRIES.set(count);
	}

	public static long hiddenDebugEntries() {
		return HIDDEN_DEBUG_ENTRIES.get();
	}

	public static void recordDisguisedBlock() {
		DISGUISED_BLOCKS.incrementAndGet();
	}

	public static void recordHiddenCloudFrame() {
		HIDDEN_CLOUD_FRAMES.incrementAndGet();
	}

	public static void recordHiddenItemFrame() {
		HIDDEN_ITEM_FRAMES.incrementAndGet();
	}

	public static void recordScrubbedChatMessage() {
		SCRUBBED_CHAT_MESSAGES.incrementAndGet();
	}

	public static long disguisedBlocks() {
		return DISGUISED_BLOCKS.get();
	}

	public static long hiddenCloudFrames() {
		return HIDDEN_CLOUD_FRAMES.get();
	}

	public static long hiddenItemFrames() {
		return HIDDEN_ITEM_FRAMES.get();
	}

	public static long scrubbedChatMessages() {
		return SCRUBBED_CHAT_MESSAGES.get();
	}
}
