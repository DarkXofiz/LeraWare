package neva.ware.injection;

import net.minecraft.class_332;
import neva.ware.features.modules.render.InvAnimBg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    targets = {"net.minecraft.class_437"},
    remap = false
)
public class MixinScreenBgInvAnim {
    @Inject(
        method = {"method_25420(Lnet/minecraft/class_332;IIF)V"},
        at = {@At("HEAD")},
        remap = false
    )
    private void invAnimBgHead(class_332 var1, int var2, int var3, float var4, CallbackInfo var5) {
        InvAnimBg.bgHead(var1);
    }

    @Inject(
        method = {"method_25420(Lnet/minecraft/class_332;IIF)V"},
        at = {@At("RETURN")},
        remap = false
    )
    private void invAnimBgReturn(class_332 var1, int var2, int var3, float var4, CallbackInfo var5) {
        InvAnimBg.bgReturn(var1);
    }

    @Inject(
        method = {"method_52752(Lnet/minecraft/class_332;)V"},
        at = {@At("HEAD")},
        remap = false
    )
    private void invAnimIgHead(class_332 var1, CallbackInfo var2) {
        InvAnimBg.bgHead(var1);
    }

    @Inject(
        method = {"method_52752(Lnet/minecraft/class_332;)V"},
        at = {@At("RETURN")},
        remap = false
    )
    private void invAnimIgReturn(class_332 var1, CallbackInfo var2) {
        InvAnimBg.bgReturn(var1);
    }
}
