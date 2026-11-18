package neva.ware.injection;

import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2358;
import net.minecraft.class_2680;
import neva.ware.core.manager.IManager;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.render.XRay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_2248.class})
public abstract class MixinBlock {
   @Inject(
      method = {"Lnet/minecraft/class_2248;method_9607(Lnet/minecraft/class_2680;Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_2350;Lnet/minecraft/class_2338;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void shouldDrawSideHook(
      class_2680 state, class_1922 world, class_2338 pos, class_2350 side, class_2338 blockPos, CallbackInfoReturnable<Boolean> cir
   ) {
      if (ModuleManager.xray.isEnabled() && ModuleManager.xray.wallHack.getValue()) {
         cir.setReturnValue(XRay.isCheckableOre(state.method_26204()));
      }

      if (ModuleManager.autoAnchor.isEnabled() && state.method_26204() instanceof class_2358) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_2248;method_23349()F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getVelocityMultiplierHook(CallbackInfoReturnable<Float> cir) {
      if (ModuleManager.noSlow.isEnabled()) {
         if (ModuleManager.noSlow.soulSand.getValue() && (Object)this == class_2246.field_10114) {
            cir.setReturnValue(class_2246.field_10566.method_23349());
         }

         if (ModuleManager.noSlow.honey.getValue() && (Object)this == class_2246.field_21211) {
            cir.setReturnValue(class_2246.field_10566.method_23349());
         }
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_2248;method_9499()F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getSlipperinessHook(CallbackInfoReturnable<Float> cir) {
      if (ModuleManager.noSlow.isEnabled()) {
         if (ModuleManager.noSlow.slime.getValue() && (Object)this == class_2246.field_10030) {
            cir.setReturnValue(class_2246.field_10566.method_9499());
         }

         if (ModuleManager.noSlow.ice.getValue()
            && ((Object)this == class_2246.field_10295 || (Object)this == class_2246.field_10225 || (Object)this == class_2246.field_10384 || (Object)this == class_2246.field_10110)
            && !IManager.mc.field_1690.field_1903.method_1434()) {
            cir.setReturnValue(class_2246.field_10566.method_9499());
         }
      }
   }
}
