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

    private static State state = State.INACTIVE;
    private static boolean isToggled = false;

    public static void onClientTick() {
        if (ModKeybinds.keyToggleMode.wasPressed()) {
            isToggled = !isToggled;
        }
    }

    public static void onCameraUpdate(Camera camera) {
        if (getMinecraft().options.getPerspective().isFrontView()) return;

        if (ModKeybinds.keyFreeLook.isPressed() || isToggled) {
            if (state == State.INACTIVE) {
                reset(camera);
                setup();
                state = State.ACTIVE;
                return;
            }

            lockPlayerRotation();
            updateMouseInput();
            updateCameraRotation(camera);

        } else if (state == State.INTERPOLATING) {
            lockPlayerRotation();
            interpolate(camera);
        } else {
            if (state == State.ACTIVE) {
                if (ModConfigs.FREELOOK.shouldInterpolate()) {
                    ((CameraAccessor) camera).callSetRotation(yaw, pitch);
                    startInterpolation();
                    state = State.INTERPOLATING;
                } else {
                    reset(camera);
                }
            }
        }
    }

    private static void startInterpolation() {
        lerpStart = System.currentTimeMillis();
    }

    private static void setup() {
        originalYaw = yaw = prevYaw = getPlayer().getYaw();
        originalPitch = pitch = prevPitch = getPlayer().getPitch();
        originalHeadYaw = getPlayer().getHeadYaw();
        prevMouseX = getMinecraft().mouse.getX();
        prevMouseY = getMinecraft().mouse.getY();
    }

    private static void updateCameraRotation(Camera camera) {
        double dx = mouseDX * getSensitivity() * 0.15D;
        double dy = mouseDY * getSensitivity() * 0.15D;

        yaw = prevYaw - (float) dx;
        if (ModConfigs.FREELOOK.shouldClamp()) {
            yaw = MathHelper.clamp(yaw, (originalYaw - 100.0F), (originalYaw + 100.0F));
        }

        if (getMinecraft().options.getInvertYMouse().getValue()) {
            pitch = prevPitch + (float) dy;
        } else {
            pitch = prevPitch - (float) dy;
        }
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);

        if (getMinecraft().options.getPerspective().isFrontView()) {
            yaw -= 180;
            pitch = -pitch;
        }

        ((CameraAccessor) camera).callSetRotation(yaw, pitch);
        getPlayer().setHeadYaw(yaw);
        getPlayer().setPitch(pitch);

        prevYaw = yaw;
        prevPitch = pitch;
    }

    private static void interpolate(Camera camera) {
        double duration = ModConfigs.FREELOOK.getInterpolateSpeed() * 1000f;
        float delta = (System.currentTimeMillis() - lerpStart) - lerpTimeElapsed;
        delta /= (float) duration;

        float percentCompleted = (float) lerpTimeElapsed / (float) duration;
        float interpolatedYaw = lerp(yaw, originalYaw, percentCompleted * 10f * delta);
        float interpolatedPitch = lerp(pitch, originalPitch, percentCompleted * 10f * delta);

        ((CameraAccessor) camera).callSetRotation(yaw, pitch);
        getPlayer().setHeadYaw(yaw);
        getPlayer().setPitch(pitch);
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
        //TODO ((CameraAccessor)camera).callSetRotation(yaw, pitch);
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
        state = State.INACTIVE;
    }

    private static void lockPlayerRotation() {
        getPlayer().setYaw(originalYaw);
        getPlayer().setPitch(originalPitch);
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
        return ModKeybinds.keyFreeLook.isPressed() || CameraEvents.isToggled || state != State.INACTIVE;
    }

    public enum State {
        INACTIVE, ACTIVE, INTERPOLATING
    }
}
