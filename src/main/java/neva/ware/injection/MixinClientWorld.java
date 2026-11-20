package neva.ware.injection;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_638;
import net.minecraft.class_1297.class_5529;
import neva.ware.NevaWare;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventEntityRemoved;
import neva.ware.events.impl.EventEntitySpawn;
import neva.ware.events.impl.EventEntitySpawnPost;
import neva.ware.features.modules.Module;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_638.class})
public class MixinClientWorld {
    @Inject(
        method = {"Lnet/minecraft/class_638;method_53875(Lnet/minecraft/class_1297;)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    public void addEntityHook(class_1297 entity, CallbackInfo ci) {
        if (!Module.fullNullCheck()) {
            EventEntitySpawn ees = new EventEntitySpawn(entity);
            NevaWare.EVENT_BUS.post(ees);
            if (ees.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_638;method_53875(Lnet/minecraft/class_1297;)V"},
        at = {@At("RETURN")},
        cancellable = true
    )
    public void addEntityHookPost(class_1297 entity, CallbackInfo ci) {
        if (!Module.fullNullCheck()) {
            EventEntitySpawnPost ees = new EventEntitySpawnPost(entity);
            NevaWare.EVENT_BUS.post(ees);
            if (ees.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_638;method_2945(ILnet/minecraft/class_1297$class_5529;)V"},
        at = {@At("HEAD")}
    )
    public void removeEntityHook(int entityId, class_5529 removalReason, CallbackInfo ci) {
        if (!Module.fullNullCheck()) {
            EventEntityRemoved eer = new EventEntityRemoved(Module.mc.field_1687.method_8469(entityId));
            NevaWare.EVENT_BUS.post(eer);
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_638;method_23777(Lnet/minecraft/class_243;F)Lnet/minecraft/class_243;"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void getSkyColorHook(class_243 cameraPos, float tickDelta, CallbackInfoReturnable<class_243> cir) {
    }

    @Inject(
        method = {"Lnet/minecraft/class_638;method_43207(DDDLnet/minecraft/class_3414;Lnet/minecraft/class_3419;FFZJ)V"},
        at = {@At("HEAD")}
    )
    private void playSoundHoof(
        double x, double y, double z, class_3414 event, class_3419 category, float volume, float pitch, boolean useDistance, long seed, CallbackInfo ci
    ) {
        if (ModuleManager.soundESP.isEnabled()) {
            ModuleManager.soundESP.add(x, y, z, event.method_14833().method_42094());
        }
    }
}
