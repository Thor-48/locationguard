package luca.locationguard;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Ersetzt Bedrock/Erze beim Rendern durch neutrale Bloecke (nur Optik, Spiellogik bleibt unveraendert). */
public final class BlockDisguise {
	private static final Map<Block, Boolean> ORE_CACHE = new ConcurrentHashMap<>();

	private record Targets(GuardConfig cfg, BlockState bedrock, BlockState ore) {}
	private static volatile Targets targets;

	private BlockDisguise() {}

	public static BlockState disguise(BlockState state) {
		GuardConfig cfg = GuardConfig.get();
		if (!cfg.isActive() || (!cfg.hideBedrockPattern && !cfg.hideOrePattern)) return state;
		Block block = state.getBlock();
		if (cfg.hideBedrockPattern && block == Blocks.BEDROCK) {
			LocationGuard.recordDisguisedBlock();
			return targets(cfg).bedrock();
		}
		if (cfg.hideOrePattern && isOre(block)) {
			LocationGuard.recordDisguisedBlock();
			BlockState explicit = targets(cfg).ore();
			if (explicit != null) return explicit;
			String path = path(block);
			if (path.contains("deepslate")) return Blocks.DEEPSLATE.defaultBlockState();
			if (path.contains("nether")) return Blocks.NETHERRACK.defaultBlockState();
			return Blocks.STONE.defaultBlockState();
		}
		return state;
	}

	private static boolean isOre(Block block) {
		return ORE_CACHE.computeIfAbsent(block, b -> path(b).endsWith("_ore"));
	}

	private static String path(Block block) {
		String id = BuiltInRegistries.BLOCK.getKey(block).toString();
		return id.substring(id.indexOf(':') + 1);
	}

	private static Targets targets(GuardConfig cfg) {
		Targets t = targets;
		if (t != null && t.cfg() == cfg) return t;
		BlockState bedrock = resolve(cfg.bedrockRenderAs, Blocks.DEEPSLATE.defaultBlockState());
		BlockState ore = "auto".equalsIgnoreCase(cfg.oreRenderAs) ? null : resolve(cfg.oreRenderAs, Blocks.STONE.defaultBlockState());
		t = new Targets(cfg, bedrock, ore);
		targets = t;
		return t;
	}

	private static BlockState resolve(String id, BlockState fallback) {
		try {
			Identifier ident = Identifier.tryParse(id);
			if (ident != null) {
				return BuiltInRegistries.BLOCK.getOptional(ident).map(Block::defaultBlockState).orElse(fallback);
			}
		} catch (RuntimeException ignored) {
		}
		return fallback;
	}
}
