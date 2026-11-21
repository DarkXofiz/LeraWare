package neva.ware.injection;

import net.minecraft.class_1299;
import net.minecraft.class_1429;
import net.minecraft.class_1496;
import net.minecraft.class_1937;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1496.class})
public abstract class MixinAbstractHorseEntity extends class_1429 {
   protected MixinAbstractHorseEntity(class_1299<? extends class_1429> entityType, class_1937 world) {
      super(entityType, world);
   }

   @Inject(
      method = {"Lnet/minecraft/class_1496;method_6725()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onIsSaddled(CallbackInfoReturnable<Boolean> cir) {
      if (ModuleManager.entityControl.isEnabled()) {
         cir.setReturnValue(true);
      }
   }
}
