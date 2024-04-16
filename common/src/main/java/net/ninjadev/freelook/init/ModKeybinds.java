package net.ninjadev.freelook.init;

import net.minecraft.client.option.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static KeyBinding keyFreeLook = new KeyBinding("Use", GLFW.GLFW_KEY_LEFT_ALT, "FreeLook");
    public static KeyBinding keyToggleMode = new KeyBinding("Toggle", GLFW.GLFW_KEY_RIGHT_ALT, "FreeLook");

    public static KeyBinding[] register(KeyBinding[] KeyBindings) {
        return ArrayUtils.addAll(KeyBindings, keyFreeLook, keyToggleMode);
    }
}
