package neva.ware.injection;

import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_4184;
import neva.ware.core.manager.client.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_4184.class})
public abstract class MixinCamera {
   @Shadow
   private boolean field_18719;

   @Shadow
   protected abstract float method_19318(float var1);

   @ModifyArgs(
      method = {"Lnet/minecraft/class_4184;method_19321(Lnet/minecraft/class_1922;Lnet/minecraft/class_1297;ZZF)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_4184;method_19324(FFF)V",
         ordinal = 0
      )
   )
   private void modifyCameraDistance(Args args) {
      if (ModuleManager.noCameraClip.isEnabled()) {
         args.set(0, -this.method_19318(ModuleManager.noCameraClip.getDistance()));
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_4184;method_19318(F)F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onClipToSpace(float f, CallbackInfoReturnable<Float> cir) {
      if (ModuleManager.noCameraClip.isEnabled()) {
         cir.setReturnValue(ModuleManager.noCameraClip.getDistance());
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_4184;method_19321(Lnet/minecraft/class_1922;Lnet/minecraft/class_1297;ZZF)V"},
      at = {@At("TAIL")}
   )
   private void updateHook(class_1922 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
      if (ModuleManager.freeCam.isEnabled()) {
         this.field_18719 = true;
      }
   }

   @ModifyArgs(
      method = {"Lnet/minecraft/class_4184;method_19321(Lnet/minecraft/class_1922;Lnet/minecraft/class_1297;ZZF)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_4184;method_19325(FF)V"
      )
   )
   private void setRotationHook(Args args) {
      if (ModuleManager.freeCam.isEnabled()) {
         args.setAll(new Object[]{ModuleManager.freeCam.getFakeYaw(), ModuleManager.freeCam.getFakePitch()});
      }
   }

   @ModifyArgs(
      method = {"Lnet/minecraft/class_4184;method_19321(Lnet/minecraft/class_1922;Lnet/minecraft/class_1297;ZZF)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_4184;method_19327(DDD)V"
      )
   )
   private void setPosHook(Args args) {
      if (ModuleManager.freeCam.isEnabled()) {
         args.setAll(new Object[]{ModuleManager.freeCam.getFakeX(), ModuleManager.freeCam.getFakeY(), ModuleManager.freeCam.getFakeZ()});
      }
   }
}
