package neva.ware.injection;

import net.minecraft.class_4970.class_4971;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_4971.class})
public class MixinAbstractBlockState {
    @Inject(
        method = {"Lnet/minecraft/class_4970$class_4971;method_26213()I"},
        at = {@At("HEAD")},
        cancellable = true
    )
    public void getLuminanceHook(CallbackInfoReturnable<Integer> cir) {
        if (ModuleManager.xray.isEnabled()) {
            cir.setReturnValue(15);
        }
    }
}
