package luca.locationguard;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(LocationGuard.MOD_ID)
public final class LocationGuardForge {
	public LocationGuardForge() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			LocationGuard.init();
		}
	}
}
