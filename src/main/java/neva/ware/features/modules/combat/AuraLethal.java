package neva.ware.features.modules.combat;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_746;

public final class AuraLethal {
   private static float animYaw;
   private static float animPitch;
   private static long lastCall = 0L;

   private AuraLethal() {
   }

   public static float[] compute(class_310 var0, class_1297 var1) {
      class_746 var2 = var0.field_1724;
      if (var2 != null && var1 != null) {
         long var3 = System.nanoTime();
         if (lastCall == 0L || var3 - lastCall > 300000000L) {
            animYaw = var2.method_36454();
            animPitch = var2.method_36455();
         }

         lastCall = var3;
         double var5 = (Math.random() - 0.5) * (double)var1.method_17681() * 0.7;
         double var7 = (Math.random() - 0.5) * (double)var1.method_17681() * 0.7;
         double var9 = (Math.random() - 0.5) * (double)var1.method_17682() * 0.4;
         class_243 var11 = var2.method_33571();
         double var12 = var1.method_23318()
            + class_3532.method_15350(var11.field_1351 - var1.method_23318(), 0.0, (double)var1.method_17682() * (Math.sqrt(var1.method_5858(var2)) / 3.0));
         class_243 var14 = new class_243(var1.method_23317() + var5, var12 + var9, var1.method_23321() + var7);
         float[] var15 = calculateRotations(var11, var14);
         double var16 = (Double)var0.field_1690.method_42495().method_41753();
         float var18 = (float)var16;
         float var19 = 15.0F + var18 * 25.0F;
         float var20 = Math.abs(class_3532.method_15393(var15[0] - animYaw));
         float var21 = Math.abs(var15[1] - animPitch);
         float var22 = Math.min(var19 * 3.0F, var20 * 0.4F + 2.0F);
         float var23 = Math.min(var19 * 3.0F, var21 * 0.4F + 2.0F);
         animYaw = animate(animYaw, var15[0], var22);
         animPitch = animate(animPitch, var15[1], var23);
         float var24 = (float)(Math.pow(var16 * 0.6F + 0.2F, 3.0) * 1.2F);
         float var25 = animYaw - animYaw % var24;
         float var26 = animPitch - animPitch % var24;
         return new float[]{var25, class_3532.method_15363(var26, -90.0F, 90.0F)};
      } else {
         return new float[]{var2 == null ? 0.0F : var2.method_36454(), var2 == null ? 0.0F : var2.method_36455()};
      }
   }

   private static float animate(float var0, float var1, float var2) {
      float var3 = class_3532.method_15393(var1 - var0);
      return Math.abs(var3) <= var2 ? var1 : var0 + (var3 > 0.0F ? var2 : -var2);
   }

   private static float[] calculateRotations(class_243 var0, class_243 var1) {
      double var2 = var1.field_1352 - var0.field_1352;
      double var4 = var1.field_1351 - var0.field_1351;
      double var6 = var1.field_1350 - var0.field_1350;
      double var8 = Math.sqrt(var2 * var2 + var6 * var6);
      float var10 = class_3532.method_15393((float)Math.toDegrees(Math.atan2(var6, var2)) - 90.0F);
      float var11 = class_3532.method_15393((float)(-Math.toDegrees(Math.atan2(var4, var8))));
      return new float[]{var10, var11};
   }
}
