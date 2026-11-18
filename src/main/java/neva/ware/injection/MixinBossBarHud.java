package neva.ware.injection;

import net.minecraft.class_337;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_337.class})
public class MixinBossBarHud {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void render(CallbackInfo ci) {
      if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.bossbar.getValue()) {
         ci.cancel();
      }
   }
}
