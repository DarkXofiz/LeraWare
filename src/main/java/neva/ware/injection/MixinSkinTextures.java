package neva.ware.injection;

import net.minecraft.class_2960;
import net.minecraft.class_8685;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.client.Media;
import neva.ware.utility.render.TextureStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_8685.class})
public class MixinSkinTextures {
   @Inject(
      method = {"Lnet/minecraft/class_8685;comp_1626()Lnet/minecraft/class_2960;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getSkinTextureHook(CallbackInfoReturnable<class_2960> cir) {
      if (ModuleManager.media.isEnabled() && Media.skinProtect.getValue()) {
         cir.setReturnValue(TextureStorage.sunRiseSkin);
      }
   }
}
