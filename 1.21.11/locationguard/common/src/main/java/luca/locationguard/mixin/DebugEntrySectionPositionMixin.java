package luca.locationguard.mixin;

import luca.locationguard.F3CoordinateScrambler;
import luca.locationguard.GuardConfig;
import net.minecraft.client.gui.components.debug.DebugEntrySectionPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DebugEntrySectionPosition.class)
public abstract class DebugEntrySectionPositionMixin {
	@ModifyArg(
			method = "display",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;addToGroup(Lnet/minecraft/resources/Identifier;Ljava/lang/String;)V"),
			index = 1)
	private String locationguard$scrambleCoordinates(String line) {
		GuardConfig cfg = GuardConfig.get();
		if (!cfg.isActive() || !cfg.hideCoordinates || !cfg.scrambleCoordinates) return line;
		return F3CoordinateScrambler.scrambleLine(line);
	}
}
