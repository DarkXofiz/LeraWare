package neva.ware.injection;

import net.minecraft.class_2338;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_8242;
import net.minecraft.class_837;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_837.class})
public class MixinSignBlockEntityRenderer {
    @Inject(
        method = {"Lnet/minecraft/class_837;method_45798(Lnet/minecraft/class_2338;Lnet/minecraft/class_8242;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;IIIZ)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    public final void renderTextHook(
        class_2338 pos,
        class_8242 signText,
        class_4587 matrices,
        class_4597 vertexConsumers,
        int light,
        int lineHeight,
        int lineWidth,
        boolean front,
        CallbackInfo ci
    ) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.signText.getValue()) {
            ci.cancel();
        }
    }
}
