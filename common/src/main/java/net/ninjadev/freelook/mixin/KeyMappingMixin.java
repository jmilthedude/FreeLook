package net.ninjadev.freelook.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(KeyBinding.class)
public class KeyMappingMixin {

    @Shadow @Final private String category;


    @Shadow @Final private static Map<String, Integer> CATEGORY_ORDER_MAP;

    @Shadow @Final private String translationKey;

    // Basically overwriting this method on KeyMapping.class so that MC takes into consideration our
    // manually added keymappings. This should not affect other mods as this is basically a clone
    // of Forge's handling of the same. Without this, the keybind screen will crash the game.
    @Inject(method = "compareTo(Lnet/minecraft/client/option/KeyBinding;)I", at = @At(value = "HEAD"), cancellable = true)
    public void compareTo(KeyBinding keyMapping, CallbackInfoReturnable<Integer> cir) {
        if (category.equals(keyMapping.getCategory())) {
            cir.setReturnValue(I18n.translate(translationKey).compareTo(I18n.translate(keyMapping.getTranslationKey())));
            cir.cancel();
            return;
        }

        Integer tCat = CATEGORY_ORDER_MAP.get(category);
        Integer oCat = CATEGORY_ORDER_MAP.get(keyMapping.getCategory());
        if (tCat == null && oCat != null) {
            cir.setReturnValue(1);
            cir.cancel();
            return;
        }
        if (tCat != null && oCat == null) {
            cir.setReturnValue(-1);
            cir.cancel();
            return;
        }
        if (tCat == null) {
            cir.setReturnValue(I18n.translate(category).compareTo(I18n.translate(keyMapping.getCategory())));
            cir.cancel();
            return;
        }
        cir.setReturnValue(tCat.compareTo(oCat));
    }

}
