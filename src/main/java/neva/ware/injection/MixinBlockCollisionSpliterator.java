package neva.ware.injection;

import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_5329;
import neva.ware.NevaWare;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventCollision;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   value = {class_5329.class},
   priority = 800
)
public abstract class MixinBlockCollisionSpliterator {
   @Redirect(
      method = {"Lnet/minecraft/class_5329;computeNext()Ljava/lang/Object;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1922;method_8320(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;"
      )
   )
   private class_2680 computeNextHook(class_1922 instance, class_2338 blockPos) {
      if (!ModuleManager.antiWeb.isEnabled() && !ModuleManager.phase.isEnabled() && !ModuleManager.jesus.isEnabled()) {
         return instance.method_8320(blockPos);
      } else {
         EventCollision event = new EventCollision(instance.method_8320(blockPos), blockPos);
         NevaWare.EVENT_BUS.post(event);
         return event.getState();
      }
   }
}
