package net.ninjadev.freelook.event;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.ninjadev.freelook.init.ModConfigs;
import net.ninjadev.freelook.init.ModKeybinds;
import net.ninjadev.freelook.mixin.CameraAccessor;

public class CameraEvents {

    private static Minecraft minecraft;
    private static LocalPlayer player;

    private static float yaw;
    private static float pitch;
    private static float prevYaw;
    private static float prevPitch;
    private static float originalYaw;
    private static float originalPitch;
    private static float originalHeadYaw;

    private static double mouseDX;
    private static double mouseDY;
    private static double prevMouseX;
    private static double prevMouseY;

    private static long lerpStart = 0;
    private static long lerpTimeElapsed = 0;
    private static boolean initialPress = true;
    public static boolean isInterpolating = false;

    public static boolean toggle = false;

    public static boolean isFreelooking = false;

    public static void onClientTick() {
        if (ModKeybinds.keyToggleMode.consumeClick()) {
            toggle = !toggle;
        }
    }

    public static void onCameraUpdate(Camera camera) {
        if (getMinecraft().options.getCameraType() == CameraType.THIRD_PERSON_FRONT) return;

        if (ModKeybinds.keyFreeLook.isDown() || toggle) {
            isFreelooking = true;
            if (initialPress) {
                reset(camera);
                setup();
                initialPress = false;
            }

            lockPlayerRotation();
            updateMouseInput();
            updateCameraRotation(camera);

        } else if (isInterpolating) {
            lockPlayerRotation();
            interpolate(camera);
        } else {
            if (!initialPress) {
                if (ModConfigs.FREELOOK.shouldInterpolate()) {
                    ((CameraAccessor) camera).setYaw(yaw);
                    ((CameraAccessor) camera).setPitch(pitch);
                    startInterpolation();
                } else {
                    reset(camera);
                }
                initialPress = true;
            }
            isFreelooking = false;
        }
    }

    private static void startInterpolation() {
        lerpStart = System.currentTimeMillis();
        isInterpolating = true;
    }

    private static void setup() {
        originalYaw = getPlayer().getYRot();
        originalPitch = getPlayer().getXRot();
        originalHeadYaw = getPlayer().yHeadRot;
        prevMouseX = getMinecraft().mouseHandler.xpos();
        prevMouseY = getMinecraft().mouseHandler.ypos();
    }

    private static void updateCameraRotation(Camera camera) {
        double dx = mouseDX * getSensitivity() * 0.15D;
        double dy = mouseDY * getSensitivity() * 0.15D;
        yaw = (float) dx - prevYaw + originalYaw;
        if (getMinecraft().options.invertYMouse) {
            pitch = (float) dy + prevPitch + originalPitch;
        } else {
            pitch = (float) dy - prevPitch + originalPitch;
        }
        if (ModConfigs.FREELOOK.shouldClamp()) {
            yaw = Mth.clamp(yaw, (originalYaw + -100.0F), (originalYaw + 100.0F));
        }
        pitch = Mth.clamp(pitch, -89.0F, 89.0F);

        prevYaw = Mth.clamp((float) dx + prevYaw, -99f, 99f);
        prevPitch = Mth.clamp((float) dy + prevPitch, -89f, 89f);

        ((CameraAccessor) camera).setYaw(yaw);
        ((CameraAccessor) camera).setPitch(pitch);
    }

    private static void interpolate(Camera camera) {
        double duration = ModConfigs.FREELOOK.getInterpolateSpeed() * 1000f;
        float delta = (System.currentTimeMillis() - lerpStart) - lerpTimeElapsed;
        delta /= duration;

        float percentCompleted = (float) lerpTimeElapsed / (float) duration;
        float interpolatedYaw = lerp(yaw, originalYaw, percentCompleted * 10f * delta);
        float interpolatedPitch = lerp(pitch, originalPitch, percentCompleted * 10f * delta);

        ((CameraAccessor) camera).setYaw(yaw);
        ((CameraAccessor) camera).setPitch(pitch);
        yaw = interpolatedYaw;
        pitch = interpolatedPitch;

        lerpTimeElapsed = (System.currentTimeMillis() - lerpStart);
        if (lerpTimeElapsed >= duration) {
            reset(camera);
        }
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static void reset(Camera camera) {
        ((CameraAccessor) camera).setYaw(yaw);
        ((CameraAccessor) camera).setPitch(pitch);
        isInterpolating = false;
        lerpTimeElapsed = 0;
        yaw = 0;
        pitch = 0;
        prevYaw = 0;
        prevPitch = 0;
        mouseDX = 0;
        mouseDY = 0;
        prevMouseX = 0;
        prevMouseY = 0;
        player = null;
        minecraft = null;
    }

    private static void lockPlayerRotation() {
        getPlayer().setYRot(originalYaw);
        getPlayer().setXRot(originalPitch);
        getPlayer().yHeadRot = originalHeadYaw;
    }

    private static void updateMouseInput() {
        mouseDX = prevMouseX - getMinecraft().mouseHandler.xpos();
        mouseDY = prevMouseY - getMinecraft().mouseHandler.ypos();

        prevMouseX = getMinecraft().mouseHandler.xpos();
        prevMouseY = getMinecraft().mouseHandler.ypos();
    }

    private static double getSensitivity() {
        return Math.pow(getMinecraft().options.sensitivity * 0.6D + 0.2D, 3.0D) * 8.0D; // some magic number based on MC code
    }

    private static LocalPlayer getPlayer() {
        if (player == null) player = getMinecraft().player;
        return player;
    }

    private static Minecraft getMinecraft() {
        if (minecraft == null) minecraft = Minecraft.getInstance();
        return minecraft;
    }

    public static boolean shouldUpdate() {
        return ModKeybinds.keyFreeLook.isDown() || CameraEvents.toggle || CameraEvents.isFreelooking || CameraEvents.isInterpolating;
    }
}
