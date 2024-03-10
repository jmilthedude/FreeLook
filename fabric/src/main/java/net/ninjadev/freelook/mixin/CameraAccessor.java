package net.ninjadev.freelook.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraAccessor {

    @Accessor("yRot")
    void setYaw(float yaw);
    @Accessor("xRot")
    void setPitch(float pitch);

}
