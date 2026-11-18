package neva.ware.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_7833;
import net.minecraft.class_293.class_5596;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.Setting;
import neva.ware.utility.NevaUtility;
import neva.ware.utility.Timer;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.TextureStorage;
import org.joml.Matrix4f;

public class JumpCircle extends Module {
   private final Setting<JumpCircle.Mode> mode = new Setting<>("Mode", JumpCircle.Mode.v1);
   private final Setting<Float> radius = new Setting<>("Radius", 1.0F, 0.05F, 2.0F, var1 -> false);
   private final Setting<Boolean> easeOut = new Setting<>("EaseOut", true, var1 -> false);
   private final Setting<Float> rotateSpeed = new Setting<>("RotateSpeed", 2.0F, 0.5F, 5.0F, var1 -> false);
   private final Setting<Float> circleScale = new Setting<>("CircleScale", 1.0F, 0.5F, 5.0F, var1 -> false);
   private final Setting<Boolean> onlySelf = new Setting<>("OnlySelf", false);
   public static Setting fxColorMode = JumpCircleFx.colorMode;
   public static Setting fxColor = JumpCircleFx.color;
   public static Setting fxColor2 = JumpCircleFx.color2;
   public static Setting fxLifetime = JumpCircleFx.lifetime;
   public static Setting fxSize = JumpCircleFx.size;
   public static Setting fxThickness = JumpCircleFx.thickness;
   public static Setting fxGlow = JumpCircleFx.glow;
   public static Setting fxGlowSize = JumpCircleFx.glowSize;
   public static Setting fxFill = JumpCircleFx.fill;
   public static Setting fxFillAlpha = JumpCircleFx.fillAlpha;
   public static Setting fxRings = JumpCircleFx.rings;
   public static Setting fxPoints = JumpCircleFx.points;
   public static Setting fxSparkCount = JumpCircleFx.sparkCount;
   public static Setting fxSpin = JumpCircleFx.spin;
   public static Setting fxRise = JumpCircleFx.rise;
   public static Setting fxBounce = JumpCircleFx.bounce;
   public static Setting fxThroughWalls = JumpCircleFx.throughWalls;
   private final List<JumpCircle.Circle> circles = new ArrayList<>();
   private final List<class_1657> cache = new CopyOnWriteArrayList<>();
   private class_2960 custom;
   private static final long NEVAWARE_LIFE = 3200L;
   private static final class_2960 NEVAWARE_CIRCLE = class_2960.method_60655("nevaware", "textures/particles/nevaware_circle.png");

   public JumpCircle() {
      super("JumpCircle", Module.Category.RENDER);
      JumpCircleFx.MODE = this.mode;
   }

   private boolean isNevaWare() {
      return this.mode != null && this.mode.is(JumpCircle.Mode.NevaWare);
   }

   @Override
   public void onEnable() {
      try {
         this.custom = NevaUtility.getCustomImg("circle");
      } catch (Exception var2) {
         this.sendMessage(var2.getMessage());
      }
   }

   @Override
   public void onUpdate() {
      if (this.mode.is(JumpCircle.Mode.Custom) && this.custom == null) {
         try {
            this.custom = NevaUtility.getCustomImg("circle");
         } catch (Exception var3) {
            this.sendMessage(".minecraft -> NevaWare -> misc -> images -> circle.png");
         }
      }

      for (class_1657 var2 : mc.field_1687.method_18456()) {
         if (!this.cache.contains(var2) && var2.method_24828() && (mc.field_1724 == var2 || !this.onlySelf.getValue())) {
            this.cache.add(var2);
         }
      }

      this.cache
         .forEach(
            var1 -> {
               if (var1 != null && !var1.method_24828()) {
                  this.circles
                     .add(
                        new JumpCircle.Circle(
                           new class_243(var1.method_23317(), (double)((float)((int)Math.floor(var1.method_23318())) + 0.001F), var1.method_23321()),
                           new Timer()
                        )
                     );
                  this.cache.remove(var1);
               }
            }
         );
      this.circles
         .removeIf(
            var1 -> JumpCircleFx.handles()
                  ? JumpCircleFx.expired(var1.timer())
                  : var1.timer.passedMs(this.isNevaWare() ? 3200L : (this.easeOut.getValue() ? 5000L : 6000L))
         );
   }

