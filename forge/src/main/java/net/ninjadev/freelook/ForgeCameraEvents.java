package net.ninjadev.freelook;

import net.minecraft.client.Camera;
import net.ninjadev.freelook.common.event.ICameraEvents;
import net.ninjadev.freelook.mixin.CameraAccessor;

public class ForgeCameraEvents implements ICameraEvents {

    private static ForgeCameraEvents INSTANCE;
    public boolean isInterpolating;
    public boolean toggle;
    public boolean isFreeLooking;
    private Camera camera;
    private float yaw;
    private float pitch;
    private float previousYaw;
    private float previousPitch;
    private float originalYaw;
    private float originalPitch;
    private float originalHeadYaw;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private double previousMouseX;
    private double previousMouseY;
    private long lerpStart;
    private long lerpTimeElapsed;
    private boolean initialPress = true;

    public static ForgeCameraEvents getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ForgeCameraEvents();
        }
        return INSTANCE;
    }

    @Override
    public Camera getCamera() {
        return this.camera;
    }

    @Override
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public boolean isInitialPress() {
        return this.initialPress;
    }

    @Override
    public void setInitialPress(boolean value) {
        this.initialPress = value;
    }

    @Override
    public boolean isInterpolating() {
        return this.isInterpolating;
    }

    @Override
    public void setIsInterpolating(boolean value) {
        this.isInterpolating = value;
    }

    @Override
    public long getLerpTimeElapsed() {
        return this.lerpTimeElapsed;
    }

    @Override
    public void setLerpTimeElapsed(long elapsed) {
        this.lerpTimeElapsed = elapsed;
    }

    @Override
    public long getLerpStart() {
        return this.lerpStart;
    }

    @Override
    public void setLerpStart(long start) {
        this.lerpStart = start;
    }

    @Override
    public boolean isToggle() {
        return this.toggle;
    }

    @Override
    public void setToggle(boolean value) {
        this.toggle = value;
    }

    @Override
    public boolean isFreeLooking() {
        return this.isFreeLooking;
    }

    @Override
    public void setFreeLooking(boolean value) {
        this.isFreeLooking = value;
    }

    @Override
    public double getMouseDeltaX() {
        return this.mouseDeltaX;
    }

    @Override
    public double getMouseDeltaY() {
        return this.mouseDeltaY;
    }

    @Override
    public void setMouseDelta(double deltaX, double deltaY) {
        this.mouseDeltaX = deltaX;
        this.mouseDeltaY = deltaY;
    }

    @Override
    public void setPreviousMouse(double mouseX, double mouseY) {
        this.previousMouseX = mouseX;
        this.previousMouseY = mouseY;
    }

    @Override
    public double getPreviousMouseX() {
        return this.previousMouseX;
    }

    @Override
    public double getPreviousMouseY() {
        return this.previousMouseY;
    }

    @Override
    public float getYaw() {
        return this.yaw;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void setYawPitch(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public float getPreviousYaw() {
        return this.previousYaw;
    }

    @Override
    public float getPreviousPitch() {
        return this.previousPitch;
    }

    @Override
    public void setPreviousYawPitch(float yaw, float pitch) {
        this.previousYaw = yaw;
        this.previousPitch = pitch;
    }

    @Override
    public float getOriginalYaw() {
        return this.originalYaw;
    }

    @Override
    public float getOriginalPitch() {
        return this.originalPitch;
    }

    @Override
    public float getOriginalHeadYaw() {
        return this.originalHeadYaw;
    }

    @Override
    public void setOriginalPlayerRotations(float originalYaw, float originalPitch, float originalHeadYaw) {
        this.originalYaw = originalYaw;
        this.originalPitch = originalPitch;
        this.originalHeadYaw = originalHeadYaw;
    }

    @Override
    public void setCameraYawPitch(float yaw, float pitch) {
        ((CameraAccessor) this.getCamera()).setYaw(yaw);
        ((CameraAccessor) this.getCamera()).setPitch(pitch);
    }
}
