package net.ninjadev.freelook.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.ninjadev.freelook.FabricCameraEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    public void setRotation(Camera instance, float f, float g) {
        FabricCameraEvents cameraEvents = FabricCameraEvents.getInstance();
        if (Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT || !cameraEvents.shouldUpdate()) {
            this.setRotation(f, g);
            return;
        }

        cameraEvents.setCamera(instance);
        cameraEvents.onUpdate();
    }
}
