package net.ninjadev.freelook;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.ninjadev.freelook.common.FreeLookCommon;
import net.ninjadev.freelook.common.init.ModKeybinds;

public class FreeLookFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FreeLookCommon.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> FabricCameraEvents.getInstance().onClientTick());
        KeyBindingHelper.registerKeyBinding(ModKeybinds.keyFreeLook);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.keyToggleMode);
    }
}
