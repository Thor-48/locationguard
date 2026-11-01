package luca.locationguard.mixin;

import luca.locationguard.GuardConfig;
import luca.locationguard.LocationGuard;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
	@Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
	private void locationguard$toggleDebugOverlay(long window, int action, KeyEvent event, CallbackInfo ci) {
		if (action == GLFW.GLFW_PRESS && event.key() == GLFW.GLFW_KEY_F8) {
			LocationGuard.toggleDebugOverlay();
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.gui != null) {
				minecraft.gui.getChat().addMessage(Component.translatable(
						LocationGuard.isDebugOverlayVisible()
								? "locationguard.debug.enabled"
								: "locationguard.debug.disabled"));
			}
			ci.cancel();
		}
	}

	@Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
	private void locationguard$blockCoordinateCopy(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
		GuardConfig cfg = GuardConfig.get();
		Minecraft minecraft = Minecraft.getInstance();
		boolean copyLocation = minecraft.options.keyDebugCopyLocation.matches(event);
		boolean copyTarget = minecraft.options.keyDebugCopyRecreateCommand.matches(event);
		if (cfg.isActive() && (cfg.hideCoordinates || cfg.hideEntireF3)
				&& (copyLocation || copyTarget)) {
			LocationGuard.recordCoordinateCopyBlocked();
			minecraft.gui.getChat().addMessage(Component.translatable("locationguard.copy_blocked"));
			cir.setReturnValue(true);
		}
	}
}
