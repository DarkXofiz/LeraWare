package neva.ware.gui.windows;

import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import neva.ware.features.modules.Module;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwNav;
import neva.ware.gui.font.FontRenderer;
import neva.ware.utility.render.TextureStorage;

public class WindowsScreen extends class_437 {
   private List<WindowBase> windows = new ArrayList<>();
   public static WindowBase lastClickedWindow;
   public static WindowBase draggingWindow;
   private static final class_2960 clickGuiIcon = class_2960.method_60655("nevaware", "textures/gui/elements/clickgui.png");
   private final NwNav nav = new NwNav();
   private final long openStart = System.nanoTime();
   private float[] hover = new float[0];
   private float[] active = new float[0];
   private float dockX;
   private float dockY;
   private float dockW;
   private float dockH;
   private float iconsX;
   private float iconS = 17.0F;
   private float iconGap = 3.0F;

   public WindowsScreen(WindowBase... var1) {
      super(class_2561.method_30163("THWindows"));
      this.windows.clear();
      lastClickedWindow = null;
      this.windows = Arrays.stream(var1).toList();
      WindowLayout.arrange(this.windows);
   }

   private static String nameOf(WindowBase var0) {
      try {
         Field var1 = WindowBase.class.getDeclaredField("name");
         var1.setAccessible(true);
         Object var2 = var1.get(var0);
         return var2 == null ? "" : var2.toString();
      } catch (Throwable var3) {
         return "";
      }
   }

   private void layoutDock(int var1, int var2, float var3) {
      this.dockH = 25.0F;
      float var4 = this.nav.width();
      float var5 = (float)this.windows.size() * this.iconS + (float)Math.max(0, this.windows.size() - 1) * this.iconGap;
      this.dockW = 4.0F + var4 + 9.0F + var5 + 4.0F;
      this.dockX = (float)Math.floor((double)((float)var1 / 2.0F - this.dockW / 2.0F));
      this.dockY = (float)var2 - this.dockH - 8.0F + (1.0F - var3) * 12.0F;
      this.iconsX = this.dockX + 4.0F + var4 + 9.0F;
   }

   public void method_25394(class_332 var1, int var2, int var3, float var4) {
      NwDraw.frame();
      NwDraw.prepareScreen(var1);
      if (Module.fullNullCheck()) {
         this.method_25420(var1, var2, var3, var4);
      }

      class_4587 var5 = var1.method_51448();
      int var6 = Module.mc.method_22683().method_4486();
      int var7 = Module.mc.method_22683().method_4502();
      float var8 = (float)(System.nanoTime() - this.openStart) / 1.0E9F;
      float var9 = NwDraw.easeOutCubic(var8 / 0.35F);
      NwDraw.alpha = var9;
      NwDraw.round(var5, 0.0F, 0.0F, (float)var6, (float)var7, 0.0F, 805767442, 1476856082);
      NwDraw.alpha = 1.0F;
      this.windows.stream().filter(WindowBase::isVisible).forEach(var3x -> {
         if (var3x != lastClickedWindow) {
            var3x.render(var1, var2, var3);
         }
      });
      if (lastClickedWindow != null && lastClickedWindow.isVisible()) {
         lastClickedWindow.render(var1, var2, var3);
      }

      if (this.hover.length != this.windows.size()) {
         this.hover = new float[this.windows.size()];
         this.active = new float[this.windows.size()];
      }

      this.layoutDock(var6, var7, var9);
      int var10 = NwDraw.accent(0);
      NwDraw.alpha = var9;
      NwDraw.shadow(var5, this.dockX, this.dockY, this.dockW, this.dockH, 8.0F, 14.0F, 2013265920);
      NwDraw.round(var5, this.dockX, this.dockY, this.dockW, this.dockH, 8.0F, -266855897, -233828071);
      NwDraw.border(var5, this.dockX, this.dockY, this.dockW, this.dockH, 8.0F, 0.6F, 318767103);
      this.nav.render(var5, this.dockX + 4.0F, this.dockY + 3.5F, this.dockH - 7.0F, 2, var2, var3, false);
      float var11 = this.dockX + 4.0F + this.nav.width() + 4.0F;
      NwDraw.rect(var5, var11, this.dockY + 6.0F, 0.6F, this.dockH - 12.0F, 587202559);
      String var12 = null;
      float var13 = 0.0F;
      float var14 = this.dockY + (this.dockH - this.iconS) / 2.0F;

      for (int var15 = 0; var15 < this.windows.size(); var15++) {
         WindowBase var16 = this.windows.get(var15);
         float var17 = this.iconsX + (float)var15 * (this.iconS + this.iconGap);
         boolean var18 = NwDraw.hovered((double)var2, (double)var3, var17, var14, this.iconS, this.iconS);
         this.hover[var15] = NwDraw.approach(this.hover[var15], var18 ? 1.0F : 0.0F, 18.0F);
         this.active[var15] = NwDraw.approach(this.active[var15], var16.isVisible() ? 1.0F : 0.0F, 14.0F);
         float var19 = this.hover[var15] * 1.2F;
         int var20 = NwDraw.mix(
            NwDraw.mix(268435455, 520093695, this.hover[var15]), NwDraw.withAlpha(NwDraw.mix(-13946792, var10, 0.45F), 220), this.active[var15]
         );
         int var21 = NwDraw.mix(
            NwDraw.mix(184549375, 385875967, this.hover[var15]), NwDraw.withAlpha(NwDraw.mix(-14341818, NwDraw.accent(180), 0.35F), 220), this.active[var15]
         );
         if (this.active[var15] > 0.02F) {
            NwDraw.shadow(var5, var17, var14 - var19, this.iconS, this.iconS, 5.0F, 5.0F, NwDraw.withAlpha(var10, Math.round(40.0F * this.active[var15])));
         }

         NwDraw.round(var5, var17, var14 - var19, this.iconS, this.iconS, 5.0F, var20, var21);
         NwDraw.border(
            var5,
            var17,
            var14 - var19,
            this.iconS,
            this.iconS,
            5.0F,
            0.5F,
            NwDraw.withAlpha(-1, Math.round(14.0F + 20.0F * Math.max(this.hover[var15], this.active[var15])))
         );
         class_2960 var22 = var16.getIcon() != null ? var16.getIcon() : TextureStorage.configIcon;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         float var23 = var9 * (0.72F + 0.28F * Math.max(this.hover[var15], this.active[var15]));
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var23);
         var1.method_25293(var22, (int)(var17 + 3.0F), (int)(var14 - var19 + 3.0F), 11, 11, 0.0F, 0.0F, 11, 11, 11, 11);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.disableBlend();
         if (this.active[var15] > 0.02F) {
            NwDraw.round(
               var5,
               var17 + this.iconS / 2.0F - 2.5F * this.active[var15],
               var14 + this.iconS + 1.2F,
               5.0F * this.active[var15],
               1.2F,
               0.6F,
               NwDraw.withAlpha(var10, Math.round(255.0F * this.active[var15]))
            );
         }

         if (var18) {
            var12 = nameOf(var16);
            var13 = var17 + this.iconS / 2.0F;
         }
      }

