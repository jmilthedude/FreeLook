package net.ninjadev.freelook.common.init;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static KeyMapping keyFreeLook = new KeyMapping("Use", GLFW.GLFW_KEY_LEFT_ALT, "FreeLook");
    public static KeyMapping keyToggleMode = new KeyMapping("Toggle", GLFW.GLFW_KEY_RIGHT_ALT, "FreeLook");

}
