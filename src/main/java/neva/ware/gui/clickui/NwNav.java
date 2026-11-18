package neva.ware.gui.clickui;

import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.client.Windows;
import neva.ware.gui.font.FontRenderer;
import neva.ware.gui.hud.HudEditorGui;

public final class NwNav {
   public static final int CLICKGUI = 0;
   public static final int HUD = 1;
   public static final int WINDOWS = 2;
   private final float[] segX = new float[3];
   private final float[] segW = new float[3];
   private final float[] hover = new float[3];
   private float y;
   private float h;
   private float x;
   private float w;
   private float indicatorX = -1.0F;
   private float indicatorW;

   private static boolean ru() {
      try {
         return ClientSettings.isRu();
      } catch (Throwable var1) {
         return false;
      }
   }

   public static String label(int var0) {
      boolean var1 = ru();

      return switch (var0) {
         case 0 -> "ClickGUI";
         case 1 -> var1 ? "HUD редактор" : "HUD Editor";
         default -> var1 ? "Окна" : "Windows";
      };
   }

   public float width() {
      FontRenderer var1 = NwDraw.moduleFont();
      float var2 = 4.0F;

      for (int var3 = 0; var3 < 3; var3++) {
         var2 += NwDraw.width(var1, label(var3)) + 18.0F;
      }

      return var2;
   }

   public float getX() {
      return this.x;
   }

   public float getW() {
      return this.w;
   }

   public void render(class_4587 var1, float var2, float var3, float var4, int var5, int var6, int var7, boolean var8) {
      FontRenderer var9 = NwDraw.moduleFont();
      this.x = var2;
      this.y = var3;
      this.h = var4;
      this.w = this.width();
      int var10 = NwDraw.accent(0);
      if (var8) {
         NwDraw.shadow(var1, this.x, this.y, this.w, this.h, this.h / 2.6F, 12.0F, 1778384896);
         NwDraw.round(var1, this.x, this.y, this.w, this.h, this.h / 2.6F, -266855897, -233828071);
         NwDraw.border(var1, this.x, this.y, this.w, this.h, this.h / 2.6F, 0.6F, 318767103);
      }

      float var11 = this.x + 2.0F;

      for (int var12 = 0; var12 < 3; var12++) {
         this.segW[var12] = NwDraw.width(var9, label(var12)) + 18.0F;
         this.segX[var12] = var11;
         var11 += this.segW[var12];
      }

      if (this.indicatorX < 0.0F) {
         this.indicatorX = this.segX[var5];
         this.indicatorW = this.segW[var5];
      }

      this.indicatorX = NwDraw.approach(this.indicatorX, this.segX[var5], 18.0F);
      this.indicatorW = NwDraw.approach(this.indicatorW, this.segW[var5], 18.0F);
      float var16 = this.h - 4.0F;
      NwDraw.shadow(var1, this.indicatorX, this.y + 2.0F, this.indicatorW, var16, var16 / 2.6F, 6.0F, NwDraw.withAlpha(var10, 50));
      NwDraw.round(
         var1,
         this.indicatorX,
         this.y + 2.0F,
         this.indicatorW,
         var16,
         var16 / 2.6F,
         NwDraw.withAlpha(NwDraw.mix(-13946792, var10, 0.45F), 235),
         NwDraw.withAlpha(NwDraw.mix(-14341818, NwDraw.accent(180), 0.35F), 235)
      );
      NwDraw.border(var1, this.indicatorX, this.y + 2.0F, this.indicatorW, var16, var16 / 2.6F, 0.5F, 587202559);

      for (int var13 = 0; var13 < 3; var13++) {
         boolean var14 = NwDraw.hovered((double)var6, (double)var7, this.segX[var13], this.y, this.segW[var13], this.h);
         this.hover[var13] = NwDraw.approach(this.hover[var13], var14 && var13 != var5 ? 1.0F : 0.0F, 18.0F);
         if (this.hover[var13] > 0.02F) {
            NwDraw.round(
               var1, this.segX[var13], this.y + 2.0F, this.segW[var13], var16, var16 / 2.6F, NwDraw.withAlpha(-1, Math.round(14.0F * this.hover[var13]))
            );
         }

         int var15 = var13 == var5 ? -1 : NwDraw.mix(-8552041, -3946796, this.hover[var13]);
         NwDraw.textCentered(
            var9, var1, label(var13), this.segX[var13] + this.segW[var13] / 2.0F, NwDraw.textTop(this.y + this.h / 2.0F, NwDraw.moduleSize()), var15
         );
      }
   }

   public boolean isOver(double var1, double var3) {
      return NwDraw.hovered(var1, var3, this.x, this.y, this.w, this.h);
   }

   public boolean click(double var1, double var3, int var5, int var6) {
      if (!this.isOver(var1, var3)) {
         return false;
      } else if (var5 != 0) {
         return true;
      } else {
         for (int var7 = 0; var7 < 3; var7++) {
            if (NwDraw.hovered(var1, var3, this.segX[var7], this.y, this.segW[var7], this.h) && var7 != var6) {
               open(var7);
               return true;
            }
         }

         return true;
      }
   }

   public static void open(int var0) {
      try {
         Managers.SOUND.playSwipeIn();
      } catch (Throwable var3) {
      }

      switch (var0) {
         case 0:
            Module.mc.method_1507(ClickGUI.getClickGui());
            break;
         case 1:
            Module.mc.method_1507(HudEditorGui.getHudGui());
            break;
         default:
            try {
               Windows var1 = ModuleManager.windows;
               if (var1.isEnabled()) {
                  var1.disable();
               }

               var1.enable();
            } catch (Throwable var2) {
               Module.mc.method_1507(null);
            }
      }
   }
}
