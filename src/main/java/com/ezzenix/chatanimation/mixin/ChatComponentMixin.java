package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.ChatAnimation;
import com.ezzenix.chatanimation.config.EasingStyle;
import com.ezzenix.chatanimation.config.ModConfig;
import com.ezzenix.chatanimation.util.TimestampedMessageLine;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_338;
import net.minecraft.class_7469;
import net.minecraft.class_7591;
import net.minecraft.class_303.class_7590;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_338.class})
public class ChatComponentMixin {
   @Shadow
   private int field_2066;
   @Unique
   private long lastMessageTime = 0L;

   @Shadow
   private int method_44752() {
      return 0;
   }

   @Unique
   private float calculateDisplacement() {
      if (ModConfig.enableMessageAnimation && this.field_2066 == 0) {
         float fadeTime = (float)ModConfig.fadeTimeMessage;
         int lineHeight = this.method_44752();
         float fadeOffsetYScale = 0.8F;
         float maxDisplacement = (float)lineHeight * fadeOffsetYScale;
         long lifetime = System.currentTimeMillis() - this.lastMessageTime;
         float alpha = Math.min((float)lifetime / fadeTime, 1.0F);
         return maxDisplacement - alpha * maxDisplacement;
      } else {
         return 0.0F;
      }
   }

   @Inject(
      method = {"method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"},
      at = {@At("TAIL")}
   )
   private void addMessage(class_2561 chatComponent, class_7469 headerSignature, class_7591 tag, CallbackInfo ci) {
      this.lastMessageTime = System.currentTimeMillis();
   }

   @WrapMethod(
      method = {"method_1805"}
   )
   private void wrapRender(class_332 guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, Operation<Void> original) {
      ChatAnimation.wrap(guiGraphics, this.calculateDisplacement(), () -> original.call(new Object[]{guiGraphics, tickCount, mouseX, mouseY, focused}));
   }

   @ModifyVariable(
      method = {"method_1805"},
      at = @At("STORE"),
      ordinal = 3
   )
   private double modifyChatOpacity(double original, @Local class_7590 line) {
      float age = TimestampedMessageLine.age(line);
      double alpha = original * ChatAnimation.getOpacityFactor(age);
      return EasingStyle.SINE.apply(alpha);
   }
}
