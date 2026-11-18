package neva.ware.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.class_243;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render3DEngine;
import org.joml.Matrix4f;

public final class CometFx {
   private static class_2960 GLOW;
   private static final Random R = new Random();
   private static double[] bx = new double[1024];
   private static double[] by = new double[1024];
   private static double[] bz = new double[1024];
   private static float[] bw = new float[1024];
   private static int[] bc = new int[1024];
   private static int bn;
   private static int[] strips = new int[256];
   private static int sn;
   private static float[] hx = new float[1024];
   private static float[] hy = new float[1024];
   private static float[] hz = new float[1024];
   private static float[] hs = new float[1024];
   private static int[] hc = new int[1024];
   private static int hn;
   private static double rx;
   private static double rz;
   private static double ux;
   private static double uy;
   private static double uz;
   private static final double[] SIDE = new double[3];
   private static final int HN = 11;
   private static final ArrayList<CometFx.Comet> WORLD = new ArrayList<>();

   private CometFx() {
   }

   public static void begin() {
      bn = 0;
      sn = 0;
      hn = 0;
   }

   public static void strip() {
      if (sn >= strips.length) {
         strips = Arrays.copyOf(strips, sn * 2);
      }

      strips[sn++] = bn;
   }

   public static void point(double var0, double var2, double var4, float var6, int var7) {
      if (bn >= bx.length) {
         int var8 = bn * 2;
         bx = Arrays.copyOf(bx, var8);
         by = Arrays.copyOf(by, var8);
         bz = Arrays.copyOf(bz, var8);
         bw = Arrays.copyOf(bw, var8);
         bc = Arrays.copyOf(bc, var8);
      }

      bx[bn] = var0;
      by[bn] = var2;
      bz[bn] = var4;
      bw[bn] = var6;
      bc[bn] = var7;
      bn++;
   }

   public static void sprite(double var0, double var2, double var4, float var6, int var7) {
      if (var7 >>> 24 > 0 && !(var6 <= 0.0F)) {
         if (hn >= hx.length) {
            int var8 = hn * 2;
            hx = Arrays.copyOf(hx, var8);
            hy = Arrays.copyOf(hy, var8);
            hz = Arrays.copyOf(hz, var8);
            hs = Arrays.copyOf(hs, var8);
            hc = Arrays.copyOf(hc, var8);
         }

         hx[hn] = (float)var0;
         hy[hn] = (float)var2;
         hz[hn] = (float)var4;
         hs[hn] = var6;
         hc[hn] = var7;
         hn++;
      }
   }

   private static void camVec(class_4184 var0) {
      double var1 = Math.toRadians((double)(-var0.method_19330()));
      double var3 = Math.toRadians((double)var0.method_19329());
      rx = Math.cos(var1);
      rz = -Math.sin(var1);
      ux = Math.sin(var3) * Math.sin(var1);
      uy = Math.cos(var3);
      uz = Math.sin(var3) * Math.cos(var1);
   }

