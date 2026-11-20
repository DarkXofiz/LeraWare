package neva.ware.injection;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_1297;
import net.minecraft.class_4184;
import net.minecraft.class_758;
import net.minecraft.class_758.class_4596;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_758.class})
public class MixinBackgroundRenderer {
    @Inject(
        method = {"Lnet/minecraft/class_758;method_3211(Lnet/minecraft/class_4184;Lnet/minecraft/class_758$class_4596;FZF)V"},
        at = {@At("TAIL")}
    )
    private static void onApplyFog(class_4184 camera, class_4596 fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.fog.getValue() && fogType == class_4596.field_20946) {
            RenderSystem.setShaderFogStart(viewDistance * 4.0F);
            RenderSystem.setShaderFogEnd(viewDistance * 4.25F);
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_758;method_42588(Lnet/minecraft/class_1297;F)Lnet/minecraft/class_758$class_7286;"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private static void onGetFogModifier(class_1297 entity, float tickDelta, CallbackInfoReturnable<Object> info) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.blindness.getValue()) {
            info.setReturnValue(null);
        }
    }
}
