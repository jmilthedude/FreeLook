package net.ninjadev.freelook.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.ninjadev.freelook.FreeLook;

public class FreeLookClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FreeLook.init();
    }
}