   public static void draw(class_4587 var0, boolean var1) {
      if (bn != 0 || hn != 0) {
         class_310 var2 = class_310.method_1551();
         camVec(var2.field_1773.method_19418());
         if (GLOW == null) {
            GLOW = class_2960.method_60655("nevaware", "textures/particles/ghost_glow.png");
         }

         if (var1) {
            RenderSystem.enableDepthTest();
         } else {
            RenderSystem.disableDepthTest();
         }

         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.setShaderTexture(0, GLOW);
         RenderSystem.setShader(class_757::method_34543);
         RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
         Matrix4f var3 = var0.method_23760().method_23761();
         flushStrips(var3, 1.0F, 1.0F);
         flushStrips(var3, 0.35F, 2.2F);
         flushSprites(var3, 1.0F, 1.0F);
         flushSprites(var3, 0.25F, 2.4F);
         RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
         RenderSystem.disableBlend();
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   private static void flushStrips(Matrix4f var0, float var1, float var2) {
      class_287 var3 = null;

      for (int var4 = 0; var4 < sn; var4++) {
         int var5 = strips[var4];
         int var6 = var4 + 1 < sn ? strips[var4 + 1] : bn;
         if (var6 - var5 >= 2) {
            for (int var7 = var5; var7 < var6 - 1; var7++) {
               int var8 = Math.min(255, (int)((float)(bc[var7] >>> 24) * var1));
               int var9 = Math.min(255, (int)((float)(bc[var7 + 1] >>> 24) * var1));
               if (var8 > 0 || var9 > 0) {
                  double[] var10 = side(var7, var5, var6);
                  double[] var11 = side(var7 + 1, var5, var6);
                  if (var10 != null && var11 != null) {
                     if (var3 == null) {
                        var3 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1575);
                     }

                     float var12 = bw[var7] * var2;
                     float var13 = bw[var7 + 1] * var2;
                     int var14 = var8 << 24 | bc[var7] & 16777215;
                     int var15 = var9 << 24 | bc[var7 + 1] & 16777215;
                     float var16 = (float)(bx[var7] - var10[0] * (double)var12);
                     float var17 = (float)(by[var7] - var10[1] * (double)var12);
                     float var18 = (float)(bz[var7] - var10[2] * (double)var12);
                     float var19 = (float)(bx[var7] + var10[0] * (double)var12);
                     float var20 = (float)(by[var7] + var10[1] * (double)var12);
                     float var21 = (float)(bz[var7] + var10[2] * (double)var12);
                     float var22 = (float)(bx[var7 + 1] + var11[0] * (double)var13);
                     float var23 = (float)(by[var7 + 1] + var11[1] * (double)var13);
                     float var24 = (float)(bz[var7 + 1] + var11[2] * (double)var13);
                     float var25 = (float)(bx[var7 + 1] - var11[0] * (double)var13);
                     float var26 = (float)(by[var7 + 1] - var11[1] * (double)var13);
                     float var27 = (float)(bz[var7 + 1] - var11[2] * (double)var13);
                     var3.method_22918(var0, var16, var17, var18).method_22913(0.0F, 0.5F).method_39415(var14);
                     var3.method_22918(var0, var19, var20, var21).method_22913(1.0F, 0.5F).method_39415(var14);
                     var3.method_22918(var0, var22, var23, var24).method_22913(1.0F, 0.5F).method_39415(var15);
                     var3.method_22918(var0, var25, var26, var27).method_22913(0.0F, 0.5F).method_39415(var15);
                  }
               }
            }
         }
      }

      if (var3 != null) {
         class_286.method_43433(var3.method_60800());
      }
   }

   private static double[] side(int var0, int var1, int var2) {
      int var3 = Math.max(var1, var0 - 1);
      int var4 = Math.min(var2 - 1, var0 + 1);
      double var5 = bx[var4] - bx[var3];
      double var7 = by[var4] - by[var3];
      double var9 = bz[var4] - bz[var3];
      double var11 = bx[var0];
      double var13 = by[var0];
      double var15 = bz[var0];
      double var17 = var7 * var15 - var9 * var13;
      double var19 = var9 * var11 - var5 * var15;
      double var21 = var5 * var13 - var7 * var11;
      double var23 = Math.sqrt(var17 * var17 + var19 * var19 + var21 * var21);
      if (var23 < 1.0E-9) {
         return null;
      } else {
         SIDE[0] = var17 / var23;
         SIDE[1] = var19 / var23;
         SIDE[2] = var21 / var23;
         return SIDE;
      }
   }

   private static void flushSprites(Matrix4f var0, float var1, float var2) {
      boolean var3 = false;

      for (int var4 = 0; var4 < hn; var4++) {
         if ((int)((float)(hc[var4] >>> 24) * var1) > 0) {
            var3 = true;
            break;
         }
      }

      if (var3) {
         class_287 var17 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1575);

         for (int var5 = 0; var5 < hn; var5++) {
            int var6 = Math.min(255, (int)((float)(hc[var5] >>> 24) * var1));
            if (var6 > 0) {
               int var7 = var6 << 24 | hc[var5] & 16777215;
               float var8 = hs[var5] * var2;
               float var9 = hx[var5];
               float var10 = hy[var5];
               float var11 = hz[var5];
               float var12 = (float)(rx * (double)var8);
               float var13 = (float)(rz * (double)var8);
               float var14 = (float)(ux * (double)var8);
               float var15 = (float)(uy * (double)var8);
               float var16 = (float)(uz * (double)var8);
               var17.method_22918(var0, var9 - var12 - var14, var10 - var15, var11 - var13 - var16).method_22913(0.0F, 1.0F).method_39415(var7);
               var17.method_22918(var0, var9 + var12 - var14, var10 - var15, var11 + var13 - var16).method_22913(1.0F, 1.0F).method_39415(var7);
               var17.method_22918(var0, var9 + var12 + var14, var10 + var15, var11 + var13 + var16).method_22913(1.0F, 0.0F).method_39415(var7);
               var17.method_22918(var0, var9 - var12 + var14, var10 + var15, var11 - var13 + var16).method_22913(0.0F, 0.0F).method_39415(var7);
            }
         }

         class_286.method_43433(var17.method_60800());
      }
   }

