package net.ninjadev.freelook.mixin;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraAccessor {

    @Accessor("yaw")
    void setYaw(float yaw);
    @Accessor("pitch")
    void setPitch(float pitch);

}
