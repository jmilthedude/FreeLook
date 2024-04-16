package net.ninjadev.freelook.forge;

import net.minecraftforge.fml.common.Mod;
import net.ninjadev.freelook.FreeLook;

@Mod("freelook")
public class FreeLookForge {

    public FreeLookForge() {
        FreeLook.init();
    }
}