   public static void clearWorld() {
      if (!WORLD.isEmpty()) {
         WORLD.clear();
      }
   }

   private static int worldColor(Setting<?> var0, Setting<?> var1, int var2) {
      if (String.valueOf(var0.getValue()).equals("Custom")) {
         ColorSetting var3 = (ColorSetting)var1.getValue();
         return var3.getRed() << 16 | var3.getGreen() << 8 | var3.getBlue();
      } else {
         return HudEditor.getColor(var2).getRGB() & 16777215;
      }
   }

   public static void tickWorld(Setting<?> var0, Setting<?> var1, Setting<?> var2, Setting<?> var3) {
      class_310 var4 = class_310.method_1551();
      if (var4.field_1724 == null) {
         WORLD.clear();
      } else {
         class_243 var5 = var4.field_1773.method_19418().method_19326();
         int var6 = Math.max(4, ((Number)var0.getValue()).intValue() / 4);

         for (int var7 = WORLD.size() - 1; var7 >= 0; var7--) {
            CometFx.Comet var8 = WORLD.get(var7);
            var8.age++;
            double var9 = var8.x - var5.field_1352;
            double var11 = var8.y - var5.field_1351;
            double var13 = var8.z - var5.field_1350;
            if (var8.age < var8.life && !(var9 * var9 + var11 * var11 + var13 * var13 > 2304.0)) {
               System.arraycopy(var8.hx, 0, var8.hx, 1, 10);
               System.arraycopy(var8.hy, 0, var8.hy, 1, 10);
               System.arraycopy(var8.hz, 0, var8.hz, 1, 10);
               var8.hx[0] = var8.x;
               var8.hy[0] = var8.y;
               var8.hz[0] = var8.z;
               if (var8.hn < 11) {
                  var8.hn++;
               }

               var8.px = var8.x;
               var8.py = var8.y;
               var8.pz = var8.z;
               double var15 = (R.nextDouble() - 0.5) * 0.14;
               double var17 = Math.cos(var15);
               double var19 = Math.sin(var15);
               double var21 = var8.vx * var17 - var8.vz * var19;
               double var23 = var8.vx * var19 + var8.vz * var17;
               var8.vx = var21;
               var8.vz = var23;
               var8.vy = var8.vy + (R.nextDouble() - 0.5) * 0.02;
               var8.vy *= 0.97;
               var8.x = var8.x + var8.vx;
               var8.y = var8.y + var8.vy;
               var8.z = var8.z + var8.vz;
            } else {
               WORLD.remove(var7);
            }
         }

         int var25 = Math.min(3, var6 - WORLD.size());

         for (int var26 = 0; var26 < var25; var26++) {
            CometFx.Comet var27 = new CometFx.Comet();
            double var10 = R.nextDouble() * Math.PI * 2.0;
            double var12 = 4.0 + R.nextDouble() * 16.0;
            var27.x = var5.field_1352 + Math.cos(var10) * var12;
            var27.z = var5.field_1350 + Math.sin(var10) * var12;
            var27.y = var5.field_1351 + (R.nextDouble() - 0.35) * 9.0;
            double var14 = R.nextDouble() * Math.PI * 2.0;
            double var16 = 0.1 + R.nextDouble() * 0.2;
            var27.vx = Math.cos(var14) * var16;
            var27.vz = Math.sin(var14) * var16;
            var27.vy = (R.nextDouble() - 0.5) * 0.08;
            var27.px = var27.x;
            var27.py = var27.y;
            var27.pz = var27.z;
            var27.life = 50 + R.nextInt(60);
            var27.seed = R.nextInt(1000);
            WORLD.add(var27);
         }
      }
   }

