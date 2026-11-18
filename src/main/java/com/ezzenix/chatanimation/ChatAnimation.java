package com.ezzenix.chatanimation;

import com.ezzenix.chatanimation.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.class_332;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatAnimation implements ModInitializer {
   public static final String MOD_ID = "chatanimation";
   public static final String MOD_NAME = "ChatAnimation";
   public static final Logger LOGGER = LoggerFactory.getLogger("ChatAnimation");

   private static void initialize() {
   }

   public static void wrap(class_332 graphics, float displacement, Runnable runnable) {
      if (displacement != 0.0F) {
         graphics.method_51448().method_22903();
         graphics.method_51448().method_46416(0.0F, displacement, 0.0F);
      }

      runnable.run();
      if (displacement != 0.0F) {
         graphics.method_51448().method_22909();
      }
   }

   public static double getOpacityFactor(float age) {
      if (ModConfig.enableMessageAnimation && ModConfig.enableOpacity) {
         float fadeTime = (float)ModConfig.fadeTimeMessage;
         return fadeTime <= 0.0F ? 1.0 : (double)Math.min(age / fadeTime, 1.0F);
      } else {
         return 1.0;
      }
   }

   public void onInitialize() {
      initialize();
   }
}
