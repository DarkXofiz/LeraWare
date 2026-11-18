package neva.ware.injection;

import net.minecraft.class_1690;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_881;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_881.class})
public class MixinBoatEntityRenderer {
   @Inject(
      method = {"Lnet/minecraft/class_881;method_3888(Lnet/minecraft/class_1690;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_4587;method_22905(FFF)V",
         shift = Shift.AFTER
      )}
   )
   public void render(class_1690 boatEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, CallbackInfo ci) {
      if (ModuleManager.boatFly.isEnabled() && ModuleManager.boatFly.hideBoat.getValue()) {
         matrixStack.method_22905(0.3F, 0.3F, 0.3F);
      }
   }
}
