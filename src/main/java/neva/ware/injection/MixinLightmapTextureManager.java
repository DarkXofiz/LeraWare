package neva.ware.injection;

import net.minecraft.class_2874;
import net.minecraft.class_3532;
import net.minecraft.class_765;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.render.Fullbright;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_765.class})
public class MixinLightmapTextureManager {
    @Inject(
        method = {"Lnet/minecraft/class_765;method_42597(F)F"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void getDarknessFactor(float tickDelta, CallbackInfoReturnable<Float> info) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.darkness.getValue()) {
            info.setReturnValue(0.0F);
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_765;method_23284(Lnet/minecraft/class_2874;I)F"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private static void getBrightnessHook(class_2874 type, int lightLevel, CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.fullbright.isEnabled()) {
            float f = lightLevel / 15.0F;
            float g = f / (4.0F - 3.0F * f);
            cir.setReturnValue(Math.max(class_3532.method_16439(type.comp_656(), g, 1.0F), Fullbright.minBright.getValue()));
        }
    }
}
