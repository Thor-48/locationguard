package luca.locationguard.mixin;

import luca.locationguard.GuardConfig;
import luca.locationguard.LocationGuard;
import net.minecraft.client.renderer.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CloudRenderer.class)
public abstract class CloudRendererMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
	private void locationguard$hideClouds(CallbackInfo ci) {
		GuardConfig cfg = GuardConfig.get();
		if (cfg.isActive() && cfg.hideClouds) {
			LocationGuard.recordHiddenCloudFrame();
			ci.cancel();
		}
	}
}
