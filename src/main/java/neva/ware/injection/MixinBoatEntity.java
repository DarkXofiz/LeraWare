package neva.ware.injection;

import net.minecraft.class_1297;
import net.minecraft.class_1690;
import net.minecraft.class_1297.class_4738;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1690.class})
public class MixinBoatEntity {
    @Unique
    private float prevYaw;
    @Unique
    private float prevHeadYaw;

    @Inject(
        method = {"Lnet/minecraft/class_1690;method_5865(Lnet/minecraft/class_1297;Lnet/minecraft/class_1297$class_4738;)V"},
        at = {@At("HEAD")}
    )
    protected void updatePassengerPositionHookPre(class_1297 passenger, class_4738 positionUpdater, CallbackInfo ci) {
        if (ModuleManager.boatFly.isEnabled()) {
            this.prevYaw = passenger.method_36454();
            this.prevHeadYaw = passenger.method_5791();
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_1690;method_5865(Lnet/minecraft/class_1297;Lnet/minecraft/class_1297$class_4738;)V"},
        at = {@At("RETURN")}
    )
    protected void updatePassengerPositionHookPost(class_1297 passenger, class_4738 positionUpdater, CallbackInfo ci) {
        if (ModuleManager.boatFly.isEnabled()) {
            passenger.method_36456(this.prevYaw);
            passenger.method_5847(this.prevHeadYaw);
        }
    }
}
