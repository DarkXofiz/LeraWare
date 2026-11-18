package neva.ware.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import neva.ware.utility.render.Render2DEngine;

public class PanoramaBackground {
   private static final class_2960 BG = class_2960.method_60655("nevaware", "textures/gui/menu/bg.png");
   private static final int TW = 2048;
   private static final int TH = 1280;
   public static float fadeAlpha = 1.0F;

   public static void drawMainMenuShader(class_4587 var0, float var1, float var2, float var3, float var4) {
      float var5 = var3 / var4;
      float var6 = 1.6F;
      float var7 = 1.06F;
      int var8;
      int var9;
      if (var5 > var6) {
         var8 = (int)(2048.0F / var7);
         var9 = (int)((float)var8 / var5);
      } else {
         var9 = (int)(1280.0F / var7);
         var8 = (int)((float)var9 * var5);
      }

      double var10 = (double)System.currentTimeMillis();
      float var12 = (float)(2048 - var8) / 2.0F + (float)Math.sin(var10 / 7000.0) * (float)(2048 - var8) / 2.0F * 0.8F;
      float var13 = (float)(1280 - var9) / 2.0F + (float)Math.cos(var10 / 9000.0) * (float)(1280 - var9) / 2.0F * 0.8F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fadeAlpha);
      fadeAlpha = 1.0F;
      RenderSystem.setShaderTexture(0, BG);
      Render2DEngine.renderTexture(var0, (double)var1, (double)var2, (double)var3, (double)var4, var12, var13, (double)var8, (double)var9, 2048.0, 1280.0);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
