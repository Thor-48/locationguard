package luca.locationguard.mixin;

import luca.locationguard.GuardConfig;
import luca.locationguard.LocationGuard;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

/**
 * Filtert die Eintraege des F3-Bildschirms. Ein versteckter Eintrag wird nie
 * angezeigt - weder im F3-Overlay noch ueber Debug-Profile.
 */
@Mixin(DebugScreenEntryList.class)
public abstract class DebugScreenEntryListMixin {

	@Inject(method = "isCurrentlyEnabled", at = @At("HEAD"), cancellable = true)
	private void locationguard$hideEntry(Identifier id, CallbackInfoReturnable<Boolean> cir) {
		if (GuardConfig.get().isDebugEntryHidden(id.toString())) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "getCurrentlyEnabled", at = @At("RETURN"), cancellable = true)
	private void locationguard$filterList(CallbackInfoReturnable<Collection<Identifier>> cir) {
		GuardConfig cfg = GuardConfig.get();
		if (!cfg.isActive()) {
			LocationGuard.setHiddenDebugEntries(0);
			return;
		}
		Collection<Identifier> original = cir.getReturnValue();
		if (original == null) return;
		if (cfg.hideEntireF3) {
			LocationGuard.setHiddenDebugEntries(original.size());
			cir.setReturnValue(List.of());
			return;
		}
		if (original.isEmpty()) return;
		List<Identifier> visible = original.stream()
				.filter(id -> !cfg.isDebugEntryHidden(id.toString()))
				.toList();
		LocationGuard.setHiddenDebugEntries(original.size() - visible.size());
		cir.setReturnValue(visible);
	}
}
