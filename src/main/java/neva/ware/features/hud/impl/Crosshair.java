package neva.ware.features.hud.impl;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_332;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.Render3DEngine;
import neva.ware.utility.render.TextureStorage;
import neva.ware.utility.render.animation.AnimationUtility;
import org.joml.Matrix4f;

public class Crosshair extends Module {
   private final Setting<Crosshair.Mode> mode = new Setting<>("Mode", Crosshair.Mode.Circle);
   private final Setting<Boolean> animated = new Setting<>("Animated", true, v -> this.mode.is(Crosshair.Mode.Default));
   private final Setting<Boolean> dot = new Setting<>("Dot", false, v -> this.mode.is(Crosshair.Mode.Default));
   private final Setting<Boolean> t = new Setting<>("T", false, v -> this.mode.is(Crosshair.Mode.Default));
   private final Setting<Crosshair.ColorMode> colorMode = new Setting<>("ColorMode", Crosshair.ColorMode.Sync);
   public final Setting<ColorSetting> color = new Setting<>("Color", new ColorSetting(575714484));
   private final Setting<Boolean> dynamic = new Setting<>("Dynamic", true);
   private final Setting<Float> range = new Setting<>("Range", 30.0F, 0.1F, 120.0F);
   private final Setting<Float> speed = new Setting<>("Speed", 3.0F, 0.1F, 20.0F);
   private final Setting<Float> backSpeed = new Setting<>("BackSpeed", 5.0F, 0.1F, 20.0F);
   private float xAnim;
   private float yAnim;
   private float prevPitch;
   private float prevProgress;

   public Crosshair() {
      super("Crosshair", Module.Category.HUD);
   }

