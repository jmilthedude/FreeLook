package net.ninjadev.freelook.common.util;

public class FreeLookUtil {

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
