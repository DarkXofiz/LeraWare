package neva.ware.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_429;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_4587;
import net.minecraft.class_500;
import net.minecraft.class_526;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.gui.font.FontRenderer;
import neva.ware.gui.font.FontRenderers;
import neva.ware.utility.render.Render2DEngine;

public class MainMenuScreen extends class_437 {
   private static final class_2960 LOGO = class_2960.method_60655("nevaware", "textures/gui/menu/wm.png");
   private static final class_2960 LOGO_GRAD = class_2960.method_60655("nevaware", "textures/gui/menu/wm_grad.png");
   private static final class_2960 LOGO_GLOW = class_2960.method_60655("nevaware", "textures/gui/menu/logo_glow.png");
   private static final Color BLUE = new Color(17, 89, 235);
   private final List<MainMenuButton> buttons = new ArrayList<>();
   public boolean confirm = false;
   public static int ticksActive;
   private static MainMenuScreen INSTANCE = new MainMenuScreen();

   protected MainMenuScreen() {
      super(class_2561.method_30163("NevaWareMainMenu"));
      INSTANCE = this;
      this.buttons
         .add(
            new MainMenuButton(
               0.0F,
               -47.0F,
               class_1074.method_4662("menu.singleplayer", new Object[0]).toUpperCase(Locale.ROOT),
               () -> Module.mc.method_1507(new class_526(this))
            )
         );
      this.buttons
         .add(
            new MainMenuButton(
               0.0F,
               -19.0F,
               class_1074.method_4662("menu.multiplayer", new Object[0]).toUpperCase(Locale.ROOT),
               () -> Module.mc.method_1507(new class_500(this))
            )
         );
      this.buttons
         .add(
            new MainMenuButton(
               0.0F,
               9.0F,
               class_1074.method_4662("menu.options", new Object[0]).toUpperCase(Locale.ROOT).replace(".", ""),
               () -> Module.mc.method_1507(new class_429(this, Module.mc.field_1690))
            )
         );
      this.buttons.add(new MainMenuButton(0.0F, 37.0F, "CLICKGUI", () -> ModuleManager.clickGui.setGui()));
      this.buttons
         .add(new MainMenuButton(0.0F, 77.0F, class_1074.method_4662("menu.quit", new Object[0]).toUpperCase(Locale.ROOT), Module.mc::method_1592, true));
   }

   public static MainMenuScreen getInstance() {
      ticksActive = 0;
      if (INSTANCE == null) {
         INSTANCE = new MainMenuScreen();
      }

      return INSTANCE;
   }

   static Color theme(int var0) {
      try {
         Color var1 = HudEditor.getColor(var0);
         if (var1 != null) {
            return new Color(var1.getRed(), var1.getGreen(), var1.getBlue());
         }
      } catch (Throwable var2) {
      }

      return BLUE;
   }

   static void tint(Color var0, float var1) {
      RenderSystem.setShaderColor((float)var0.getRed() / 255.0F, (float)var0.getGreen() / 255.0F, (float)var0.getBlue() / 255.0F, var1);
   }

   static void drawSpacedTheme(class_4587 var0, String var1, float var2, float var3, float var4, int var5) {
      FontRenderer var6 = FontRenderers.sf_bold;
      float var7 = 0.0F;

      for (int var8 = 0; var8 < var1.length(); var8++) {
         var7 += var6.getStringWidth(String.valueOf(var1.charAt(var8)));
         if (var8 < var1.length() - 1) {
            var7 += var4;
         }
      }

      float var11 = var2 - var7 / 2.0F;

      for (int var9 = 0; var9 < var1.length(); var9++) {
         String var10 = String.valueOf(var1.charAt(var9));
         var6.drawString(var0, var10, (double)var11, (double)var3, theme(var5 + var9 * 40).getRGB());
         var11 += var6.getStringWidth(var10) + var4;
      }
   }

   static float columnX() {
      return (float)Module.mc.method_22683().method_4486() * 0.5F;
   }

   static float centerY() {
      return (float)Module.mc.method_22683().method_4502() / 2.0F;
   }

