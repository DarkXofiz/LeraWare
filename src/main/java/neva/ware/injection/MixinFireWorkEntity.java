package neva.ware.injection;

import net.minecraft.class_1309;
import net.minecraft.class_1671;
import net.minecraft.class_243;
import neva.ware.core.Managers;
import neva.ware.core.manager.IManager;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.combat.Aura;
import neva.ware.features.modules.combat.ElytraCatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1671.class})
public class MixinFireWorkEntity {
    @Shadow
    private class_1309 field_7616;

    @Redirect(
        method = {"Lnet/minecraft/class_1671;method_5773()V"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_1309;method_5720()Lnet/minecraft/class_243;"
        )
    )
    private class_243 tickHook(class_1309 instance) {
        return ModuleManager.aura.isEnabled()
                && ModuleManager.aura.rotationMode.not(null)
                && Aura.target != null
                && this.field_7616 == IManager.mc.field_1724
                && ModuleManager.aura.elytraTarget.getValue()
            ? Managers.PLAYER.getRotationVector(ModuleManager.aura.rotationPitch, ModuleManager.aura.rotationYaw)
            : this.field_7616.method_5720();
    }

    @Inject(
        method = {"Lnet/minecraft/class_1671;method_5773()V"},
        at = {@At("TAIL")}
    )
    private void onTickTail(CallbackInfo ci) {
        if (this.field_7616 == IManager.mc.field_1724) {
            ElytraCatch.onFireworkTick();
        }
    }
}
