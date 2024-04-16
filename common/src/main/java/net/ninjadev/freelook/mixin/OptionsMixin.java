package net.ninjadev.freelook.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.ninjadev.freelook.init.ModKeybinds;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameOptions.class)
public abstract class OptionsMixin {


    @Mutable
    @Shadow
    @Final
    public KeyBinding[] allKeys;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void registerKeymappings(MinecraftClient minecraft, File file, CallbackInfo ci) {

        allKeys = ModKeybinds.register(allKeys);
    }
}
