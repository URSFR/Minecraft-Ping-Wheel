package nx.pingwheel.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import nx.pingwheel.shared.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class PingWheelClient implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("nx-ping-wheel");

	private static KeyBinding kbPing;
	private static KeyBinding kbSettings;

	@Override
	public void onInitializeClient() {
		LOGGER.info("[Ping-Wheel] Client Init");

		SetupKeyBindings();

		PingWheelConfigHandler.getInstance().load();

		ClientPlayNetworking.registerGlobalReceiver(Constants.S2C_PING_LOCATION, Core::onReceivePing);
	}

	private void SetupKeyBindings() {
		kbPing = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping-wheel.key.mark-location", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5, "ping-wheel.name"));
		kbSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding("ping-wheel.key.open-settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F6, "ping-wheel.name"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (kbPing.wasPressed() && client.player.hasPermissionLevel(2)) {
				Core.markLocation();
			}

			if (kbSettings.wasPressed()) {
				MinecraftClient.getInstance().setScreen(new PingWheelSettingsScreen());
			}
		});
	}
}
