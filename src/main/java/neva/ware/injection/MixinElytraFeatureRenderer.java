package neva.ware.injection;

import net.minecraft.class_1309;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_979;
import neva.ware.features.modules.render.NoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_979.class})
public abstract class MixinElytraFeatureRenderer<T extends class_1309> {
    @Inject(
        method = {"method_17161(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1309;FFFFFF)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void x_x$hideElytra(
        class_4587 var1, class_4597 var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10, CallbackInfo var11
    ) {
        if (NoRender.isNoElytraEnabled()) {
            var11.cancel();
        }
    }
}
