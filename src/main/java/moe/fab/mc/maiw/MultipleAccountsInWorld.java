package moe.fab.mc.maiw;

import moe.fab.mc.maiw.fakeplayer.FakePlayer;
import moe.fab.mc.maiw.screen.MainScreen;
import moe.fab.mc.maiw.storage.PlayersStore;
import moe.fab.mc.maiw.storage.ScriptStore;
import moe.fab.mc.maiw.storage.SettingStore;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MultipleAccountsInWorld implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "multiple-accounts-in-world";
	public static final String MOD_VERSION = getModVersionString();
    public static final Logger LOGGER = LoggerFactory.getLogger("multiple-accounts-in-world");

	public static KeyBinding guiKey;
	@Override
	public void onInitialize() {
		guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.multiple_accounts_in_world.open_gui", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_BACKSLASH, // The keycode of the key
				"category.multiple_accounts_in_world" // The translation key of the keybinding's category.
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (guiKey.wasPressed()) {
				client.setScreen(new MainScreen());
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			PlayersStore.getInstance().write();
			SettingStore.getInstance().write();
			ScriptStore.getInstance().write();
		});

	}

	public static String getModVersionString()
	{
		for (ModContainer container : FabricLoader.getInstance().getAllMods())
		{
			if (container.getMetadata().getId().equals(MOD_ID))
			{
				return container.getMetadata().getVersion().getFriendlyString();
			}
		}
		return "?";
	}


}