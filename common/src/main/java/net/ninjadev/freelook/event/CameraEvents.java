package net.ninjadev.freelook.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import net.ninjadev.freelook.init.ModConfigs;
import net.ninjadev.freelook.init.ModKeybinds;
import net.ninjadev.freelook.mixin.CameraAccessor;

public class CameraEvents {

    private static MinecraftClient minecraft;
    private static ClientPlayerEntity player;

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
        if (ModKeybinds.keyToggleMode.wasPressed()) {
            toggle = !toggle;
        }
    }

    public static void onCameraUpdate(Camera camera) {
        if (getMinecraft().options.getPerspective().isFrontView()) return;

        if (ModKeybinds.keyFreeLook.isPressed() || toggle) {
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
        originalYaw = getPlayer().getYaw();
        originalPitch = getPlayer().getPitch();
        originalHeadYaw = getPlayer().getHeadYaw();
        prevMouseX = getMinecraft().mouse.getX();
        prevMouseY = getMinecraft().mouse.getY();
    }

    private static void updateCameraRotation(Camera camera) {
        double dx = mouseDX * getSensitivity() * 0.15D;
        double dy = mouseDY * getSensitivity() * 0.15D;
        yaw = (float) dx - prevYaw + originalYaw;
        if (getMinecraft().options.getInvertYMouse().getValue()) {
            pitch = (float) dy + prevPitch + originalPitch;
        } else {
            pitch = (float) dy - prevPitch + originalPitch;
        }
        if (ModConfigs.FREELOOK.shouldClamp()) {
            yaw = MathHelper.clamp(yaw, (originalYaw + -100.0F), (originalYaw + 100.0F));
        }
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);

        prevYaw = ModConfigs.FREELOOK.shouldClamp() ? MathHelper.clamp((float) dx + prevYaw, -100.0F, 100.0F) : (float) (dx + prevYaw);

        prevPitch = MathHelper.clamp((float) dy + prevPitch, -90.0F, 90.0F);

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
        getPlayer().setYaw(originalYaw);
        getPlayer().setPitch(originalPitch);
        getPlayer().headYaw = originalHeadYaw;
    }

    private static void updateMouseInput() {
        mouseDX = prevMouseX - getMinecraft().mouse.getX();
        mouseDY = prevMouseY - getMinecraft().mouse.getY();

        prevMouseX = getMinecraft().mouse.getX();
        prevMouseY = getMinecraft().mouse.getY();
    }

    private static double getSensitivity() {
        return Math.pow(getMinecraft().options.getMouseSensitivity().getValue() * 0.6D + 0.2D, 3.0D) * 8.0D; // some magic number based on MC code
    }

    private static ClientPlayerEntity getPlayer() {
        if (player == null) player = getMinecraft().player;
        return player;
    }

    private static MinecraftClient getMinecraft() {
        if (minecraft == null) minecraft = MinecraftClient.getInstance();
        return minecraft;
    }

    public static boolean shouldUpdate() {
        return ModKeybinds.keyFreeLook.isPressed() || CameraEvents.toggle || CameraEvents.isFreelooking || CameraEvents.isInterpolating;
    }
}
