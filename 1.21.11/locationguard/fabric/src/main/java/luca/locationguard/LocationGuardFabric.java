package luca.locationguard;

import net.fabricmc.api.ClientModInitializer;

public final class LocationGuardFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LocationGuard.init();
	}
}
