package luca.locationguard;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public final class ClientThreads {
	private ClientThreads() {}

	/** true, wenn der Aufruf vom Server-Thread (Singleplayer) kommt - dort darf nichts veraendert werden. */
	public static boolean isServerThread() {
		MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
		return server != null && server.isSameThread();
	}
}
