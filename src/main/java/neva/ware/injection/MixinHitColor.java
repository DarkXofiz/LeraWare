package neva.ware.injection;

import net.minecraft.class_1309;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_922;
import neva.ware.features.modules.render.HitColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = {class_922.class},
    remap = false
)
public abstract class MixinHitColor {
    @Inject(
        method = {"method_4054"},
        at = {@At("HEAD")},
        remap = false
    )
    private void nwHitColorHead(class_1309 var1, float var2, float var3, class_4587 var4, class_4597 var5, int var6, CallbackInfo var7) {
        HitColor.renderEntity = var1;
    }

    @Inject(
        method = {"method_4054"},
        at = {@At("RETURN")},
        remap = false
    )
    private void nwHitColorReturn(class_1309 var1, float var2, float var3, class_4587 var4, class_4597 var5, int var6, CallbackInfo var7) {
        HitColor.renderEntity = null;
    }

    @ModifyArg(
        method = {"method_4054"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_583;method_2828(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V",
            remap = false
        ),
        index = 3,
        remap = false
    )
    private int nwHitColorOverlay(int var1) {
        return HitColor.modifyOverlay(var1);
    }

    @ModifyArg(
        method = {"method_4054"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_583;method_2828(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V",
            remap = false
        ),
        index = 4,
        remap = false
    )
    private int nwHitColorColor(int var1) {
        return HitColor.modifyColor(var1);
    }
}
