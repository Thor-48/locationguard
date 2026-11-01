package luca.locationguard.mixin;

import luca.locationguard.GuardConfig;
import luca.locationguard.LocationGuard;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Ersetzt Koordinaten/Seeds in allen Chat- und Systemnachrichten, bevor sie angezeigt werden. */
@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

	@ModifyVariable(method = "addMessage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private Component locationguard$scrub(Component message) {
		GuardConfig cfg = GuardConfig.get();
		if (!cfg.isActive() || message == null) return message;
		String original = message.getString();
		String scrubbed = cfg.scrubText(original);
		if (scrubbed.equals(original)) return message;
		LocationGuard.recordScrubbedChatMessage();
		return Component.literal(scrubbed).withStyle(message.getStyle());
	}
}
