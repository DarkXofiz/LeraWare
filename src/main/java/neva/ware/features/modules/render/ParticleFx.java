package neva.ware.features.modules.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render3DEngine;

public final class ParticleFx {
   private static final String[] MODES = new String[]{"Aurora", "Vortex", "Galaxy", "Rain", "Embers", "Orbit", "Helix", "Bubbles", "Plasma", "Fireworks"};
   private static final Random R = new Random();
   private static final ArrayList<ParticleFx.P> L = new ArrayList<>();
   private static final ArrayList<ParticleFx.P> ADD = new ArrayList<>();
   private static String cur;
   private static long lastMs;
   private static long ticks;
   private static int seedCounter;
   private static double cx;
   private static double cy;
   private static double cz;
   private static double camX;
   private static double camY;
   private static double camZ;
   private static double rx;
   private static double rz;
   private static double ux;
   private static double uy;
   private static double uz;
   private static Setting<?> sLmode;
   private static Setting<?> sColor;
   private static int lastCount = 100;
   private static final float VH = 9.0F;

   private ParticleFx() {
   }

   private static int idOf(Object var0) {
      String var1 = String.valueOf(var0);

      for (int var2 = 0; var2 < MODES.length; var2++) {
         if (MODES[var2].equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   public static boolean physicsVisible(Object var0) {
      String var1 = String.valueOf(var0);
      return !"Off".equals(var1) && idOf(var1) < 0;
   }

   public static boolean tick(Setting<?> var0, Setting<?> var1, Setting<?> var2, Setting<?> var3, Setting<?> var4) {
      int var5 = idOf(var0.getValue());
      if (var5 < 0) {
         if (!L.isEmpty()) {
            L.clear();
         }

         cur = null;
         return false;
      } else {
         CometFx.clearWorld();
         class_310 var6 = class_310.method_1551();
         if (var6 != null && var6.field_1724 != null && var6.field_1687 != null) {
            class_746 var7 = var6.field_1724;
            long var8 = System.currentTimeMillis();
            String var10 = MODES[var5];
            double var11 = var7.method_23317();
            double var13 = var7.method_23318();
            double var15 = var7.method_23321();
            double var17 = (var11 - cx) * (var11 - cx) + (var15 - cz) * (var15 - cz);
            if (!var10.equals(cur) || var8 - lastMs > 1500L || var17 > 4096.0) {
               L.clear();
               cur = var10;
            }

            lastMs = var8;
            ticks++;
            cx = var11;
            cy = var13;
            cz = var15;
            sLmode = var3;
            sColor = var4;
            int var19 = Math.max(1, ((Number)var1.getValue()).intValue());
            lastCount = var19;
            switch (var5) {
               case 1:
                  tickVortex(var19);
                  break;
               case 2:
                  tickGalaxy(var19);
                  break;
               case 3:
                  tickRain(var19);
                  break;
               case 4:
                  tickEmbers(var19);
                  break;
               case 5:
                  tickOrbit(var19);
                  break;
               case 6:
                  tickHelix(var19);
                  break;
               case 7:
                  tickBubbles(var19);
                  break;
               case 8:
                  tickPlasma(var19);
                  break;
               case 9:
                  tickFireworks(var19);
            }

            if (!ADD.isEmpty()) {
               L.addAll(ADD);
               ADD.clear();
            }

            return true;
         } else {
            L.clear();
            return true;
         }
      }
   }

   public static boolean render(class_4587 var0, Setting<?> var1, Setting<?> var2, Setting<?> var3, Setting<?> var4) {
      int var5 = idOf(var1.getValue());
      if (var5 < 0) {
         return false;
      } else {
         class_310 var6 = class_310.method_1551();
         if (var6 != null && var6.field_1724 != null && var6.field_1773 != null) {
            class_746 var7 = var6.field_1724;
            class_4184 var8 = var6.field_1773.method_19418();
            if (var8 == null) {
               return true;
            } else {
               class_243 var9 = var8.method_19326();
               camX = var9.field_1352;
               camY = var9.field_1351;
               camZ = var9.field_1350;
               camVec(var8);
               sLmode = var3;
               sColor = var4;
               float var10 = Render3DEngine.getTickDelta();
               double var11 = var7.field_6014 + (var7.method_23317() - var7.field_6014) * (double)var10;
               double var13 = var7.field_6036 + (var7.method_23318() - var7.field_6036) * (double)var10;
               double var15 = var7.field_5969 + (var7.method_23321() - var7.field_5969) * (double)var10;
               float var17 = ((Number)var2.getValue()).floatValue();
               float var18 = (float)ticks + var10;
               CometFx.begin();
               switch (var5) {
                  case 0:
                     renderAurora(var11, var13, var15, var17, var18);
                     break;
                  case 1:
                     renderVortex(var11, var13, var15, var17, var18, var10);
                     break;
                  case 2:
                     renderGalaxy(var11, var13, var15, var17, var18);
                     break;
                  case 3:
                     renderRain(var17, var10);
                     break;
                  case 4:
                     renderEmbers(var17, var18, var10);
                     break;
                  case 5:
                     renderOrbit(var17, var18, var10);
                     break;
                  case 6:
                     renderHelix(var17, var18, var10);
                     break;
                  case 7:
                     renderBubbles(var17, var18, var10);
                     break;
                  case 8:
                     renderPlasma(var17, var18, var10);
                     break;
                  case 9:
                     renderFireworks(var17, var18, var10);
               }

               CometFx.draw(var0, true);
               return true;
            }
         } else {
            return true;
         }
      }
   }

   private static float rnd(float var0, float var1) {
      return var0 + R.nextFloat() * (var1 - var0);
   }

   private static float clamp01(float var0) {
      return var0 < 0.0F ? 0.0F : (var0 > 1.0F ? 1.0F : var0);
   }

   private static int col(int var0) {
      try {
         if ("Custom".equals(String.valueOf(sLmode.getValue()))) {
            ColorSetting var1 = (ColorSetting)sColor.getValue();
            return var1.getRed() << 16 | var1.getGreen() << 8 | var1.getBlue();
         } else {
            return HudEditor.getColor(var0).getRGB() & 16777215;
         }
      } catch (Throwable var2) {
         return 3650042;
      }
   }

   private static int mix(int var0, int var1, float var2) {
      var2 = clamp01(var2);
      int var3 = (int)((float)(var0 >> 16 & 0xFF) + (float)((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var2);
      int var4 = (int)((float)(var0 >> 8 & 0xFF) + (float)((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var2);
      int var5 = (int)((float)(var0 & 0xFF) + (float)((var1 & 0xFF) - (var0 & 0xFF)) * var2);
      return var3 << 16 | var4 << 8 | var5;
   }

   private static int argb(float var0, int var1) {
      int var2 = (int)(clamp01(var0) * 255.0F);
      return var2 << 24 | var1 & 16777215;
   }

   private static void spr(double var0, double var2, double var4, float var6, int var7) {
      if (var7 >>> 24 != 0 && !(var6 <= 0.0F)) {
         CometFx.sprite(var0 - camX, var2 - camY, var4 - camZ, var6, var7);
      }
   }

   private static void pt(double var0, double var2, double var4, float var6, int var7) {
      CometFx.point(var0 - camX, var2 - camY, var4 - camZ, var6, var7);
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

   private static float fade(int var0, int var1, float var2, float var3) {
      float var4 = var2 <= 0.0F ? 1.0F : clamp01((float)var0 / var2);
      float var5 = var3 <= 0.0F ? 1.0F : clamp01((float)(var1 - var0) / var3);
      return Math.min(var4, var5);
   }

   private static int countKind(int var0) {
      int var1 = 0;

      for (ParticleFx.P var3 : L) {
         if (var3.kind == var0) {
            var1++;
         }
      }

      return var1;
   }

   private static double distH(ParticleFx.P var0) {
      double var1 = var0.x - cx;
      double var3 = var0.z - cz;
      return Math.sqrt(var1 * var1 + var3 * var3);
   }

   private static void trail(ParticleFx.P var0, float var1, float var2, float var3, float var4, int var5) {
      if (var0.tn >= 1) {
         CometFx.strip();
         int var6 = var0.tn;

         for (int var7 = var6 - 1; var7 >= 0; var7--) {
            float var8 = 1.0F - ((float)var7 + 1.0F) / ((float)var6 + 1.0F);
            pt(var0.tx[var7], var0.ty[var7], var0.tz[var7], var2 + (var3 - var2) * var8, argb(var4 * var8 * var8, var5));
         }

         pt(var0.ix(var1), var0.iy(var1), var0.iz(var1), var3, argb(var4, var5));
      }
   }

   private static void renderAurora(double var0, double var2, double var4, float var6, float var7) {
      int var8 = 3 + Math.min(3, idxCount() / 150);
      byte var9 = 56;
      byte var10 = 8;

      for (int var11 = 0; var11 < var8; var11++) {
         double var12 = (double)var11 * 1.05 + 0.4 + Math.sin((double)var7 * 0.0015 + (double)var11) * 0.15;
         double var14 = Math.cos(var12);
         double var16 = Math.sin(var12);
         double var18 = -var16;
         double var20 = var14;
         double var22 = ((double)var11 - (double)(var8 - 1) / 2.0) * 11.0;
         double var24 = var2 + 24.0 + (double)var11 * 3.0 + Math.sin((double)var7 * 0.01 + (double)var11 * 1.7) * 1.2;
         double var26 = 110.0;

         for (int var28 = 0; var28 < var10; var28++) {
            float var29 = (float)var28 / (float)(var10 - 1);
            CometFx.strip();

            for (int var30 = 0; var30 <= var9; var30++) {
               float var31 = (float)var30 / (float)var9;
               double var32 = ((double)var31 - 0.5) * var26;
               double var34 = 7.0 * Math.sin((double)var31 * 3.3 + (double)var7 * 0.018 + (double)var11 * 1.3)
                  + 2.6 * Math.sin((double)var31 * 8.1 - (double)var7 * 0.031 + (double)var11 * 2.1);
               double var36 = var0 + var14 * var32 + var18 * (var22 + var34);
               double var38 = var4 + var16 * var32 + var20 * (var22 + var34);
               double var40 = 0.55
                  + 0.45
                     * Math.sin((double)(var31 * 46.0F) + (double)var7 * 0.07 + (double)var11 * 3.0)
                     * Math.sin((double)(var31 * 13.0F) - (double)var7 * 0.045);
               double var42 = var24 + (double)var28 * 1.55 * (0.8 + 0.4 * var40);
               float var44 = (float)Math.pow(Math.sin(Math.PI * (double)var31), 0.9);
               float var45 = (float)(Math.pow((double)(1.0F - var29), 1.6) * 0.5 * (double)var44 * (0.35 + 0.65 * var40));
               int var46 = col(var11 * 25 + (int)(var31 * 90.0F) + var28 * 4);
               if (var28 == 0) {
                  var46 = mix(var46, 16777215, 0.35F);
               }

               pt(var36, var42, var38, 1.05F * Math.max(0.3F, var6), argb(var45, var46));
            }
         }

         for (int var47 = 0; var47 < 12; var47++) {
            float var48 = ((float)var47 * 0.618F + (float)var11 * 0.27F) % 1.0F;
            float var49 = (var7 * 0.012F + (float)var47 * 0.37F + (float)var11) % 1.0F;
            double var50 = ((double)var48 - 0.5) * var26;
            double var33 = 7.0 * Math.sin((double)var48 * 3.3 + (double)var7 * 0.018 + (double)var11 * 1.3)
               + 2.6 * Math.sin((double)var48 * 8.1 - (double)var7 * 0.031 + (double)var11 * 2.1);
            double var35 = var0 + var14 * var50 + var18 * (var22 + var33);
            double var37 = var4 + var16 * var50 + var20 * (var22 + var33);
            double var39 = var24 - (double)var49 * 6.0;
            float var41 = (float)Math.sin(Math.PI * (double)var49) * (float)Math.sin(Math.PI * (double)var48) * 0.8F;
            spr(var35, var39, var37, 0.18F * var6, argb(var41, mix(col(var11 * 25 + var47 * 7), 16777215, 0.5F)));
         }
      }
   }

   private static int idxCount() {
      return lastCount;
   }

   private static float vRad(float var0, float var1) {
      return (float)((1.1 + 5.2 * Math.pow((double)(var0 / 9.0F), 1.35)) * (double)var1);
   }

   private static void tickVortex(int var0) {
      lastCount = var0;
      int var1 = Math.min(450, var0);
      Iterator var2 = L.iterator();

      while (var2.hasNext()) {
         ParticleFx.P var3 = (ParticleFx.P)var2.next();
         var3.d = var3.a;
         var3.e = var3.b;
         var3.b = var3.b + var3.c;
         var3.a = var3.a + var3.f / Math.max(0.6F, vRad(var3.b, var3.g));
         var3.age++;
         if (var3.b > 9.0F) {
            var2.remove();
         }
      }

      int var5 = Math.min(10, var1 - L.size());

      for (int var6 = 0; var6 < var5; var6++) {
         ParticleFx.P var4 = new ParticleFx.P(0);
         var4.a = rnd(0.0F, (float) (Math.PI * 2));
         var4.b = rnd(0.0F, 9.0F * (L.isEmpty() ? 0.9F : 0.15F));
         var4.c = rnd(0.035F, 0.07F);
         var4.f = rnd(0.22F, 0.34F);
         var4.g = rnd(0.75F, 1.25F);
         var4.d = var4.a;
         var4.e = var4.b;
         ADD.add(var4);
      }
   }

   private static void renderVortex(double var0, double var2, double var4, float var6, float var7, float var8) {
      double var9 = var2 - 0.4;

      for (ParticleFx.P var12 : L) {
         float var13 = var12.d + (var12.a - var12.d) * var8;
         float var14 = var12.e + (var12.b - var12.e) * var8;
         float var15 = Math.min(clamp01((float)var12.age / 10.0F), clamp01((9.0F - var14) / 2.5F));
         int var16 = col(var12.seed + var12.age * 2);
         CometFx.strip();

         for (int var17 = 7; var17 >= 0; var17--) {
            float var18 = Math.max(0.0F, var14 - (float)var17 * var12.c * 1.4F);
            float var19 = vRad(var18, var12.g);
            float var20 = var13 - (float)var17 * (var12.f / Math.max(0.6F, var19)) * 1.4F;
            float var21 = 1.0F - (float)var17 / 8.0F;
            pt(
               var0 + Math.cos((double)var20) * (double)var19,
               var9 + (double)var18,
               var4 + Math.sin((double)var20) * (double)var19,
               (0.02F + 0.035F * var21) * var6,
               argb(var15 * var21 * 0.85F, var16)
            );
         }

         float var25 = vRad(var14, var12.g);
         double var26 = var0 + Math.cos((double)var13) * (double)var25;
         double var27 = var4 + Math.sin((double)var13) * (double)var25;
         spr(var26, var9 + (double)var14, var27, 0.075F * var6, argb(var15, var16));
         spr(var26, var9 + (double)var14, var27, 0.03F * var6, argb(var15 * 0.9F, 16777215));
      }

      CometFx.strip();

      for (int var22 = 0; var22 <= 48; var22++) {
         double var23 = (double)var22 / 48.0 * Math.PI * 2.0 + (double)var7 * 0.05;
         float var24 = 1.2F + 0.08F * (float)Math.sin((double)var7 * 0.2 + (double)var22);
         pt(var0 + Math.cos(var23) * (double)var24, var9 + 0.05, var4 + Math.sin(var23) * (double)var24, 0.04F * var6, argb(0.45F, col(var22 * 4 + (int)var7)));
      }
   }

   private static void tickGalaxy(int var0) {
      lastCount = var0;
      int var1 = Math.min(1400, var0 * 3);
      if (L.size() > var1) {
         while (L.size() > var1) {
            L.remove(L.size() - 1);
         }
      }

      int var2 = Math.min(120, var1 - L.size());

      for (int var3 = 0; var3 < var2; var3++) {
         ParticleFx.P var4 = new ParticleFx.P(0);
         int var5 = R.nextInt(3);
         float var6 = (float)Math.pow((double)R.nextFloat(), 1.6);
         var4.a = (float)((double)var5 * Math.PI * 2.0 / 3.0);
         var4.b = Math.max(0.02F, var6);
         var4.c = (float)(R.nextGaussian() * 0.32);
         var4.d = (float)R.nextGaussian();
         var4.e = rnd(0.6F, 1.4F);
         var4.f = rnd(0.04F, 0.16F);
         var4.kind = R.nextFloat() < 0.12F ? 1 : 0;
         if (var4.kind == 1) {
            var4.a = rnd(0.0F, (float) (Math.PI * 2));
         }

         ADD.add(var4);
      }

      for (ParticleFx.P var8 : L) {
         var8.age++;
      }
   }

   private static void renderGalaxy(double var0, double var2, double var4, float var6, float var7) {
      double var8 = var0;
      double var10 = var2 + 30.0;
      double var12 = var4;
      double var14 = 30.0;
      double var16 = 0.32;
      double var18 = Math.cos(var16);
      double var20 = Math.sin(var16);

      for (ParticleFx.P var23 : L) {
         float var24 = var23.b;
         double var25 = (double)var23.a
            + (double)var24 * 5.6
            + (double)var23.c * (0.25 + 0.5 * (double)(1.0F - var24))
            - (double)var7 * 0.0035 / (0.22 + (double)var24);
         double var27 = (double)var24 * var14;
         double var29 = Math.cos(var25) * var27;
         double var31 = Math.sin(var25) * var27;
         double var33 = (double)(var23.d * (1.0F - var24)) * 2.2;
         double var35 = var33 * var18 - var31 * var20;
         double var37 = var33 * var20 + var31 * var18;
         float var39 = 0.55F + 0.45F * (float)Math.sin((double)(var7 * var23.f + (float)var23.seed));
         int var40 = mix(col(var23.seed % 60 + (int)(var24 * 80.0F)), 16773596, (float)Math.pow((double)(1.0F - var24), 2.2));
         float var41 = (0.14F + 0.2F * (1.0F - var24)) * var23.e * var6;
         float var42 = (var23.kind == 1 ? 0.35F : 0.85F) * var39 * clamp01((float)var23.age / 20.0F);
         spr(var8 + var29, var10 + var35, var12 + var37, var41, argb(var42, var40));
      }

      float var43 = 0.85F + 0.15F * (float)Math.sin((double)var7 * 0.05);
      spr(var8, var10, var12, 2.6F * var6, argb(0.55F * var43, 16773596));
      spr(var8, var10, var12, 6.5F * var6, argb(0.22F, col(0)));
      spr(var8, var10, var12, 12.0F * var6, argb(0.08F, col(40)));
   }

   private static void tickRain(int var0) {
      lastCount = var0;
      int var1 = Math.min(1200, var0 * 3);
      double var2 = 0.12 * Math.sin((double)ticks * 0.009);
      double var4 = 0.09 * Math.cos((double)ticks * 0.0127);
      Iterator var6 = L.iterator();

      while (var6.hasNext()) {
         ParticleFx.P var7 = (ParticleFx.P)var6.next();
         var7.vx = var7.vx + (var2 - var7.vx) * 0.05;
         var7.vz = var7.vz + (var4 - var7.vz) * 0.05;
         var7.move();
         var7.age++;
         if (var7.y < cy - 5.0 || var7.age > var7.life || distH(var7) > 36.0) {
            var6.remove();
         }
      }

      int var13 = Math.min(Math.max(4, var1 / 18), var1 - L.size());

      for (int var14 = 0; var14 < var13; var14++) {
         ParticleFx.P var8 = new ParticleFx.P(0);
         double var9 = R.nextDouble() * Math.PI * 2.0;
         double var11 = Math.sqrt(R.nextDouble()) * 28.0;
         var8.x = var8.px = cx + Math.cos(var9) * var11;
         var8.z = var8.pz = cz + Math.sin(var9) * var11;
         var8.y = var8.py = cy + (double)rnd(L.isEmpty() ? -3.0F : 10.0F, 24.0F);
         var8.vx = var2;
         var8.vz = var4;
         var8.vy = (double)rnd(-1.15F, -0.8F);
         var8.life = 70;
         ADD.add(var8);
      }
   }

   private static void renderRain(float var0, float var1) {
      for (ParticleFx.P var3 : L) {
         double var4 = var3.ix(var1);
         double var6 = var3.iy(var1);
         double var8 = var3.iz(var1);
         float var10 = Math.min(clamp01((float)var3.age / 4.0F), clamp01((float)(var6 - (cy - 5.0)) / 4.0F)) * 0.8F;
         int var11 = mix(col(var3.seed % 90), 16777215, 0.25F);
         CometFx.strip();
         pt(var4 - var3.vx * 2.4, var6 - var3.vy * 2.4, var8 - var3.vz * 2.4, 0.012F * var0, argb(0.0F, var11));
         pt(var4 - var3.vx * 1.1, var6 - var3.vy * 1.1, var8 - var3.vz * 1.1, 0.018F * var0, argb(var10 * 0.45F, var11));
         pt(var4, var6, var8, 0.022F * var0, argb(var10, var11));
         spr(var4, var6, var8, 0.04F * var0, argb(var10 * 0.7F, var11));
      }
   }

   private static void tickEmbers(int var0) {
      lastCount = var0;
      int var1 = Math.min(600, var0 * 2);
      Iterator var2 = L.iterator();

      while (var2.hasNext()) {
         ParticleFx.P var3 = (ParticleFx.P)var2.next();
         var3.vx = var3.vx + Math.sin(var3.y * 1.7 + (double)var3.seed) * 0.0035 + (double)rnd(-0.003F, 0.003F);
         var3.vz = var3.vz + Math.cos(var3.x * 1.3 + (double)var3.seed * 0.5) * 0.0035 + (double)rnd(-0.003F, 0.003F);
         var3.vx *= 0.955;
         var3.vz *= 0.955;
         var3.vy = Math.min(0.12, var3.vy * 0.992 + 0.0014);
         var3.push();
         var3.move();
         var3.age++;
         if (var3.age >= var3.life || distH(var3) > 40.0) {
            var2.remove();
         }
      }

      int var9 = Math.min(8, var1 - L.size());

      for (int var10 = 0; var10 < var9; var10++) {
         ParticleFx.P var4 = new ParticleFx.P(6);
         double var5 = R.nextDouble() * Math.PI * 2.0;
         double var7 = 2.5 + Math.sqrt(R.nextDouble()) * 16.0;
         var4.x = var4.px = cx + Math.cos(var5) * var7;
         var4.z = var4.pz = cz + Math.sin(var5) * var7;
         var4.y = var4.py = cy + (double)rnd(-1.2F, 1.5F) + (double)(L.isEmpty() ? rnd(0.0F, 6.0F) : 0.0F);
         var4.vx = (double)rnd(-0.02F, 0.02F);
         var4.vz = (double)rnd(-0.02F, 0.02F);
         var4.vy = (double)rnd(0.025F, 0.07F);
         var4.life = (int)rnd(55.0F, 120.0F);
         ADD.add(var4);
      }
   }

   private static void renderEmbers(float var0, float var1, float var2) {
      for (ParticleFx.P var4 : L) {
         float var5 = ((float)var4.age + var2) / (float)var4.life;
         float var6 = 1.0F - var5;
         int var7 = col(var4.seed % 90);
         int var8 = mix(var7, 16774352, (var6 - 0.5F) * 1.8F);
         if (var6 < 0.35F) {
            var8 = mix(var7, 2099200, (0.35F - var6) * 1.6F);
         }

         float var9 = 0.65F + 0.35F * (float)Math.sin((double)var1 * 0.9 + (double)var4.seed);
         float var10 = clamp01((float)var4.age / 6.0F) * (float)Math.pow((double)var6, 0.7) * var9;
         trail(var4, var2, 0.006F * var0, 0.028F * var0, var10 * 0.8F, var8);
         float var11 = (0.045F + 0.05F * var6) * var0;
         double var12 = var4.ix(var2);
         double var14 = var4.iy(var2);
         double var16 = var4.iz(var2);
         spr(var12, var14, var16, var11, argb(var10, var8));
         spr(var12, var14, var16, var11 * 0.4F, argb(var10 * var6, 16777215));
      }
   }

   private static void tickOrbit(int var0) {
      lastCount = var0;
      int var1 = Math.max(2, Math.min(18, var0 / 22));
      Iterator var2 = L.iterator();

      while (var2.hasNext()) {
         ParticleFx.P var3 = (ParticleFx.P)var2.next();
         var3.vx = var3.vx * 0.98 + (double)rnd(-0.0015F, 0.0015F);
         var3.vy = var3.vy * 0.98 + (double)rnd(-0.001F, 0.001F) + (cy + 3.5 - var3.y) * 4.0E-4;
         var3.vz = var3.vz * 0.98 + (double)rnd(-0.0015F, 0.0015F);
         var3.move();
         var3.age++;
         if (var3.age >= var3.life || distH(var3) > 40.0) {
            var2.remove();
         }
      }

      if (L.size() < var1 && (R.nextFloat() < 0.15F || L.isEmpty())) {
         int var9 = L.isEmpty() ? var1 : 1;

         for (int var10 = 0; var10 < var9; var10++) {
            ParticleFx.P var4 = new ParticleFx.P(0);
            double var5 = R.nextDouble() * Math.PI * 2.0;
            double var7 = 5.0 + R.nextDouble() * 17.0;
            var4.x = var4.px = cx + Math.cos(var5) * var7;
            var4.z = var4.pz = cz + Math.sin(var5) * var7;
            var4.y = var4.py = cy + (double)rnd(1.2F, 7.0F);
            var4.kind = R.nextFloat() < 0.5F ? 2 : 3;
            var4.a = rnd(0.85F, 1.6F);
            var4.b = rnd(0.0F, (float) Math.PI);
            var4.c = rnd(0.0F, (float) (Math.PI * 2));
            var4.d = rnd(0.12F, 0.2F) * (float)(R.nextBoolean() ? 1 : -1);
            var4.life = (int)rnd(320.0F, 640.0F);
            ADD.add(var4);
         }
      }
   }

   private static void renderOrbit(float var0, float var1, float var2) {
      for (ParticleFx.P var4 : L) {
         float var5 = fade(var4.age, var4.life, 30.0F, 30.0F);
         double var6 = var4.ix(var2);
         double var8 = var4.iy(var2);
         double var10 = var4.iz(var2);
         int var12 = col(var4.seed % 90);
         float var13 = 0.8F + 0.2F * (float)Math.sin((double)var1 * 0.12 + (double)var4.seed);
         spr(var6, var8, var10, 0.32F * var0 * var13, argb(0.75F * var5, var12));
         spr(var6, var8, var10, 0.11F * var0, argb(0.95F * var5, 16777215));

         for (int var14 = 0; var14 < var4.kind; var14++) {
            double var15 = (double)var4.b + (double)var14 * (Math.PI / (double)var4.kind);
            double var17 = (double)var4.c + (double)var14 * 1.3;
            double var19 = Math.sin(var15) * Math.cos(var17);
            double var21 = Math.cos(var15);
            double var23 = Math.sin(var15) * Math.sin(var17);
            double var25 = Math.abs(var21) < 0.9 ? 0.0 : 1.0;
            double var27 = Math.abs(var21) < 0.9 ? 1.0 : 0.0;
            double var29 = var21 * 0.0 - var23 * var27;
            double var31 = var23 * var25 - var19 * 0.0;
            double var33 = var19 * var27 - var21 * var25;
            double var35 = Math.sqrt(var29 * var29 + var31 * var31 + var33 * var33);
            var29 /= var35;
            var31 /= var35;
            var33 /= var35;
            double var37 = var21 * var33 - var23 * var31;
            double var39 = var23 * var29 - var19 * var33;
            double var41 = var19 * var31 - var21 * var29;
            double var43 = (double)var4.a * (1.0 + (double)var14 * 0.18);
            int var45 = col(var4.seed % 90 + 30 + var14 * 25);
            CometFx.strip();

            for (int var46 = 0; var46 <= 40; var46++) {
               double var47 = (double)var46 / 40.0 * Math.PI * 2.0;
               pt(
                  var6 + var43 * (Math.cos(var47) * var29 + Math.sin(var47) * var37),
                  var8 + var43 * (Math.cos(var47) * var31 + Math.sin(var47) * var39),
                  var10 + var43 * (Math.cos(var47) * var33 + Math.sin(var47) * var41),
                  0.01F * var0,
                  argb(0.16F * var5, var45)
               );
            }

            double var57 = (double)(var1 * var4.d) * (1.0 + 0.2 * (double)var14) + (double)var14 * 2.1 + (double)var4.seed;
            CometFx.strip();

            for (int var48 = 22; var48 >= 0; var48--) {
               double var49 = var57 - (double)var48 * 0.11 * (double)Math.signum(var4.d);
               float var51 = 1.0F - (float)var48 / 22.0F;
               pt(
                  var6 + var43 * (Math.cos(var49) * var29 + Math.sin(var49) * var37),
                  var8 + var43 * (Math.cos(var49) * var31 + Math.sin(var49) * var39),
                  var10 + var43 * (Math.cos(var49) * var33 + Math.sin(var49) * var41),
                  (0.008F + 0.04F * var51) * var0,
                  argb(var5 * var51 * var51 * 0.9F, var45)
               );
            }

            double var58 = var6 + var43 * (Math.cos(var57) * var29 + Math.sin(var57) * var37);
            double var50 = var8 + var43 * (Math.cos(var57) * var31 + Math.sin(var57) * var39);
            double var52 = var10 + var43 * (Math.cos(var57) * var33 + Math.sin(var57) * var41);
            spr(var58, var50, var52, 0.1F * var0, argb(var5, var45));
            spr(var58, var50, var52, 0.04F * var0, argb(var5, 16777215));
         }
      }
   }

   private static void tickHelix(int var0) {
      lastCount = var0;
      int var1 = Math.max(2, Math.min(14, var0 / 28));
      Iterator var2 = L.iterator();

      while (var2.hasNext()) {
         ParticleFx.P var3 = (ParticleFx.P)var2.next();
         var3.move();
         var3.age++;
         if (var3.age >= var3.life || distH(var3) > 40.0) {
            var2.remove();
         }
      }

      if (L.size() < var1 && (R.nextFloat() < 0.08F || L.isEmpty())) {
         int var9 = L.isEmpty() ? var1 : 1;

         for (int var10 = 0; var10 < var9; var10++) {
            ParticleFx.P var4 = new ParticleFx.P(0);
            double var5 = R.nextDouble() * Math.PI * 2.0;
            double var7 = 6.0 + R.nextDouble() * 16.0;
            var4.x = var4.px = cx + Math.cos(var5) * var7;
            var4.z = var4.pz = cz + Math.sin(var5) * var7;
            var4.y = var4.py = cy - 0.5 + (double)rnd(-0.5F, 2.0F);
            var4.vy = (double)rnd(0.002F, 0.008F);
            var4.a = rnd(4.5F, 7.5F);
            var4.b = rnd(0.5F, 0.8F);
            var4.c = rnd(0.04F, 0.07F) * (float)(R.nextBoolean() ? 1 : -1);
            var4.life = (int)rnd(400.0F, 760.0F);
            ADD.add(var4);
         }
      }
   }

   private static void renderHelix(float var0, float var1, float var2) {
      byte var3 = 44;
      double[] var4 = new double[var3 + 1];
      double[] var5 = new double[var3 + 1];
      double[] var6 = new double[var3 + 1];
      double[] var7 = new double[var3 + 1];
      double[] var8 = new double[var3 + 1];

      for (ParticleFx.P var10 : L) {
         float var11 = fade(var10.age, var10.life, 40.0F, 40.0F);
         double var12 = var10.ix(var2);
         double var14 = var10.iy(var2);
         double var16 = var10.iz(var2);

         for (int var18 = 0; var18 <= var3; var18++) {
            double var19 = (double)var18 / (double)var3 * (double)var10.a;
            double var21 = var19 * 1.55 + (double)(var1 * var10.c) + (double)var10.seed;
            var4[var18] = var12 + Math.cos(var21) * (double)var10.b;
            var6[var18] = var16 + Math.sin(var21) * (double)var10.b;
            var7[var18] = var12 - Math.cos(var21) * (double)var10.b;
            var8[var18] = var16 - Math.sin(var21) * (double)var10.b;
            var5[var18] = var14 + var19;
         }

         int var24 = col(var10.seed % 90);
         int var25 = col(var10.seed % 90 + 45);

         for (int var20 = 0; var20 < 2; var20++) {
            CometFx.strip();

            for (int var28 = 0; var28 <= var3; var28++) {
               float var22 = (float)Math.pow(Math.sin(Math.PI * (double)var28 / (double)var3), 0.6) * var11;
               int var23 = var20 == 0 ? col(var10.seed % 90 + var28 * 2) : col(var10.seed % 90 + 45 + var28 * 2);
               pt(var20 == 0 ? var4[var28] : var7[var28], var5[var28], var20 == 0 ? var6[var28] : var8[var28], 0.035F * var0, argb(var22 * 0.85F, var23));
            }
         }

         for (byte var26 = 2; var26 < var3; var26 += 4) {
            float var29 = (float)Math.pow(Math.sin(Math.PI * (double)var26 / (double)var3), 0.6) * var11;
            CometFx.strip();

            for (int var31 = 0; var31 <= 4; var31++) {
               float var33 = (float)var31 / 4.0F;
               pt(
                  var4[var26] + (var7[var26] - var4[var26]) * (double)var33,
                  var5[var26],
                  var6[var26] + (var8[var26] - var6[var26]) * (double)var33,
                  0.014F * var0,
                  argb(var29 * 0.4F, mix(var24, var25, var33))
               );
            }

            spr(var4[var26], var5[var26], var6[var26], 0.085F * var0, argb(var29, var24));
            spr(var7[var26], var5[var26], var8[var26], 0.085F * var0, argb(var29, var25));
         }

         float var27 = (var1 * 0.012F + (float)(var10.seed % 100) / 100.0F) % 1.0F;
         int var30 = Math.min(var3, (int)(var27 * (float)var3));
         float var32 = (float)Math.sin(Math.PI * (double)var27) * var11;
         spr(var4[var30], var5[var30], var6[var30], 0.16F * var0, argb(var32, 16777215));
         spr(var7[var30], var5[var30], var8[var30], 0.16F * var0, argb(var32, 16777215));
      }
   }

   private static void tickBubbles(int var0) {
      lastCount = var0;
      int var1 = Math.min(450, (int)((float)var0 * 1.5F));
      Iterator var2 = L.iterator();

      while (var2.hasNext()) {
         ParticleFx.P var3 = (ParticleFx.P)var2.next();
         var3.age++;
         if (var3.kind == 0) {
            var3.vx = Math.sin((double)var3.age * 0.15 + (double)var3.seed) * 0.012;
            var3.vz = Math.cos((double)var3.age * 0.13 + (double)var3.seed) * 0.012;
            var3.move();
            if (var3.age >= var3.life) {
               var3.kind = 1;
               var3.age = 0;
               var3.life = 9;
               var3.px = var3.x;
               var3.py = var3.y;
               var3.pz = var3.z;
            }
         } else if (var3.age >= var3.life) {
            var2.remove();
            continue;
         }

         if (distH(var3) > 40.0) {
            var2.remove();
         }
      }

      int var10 = countKind(0);
      int var11 = Math.min(6, var1 - var10);

      for (int var4 = 0; var4 < var11; var4++) {
         ParticleFx.P var5 = new ParticleFx.P(0);
         double var6 = R.nextDouble() * Math.PI * 2.0;
         double var8 = 2.0 + Math.sqrt(R.nextDouble()) * 18.0;
         var5.x = var5.px = cx + Math.cos(var6) * var8;
         var5.z = var5.pz = cz + Math.sin(var6) * var8;
         var5.y = var5.py = cy + (double)rnd(-2.0F, 1.0F) + (double)(L.isEmpty() ? rnd(0.0F, 6.0F) : 0.0F);
         var5.vy = (double)rnd(0.018F, 0.045F);
         var5.a = rnd(0.08F, 0.3F);
         var5.life = (int)rnd(80.0F, 210.0F);
         ADD.add(var5);
      }
   }

   private static void renderBubbles(float var0, float var1, float var2) {
      for (ParticleFx.P var4 : L) {
         double var5 = var4.ix(var2);
         double var7 = var4.iy(var2);
         double var9 = var4.iz(var2);
         float var11 = var4.a * var0;
         int var12 = mix(col(var4.seed % 90), 16777215, 0.3F);
         if (var4.kind == 0) {
            float var25 = fade(var4.age, var4.life + 1, 8.0F, 0.0F);
            float var26 = 1.0F + 0.06F * (float)Math.sin((double)var1 * 0.4 + (double)var4.seed);
            CometFx.strip();

            for (int var27 = 0; var27 <= 20; var27++) {
               double var30 = (double)var27 / 20.0 * Math.PI * 2.0;
               double var18 = Math.cos(var30) * (double)var11 * (double)var26;
               double var20 = Math.sin(var30) * (double)var11 / (double)var26;
               float var22 = 0.55F + 0.45F * (float)Math.sin(var30 + 2.3);
               pt(var5 + var18 * rx + var20 * ux, var7 + var20 * uy, var9 + var18 * rz + var20 * uz, 0.012F * var0, argb(0.75F * var25 * var22, var12));
            }

            spr(var5, var7, var9, var11 * 0.95F, argb(0.07F * var25, var12));
            double var28 = -0.38 * (double)var11;
            double var32 = 0.42 * (double)var11;
            spr(var5 + var28 * rx + var32 * ux, var7 + var32 * uy, var9 + var28 * rz + var32 * uz, var11 * 0.2F, argb(0.85F * var25, 16777215));
         } else {
            float var13 = clamp01(((float)var4.age + var2) / (float)var4.life);
            float var14 = var11 * (1.0F + var13 * 1.3F);
            float var15 = (1.0F - var13) * 0.8F;
            CometFx.strip();

            for (int var16 = 0; var16 <= 20; var16++) {
               double var17 = (double)var16 / 20.0 * Math.PI * 2.0;
               double var19 = Math.cos(var17) * (double)var14;
               double var21 = Math.sin(var17) * (double)var14;
               pt(var5 + var19 * rx + var21 * ux, var7 + var21 * uy, var9 + var19 * rz + var21 * uz, 0.01F * var0 * (1.0F - var13), argb(var15, var12));
            }

            for (int var29 = 0; var29 < 7; var29++) {
               double var31 = (double)var29 / 7.0 * Math.PI * 2.0 + (double)var4.seed;
               double var33 = (double)var11 * (1.0 + (double)var13 * 2.4);
               double var34 = Math.cos(var31) * var33;
               double var23 = Math.sin(var31) * var33 - (double)(var13 * var13) * 0.3;
               spr(var5 + var34 * rx + var23 * ux, var7 + var23 * uy, var9 + var34 * rz + var23 * uz, 0.035F * var0, argb(var15, var12));
            }
         }
      }
   }

   private static void buildArc(ParticleFx.P var0, double var1, double var3, double var5, double var7, double var9, double var11, double var13) {
      byte var15 = 17;
      var0.tx = new double[var15];
      var0.ty = new double[var15];
      var0.tz = new double[var15];
      var0.tn = var15;
      var0.tx[0] = var1;
      var0.ty[0] = var3;
      var0.tz[0] = var5;
      var0.tx[var15 - 1] = var7;
      var0.ty[var15 - 1] = var9;
      var0.tz[var15 - 1] = var11;
      double var16 = Math.sqrt((var7 - var1) * (var7 - var1) + (var9 - var3) * (var9 - var3) + (var11 - var5) * (var11 - var5));
      double var18 = var16 * var13;

      for (int var20 = var15 - 1; var20 >= 2; var20 /= 2) {
         for (int var21 = 0; var21 + var20 < var15; var21 += var20) {
            int var22 = var21 + var20 / 2;
            var0.tx[var22] = (var0.tx[var21] + var0.tx[var21 + var20]) / 2.0 + R.nextGaussian() * var18;
            var0.ty[var22] = (var0.ty[var21] + var0.ty[var21 + var20]) / 2.0 + R.nextGaussian() * var18;
            var0.tz[var22] = (var0.tz[var21] + var0.tz[var21 + var20]) / 2.0 + R.nextGaussian() * var18;
         }

         var18 *= 0.55;
      }
   }

   private static void tickPlasma(int var0) {
      lastCount = var0;
      int var1 = Math.max(3, Math.min(22, var0 / 18));
      ArrayList<ParticleFx.P> var2 = new ArrayList<>();
      Iterator var3 = L.iterator();

      while (var3.hasNext()) {
         ParticleFx.P var4 = (ParticleFx.P)var3.next();
         var4.age++;
         if (var4.kind == 0) {
            var4.vx = var4.vx * 0.97 + (double)rnd(-0.003F, 0.003F);
            var4.vy = var4.vy * 0.97 + (double)rnd(-0.002F, 0.002F) + (cy + 3.0 - var4.y) * 5.0E-4;
            var4.vz = var4.vz * 0.97 + (double)rnd(-0.003F, 0.003F);
            var4.move();
            if (var4.age < var4.life && !(distH(var4) > 40.0)) {
               var2.add(var4);
            } else {
               var3.remove();
            }
         } else if (var4.age >= var4.life) {
            var3.remove();
         } else if (var4.tn > 1 && R.nextFloat() < 0.7F) {
            buildArc(var4, var4.tx[0], var4.ty[0], var4.tz[0], var4.tx[var4.tn - 1], var4.ty[var4.tn - 1], var4.tz[var4.tn - 1], (double)var4.a);
         }
      }

      if (var2.size() < var1 && (R.nextFloat() < 0.12F || var2.isEmpty())) {
         int var21 = var2.isEmpty() ? var1 : 1;

         for (int var23 = 0; var23 < var21; var23++) {
            ParticleFx.P var5 = new ParticleFx.P(0);
            double var6 = R.nextDouble() * Math.PI * 2.0;
            double var8 = 4.0 + R.nextDouble() * 16.0;
            var5.x = var5.px = cx + Math.cos(var6) * var8;
            var5.z = var5.pz = cz + Math.sin(var6) * var8;
            var5.y = var5.py = cy + (double)rnd(0.8F, 6.0F);
            var5.life = (int)rnd(400.0F, 800.0F);
            ADD.add(var5);
         }
      }

      for (ParticleFx.P var24 : var2) {
         if (var24.age >= 20 && var24.life - var24.age >= 20 && !(R.nextFloat() > 0.22F)) {
            ParticleFx.P var25 = new ParticleFx.P(0);
            var25.kind = 1;
            var25.life = (int)rnd(3.0F, 7.0F);
            var25.seed = var24.seed;
            ParticleFx.P var26 = null;
            if (R.nextFloat() < 0.35F) {
               for (ParticleFx.P var28 : var2) {
                  if (var28 != var24) {
                     double var9 = var28.x - var24.x;
                     double var11 = var28.y - var24.y;
                     double var13 = var28.z - var24.z;
                     if (var9 * var9 + var11 * var11 + var13 * var13 < 121.0) {
                        var26 = var28;
                        break;
                     }
                  }
               }
            }

            double var27;
            double var29;
            double var30;
            if (var26 != null) {
               var27 = var26.x;
               var29 = var26.y;
               var30 = var26.z;
               var25.a = 0.13F;
            } else {
               double var31 = R.nextDouble() * Math.PI * 2.0;
               double var15 = R.nextDouble() * Math.PI - (Math.PI / 2);
               double var17 = (double)rnd(1.5F, 4.2F);
               var27 = var24.x + Math.cos(var31) * Math.cos(var15) * var17;
               var29 = var24.y + Math.sin(var15) * var17;
               var30 = var24.z + Math.sin(var31) * Math.cos(var15) * var17;
               var25.a = 0.2F;
            }

            buildArc(var25, var24.x, var24.y, var24.z, var27, var29, var30, (double)var25.a);
            ADD.add(var25);
            if (R.nextFloat() < 0.5F) {
               ParticleFx.P var32 = new ParticleFx.P(0);
               var32.kind = 1;
               var32.life = var25.life;
               var32.seed = var24.seed;
               var32.a = 0.25F;
               int var14 = 4 + R.nextInt(8);
               double var33 = var25.tx[var14];
               double var34 = var25.ty[var14];
               double var19 = var25.tz[var14];
               buildArc(
                  var32,
                  var33,
                  var34,
                  var19,
                  var33 + (double)rnd(-1.6F, 1.6F),
                  var34 + (double)rnd(-1.6F, 1.6F),
                  var19 + (double)rnd(-1.6F, 1.6F),
                  (double)var32.a
               );
               ADD.add(var32);
            }
         }
      }
   }

   private static void renderPlasma(float var0, float var1, float var2) {
      for (ParticleFx.P var4 : L) {
         if (var4.kind == 0) {
            float var19 = fade(var4.age, var4.life, 25.0F, 25.0F);
            double var20 = var4.ix(var2);
            double var22 = var4.iy(var2);
            double var10 = var4.iz(var2);
            int var12 = col(var4.seed % 90);
            float var13 = 0.75F + 0.25F * (float)Math.sin((double)var1 * 0.35 + (double)var4.seed);
            spr(var20, var22, var10, 0.7F * var0 * var13, argb(0.35F * var19, var12));
            spr(var20, var22, var10, 0.22F * var0, argb(0.9F * var19, mix(var12, 16777215, 0.5F)));
            spr(var20, var22, var10, 0.08F * var0, argb(var19, 16777215));

            for (int var14 = 0; var14 < 3; var14++) {
               double var15 = (double)var1 * 0.25 + (double)var14 * 2.09 + (double)var4.seed;
               double var17 = 0.35 * (double)var0;
               spr(
                  var20 + Math.cos(var15) * var17,
                  var22 + Math.sin(var15 * 1.3) * var17 * 0.6,
                  var10 + Math.sin(var15) * var17,
                  0.04F * var0,
                  argb(0.8F * var19, var12)
               );
            }
         } else if (var4.tn > 1) {
            float var5 = clamp01(((float)var4.age + var2) / (float)var4.life);
            float var6 = (1.0F - var5) * (0.75F + 0.25F * R.nextFloat());
            int var7 = col(var4.seed % 90);
            CometFx.strip();

            for (int var8 = 0; var8 < var4.tn; var8++) {
               pt(var4.tx[var8], var4.ty[var8], var4.tz[var8], 0.035F * var0, argb(var6 * 0.8F, var7));
            }

            CometFx.strip();

            for (int var21 = 0; var21 < var4.tn; var21++) {
               pt(var4.tx[var21], var4.ty[var21], var4.tz[var21], 0.012F * var0, argb(var6, 16777215));
            }

            spr(var4.tx[var4.tn - 1], var4.ty[var4.tn - 1], var4.tz[var4.tn - 1], 0.12F * var0, argb(var6, mix(var7, 16777215, 0.4F)));
         }
      }
   }

   private static void tickFireworks(int var0) {
      lastCount = var0;
      Iterator var1 = L.iterator();

      while (var1.hasNext()) {
         ParticleFx.P var2 = (ParticleFx.P)var1.next();
         var2.age++;
         if (var2.kind == 0) {
            var2.push();
            var2.vy = var2.vy * 0.97 - 0.008;
            var2.vx = var2.vx + (double)rnd(-0.004F, 0.004F);
            var2.vz = var2.vz + (double)rnd(-0.004F, 0.004F);
            var2.move();
            if (var2.age >= var2.life) {
               explode(var2);
               var1.remove();
            }
         } else if (var2.kind == 1) {
            var2.push();
            var2.vx *= 0.915;
            var2.vy = var2.vy * 0.915 - 0.011;
            var2.vz *= 0.915;
            var2.move();
            if (var2.age >= var2.life) {
               var1.remove();
            }
         } else if (var2.age >= var2.life) {
            var1.remove();
         }
      }

      int var8 = L.size();
      float var9 = Math.min(0.5F, (float)var0 / 700.0F);
      if (var8 < 2600 && R.nextFloat() < var9) {
         ParticleFx.P var3 = new ParticleFx.P(7);
         double var4 = R.nextDouble() * Math.PI * 2.0;
         double var6 = 7.0 + R.nextDouble() * 24.0;
         var3.x = var3.px = cx + Math.cos(var4) * var6;
         var3.z = var3.pz = cz + Math.sin(var4) * var6;
         var3.y = var3.py = cy - 1.0;
         var3.vy = (double)rnd(0.62F, 0.85F);
         var3.vx = (double)rnd(-0.04F, 0.04F);
         var3.vz = (double)rnd(-0.04F, 0.04F);
         var3.life = (int)rnd(18.0F, 28.0F);
         var3.e = (float)R.nextInt(1000);
         ADD.add(var3);
      }
   }

   private static void explode(ParticleFx.P var0) {
      int var1 = (int)var0.e;
      int var2 = (int)rnd(45.0F, 75.0F);
      boolean var3 = R.nextFloat() < 0.25F;
      double var4 = R.nextGaussian();
      double var6 = R.nextGaussian() * 0.3 + 1.0;
      double var8 = R.nextGaussian();
      double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
      var4 /= var10;
      var6 /= var10;
      var8 /= var10;
      float var12 = rnd(0.3F, 0.42F);

      for (int var13 = 0; var13 < var2; var13++) {
         ParticleFx.P var14 = new ParticleFx.P(6);
         var14.kind = 1;
         var14.x = var14.px = var0.x;
         var14.y = var14.py = var0.y;
         var14.z = var14.pz = var0.z;
         double var15 = R.nextGaussian();
         double var17 = R.nextGaussian();
         double var19 = R.nextGaussian();
         if (var3) {
            double var21 = var15 * var4 + var17 * var6 + var19 * var8;
            var15 -= var4 * var21;
            var17 -= var6 * var21;
            var19 -= var8 * var21;
         }

         double var28 = Math.sqrt(var15 * var15 + var17 * var17 + var19 * var19);
         if (var28 < 1.0E-4) {
            var28 = 1.0;
         }

         float var23 = var12 * (var3 ? 1.0F : rnd(0.75F, 1.0F));
         var14.vx = var15 / var28 * (double)var23 + var0.vx * 0.3;
         var14.vy = var17 / var28 * (double)var23 + var0.vy * 0.3;
         var14.vz = var19 / var28 * (double)var23 + var0.vz * 0.3;
         var14.life = (int)rnd(26.0F, 46.0F);
         var14.e = (float)(var1 + var13 % 3 * 12);
         var14.f = R.nextFloat() < 0.35F ? 1.0F : 0.0F;
         ADD.add(var14);
      }

      ParticleFx.P var27 = new ParticleFx.P(0);
      var27.kind = 2;
      var27.x = var27.px = var0.x;
      var27.y = var27.py = var0.y;
      var27.z = var27.pz = var0.z;
      var27.life = 7;
      var27.e = (float)var1;
      ADD.add(var27);
   }

   private static void renderFireworks(float var0, float var1, float var2) {
      boolean var3 = "Custom".equals(String.valueOf(sLmode.getValue()));

      for (ParticleFx.P var5 : L) {
         if (var5.kind == 0) {
            int var6 = 16767392;
            trail(var5, var2, 0.004F * var0, 0.03F * var0, 0.8F, var6);
            spr(var5.ix(var2), var5.iy(var2), var5.iz(var2), 0.08F * var0, argb(1.0F, 16777215));
         } else if (var5.kind == 1) {
            float var15 = clamp01(((float)var5.age + var2) / (float)var5.life);
            int var7 = col((int)var5.e);
            if (var3) {
               var7 = mix(var7, 16777215, (float)(var5.seed % 7) / 7.0F * 0.35F);
            }

            float var8 = (float)Math.pow((double)(1.0F - var15), 0.8);
            if (var5.f > 0.0F && var15 > 0.55F) {
               var8 *= R.nextFloat() < 0.5F ? 1.0F : 0.15F;
            }

            trail(var5, var2, 0.004F * var0, 0.03F * var0, var8 * 0.85F, var7);
            double var9 = var5.ix(var2);
            double var11 = var5.iy(var2);
            double var13 = var5.iz(var2);
            spr(var9, var11, var13, 0.075F * var0, argb(var8, var7));
            spr(var9, var11, var13, 0.028F * var0, argb(var8 * (1.0F - var15), 16777215));
         } else {
            float var16 = clamp01(((float)var5.age + var2) / (float)var5.life);
            spr(var5.x, var5.y, var5.z, 3.2F * var0 * (0.6F + var16 * 0.6F), argb((1.0F - var16) * 0.5F, col((int)var5.e)));
            spr(var5.x, var5.y, var5.z, 0.9F * var0, argb((1.0F - var16) * 0.9F, 16777215));
         }
      }
   }

   static final class P {
      double x;
      double y;
      double z;
      double px;
      double py;
      double pz;
      double vx;
      double vy;
      double vz;
      int age;
      int life;
      int seed = ParticleFx.seedCounter++ * 7919 & 65535;
      int kind;
      float a;
      float b;
      float c;
      float d;
      float e;
      float f;
      float g;
      double[] tx;
      double[] ty;
      double[] tz;
      int tn;

      P(int var1) {
         if (var1 > 0) {
            this.tx = new double[var1];
            this.ty = new double[var1];
            this.tz = new double[var1];
         }
      }

      void push() {
         if (this.tx != null) {
            int var1 = this.tx.length;
            System.arraycopy(this.tx, 0, this.tx, 1, var1 - 1);
            System.arraycopy(this.ty, 0, this.ty, 1, var1 - 1);
            System.arraycopy(this.tz, 0, this.tz, 1, var1 - 1);
            this.tx[0] = this.x;
            this.ty[0] = this.y;
            this.tz[0] = this.z;
            if (this.tn < var1) {
               this.tn++;
            }
         }
      }

      void move() {
         this.px = this.x;
         this.py = this.y;
         this.pz = this.z;
         this.x = this.x + this.vx;
         this.y = this.y + this.vy;
         this.z = this.z + this.vz;
      }

      double ix(float var1) {
         return this.px + (this.x - this.px) * (double)var1;
      }

      double iy(float var1) {
         return this.py + (this.y - this.py) * (double)var1;
      }

      double iz(float var1) {
         return this.pz + (this.z - this.pz) * (double)var1;
      }
   }
}
