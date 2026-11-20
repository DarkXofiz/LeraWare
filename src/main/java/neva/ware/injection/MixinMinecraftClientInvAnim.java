package neva.ware.injection;

import net.minecraft.class_437;
import neva.ware.features.modules.render.InventoryAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    targets = {"net.minecraft.class_310"}
)
public abstract class MixinMinecraftClientInvAnim {
    @Inject(
        method = {"method_1507(Lnet/minecraft/class_437;)V"},
        at = {@At("HEAD")},
        remap = false
    )
    private void invAnimSetScreen(class_437 var1, CallbackInfo var2) {
        InventoryAnimation.onSetScreen(var1);
    }
}
