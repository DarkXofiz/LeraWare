package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.config.ModConfig;
import com.ezzenix.chatanimation.util.TimestampedMessageLine;
import net.minecraft.class_7591;
import net.minecraft.class_303.class_7590;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_7590.class})
public class GuiMessageLineMixin implements TimestampedMessageLine {
   @Unique
   private long chatAnimation$addedTime;

   @Override
   public long chatAnimation$getAddedTime() {
      return this.chatAnimation$addedTime;
   }

   @Override
   public void chatAnimation$setAddedTime(long time) {
      this.chatAnimation$addedTime = time;
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void afterInit(CallbackInfo ci) {
      TimestampedMessageLine.of((class_7590)this).chatAnimation$setAddedTime(System.currentTimeMillis());
   }

   @Inject(
      method = {"comp_897"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectIndicator(CallbackInfoReturnable<class_7591> cir) {
      if (ModConfig.removeMessageIndicator) {
         cir.setReturnValue(null);
      }
   }
}
