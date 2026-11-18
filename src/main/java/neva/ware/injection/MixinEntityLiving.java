package neva.ware.injection;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1309;
import net.minecraft.class_1313;
import net.minecraft.class_2338;
import net.minecraft.class_2404;
import net.minecraft.class_243;
import neva.ware.NevaWare;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventTravel;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.Aura;
import neva.ware.features.modules.movement.WaterSpeed;
import neva.ware.features.modules.render.Animations;
import neva.ware.utility.interfaces.IEntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1309.class})
public class MixinEntityLiving implements IEntityLiving {
   @Shadow
   protected double field_6224;
   @Shadow
   protected double field_6245;
   @Shadow
   protected double field_6263;
   @Unique
   double prevServerX;
   @Unique
   double prevServerY;
   @Unique
   double prevServerZ;
   @Unique
   public List<Aura.Position> positonHistory = new ArrayList<>();
   @Unique
   private boolean prevFlying = false;

   @Override
   public List<Aura.Position> getPositionHistory() {
      return this.positonHistory;
   }

   @Inject(
      method = {"Lnet/minecraft/class_1309;method_6028()I"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getArmSwingAnimationEnd(CallbackInfoReturnable<Integer> info) {
      if (!ModuleManager.noRender.noSwing.getValue() && ModuleManager.animations.shouldChangeAnimationDuration() && Animations.slowAnimation.getValue()) {
         info.setReturnValue(Animations.slowAnimationVal.getValue());
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_1309;method_5759(DDDFFI)V"},
      at = {@At("HEAD")}
   )
   private void updateTrackedPositionAndAnglesHook(double x, double y, double z, float yaw, float pitch, int interpolationSteps, CallbackInfo ci) {
      if (!Module.fullNullCheck()) {
         this.prevServerX = this.field_6224;
         this.prevServerY = this.field_6245;
         this.prevServerZ = this.field_6263;
         this.positonHistory.add(new Aura.Position(this.field_6224, this.field_6245, this.field_6263));
         this.positonHistory.removeIf(Aura.Position::shouldRemove);
      }
   }

   @Override
   public double getPrevServerX() {
      return this.prevServerX;
   }

   @Override
   public double getPrevServerY() {
      return this.prevServerY;
   }

   @Override
   public double getPrevServerZ() {
      return this.prevServerZ;
   }

   @Inject(
      method = {"Lnet/minecraft/class_1309;method_6128()Z"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void isFallFlyingHook(CallbackInfoReturnable<Boolean> cir) {
      if (ModuleManager.elytraRecast.isEnabled()) {
         boolean elytra = (Boolean)cir.getReturnValue();
         if (this.prevFlying && !(Boolean)cir.getReturnValue()) {
            cir.setReturnValue(ModuleManager.elytraRecast.castElytra());
         }

         this.prevFlying = elytra;
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_1309;method_6091(Lnet/minecraft/class_243;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void travelHook(class_243 movementInput, CallbackInfo ci) {
      if (!Module.fullNullCheck()) {
         if ((class_1309)(Object)(Object)this == Module.mc.field_1724) {
            EventTravel event = new EventTravel(Module.mc.field_1724.method_18798(), true);
            NevaWare.EVENT_BUS.post(event);
            if (event.isCancelled()) {
               Module.mc.field_1724.method_5784(class_1313.field_6308, event.getmVec());
               ci.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_1309;method_6091(Lnet/minecraft/class_243;)V"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void travelPostHook(class_243 movementInput, CallbackInfo ci) {
      if (!Module.fullNullCheck()) {
         if ((class_1309)(Object)(Object)this == Module.mc.field_1724) {
            EventTravel event = new EventTravel(movementInput, false);
            NevaWare.EVENT_BUS.post(event);
            if (event.isCancelled()) {
               Module.mc.field_1724.method_5784(class_1313.field_6308, Module.mc.field_1724.method_18798());
               ci.cancel();
            }
         }
      }
   }

   @ModifyVariable(
      method = {"Lnet/minecraft/class_1309;method_5728(Z)V"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private boolean setSprintingHook(boolean sprinting) {
      return Module.mc.field_1724 == null
            || Module.mc.field_1687 == null
            || !ModuleManager.waterSpeed.isEnabled()
            || !ModuleManager.waterSpeed.mode.is(WaterSpeed.Mode.CancelResurface)
            || !Module.mc.field_1724.method_5799()
               && !(
                  Module.mc.field_1687.method_8320(class_2338.method_49638(Module.mc.field_1724.method_19538().method_1031(0.0, -0.5, 0.0))).method_26204() instanceof class_2404
               )
         ? sprinting
         : true;
   }

   @Inject(
      method = {"Lnet/minecraft/class_1309;method_6028()I"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
      if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.noSwing.getValue()) {
         cir.setReturnValue(0);
         cir.cancel();
      }
   }
}
