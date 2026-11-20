package neva.ware.injection;

import net.minecraft.class_1058;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4603;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_4603.class})
public class MixinInGameOverlayRenderer {
    @Inject(
        method = {"Lnet/minecraft/class_4603;method_23070(Lnet/minecraft/class_310;Lnet/minecraft/class_4587;)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private static void renderFireOverlayHook(class_310 minecraftClient, class_4587 matrixStack, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.fireOverlay.getValue()) {
            ci.cancel();
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_4603;method_23069(Lnet/minecraft/class_310;Lnet/minecraft/class_4587;)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private static void renderUnderwaterOverlayHook(class_310 minecraftClient, class_4587 matrixStack, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.waterOverlay.getValue() || ModuleManager.shaders.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_4603;method_23068(Lnet/minecraft/class_1058;Lnet/minecraft/class_4587;)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private static void renderInWallOverlayHook(class_1058 sprite, class_4587 matrices, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.blockOverlay.getValue() || ModuleManager.shaders.isEnabled()) {
            ci.cancel();
        }
    }
}
