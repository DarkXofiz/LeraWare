package neva.ware.injection;

import net.minecraft.class_279;
import net.minecraft.class_761;
import net.minecraft.class_765;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.core.manager.client.ShaderManager;
import neva.ware.features.modules.Module;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_761.class})
public abstract class MixinWorldRenderer {
    @ModifyArg(
        method = {"Lnet/minecraft/class_761;method_22710(Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lnet/minecraft/class_757;Lnet/minecraft/class_765;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_761;method_3273(Lnet/minecraft/class_4184;Lnet/minecraft/class_4604;ZZ)V"
        ),
        index = 3
    )
    private boolean renderSetupTerrainModifyArg(boolean spectator) {
        return ModuleManager.freeCam.isEnabled() || spectator;
    }

    @Redirect(
        method = {"Lnet/minecraft/class_761;method_22710(Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lnet/minecraft/class_757;Lnet/minecraft/class_765;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_279;method_1258(F)V",
            ordinal = 0
        )
    )
    private void replaceShaderHook(class_279 instance, float tickDelta) {
        ShaderManager.Shader shaders = ModuleManager.shaders.mode.getValue();
        if (ModuleManager.shaders.isEnabled() && Module.mc.field_1687 != null) {
            if (Managers.SHADER.fullNullCheck()) {
                return;
            }

            Managers.SHADER.setupShader(shaders, Managers.SHADER.getShaderOutline(shaders));
        } else {
            instance.method_1258(tickDelta);
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_761;method_22714(Lnet/minecraft/class_765;FDDD)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void renderWeatherHook(class_765 manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.noWeather.getValue()) {
            ci.cancel();
        }
    }
}
