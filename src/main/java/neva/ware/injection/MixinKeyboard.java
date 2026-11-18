package neva.ware.injection;

import net.minecraft.class_309;
import neva.ware.NevaWare;
import neva.ware.core.Managers;
import neva.ware.events.impl.EventKeyPress;
import neva.ware.events.impl.EventKeyRelease;
import neva.ware.features.modules.Module;
import neva.ware.gui.clickui.ClickGUI;
import neva.ware.gui.hud.HudEditorGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_309.class})
public class MixinKeyboard {
   @Inject(
      method = {"onKey"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
      if (!Module.fullNullCheck()) {
         boolean whitelist = Module.mc.field_1755 == null || Module.mc.field_1755 instanceof ClickGUI || Module.mc.field_1755 instanceof HudEditorGui;
         if (whitelist) {
            if (action == 0) {
               Managers.MODULE.onKeyReleased(key);
            }

            if (action == 1) {
               Managers.MODULE.onKeyPressed(key);
            }

            if (action == 2) {
               action = 1;
            }

            switch (action) {
               case 0:
                  EventKeyRelease eventx = new EventKeyRelease(key, scanCode);
                  NevaWare.EVENT_BUS.post(eventx);
                  if (eventx.isCancelled()) {
                     ci.cancel();
                  }
                  break;
               case 1:
                  EventKeyPress event = new EventKeyPress(key, scanCode);
                  NevaWare.EVENT_BUS.post(event);
                  if (event.isCancelled()) {
                     ci.cancel();
                  }
            }
         }
      }
   }
}
