package luca.locationguard.mixin;

import luca.locationguard.F3CoordinateScrambler;
import luca.locationguard.GuardConfig;
import net.minecraft.client.gui.components.debug.DebugEntryPosition;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collection;

@Mixin(DebugEntryPosition.class)
public abstract class DebugEntryPositionMixin {
	@ModifyArg(
			method = "display",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;addToGroup(Lnet/minecraft/resources/Identifier;Ljava/util/Collection;)V"),
			index = 1)
	private Collection<String> locationguard$scrambleCoordinates(Collection<String> lines) {
		GuardConfig cfg = GuardConfig.get();
		if (!cfg.isActive() || !cfg.hideCoordinates || !cfg.scrambleCoordinates) return lines;
		return F3CoordinateScrambler.scramble(lines);
	}
}
