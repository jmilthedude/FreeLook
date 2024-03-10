package net.ninjadev.freelook.common.init;


import net.ninjadev.freelook.common.FreeLookCommon;
import net.ninjadev.freelook.common.config.FreeLookConfiguration;

public class ModConfigs {

    public static FreeLookConfiguration FREELOOK;

    public static void register() {
        FreeLookCommon.LOGGER.info("registerConfigs()");
        FREELOOK = (FreeLookConfiguration) new FreeLookConfiguration().readConfig();
    }
}