   public static void renderWorld(class_4587 var0, Setting<?> var1, Setting<?> var2, Setting<?> var3) {
      if (!WORLD.isEmpty()) {
         class_310 var4 = class_310.method_1551();
         class_243 var5 = var4.field_1773.method_19418().method_19326();
         float var6 = Render3DEngine.getTickDelta();
         float var7 = ((Number)var1.getValue()).floatValue();
         begin();

         for (CometFx.Comet var9 : WORLD) {
            double var10 = Math.min(1.0, (double)var9.age / 8.0);
            double var12 = Math.min(1.0, (double)(var9.life - var9.age) / 10.0);
            double var14 = Math.max(0.0, Math.min(var10, var12));
            double var16 = var9.px + (var9.x - var9.px) * (double)var6 - var5.field_1352;
            double var18 = var9.py + (var9.y - var9.py) * (double)var6 - var5.field_1351;
            double var20 = var9.pz + (var9.z - var9.pz) * (double)var6 - var5.field_1350;
            int var22 = worldColor(var2, var3, var9.seed * 7 + var9.age * 3);
            int var23 = var9.hn;
            strip();

            for (int var24 = 0; var24 <= var23; var24++) {
               double var25;
               double var27;
               double var29;
               if (var24 == 0) {
                  var25 = var16;
                  var27 = var18;
                  var29 = var20;
               } else {
                  var25 = var9.hx[var24 - 1] - var5.field_1352;
                  var27 = var9.hy[var24 - 1] - var5.field_1351;
                  var29 = var9.hz[var24 - 1] - var5.field_1350;
               }

               float var31 = (float)var24 / (float)(var23 + 1);
               double var32 = Math.pow(1.0 - (double)var31, 1.5) * var14;
               float var34 = (float)(0.032 * (double)var7 * (1.0 - (double)var31 * 0.92));
               point(var25, var27, var29, var34, Math.min(255, (int)(255.0 * var32)) << 24 | var22);
            }

            int var35 = (int)(255.0 * var14);
            sprite(var16, var18, var20, 0.085F * var7, Math.min(255, (int)((double)var35 * 0.85)) << 24 | var22);
            sprite(var16, var18, var20, 0.03F * var7, var35 << 24 | 16777215);

            for (byte var36 = 3; var36 < var23; var36 += 4) {
               double var26 = (double)var36 / (double)(var23 + 1);
               double var28 = (1.0 - var26) * var14 * 0.55;
               sprite(
                  var9.hx[var36 - 1] - var5.field_1352,
                  var9.hy[var36 - 1] - var5.field_1351,
                  var9.hz[var36 - 1] - var5.field_1350,
                  0.02F * var7,
                  Math.min(255, (int)(255.0 * var28)) << 24 | 16777215
               );
            }
         }

         draw(var0, true);
      }
   }

   public static void hitStreak(class_4587 var0, float var1, float var2, float var3, float var4, float var5, float var6, Color var7, float var8) {
      class_310 var9 = class_310.method_1551();
      class_243 var10 = var9.field_1773.method_19418().method_19326();
      float var11 = Render3DEngine.getTickDelta();
      double var12 = (double)(var4 + (var1 - var4) * var11);
      double var14 = (double)(var5 + (var2 - var5) * var11) + 0.1;
      double var16 = (double)(var6 + (var3 - var6) * var11);
      double var18 = (double)(var1 - var4);
      double var20 = (double)(var2 - var5);
      double var22 = (double)(var3 - var6);
      double var24 = Math.sqrt(var18 * var18 + var20 * var20 + var22 * var22);
      double var26 = 7.0;
      double var28 = 1.6;
      if (var24 * var26 > var28) {
         var26 = var28 / var24;
      }

      float var30 = var8 / 3.0F;
      int var31 = var7.getRGB() & 16777215;
      int var32 = var7.getAlpha();
      if (var32 > 0) {
         begin();
         byte var33 = 12;
         strip();

         for (int var34 = 0; var34 <= var33; var34++) {
            double var35 = (double)var34 / (double)var33;
            double var37 = Math.pow(1.0 - var35, 1.4);
            point(
               var12 - var18 * var26 * var35 - var10.field_1352,
               var14 - var20 * var26 * var35 - var10.field_1351,
               var16 - var22 * var26 * var35 - var10.field_1350,
               (float)(0.032 * (double)var30 * (1.0 - var35 * 0.9)),
               Math.min(255, (int)((double)var32 * var37)) << 24 | var31
            );
         }

         double var40 = var12 - var10.field_1352;
         double var36 = var14 - var10.field_1351;
         double var38 = var16 - var10.field_1350;
         sprite(var40, var36, var38, 0.085F * var30, Math.min(255, (int)((double)var32 * 0.85)) << 24 | var31);
         sprite(var40, var36, var38, 0.03F * var30, var32 << 24 | 16777215);
         draw(var0, true);
      }
   }

   private static final class Comet {
      double x;
      double y;
      double z;
      double px;
      double py;
      double pz;
      double vx;
      double vy;
      double vz;
      final double[] hx = new double[11];
      final double[] hy = new double[11];
      final double[] hz = new double[11];
      int hn;
      int age;
      int life;
      int seed;
   }
}