      if (var12 != null && !var12.isEmpty()) {
         FontRenderer var24 = NwDraw.smallFont();
         float var25 = NwDraw.width(var24, var12) + 10.0F;
         float var26 = 12.0F;
         float var27 = var13 - var25 / 2.0F;
         float var28 = this.dockY - var26 - 5.0F;
         NwDraw.shadow(var5, var27, var28, var25, var26, 4.0F, 6.0F, 1342177280);
         NwDraw.round(var5, var27, var28, var25, var26, 4.0F, -233038292, -233433310);
         NwDraw.border(var5, var27, var28, var25, var26, 4.0F, 0.5F, 385875967);
         NwDraw.textCentered(var24, var5, var12, var13, NwDraw.textTop(var28 + var26 / 2.0F, NwDraw.smallSize()), -1);
      }

      NwDraw.alpha = 1.0F;
   }

   public boolean method_25406(double var1, double var3, int var5) {
      this.windows.forEach(var5x -> var5x.mouseReleased(var1, var3, var5));
      return super.method_25406(var1, var3, var5);
   }

   public boolean method_25402(double var1, double var3, int var5) {
      if (NwDraw.hovered(var1, var3, this.dockX, this.dockY, this.dockW, this.dockH)) {
         if (this.nav.click(var1, var3, var5, 2)) {
            return true;
         } else {
            float var6 = this.dockY + (this.dockH - this.iconS) / 2.0F;

            for (int var7 = 0; var7 < this.windows.size(); var7++) {
               float var8 = this.iconsX + (float)var7 * (this.iconS + this.iconGap);
               if (NwDraw.hovered(var1, var3, var8, var6 - 1.5F, this.iconS, this.iconS + 1.5F)) {
                  WindowBase var9 = this.windows.get(var7);
                  var9.setVisible(!var9.isVisible());
                  WindowLayout.arrange(this.windows);
                  return true;
               }
            }

            return true;
         }
      } else {
         this.windows.stream().filter(WindowBase::isVisible).forEach(var5x -> var5x.mouseClicked(var1, var3, var5));
         return super.method_25402(var1, var3, var5);
      }
   }

   public boolean method_25404(int var1, int var2, int var3) {
      this.windows.stream().filter(WindowBase::isVisible).forEach(var3x -> var3x.keyPressed(var1, var2, var3));
      return super.method_25404(var1, var2, var3);
   }

   public boolean method_25400(char var1, int var2) {
      this.windows.stream().filter(WindowBase::isVisible).forEach(var2x -> var2x.charTyped(var1, var2));
      return super.method_25400(var1, var2);
   }

   public boolean method_25401(double var1, double var3, double var5, double var7) {
      this.windows.stream().filter(WindowBase::isVisible).forEach(var2 -> var2.mouseScrolled((int)(var7 * 5.0)));
      return super.method_25401(var1, var3, var5, var7);
   }
}
