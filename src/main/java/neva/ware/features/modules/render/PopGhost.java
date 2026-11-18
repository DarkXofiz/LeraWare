package neva.ware.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_583;
import net.minecraft.class_630;
import net.minecraft.class_742;
import net.minecraft.class_757;
import net.minecraft.class_7833;
import net.minecraft.class_897;
import net.minecraft.class_922;
import net.minecraft.class_293.class_5596;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.TotemPopEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.Render3DEngine;
import org.joml.Matrix4f;

public class PopGhost extends Module {
   public final Setting<PopGhost.ColorMode> colorMode = new Setting<>("Color Mode", PopGhost.ColorMode.Sync);
   public final Setting<ColorSetting> color = new Setting<>(
      "Color", new ColorSetting(new Color(-6595329, true)), var1 -> this.colorMode.is(PopGhost.ColorMode.Custom)
   );
   public final Setting<Boolean> texture = new Setting<>("Skin Texture", Boolean.FALSE);
   public final Setting<Boolean> glow = new Setting<>("Glow", Boolean.TRUE);
   public final Setting<Integer> alpha = new Setting<>("Alpha", 70, 10, 100);
   public final Setting<Integer> lifetime = new Setting<>("Lifetime", 2000, 500, 6000);
   public final Setting<Float> height = new Setting<>("Height", 3.0F, 0.5F, 10.0F);
   public final Setting<Boolean> expand = new Setting<>("Expand", Boolean.FALSE);
   public final Setting<Boolean> self = new Setting<>("Self", Boolean.FALSE);
   public final Setting<Boolean> throughWalls = new Setting<>("Through Walls", Boolean.FALSE);
   public final Setting<Integer> maxGhosts = new Setting<>("Max Ghosts", 10, 1, 30);
   private final List<PopGhost.Ghost> ghosts = new ArrayList<>();
   private final List<class_1657> pending = new ArrayList<>();
   private static Field modelField;
   private static Field[] partFloatFields;
   private static Field[] partBoolFields;
   private static final Map<Class<?>, Field[]> PART_FIELDS = new IdentityHashMap<>();

   public PopGhost() {
      super("PopGhost", Module.Category.RENDER);
   }

   @Override
   public void onDisable() {
      this.ghosts.clear();
      synchronized (this.pending) {
         this.pending.clear();
      }
   }

   @EventHandler
   public void onTotemPop(TotemPopEvent var1) {
      class_1657 var2 = var1.getEntity();
      if (var2 != null) {
         if (var2 != mc.field_1724 || this.self.getValue()) {
            synchronized (this.pending) {
               if (!this.pending.contains(var2)) {
                  this.pending.add(var2);
               }
            }
         }
      }
   }

   private static class_583<class_1657> modelOf(class_1657 var0) {
      try {
         class_897 var1 = mc.method_1561().method_3953(var0);
         if (!(var1 instanceof class_922)) {
            return null;
         } else {
            if (modelField == null) {
               Field var2 = null;

               try {
                  var2 = class_922.class.getDeclaredField("field_4737");
               } catch (NoSuchFieldException var8) {
                  for (Field var7 : class_922.class.getDeclaredFields()) {
                     if (class_583.class.isAssignableFrom(var7.getType()) && !Modifier.isStatic(var7.getModifiers())) {
                        var2 = var7;
                        break;
                     }
                  }
               }

               if (var2 == null) {
                  return null;
               }

               var2.setAccessible(true);
               modelField = var2;
            }

            return (class_583<class_1657>)modelField.get(var1);
         }
      } catch (Throwable var9) {
         return null;
      }
   }

   private static void partFields() {
      if (partFloatFields == null) {
         ArrayList var0 = new ArrayList();
         ArrayList var1 = new ArrayList();

         for (Field var5 : class_630.class.getDeclaredFields()) {
            if (!Modifier.isStatic(var5.getModifiers())) {
               if (var5.getType() == float.class) {
                  var5.setAccessible(true);
                  var0.add(var5);
               } else if (var5.getType() == boolean.class) {
                  var5.setAccessible(true);
                  var1.add(var5);
               }
            }
         }

         partFloatFields = var0.toArray(new Field[0]);
         partBoolFields = var1.toArray(new Field[0]);
      }
   }