   static void drawSpaced(class_4587 var0, String var1, float var2, float var3, float var4, int var5) {
      drawSpaced(FontRenderers.sf_bold, var0, var1, var2, var3, var4, var5);
   }

   static void drawSpaced(FontRenderer var0, class_4587 var1, String var2, float var3, float var4, float var5, int var6) {
      float var7 = 0.0F;

      for (int var8 = 0; var8 < var2.length(); var8++) {
         var7 += var0.getStringWidth(String.valueOf(var2.charAt(var8)));
         if (var8 < var2.length() - 1) {
            var7 += var5;
         }
      }

      float var11 = var3 - var7 / 2.0F;

      for (int var9 = 0; var9 < var2.length(); var9++) {
         String var10 = String.valueOf(var2.charAt(var9));
         var0.drawString(var1, var10, (double)var11, (double)var4, var6);
         var11 += var0.getStringWidth(var10) + var5;
      }
   }

   public void method_25393() {
      ticksActive++;
      if (ticksActive > 400) {
         ticksActive = 0;
      }
   }

   public void method_25394(class_332 var1, int var2, int var3, float var4) {
      float var5 = (float)Module.mc.method_22683().method_4486();
      float var6 = (float)Module.mc.method_22683().method_4502();
      float var7 = columnX();
      float var8 = var6 / 2.0F;
      long var9 = System.currentTimeMillis();
      this.method_25420(var1, var2, var3, var4);
      class_4587 var11 = var1.method_51448();
      float var12 = var6 * 0.52F;
      float var13 = (float)Math.sin((double)var9 / 900.0) * 3.0F;
      float var14 = var5 * 0.27F - var12 / 2.0F;
      float var15 = var8 - var12 / 2.0F + var13;
      float var16 = var12 * 1.12F;
      float var17 = 0.55F + 0.35F * (float)Math.sin((double)var9 / 650.0);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      Color var18 = theme(0);
      Color var19 = theme(270);
      new Color((var18.getRed() + var19.getRed()) / 2, (var18.getGreen() + var19.getGreen()) / 2, (var18.getBlue() + var19.getBlue()) / 2);
      tint(var18, 1.0F);
      var1.method_25293(LOGO, Math.round(var5 / 2.0F) - 128, (int)Math.max(var8 - 101.0F, (float)4), 256, 36, 0.0F, 0.0F, 1024, 142, 1024, 142);
      tint(var19, 1.0F);
      var1.method_25293(LOGO_GRAD, Math.round(var5 / 2.0F) - 128, (int)Math.max(var8 - 101.0F, (float)4), 256, 36, 0.0F, 0.0F, 1024, 142, 1024, 142);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

      for (MainMenuButton var22 : this.buttons) {
         var22.onRender(var1, (float)var2, (float)var3);
      }

      boolean var25 = Render2DEngine.isHovered((double)var2, (double)var3, (double)(var7 - 50.0F), (double)(var8 + 104.0F), 100.0, 10.0);
      FontRenderers.sf_medium
         .drawCenteredString(var11, "                        ", (double)var7, (double)(var8 + 104.0F), var25 ? -1 : Render2DEngine.applyOpacity(-1, 0.6F));
      int var26 = 0;

      try {
         var26 = Managers.TELEMETRY.getOnlinePlayers().size();
      } catch (Throwable var24) {
      }

      String var23 = "LeraWare Client  |  online: " + var26;
   }

   public boolean method_25402(double var1, double var3, int var5) {
      float var6 = (float)Module.mc.method_22683().method_4486();
      float var7 = (float)Module.mc.method_22683().method_4502();
      float var8 = columnX();
      float var9 = var7 / 2.0F;
      if (var5 == 0) {
         for (MainMenuButton var11 : this.buttons) {
            var11.onClick((int)var1, (int)var3);
         }

         if (Render2DEngine.isHovered(var1, var3, (double)(var8 - 50.0F), (double)(var9 + 104.0F), 0.0, 10.0)) {
            this.confirm = true;
            Module.mc.method_1507(new class_442());
            this.confirm = false;
         }
      }

      return super.method_25402(var1, var3, var5);
   }

   public boolean method_25422() {
      return false;
   }
}
