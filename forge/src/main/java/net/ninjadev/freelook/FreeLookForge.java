package net.ninjadev.freelook;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ninjadev.freelook.common.FreeLookCommon;
import net.ninjadev.freelook.common.init.ModKeybinds;

@Mod(FreeLookCommon.MOD_ID)
public class FreeLookForge {
    public FreeLookForge() {
        FreeLookCommon.init();
    }

    @Mod.EventBusSubscriber
    public static class FreeLookSetup {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            ForgeCameraEvents.getInstance().onClientTick();
        }

        @SubscribeEvent
        public static void onKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(ModKeybinds.keyFreeLook);
            event.register(ModKeybinds.keyToggleMode);
        }
    }
}
