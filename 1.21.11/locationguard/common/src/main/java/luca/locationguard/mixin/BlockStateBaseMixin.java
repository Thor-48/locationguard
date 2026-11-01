package luca.locationguard.mixin;

import luca.locationguard.ClientThreads;
import luca.locationguard.GuardConfig;
import luca.locationguard.LocationGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Positionsabhaengige Zufallswerte (Rotation/Variante, Versatz) neutralisieren. */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

	@Inject(method = "getSeed", at = @At("HEAD"), cancellable = true, require = 0)
	private void locationguard$seed(BlockPos pos, CallbackInfoReturnable<Long> cir) {
		GuardConfig cfg = GuardConfig.get();
		if (cfg.isActive() && cfg.hideBlockRotation && !ClientThreads.isServerThread()) {
			LocationGuard.recordBlockSeedOverride();
			cir.setReturnValue(0L);
		}
	}

	@Inject(method = "getOffset", at = @At("HEAD"), cancellable = true, require = 0)
	private void locationguard$offset(BlockPos pos, CallbackInfoReturnable<Vec3> cir) {
		GuardConfig cfg = GuardConfig.get();
		if (cfg.isActive() && cfg.hideBlockOffset && !ClientThreads.isServerThread()) {
			LocationGuard.recordOffsetOverride();
			cir.setReturnValue(Vec3.ZERO);
		}
	}
}
