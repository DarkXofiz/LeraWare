package neva.ware.injection;

import net.minecraft.class_1747;
import net.minecraft.class_1750;
import net.minecraft.class_2680;
import neva.ware.NevaWare;
import neva.ware.events.impl.EventPlaceBlock;
import neva.ware.features.modules.Module;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1747.class})
public class MixinBlockItem {
    @Inject(
        method = {"Lnet/minecraft/class_1747;method_7708(Lnet/minecraft/class_1750;Lnet/minecraft/class_2680;)Z"},
        at = {@At("RETURN")}
    )
    private void onPlace(@NotNull class_1750 context, class_2680 state, CallbackInfoReturnable<Boolean> info) {
        if (!Module.fullNullCheck()) {
            if (context.method_8045().field_9236) {
                NevaWare.EVENT_BUS.post(new EventPlaceBlock(context.method_8037(), state.method_26204()));
            }
        }
    }
}
