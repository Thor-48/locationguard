package luca.locationguard.mixin;

import luca.locationguard.BlockDisguise;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Bedrock-/Erz-Muster: Bloecke werden nur beim Rendern durch neutrale ersetzt. */
@Mixin(BlockModelShaper.class)
public abstract class BlockModelShaperMixin {

	@ModifyVariable(method = "getBlockModel", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
	private BlockState locationguard$disguise(BlockState state) {
		return BlockDisguise.disguise(state);
	}
}
