package neva.ware.injection;

import net.minecraft.class_312;
import neva.ware.NevaWare;
import neva.ware.core.Managers;
import neva.ware.events.impl.EventMouse;
import neva.ware.features.modules.Module;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_312.class})
public class MixinMouse {
   @Inject(
      method = {"Lnet/minecraft/class_312;method_1601(JIII)V"},
      at = {@At("HEAD")}
   )
   public void onMouseButtonHook(long window, int button, int action, int mods, CallbackInfo ci) {
      if (window == Module.mc.method_22683().method_4490()) {
         if (action == 0) {
            Managers.MODULE.onMoseKeyReleased(button);
         }

         if (action == 1) {
            Managers.MODULE.onMoseKeyPressed(button);
         }

         NevaWare.EVENT_BUS.post(new EventMouse(button, action));
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_312;method_1598(JDD)V"},
      at = {@At("HEAD")}
   )
   private void onMouseScrollHook(long window, double horizontal, double vertical, CallbackInfo ci) {
      if (window == Module.mc.method_22683().method_4490()) {
         NevaWare.EVENT_BUS.post(new EventMouse((int)vertical, 2));
      }
   }
}
