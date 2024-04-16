package net.ninjadev.freelook.init;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ninjadev.freelook.FreeLook;
import net.ninjadev.freelook.config.FreeLookConfiguration;

public class ModConfigs {

    public static FreeLookConfiguration FREELOOK;

    @Environment(EnvType.CLIENT)
    public static void register() {
        FreeLook.LOGGER.info("registerConfigs()");
        FREELOOK = (FreeLookConfiguration) new FreeLookConfiguration().readConfig();
    }
}
