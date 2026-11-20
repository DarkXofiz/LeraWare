package neva.ware.injection;

import net.minecraft.class_1511;
import neva.ware.utility.interfaces.ICrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1511.class})
public class MixinEndCrystal implements ICrystal {
    @Unique
    int attacks;
    @Unique
    int cooldown;

    @Override
    public boolean canAttack() {
        return this.cooldown == 0;
    }

    @Override
    public void attack() {
        if (this.attacks++ >= 5) {
            this.cooldown = 20;
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_1511;method_5773()V"},
        at = {@At("HEAD")}
    )
    public void tickHook(CallbackInfo ci) {
        if (this.cooldown > 0) {
            this.cooldown--;
        }
    }
}
