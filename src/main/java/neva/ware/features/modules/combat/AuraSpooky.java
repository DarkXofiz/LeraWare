package neva.ware.features.modules.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

public final class AuraSpooky {
   private static float curYaw;
   private static float curPitch;
   private static float smoothRand = 2.0F;
   private static long lastCall = 0L;
   private static int tick = 0;

   private AuraSpooky() {
   }

   private static float wrap(float var0) {
      var0 %= 360.0F;
      if (var0 >= 180.0F) {
         var0 -= 360.0F;
      }

      if (var0 < -180.0F) {
         var0 += 360.0F;
      }

      return var0;
   }

   private static float clamp(float var0, float var1, float var2) {
      return var0 < var1 ? var1 : Math.min(var0, var2);
   }

   private static float rand(float var0, float var1) {
      return var0 + (float)Math.random() * (var1 - var0);
   }

   private static float smoothRandom(float var0, float var1) {
      smoothRand = smoothRand + (rand(var0, var1) - smoothRand) * 0.15F;
      return clamp(smoothRand, var0, var1);
   }

   public static float[] compute(class_310 var0, class_1297 var1, boolean var2) {
      class_746 var3 = var0.field_1724;
      if (var3 != null && var1 != null) {
         long var4 = System.nanoTime();
         if (var4 - lastCall > 300000000L) {
            curYaw = var3.method_36454();
            curPitch = var3.method_36455();
            tick = 0;
         }

         lastCall = var4;
         class_243 var6 = var3.method_33571();
         long var7 = System.currentTimeMillis();
         float var9 = 0.12F * (float)Math.cos((double)(var7 / 820L));
         float var10 = 0.18F * (float)Math.cos((double)(var7 / 1010L));
         float var11 = 0.21F * (float)Math.cos((double)(var7 / 750L));
         double var12 = var1.method_23317();
         double var14 = var1.method_23318();
         double var16 = var1.method_23321();
         double var18 = var12 + (double)var10 - var6.field_1352;
         double var20 = var14 + (double)clamp((float)(var6.field_1351 - var14), 0.0F, 1.0F + var9) - var6.field_1351;
         double var22 = var16 + (double)var11 - var6.field_1350;
         double var24 = Math.sqrt(var18 * var18 + var20 * var20 + var22 * var22);
         if (var24 > 1.0E-6) {
            var18 /= var24;
            var20 /= var24;
            var22 /= var24;
         }

         float var26 = (float)Math.toDegrees(Math.atan2(-var18, var22));
         float var27 = clamp((float)(-Math.toDegrees(Math.atan2(var20, Math.hypot(var18, var22)))), -90.0F, 90.0F);
         boolean var28 = false;
         if (var2) {
            tick = 4;
         }

         if (tick > 0) {
            var28 = true;
            tick--;
         }

         float var29 = (float)Math.cos((double)var7 / 60.0);
         float var30 = (float)Math.sin((double)var7 / 90.0);
         float var31 = var28 ? (float)ThreadLocalRandom.current().nextLong(-2L, 4L) : 0.0F;
         float var32 = (float)(Math.random() * 3.0) * var30 + var29 * (float)(Math.random() * 3.0);
         float var33 = (float)Math.random() * var29 + var30 * smoothRandom(1.0F, 3.0F);
         float var34 = var26 + var32 + var31;
         float var35 = var27 + var33 + var31;
         float var36 = rand(40.0F, 55.0F);
         float var37 = rand(5.0F, 10.0F);
         float var38 = clamp(wrap(var34 - curYaw), -var36, var36);
         float var39 = clamp(var35 - curPitch, -var37, var37);
         double var40 = (Double)var0.field_1690.method_42495().method_41753();
         double var42 = var40 * 0.6 + 0.2;
         float var44 = (float)(var42 * var42 * var42 * 1.2);
         if (var44 > 0.0F) {
            var38 = (float)Math.round(var38 / var44) * var44;
            var39 = (float)Math.round(var39 / var44) * var44;
         }

         curYaw += var38;
         curPitch = clamp(curPitch + var39, -90.0F, 90.0F);
         return new float[]{curYaw, curPitch};
      } else {
         return new float[]{var3 == null ? 0.0F : var3.method_36454(), var3 == null ? 0.0F : var3.method_36455()};
      }
   }
}
