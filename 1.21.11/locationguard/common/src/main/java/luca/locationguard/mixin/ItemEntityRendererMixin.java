package luca.locationguard.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import luca.locationguard.GuardConfig;
import luca.locationguard.LocationGuard;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {
	@Inject(
			method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
			at = @At("HEAD"),
			cancellable = true)
	private void locationguard$hideItems(
			ItemEntityRenderState state,
			PoseStack poseStack,
			SubmitNodeCollector collector,
			CameraRenderState camera,
			CallbackInfo ci) {
		GuardConfig cfg = GuardConfig.get();
		if (cfg.isActive() && cfg.hideDroppedItems) {
			LocationGuard.recordHiddenItemFrame();
			ci.cancel();
		}
	}
}
