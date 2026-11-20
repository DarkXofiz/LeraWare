package neva.ware.injection;

import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2480;
import net.minecraft.class_2561;
import net.minecraft.class_1792.class_9635;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.render.Tooltips;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2480.class})
public class MixinShulkerBoxBlock {
    @Inject(
        method = {"Lnet/minecraft/class_2480;method_9568(Lnet/minecraft/class_1799;Lnet/minecraft/class_1792$class_9635;Ljava/util/List;Lnet/minecraft/class_1836;)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void onAppendTooltip(class_1799 stack, class_9635 context, List<class_2561> tooltip, class_1836 options, CallbackInfo ci) {
        if (ModuleManager.tooltips != null) {
            if (Tooltips.storage.getValue()) {
                ci.cancel();
            }
        }
    }
}
