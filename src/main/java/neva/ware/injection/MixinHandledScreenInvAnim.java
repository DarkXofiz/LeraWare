package neva.ware.injection;

import net.minecraft.class_332;
import net.minecraft.class_437;
import neva.ware.features.modules.render.InvAnimBg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"net.minecraft.class_465"},
   remap = false
)
public class MixinHandledScreenInvAnim {
   @Inject(
      method = {"method_25394(Lnet/minecraft/class_332;IIF)V"},
      at = {@At("HEAD")},
      remap = false
   )
   private void invAnimHead(class_332 var1, int var2, int var3, float var4, CallbackInfo var5) {
      InvAnimBg.head(var1, (class_437)this, var2, var3, var4);
   }

   @Inject(
      method = {"method_25394(Lnet/minecraft/class_332;IIF)V"},
      at = {@At("RETURN")},
      remap = false
   )
   private void invAnimTail(class_332 var1, int var2, int var3, float var4, CallbackInfo var5) {
      InvAnimBg.tail(var1);
   }

   @Inject(
      method = {"method_2380(Lnet/minecraft/class_332;II)V"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void invAnimTooltip(class_332 var1, int var2, int var3, CallbackInfo var4) {
      if (InvAnimBg.cancelTooltip()) {
         var4.cancel();
      }
   }
}