   private static Field[] modelParts(Object var0) {
      Class var1 = var0.getClass();
      Field[] var2 = PART_FIELDS.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         ArrayList var3 = new ArrayList();

         for (Class var4 = var1; var4 != null && var4 != Object.class; var4 = var4.getSuperclass()) {
            for (Field var8 : var4.getDeclaredFields()) {
               if (!Modifier.isStatic(var8.getModifiers()) && var8.getType() == class_630.class) {
                  var8.setAccessible(true);
                  var3.add(var8);
               }
            }
         }

         var2 = var3.toArray(new Field[0]);
         PART_FIELDS.put(var1, var2);
         return var2;
      }
   }

   private static class_630[] parts(Object var0) {
      Field[] var1 = modelParts(var0);
      class_630[] var2 = new class_630[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         try {
            var2[var3] = (class_630)var1[var3].get(var0);
         } catch (Throwable var5) {
         }
      }

      return var2;
   }

   private static void save(class_630[] var0, float[][] var1, boolean[][] var2) throws IllegalAccessException {
      for (int var3 = 0; var3 < var0.length; var3++) {
         if (var0[var3] != null) {
            var1[var3] = new float[partFloatFields.length];
            var2[var3] = new boolean[partBoolFields.length];

            for (int var4 = 0; var4 < partFloatFields.length; var4++) {
               var1[var3][var4] = partFloatFields[var4].getFloat(var0[var3]);
            }

            for (int var5 = 0; var5 < partBoolFields.length; var5++) {
               var2[var3][var5] = partBoolFields[var5].getBoolean(var0[var3]);
            }
         }
      }
   }

   private static void load(class_630[] var0, float[][] var1, boolean[][] var2) throws IllegalAccessException {
      for (int var3 = 0; var3 < var0.length && var3 < var1.length; var3++) {
         if (var0[var3] != null && var1[var3] != null) {
            for (int var4 = 0; var4 < partFloatFields.length; var4++) {
               partFloatFields[var4].setFloat(var0[var3], var1[var3][var4]);
            }

            for (int var5 = 0; var5 < partBoolFields.length; var5++) {
               partBoolFields[var5].setBoolean(var0[var3], var2[var3][var5]);
            }
         }
      }
   }

   private PopGhost.Ghost capture(class_1657 var1, float var2) {
      class_583 var3 = modelOf(var1);
      if (var3 == null) {
         return null;
      } else {
         partFields();
         class_630[] var4 = parts(var3);
         float[][] var5 = new float[var4.length][];
         boolean[][] var6 = new boolean[var4.length][];

         try {
            save(var4, var5, var6);
            float var7 = class_3532.method_17821(var2, var1.field_6220, var1.field_6283);
            float var8 = class_3532.method_17821(var2, var1.field_6259, var1.field_6241);
            float var9 = class_3532.method_15393(var8 - var7);
            float var10 = class_3532.method_16439(var2, var1.field_6004, var1.method_36455());
            float var11 = (float)var1.field_6012 + var2;
            float var12 = 0.0F;
            float var13 = 0.0F;
            if (!var1.method_5765()) {
               var12 = Math.min(1.0F, var1.field_42108.method_48570(var2));
               var13 = var1.field_42108.method_48572(var2);
               if (var1.method_6109()) {
                  var13 *= 3.0F;
               }
            }

            var3.field_3447 = var1.method_6055(var2);
            var3.field_3449 = var1.method_5765();
            var3.field_3448 = false;
            var3.method_2816(var1, var13, var12, var2);
            var3.method_2819(var1, var13, var12, var11, var9, var10);
            PopGhost.Ghost var14 = new PopGhost.Ghost();
            var14.model = var3;
            var14.floats = new float[var4.length][];
            var14.bools = new boolean[var4.length][];
            save(var4, var14.floats, var14.bools);
            int var15 = -1;

            for (int var16 = 0; var16 < partBoolFields.length; var16++) {
               if (partBoolFields[var16].getName().equals("field_3665")) {
                  var15 = var16;
               }
            }

            if (var15 >= 0) {
               for (boolean[] var19 : var14.bools) {
                  if (var19 != null) {
                     var19[var15] = true;
                  }
               }
            }

            class_4587 var24 = new class_4587();
            Chams var25 = ModuleManager.chams;
            if (var25 != null) {
               var25.setupTransforms1(var1, var24, var11, var7, var2);
            } else {
               var24.method_22907(class_7833.field_40716.rotationDegrees(180.0F - var7));
            }

            var24.method_22905(-1.0F, -1.0F, 1.0F);
            var24.method_22905(0.9375F, 0.9375F, 0.9375F);
            var24.method_46416(0.0F, -1.501F, 0.0F);
            var14.pose = new Matrix4f(var24.method_23760().method_23761());
            var14.x = var1.method_23317();
            var14.y = var1.method_23318() - (var1.method_5715() ? 0.125 : 0.0);
            var14.z = var1.method_23321();
            if (var1 instanceof class_742) {
               try {
                  var14.skin = ((class_742)var1).method_52814().comp_1626();
               } catch (Throwable var21) {
               }
            }

            var14.start = System.currentTimeMillis();
            load(var4, var5, var6);
            return var14;
         } catch (Throwable var22) {
            try {
               load(var4, var5, var6);
            } catch (Throwable var20) {
            }

            return null;
         }
      }
   }

   @Override
   public void onRender3D(class_4587 var1) {
      if (mc.field_1724 != null) {
         float var2 = Render3DEngine.getTickDelta();
         synchronized (this.pending) {
            for (class_1657 var5 : this.pending) {
               PopGhost.Ghost var6 = this.capture(var5, var2);
               if (var6 != null) {
                  this.ghosts.add(var6);
               }
            }

            this.pending.clear();
         }

         long var35 = System.currentTimeMillis();
         long var36 = (long)this.lifetime.getValue().intValue();
         this.ghosts.removeIf(var4 -> var35 - var4.start >= var36);

         while (this.ghosts.size() > this.maxGhosts.getValue()) {
            this.ghosts.remove(0);
         }

         if (!this.ghosts.isEmpty()) {
            class_243 var7 = mc.field_1773.method_19418().method_19326();
            boolean var8 = this.texture.getValue();
            RenderSystem.enableBlend();
            if (this.glow.getValue()) {
               RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
            } else {
               RenderSystem.defaultBlendFunc();
            }

            if (this.throughWalls.getValue()) {
               RenderSystem.disableDepthTest();
            } else {
               RenderSystem.enableDepthTest();
            }

            RenderSystem.depthMask(false);
            RenderSystem.enableCull();

            for (PopGhost.Ghost var10 : this.ghosts) {
               double var11 = Math.min(1.0, (double)(var35 - var10.start) / (double)var36);
               double var13 = 1.0 - (1.0 - var11) * (1.0 - var11);
               float var15 = (float)((double)this.alpha.getValue().intValue() / 100.0 * Math.pow(1.0 - var11, 1.4));
               if (!(var15 <= 0.004F)) {
                  class_583 var16 = var10.model;
                  if (var16 != null) {
                     class_630[] var17 = parts(var16);
                     float[][] var18 = new float[var17.length][];
                     boolean[][] var19 = new boolean[var17.length][];
                     Color var20 = this.colorMode.is(PopGhost.ColorMode.Custom)
                        ? this.color.getValue().getColorObject()
                        : HudEditor.getColor((int)(var11 * 180.0));
                     RenderSystem.setShaderColor((float)var20.getRed() / 255.0F, (float)var20.getGreen() / 255.0F, (float)var20.getBlue() / 255.0F, var15);
                     class_287 var21;
                     if (var8 && var10.skin != null) {
                        RenderSystem.setShaderTexture(0, var10.skin);
                        RenderSystem.setShader(class_757::method_34542);
                        var21 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1585);
                     } else {
                        RenderSystem.setShader(class_757::method_34539);
                        var21 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1592);
                     }

                     var1.method_22903();
                     var1.method_46416(
                        (float)(var10.x - var7.field_1352),
                        (float)(var10.y - var7.field_1351 + (double)this.height.getValue().floatValue() * var13),
                        (float)(var10.z - var7.field_1350)
                     );
                     if (this.expand.getValue()) {
                        float var22 = (float)(1.0 + 0.35 * var13);
                        var1.method_46416(0.0F, 0.9F, 0.0F);
                        var1.method_22905(var22, var22, var22);
                        var1.method_46416(0.0F, -0.9F, 0.0F);
                     }

                     var1.method_23760().method_23761().mul(var10.pose);

                     try {
                        save(var17, var18, var19);
                        load(var17, var10.floats, var10.bools);
                        var16.method_60879(var1, var21, 15728880, class_4608.field_21444);
                        Render2DEngine.endBuilding(var21);
                     } catch (Throwable var32) {
                     } finally {
                        try {
                           load(var17, var18, var19);
                        } catch (Throwable var31) {
                        }

                        var1.method_22909();
                     }
                  }
               }
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
         }
      }
   }

   public static enum ColorMode {
      Sync,
      Custom;
   }

   private static final class Ghost {
      double x;
      double y;
      double z;
      Matrix4f pose;
      float[][] floats;
      boolean[][] bools;
      class_2960 skin;
      class_583<class_1657> model;
      long start;
   }
}
