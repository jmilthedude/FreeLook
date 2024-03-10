package net.ninjadev.freelook.common;

import net.ninjadev.freelook.common.init.ModConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FreeLookCommon {

    public static final String MOD_ID = "freelook";
    public static final Logger LOGGER = LoggerFactory.getLogger("FreeLook");

    public static void init() {
        ModConfigs.register();
    }
}