   @Override
   public void onRender2D(class_332 context) {
      if (mc.field_1690.method_31044().method_31034()) {
         float midX = (float)mc.method_22683().method_4486() / 2.0F;
         float midY = (float)mc.method_22683().method_4502() / 2.0F;
         float yawDelta = mc.field_1724.field_6259 - mc.field_1724.method_5791();
         float pitchDelta = this.prevPitch - mc.field_1724.method_36455();
         if (yawDelta > 0.0F) {
            this.xAnim = AnimationUtility.fast(this.xAnim, midX - this.range.getValue(), this.speed.getValue());
         } else if (yawDelta < 0.0F) {
            this.xAnim = AnimationUtility.fast(this.xAnim, midX + this.range.getValue(), this.speed.getValue());
         } else {
            this.xAnim = AnimationUtility.fast(this.xAnim, midX, this.backSpeed.getValue());
         }

         if (pitchDelta > 0.0F) {
            this.yAnim = AnimationUtility.fast(this.yAnim, midY - this.range.getValue(), this.speed.getValue());
         } else if (pitchDelta < 0.0F) {
            this.yAnim = AnimationUtility.fast(this.yAnim, midY + this.range.getValue(), this.speed.getValue());
         } else {
            this.yAnim = AnimationUtility.fast(this.yAnim, midY, this.backSpeed.getValue());
         }

         this.prevPitch = mc.field_1724.method_36455();
         if (!this.dynamic.getValue()) {
            this.xAnim = midX;
            this.yAnim = midY;
         }

         float progress = 360.0F * mc.field_1724.method_7261(0.5F);
         progress = progress == 0.0F ? 360.0F : progress;
         switch ((Crosshair.Mode)this.mode.getValue()) {
            case Circle:
               Color c1;
               Color c2;
               if (this.colorMode.getValue() == Crosshair.ColorMode.Sync) {
                  HudEditor.Theme theme = HudEditor.getCurrentTheme();
                  c1 = theme.color1;
                  c2 = theme.color2;
               } else {
                  c1 = this.color.getValue().getColorObject();
                  c2 = this.color.getValue().getColorObject();
               }

               Render2DEngine.drawArc(
                  context.method_51448(),
                  this.xAnim - 25.0F,
                  this.yAnim - 25.0F,
                  50.0F,
                  50.0F,
                  0.025F,
                  0.12F,
                  0.0F,
                  Render2DEngine.interpolateFloat(this.prevProgress, progress, (double)Render3DEngine.getTickDelta()),
                  c1,
                  c2
               );
               this.prevProgress = progress;
               break;
            case Dot:
               Color dotColor;
               if (this.colorMode.getValue() == Crosshair.ColorMode.Sync) {
                  HudEditor.Theme theme = HudEditor.getCurrentTheme();
                  dotColor = theme.color1;
               } else {
                  dotColor = this.color.getValue().getColorObject();
               }

               context.method_51448().method_22903();
               context.method_51448().method_46416(this.xAnim + 4.0F, this.yAnim + 4.0F, 0.0F);
               RenderSystem.enableBlend();
               RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
               RenderSystem.setShader(class_757::method_34543);
               class_287 bufferBuilder = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1575);
               RenderSystem.setShaderTexture(0, TextureStorage.firefly);
               Matrix4f posMatrix = context.method_51448().method_23760().method_23761();
               bufferBuilder.method_22918(posMatrix, 0.0F, -8.0F, 0.0F).method_22913(0.0F, 1.0F).method_39415(dotColor.getRGB());
               bufferBuilder.method_22918(posMatrix, -8.0F, -8.0F, 0.0F).method_22913(1.0F, 1.0F).method_39415(dotColor.getRGB());
               bufferBuilder.method_22918(posMatrix, -8.0F, 0.0F, 0.0F).method_22913(1.0F, 0.0F).method_39415(dotColor.getRGB());
               bufferBuilder.method_22918(posMatrix, 0.0F, 0.0F, 0.0F).method_22913(0.0F, 0.0F).method_39415(dotColor.getRGB());
               class_286.method_43433(bufferBuilder.method_60800());
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableBlend();
               context.method_51448().method_22909();
               break;
            case Default:
               Color drawColor;
               if (this.colorMode.getValue() == Crosshair.ColorMode.Sync) {
                  HudEditor.Theme theme = HudEditor.getCurrentTheme();
                  drawColor = theme.color1;
               } else {
                  drawColor = this.color.getValue().getColorObject();
               }

               float offset = this.animated.getValue()
                  ? -3.0F + Render2DEngine.interpolateFloat(this.prevProgress, progress, (double)Render3DEngine.getTickDelta()) / 100.0F
                  : 0.0F;
               this.prevProgress = progress;
               if (!this.t.getValue()) {
                  Render2DEngine.drawRect(context.method_51448(), this.xAnim - 1.0F, this.yAnim - 6.0F + offset, 2.0F, 4.0F, Color.BLACK);
                  Render2DEngine.drawRect(context.method_51448(), this.xAnim - 0.5F, this.yAnim - 5.5F + offset, 1.0F, 3.0F, drawColor);
               }

               Render2DEngine.drawRect(context.method_51448(), this.xAnim - 1.0F, this.yAnim + 2.0F - offset, 2.0F, 4.0F, Color.BLACK);
               Render2DEngine.drawRect(context.method_51448(), this.xAnim - 0.5F, this.yAnim + 2.5F - offset, 1.0F, 3.0F, drawColor);
               Render2DEngine.drawRect(context.method_51448(), this.xAnim - 6.0F + offset, this.yAnim - 1.0F, 4.0F, 2.0F, Color.BLACK);
               Render2DEngine.drawRect(context.method_51448(), this.xAnim - 5.5F + offset, this.yAnim - 0.5F, 3.0F, 1.0F, drawColor);
               Render2DEngine.drawRect(context.method_51448(), this.xAnim + 2.0F - offset, this.yAnim - 1.0F, 4.0F, 2.0F, Color.BLACK);
               Render2DEngine.drawRect(context.method_51448(), this.xAnim + 2.5F - offset, this.yAnim - 0.5F, 3.0F, 1.0F, drawColor);
               if (this.dot.getValue()) {
                  Render2DEngine.drawRect(context.method_51448(), this.xAnim - 1.0F, this.yAnim - 1.0F, 2.0F, 2.0F, Color.BLACK);
                  Render2DEngine.drawRect(context.method_51448(), this.xAnim - 0.5F, this.yAnim - 0.5F, 1.0F, 1.0F, drawColor);
               }
         }
      }
   }

   public float getAnimatedPosX() {
      return this.xAnim == 0.0F ? (float)mc.method_22683().method_4486() / 2.0F : this.xAnim;
   }

   public float getAnimatedPosY() {
      return this.yAnim == 0.0F ? (float)mc.method_22683().method_4502() / 2.0F : this.yAnim;
   }

   private static enum ColorMode {
      Custom,
      Sync;
   }

   private static enum Mode {
      Circle,
      Dot,
      Default;
   }
}
