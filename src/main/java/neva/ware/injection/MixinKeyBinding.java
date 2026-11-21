package neva.ware.injection;

import net.minecraft.class_2338;
import net.minecraft.class_304;
import neva.ware.NevaWare;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_304.class})
public abstract class MixinKeyBinding {
   @Shadow
   public abstract boolean method_1435(class_304 var1);

   @Shadow
   public abstract boolean method_1434();

   @Inject(
      method = {"Lnet/minecraft/class_304;method_1434()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pressHook(CallbackInfoReturnable<Boolean> cir) {
      if (this.method_1435(NevaWare.mc.field_1690.field_1832)
         && NevaWare.mc.field_1724 != null
         && NevaWare.mc.field_1687 != null
         && ModuleManager.safeWalk.isEnabled()
         && NevaWare.mc.field_1724.method_24828()
         && NevaWare.mc
            .field_1687
            .method_8320(
               new class_2338(
                  (int)Math.floor(NevaWare.mc.field_1724.method_19538().method_10216()),
                  (int)Math.floor(NevaWare.mc.field_1724.method_19538().method_10214()) - 1,
                  (int)Math.floor(NevaWare.mc.field_1724.method_19538().method_10215())
               )
            )
            .method_26215()
         && !ModuleManager.scaffold.isEnabled()) {
         cir.setReturnValue(true);
      }
   }
}
