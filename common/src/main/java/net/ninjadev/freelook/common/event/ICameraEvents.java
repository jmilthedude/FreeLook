package net.ninjadev.freelook.common.event;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.ninjadev.freelook.common.init.ModConfigs;
import net.ninjadev.freelook.common.init.ModKeybinds;
import net.ninjadev.freelook.common.util.FreeLookUtil;

public interface ICameraEvents {

    boolean isInitialPress();
    void setInitialPress(boolean value);

    boolean isInterpolating();
    void setIsInterpolating(boolean value);

    long getLerpTimeElapsed();
    void setLerpTimeElapsed(long elapsed);

    long getLerpStart();
    void setLerpStart(long start);


    boolean isToggle();
    void setToggle(boolean value);

    boolean isFreeLooking();
    void setFreeLooking(boolean value);

    double getMouseDeltaX();
    double getMouseDeltaY();
    void setMouseDelta(double deltaX, double deltaY);

    void setPreviousMouse(double mouseX, double mouseY);
    double getPreviousMouseX();
    double getPreviousMouseY();

    float getYaw();
    float getPitch();
    void setYawPitch(float yaw, float pitch);

    float getPreviousYaw();
    float getPreviousPitch();
    void setPreviousYawPitch(float yaw, float pitch);

    float getOriginalYaw();
    float getOriginalPitch();
    float getOriginalHeadYaw();
    void setOriginalPlayerRotations(float originalYaw, float originalPitch, float originalHeadYaw);

    Camera getCamera();
    void setCamera(Camera camera);
    void setCameraYawPitch(float yaw, float pitch);

    default void onClientTick() {
        if (ModKeybinds.keyToggleMode.consumeClick()) {
            this.setToggle(!this.isToggle());
        }
    }

    default void setup() {
        this.setOriginalPlayerRotations(this.getPlayer().getYRot(), this.getPlayer().getXRot(), this.getPlayer().yHeadRot);
        this.setPreviousMouse(this.getMinecraft().mouseHandler.xpos(), this.getMinecraft().mouseHandler.ypos());
    }

    default void reset() {
        this.setCameraYawPitch(this.getYaw(), this.getPitch());
        this.setIsInterpolating(false);
        this.setLerpTimeElapsed(0);
        this.setYawPitch(0, 0);
        this.setPreviousYawPitch(0, 0);
        this.setMouseDelta(0, 0);
        this.setPreviousMouse(0, 0);
    }

    default void onUpdate() {
        if (getMinecraft().options.getCameraType() == CameraType.THIRD_PERSON_FRONT) return;

        if (ModKeybinds.keyFreeLook.isDown() || this.isToggle()) {
            this.setFreeLooking(true);
            if (this.isInitialPress()) {
                this.reset();
                this.setup();
                this.setInitialPress(false);
            }

            this.lockPlayerRotation();
            this.updateMouse();
            this.updateCameraRotation();

        } else if (this.isInterpolating()) {
            this.lockPlayerRotation();
            this.interpolate();
        } else {
            if (!this.isInitialPress()) {
                if (ModConfigs.FREELOOK.shouldInterpolate()) {
                    this.setCameraYawPitch(this.getYaw(), this.getPitch());
                    this.startInterpolation();
                } else {
                    this.reset();
                }
                this.setInitialPress(true);
            }
            this.setFreeLooking(false);
        }
    }

    default void lockPlayerRotation() {
        this.getPlayer().setYRot(this.getOriginalYaw());
        this.getPlayer().setXRot(this.getOriginalPitch());
        this.getPlayer().yHeadRot = this.getOriginalHeadYaw();
    }

    default void updateMouse() {
        double mouseX = this.getMinecraft().mouseHandler.xpos();
        double mouseY = this.getMinecraft().mouseHandler.ypos();
        this.setMouseDelta(this.getPreviousMouseX() - mouseX, this.getPreviousMouseY() - mouseY);

        this.setPreviousMouse(mouseX, mouseY);
    }

    default void updateCameraRotation() {
        double dx = this.getMouseDeltaX() * getSensitivity() * 0.15D;
        double dy = this.getMouseDeltaY() * getSensitivity() * 0.15D;

        float previousYaw = this.getPreviousYaw();
        float previousPitch = this.getPreviousPitch();
        float originalYaw = this.getOriginalYaw();
        float originalPitch = this.getOriginalPitch();

        float yaw = (float) dx - previousYaw + originalYaw;
        float pitch = this.getMinecraft().options.invertYMouse().get() ? (float) dy + previousPitch + previousYaw : (float) dy - previousPitch + originalPitch;
        if (ModConfigs.FREELOOK.shouldClamp()) {
            yaw = Mth.clamp(yaw, (originalYaw + -100.0F), (originalYaw + 100.0F));
        }
        pitch = Mth.clamp(pitch, -89.0F, 89.0F);

        this.setYawPitch(yaw, pitch);
        this.setPreviousYawPitch(Mth.clamp((float) dx + previousYaw, -99f, 99f), Mth.clamp((float) dy + previousPitch, -89f, 89f));

        this.setCameraYawPitch(this.getYaw(), this.getPitch());
    }

    default void startInterpolation() {
        this.setLerpStart(System.currentTimeMillis());
        this.setIsInterpolating(true);
    }

    default void interpolate() {
        double duration = ModConfigs.FREELOOK.getInterpolateSpeed() * 1000f;
        float delta = (System.currentTimeMillis() - this.getLerpStart()) - this.getLerpTimeElapsed();
        delta /= duration;

        float percentCompleted = (float) this.getLerpTimeElapsed() / (float) duration;
        float interpolatedYaw = FreeLookUtil.lerp(this.getYaw(), this.getOriginalYaw(), percentCompleted * 10f * delta);
        float interpolatedPitch = FreeLookUtil.lerp(this.getPitch(), this.getOriginalPitch(), percentCompleted * 10f * delta);

        this.setCameraYawPitch(this.getYaw(), this.getPitch());
        this.setYawPitch(interpolatedYaw, interpolatedPitch);

        this.setLerpTimeElapsed((System.currentTimeMillis() - this.getLerpStart()));
        if (this.getLerpTimeElapsed() >= duration) {
            this.reset();
        }
    }

    /* ------------------------------ */

    default Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    default LocalPlayer getPlayer() {
        return this.getMinecraft().player;
    }

    private double getSensitivity() {
        return Math.pow(this.getMinecraft().options.sensitivity().get() * 0.6D + 0.2D, 3.0D) * 8.0D; // some magic number based on MC code
    }

    default boolean shouldUpdate() {
        return ModKeybinds.keyFreeLook.isDown() || this.isToggle() || this.isFreeLooking() || this.isInterpolating();
    }

}
