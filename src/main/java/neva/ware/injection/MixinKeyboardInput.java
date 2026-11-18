package neva.ware.injection;

import net.minecraft.class_743;
import neva.ware.NevaWare;
import neva.ware.events.impl.EventKeyboardInput;
import neva.ware.features.modules.Module;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_743.class})
public class MixinKeyboardInput {
   @Inject(
      method = {"Lnet/minecraft/class_743;method_3129(ZF)V"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/class_743;field_3903:Z",
         shift = Shift.BEFORE
      )},
      cancellable = true
   )
   private void onSneak(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
      if (!Module.fullNullCheck()) {
         EventKeyboardInput event = new EventKeyboardInput();
         NevaWare.EVENT_BUS.post(event);
         if (event.isCancelled()) {
            ci.cancel();
         }
      }
   }
}
