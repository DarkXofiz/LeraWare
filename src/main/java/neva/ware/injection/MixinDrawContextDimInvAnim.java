package neva.ware.injection;

import neva.ware.features.modules.render.InvAnimBg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"net.minecraft.class_332"},
   remap = false
)
public class MixinDrawContextDimInvAnim {
   @Inject(
      method = {"method_25296(IIIIII)V"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void invAnimDim(int var1, int var2, int var3, int var4, int var5, int var6, CallbackInfo var7) {
      if (InvAnimBg.dim(var1, var2, var5, var6)) {
         var7.cancel();
      }
   }
}
