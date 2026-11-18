package neva.ware.injection;

import net.minecraft.class_8685;
import neva.ware.features.modules.misc.FakePlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   targets = {"net.minecraft.class_742"},
   remap = false
)
public class MixinFakePlayerSkin {
   @Inject(
      method = {"method_52814()Lnet/minecraft/class_8685;"},
      at = {@At("RETURN")},
      cancellable = true,
      remap = false
   )
   private void fakePlayerBlackSkin(CallbackInfoReturnable<class_8685> var1) {
      class_8685 var2 = FakePlayerSkin.apply(this, (class_8685)var1.getReturnValue());
      if (var2 != null) {
         var1.setReturnValue(var2);
      }
   }
}