   @Override
   public void onRender3D(class_4587 var1) {
      if (JumpCircleFx.handles()) {
         JumpCircleFx.render(var1, this.circles, this.custom, NEVAWARE_CIRCLE);
      } else if (this.isNevaWare()) {
         this.renderNevaWare(var1);
      } else {
         Collections.reverse(this.circles);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
         switch ((JumpCircle.Mode)this.mode.getValue()) {
            case v1:
               RenderSystem.setShaderTexture(0, TextureStorage.default_circle);
               break;
            case Portal:
               RenderSystem.setShaderTexture(0, TextureStorage.bubble);
               break;
            case Custom:
               RenderSystem.setShaderTexture(0, Objects.requireNonNullElse(this.custom, TextureStorage.default_circle));
         }

         RenderSystem.setShader(class_757::method_34543);
         class_287 var2 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1575);

         for (JumpCircle.Circle var4 : this.circles) {
            float var5 = (float)var4.timer.getPassedTimeMs() / 6000.0F;
            float var6 = this.circleScale.getValue()
               - (float)Math.pow((double)(1.0F - (float)var4.timer.getPassedTimeMs() * (this.easeOut.getValue() ? 2.0F : 1.0F) / 5000.0F), 4.0);
            var1.method_22903();
            var1.method_22904(
               var4.pos().field_1352 - mc.method_1561().field_4686.method_19326().method_10216(),
               var4.pos().field_1351 - mc.method_1561().field_4686.method_19326().method_10214(),
               var4.pos().field_1350 - mc.method_1561().field_4686.method_19326().method_10215()
            );
            var1.method_22907(class_7833.field_40714.rotationDegrees(90.0F));
            var1.method_22907(class_7833.field_40718.rotationDegrees(var6 * this.rotateSpeed.getValue() * 1000.0F));
            float var7 = var6 * 2.0F;
            Matrix4f var8 = var1.method_23760().method_23761();
            var2.method_22918(var8, -var6, -var6 + var7, 0.0F)
               .method_22913(0.0F, 1.0F)
               .method_39415(Render2DEngine.applyOpacity(HudEditor.getColor(270), 1.0F - var5).getRGB());
            var2.method_22918(var8, -var6 + var7, -var6 + var7, 0.0F)
               .method_22913(1.0F, 1.0F)
               .method_39415(Render2DEngine.applyOpacity(HudEditor.getColor(0), 1.0F - var5).getRGB());
            var2.method_22918(var8, -var6 + var7, -var6, 0.0F)
               .method_22913(1.0F, 0.0F)
               .method_39415(Render2DEngine.applyOpacity(HudEditor.getColor(180), 1.0F - var5).getRGB());
            var2.method_22918(var8, -var6, -var6, 0.0F)
               .method_22913(0.0F, 0.0F)
               .method_39415(Render2DEngine.applyOpacity(HudEditor.getColor(90), 1.0F - var5).getRGB());
            var1.method_22909();
         }

         Render2DEngine.endBuilding(var2);
         RenderSystem.disableBlend();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableDepthTest();
         Collections.reverse(this.circles);
      }
   }

   private void renderNevaWare(class_4587 var1) {
      if (!this.circles.isEmpty()) {
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
         RenderSystem.setShaderTexture(0, NEVAWARE_CIRCLE);
         RenderSystem.setShader(class_757::method_34543);
         class_243 var2 = mc.method_1561().field_4686.method_19326();
         class_287 var3 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1575);

         for (JumpCircle.Circle var5 : this.circles) {
            long var6 = var5.timer.getPassedTimeMs();
            if (var6 <= 3200L) {
               float var8 = Math.min(1.0F, (float)var6 / 10000.0F);
               float var9 = 10.0F * (float)Math.sqrt(1.0 - Math.pow((double)var8 - 1.0, 2.0));
               float var10 = var9 / 2.5F + this.radius.getValue();
               float var11 = var10 / 2.0F;
               float var12 = Math.max(0.0F, Math.min(1.0F, 1.0F - (float)var6 / 3200.0F));
               int var13 = Render2DEngine.applyOpacity(HudEditor.getColor(5), var12).getRGB();
               int var14 = Render2DEngine.applyOpacity(HudEditor.getColor(10), var12).getRGB();
               int var15 = Render2DEngine.applyOpacity(HudEditor.getColor(15), var12).getRGB();
               int var16 = Render2DEngine.applyOpacity(HudEditor.getColor(20), var12).getRGB();
               var1.method_22903();
               var1.method_22904(
                  var5.pos().field_1352 - var2.method_10216(), var5.pos().field_1351 + 0.05 - var2.method_10214(), var5.pos().field_1350 - var2.method_10215()
               );
               Matrix4f var17 = var1.method_23760().method_23761();

               for (int var18 = 0; var18 < 2; var18++) {
                  var3.method_22918(var17, -var11, 0.0F, -var11).method_22913(0.0F, 0.0F).method_39415(var13);
                  var3.method_22918(var17, var11, 0.0F, -var11).method_22913(1.0F, 0.0F).method_39415(var14);
                  var3.method_22918(var17, var11, 0.0F, var11).method_22913(1.0F, 1.0F).method_39415(var15);
                  var3.method_22918(var17, -var11, 0.0F, var11).method_22913(0.0F, 1.0F).method_39415(var16);
               }

               var1.method_22909();
            }
         }

         Render2DEngine.endBuilding(var3);
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public static record Circle(class_243 pos, Timer timer) {
   }

   public static enum Mode {
      v1,
      Portal,
      Custom,
      NevaWare,
      Ring,
      Ripple,
      Shockwave,
      Hexagon,
      Star,
      Spiral,
      Pulse,
      Sparks,
      Flower,
      Vortex;
   }
}
