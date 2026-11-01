package luca.locationguard.mixin;

import luca.locationguard.GuardConfig;
import luca.locationguard.LocationGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiDebugOverlayMixin {
	@Inject(method = "render", at = @At("TAIL"))
	private void locationguard$renderDebugOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (!LocationGuard.isDebugOverlayVisible()) return;

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) return;

		GuardConfig cfg = GuardConfig.get();
		int left = Math.max(6, minecraft.getWindow().getGuiScaledWidth() - 254);
		int top = Math.max(6, minecraft.getWindow().getGuiScaledHeight() - 136);
		graphics.fill(left - 2, top - 2, left + 248, top + 132, 0x99000000);
		graphics.drawString(minecraft.font, Component.translatable("locationguard.debug.title"), left, top, 0xFFFFFF);
		graphics.drawString(minecraft.font, Component.translatable(
				cfg.isActive() ? "locationguard.debug.active" : "locationguard.debug.inactive"), left, top + 12, 0xFFFFFF);
		String coordinateMode = !cfg.isActive() || !cfg.hideCoordinates
				? "locationguard.debug.coordinates.normal"
				: cfg.scrambleCoordinates
						? "locationguard.debug.coordinates.scrambled"
						: "locationguard.debug.coordinates.hidden";
		graphics.drawString(minecraft.font, Component.translatable(coordinateMode), left, top + 24, 0xFFFFFF);
		graphics.drawString(minecraft.font, Component.translatable(
				"locationguard.debug.rotation", LocationGuard.blockSeedOverrides()), left, top + 36, 0xFFFFFF);
		graphics.drawString(minecraft.font, Component.translatable(
				"locationguard.debug.offset", LocationGuard.offsetOverrides()), left, top + 48, 0xFFFFFF);
		graphics.drawString(minecraft.font, Component.translatable(
				"locationguard.debug.blocks", LocationGuard.disguisedBlocks()), left, top + 60, 0xFFFFFF);
		graphics.drawString(minecraft.font, Component.translatable(
				"locationguard.debug.clouds", LocationGuard.hiddenCloudFrames()), left, top + 72, 0xFFFFFF);
		graphics.drawString(minecraft.font, Component.translatable(
				"locationguard.debug.items", LocationGuard.hiddenItemFrames()), left, top + 84, 0xFFFFFF);
		graphics.drawString(minecraft.font, Component.translatable(
				"locationguard.debug.chat", LocationGuard.scrubbedChatMessages()), left, top + 96, 0xFFFFFF);
		graphics.drawString(minecraft.font, Component.translatable(
				"locationguard.debug.f3", LocationGuard.hiddenDebugEntries()), left, top + 108, 0xFFFFFF);
		graphics.drawString(minecraft.font, Component.translatable(
				"locationguard.debug.copy", LocationGuard.coordinateCopiesBlocked()), left, top + 120, 0xFFFFFF);
	}
}
