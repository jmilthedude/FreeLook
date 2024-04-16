package net.ninjadev.freelook.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.ninjadev.freelook.event.CameraEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow
    protected void setRotation(float f, float g) {
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    public void setRotation(Camera instance, float f, float g) {
        if (MinecraftClient.getInstance().options.getPerspective().isFrontView()) {
            this.setRotation(f, g);
        } else if (CameraEvents.shouldUpdate()) {
            CameraEvents.onCameraUpdate(instance);
        } else {
            this.setRotation(f, g);
        }
    }
}
