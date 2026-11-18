package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.ChatAnimation;
import com.ezzenix.chatanimation.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_408;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_408.class})
public class ChatScreenMixin {
   @Unique
   private boolean wasOpenedLastFrame = false;
   @Unique
   private long lastOpenTime = 0L;

   @Unique
   private float calculateDisplacement() {
      if (!ModConfig.enableTextFieldAnimation) {
         return 0.0F;
      } else {
         class_310 client = class_310.method_1551();
         if (client.field_1724 != null && !this.wasOpenedLastFrame && !client.field_1724.method_6113()) {
            this.wasOpenedLastFrame = true;
            this.lastOpenTime = System.currentTimeMillis();
         }

         float FADE_TIME = (float)ModConfig.fadeTimeTextField;
         float FADE_OFFSET = 8.0F;
         float screenFactor = (float)client.method_22683().method_4506() / 1080.0F;
         float timeSinceOpen = Math.min((float)(System.currentTimeMillis() - this.lastOpenTime), FADE_TIME);
         float alpha = 1.0F - timeSinceOpen / FADE_TIME;
         float c1 = 1.70158F;
         float c3 = c1 + 1.0F;
         float modifiedAlpha = c3 * alpha * alpha * alpha - c1 * alpha * alpha;
         return modifiedAlpha * FADE_OFFSET * screenFactor;
      }
   }

   @Inject(
      method = {"method_25432"},
      at = {@At("HEAD")}
   )
   private void onClosed(CallbackInfo ci) {
      this.wasOpenedLastFrame = false;
   }

   @WrapOperation(
      method = {"method_25394"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_332;method_25294(IIIII)V"
      )}
   )
   private void wrapBackgroundFill(class_332 graphics, int x0, int y0, int x1, int y1, int col, Operation<Void> original) {
      ChatAnimation.wrap(graphics, this.calculateDisplacement(), () -> original.call(new Object[]{graphics, x0, y0, x1, y1, col}));
   }

   @WrapOperation(
      method = {"method_25394"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_437;method_25394(Lnet/minecraft/class_332;IIF)V"
      )}
   )
   private void wrapScreenRender(class_408 instance, class_332 graphics, int mouseX, int mouseY, float delta, Operation<Void> original) {
      ChatAnimation.wrap(graphics, this.calculateDisplacement(), () -> original.call(new Object[]{instance, graphics, mouseX, mouseY, delta}));
   }

   @WrapOperation(
      method = {"method_25394"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_342;method_25394(Lnet/minecraft/class_332;IIF)V"
      )}
   )
   private void wrapInputRender(class_342 instance, class_332 graphics, int i, int j, float v, Operation<Void> original) {
      ChatAnimation.wrap(graphics, this.calculateDisplacement(), () -> original.call(new Object[]{instance, graphics, i, j, v}));
   }
}
