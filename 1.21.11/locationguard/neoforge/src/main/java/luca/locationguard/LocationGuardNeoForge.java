package luca.locationguard;

import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;

@Mod(value = LocationGuard.MOD_ID, dist = Dist.CLIENT)
public final class LocationGuardNeoForge {
	public LocationGuardNeoForge() {
		LocationGuard.init();
	}
}
