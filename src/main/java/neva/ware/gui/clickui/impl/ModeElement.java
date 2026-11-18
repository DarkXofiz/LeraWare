package neva.ware.gui.clickui.impl;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.gui.clickui.AbstractElement;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwEl;
import neva.ware.gui.clickui.NwSized;
import neva.ware.gui.font.FontRenderer;
import neva.ware.setting.Setting;

public class ModeElement extends AbstractElement implements NwSized {
   public Setting setting2;
   private boolean open;
   private double wheight = 14.0;
   private String prevMode;
   private float animation;
   private float animation2;
   private float headerHover;
   private float[] chipX = new float[0];
   private float[] chipY = new float[0];
   private float[] chipW = new float[0];
   private float[] chipHover = new float[0];
   private float gridHeight;
   private static final float HEAD = 14.0F;
   private static final float CHIP_H = 11.0F;
   private static final float GAP = 3.0F;

   public ModeElement(Setting var1) {
      super(var1);
      this.setting2 = var1;
      this.prevMode = var1.currentEnumName();
   }

   private String[] modes() {
      try {
         String[] var1 = this.setting2.getModes();
         return var1 == null ? new String[0] : var1;
      } catch (Throwable var2) {
         return new String[0];
      }
   }

   private float layoutGrid() {
      String[] var1 = this.modes();
      if (this.chipX.length != var1.length) {
         this.chipX = new float[var1.length];
         this.chipY = new float[var1.length];
         this.chipW = new float[var1.length];
         this.chipHover = new float[var1.length];
      }

      FontRenderer var2 = NwDraw.settingFont();
      float var3 = NwEl.indent(this.setting);
      float var4 = this.x + 5.0F + 1.0F + var3;
      float var5 = this.width - 12.0F - var3;
      float var6 = 0.0F;
      float var7 = 0.0F;

      for (int var8 = 0; var8 < var1.length; var8++) {
         float var9 = Math.min(var5, NwDraw.width(var2, var1[var8]) + 10.0F);
         if (var6 > 0.0F && var6 + var9 > var5) {
            var6 = 0.0F;
            var7 += 14.0F;
         }

         this.chipX[var8] = var4 + var6;
         this.chipY[var8] = var7;
         this.chipW[var8] = var9;
         var6 += var9 + 3.0F;
      }

      return var1.length == 0 ? 0.0F : var7 + 11.0F + 4.0F;
   }

   @Override
   public float prefHeight() {
      this.gridHeight = this.layoutGrid();
      return 14.0F + this.gridHeight * this.animation;
   }

   @Override
   public void render(class_332 var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      class_4587 var5 = var1.method_51448();
      int var6 = NwDraw.accent(0);
      this.gridHeight = this.layoutGrid();
      this.animation = NwDraw.approach(this.animation, this.open ? 1.0F : 0.0F, 15.0F);
      boolean var7 = NwDraw.hovered((double)var2, (double)var3, this.x, this.y, this.width, 14.0F);
      this.headerHover = NwDraw.approach(this.headerHover, var7 ? 1.0F : 0.0F, 18.0F);
      this.animation2 = NwDraw.approach(this.animation2, 1.0F, 10.0F);
      if (var7) {
         NwEl.hand();
      }

      float var8 = NwEl.indent(this.setting);
      NwEl.guides(var5, this.x, this.y, this.height, this.setting);
      NwEl.hoverBg(var5, this.x + 2.0F + var8, this.y + 0.5F, this.width - 4.0F - var8, 13.0F, this.headerHover);
      float var9 = this.y + 7.0F;
      String var10 = this.setting2.currentEnumName();
      FontRenderer var11 = NwDraw.settingFont();
      float var12 = this.x + this.width - 5.0F - 1.0F;
      NwDraw.chevron(var5, var12 - 2.5F, var9 + 0.2F, 2.2F, 180.0F * this.animation, NwDraw.mix(-8552041, -1, this.headerHover));
      var12 -= 8.0F;
      float var13 = (this.width - var8) * 0.55F;
      String var14 = NwDraw.trim(var11, var10, var13 - 8.0F);
      int var15 = NwDraw.withAlpha(NwDraw.mix(-13946792, var6, 0.45F), Math.round(150.0F + 60.0F * this.headerHover));
      float var16 = NwEl.chip(var5, var14, var12, var9, var15, -1);
      NwEl.label(var5, this.setting.getName(), this.x + 5.0F + 1.0F + var8, var9, this.width - var16 - 24.0F - var8, this.headerHover);
      if (this.animation > 0.02F) {
         String[] var17 = this.modes();
         float var18 = NwDraw.alpha;
         NwDraw.alpha = var18 * this.animation;
         float var19 = this.y + 14.0F + 1.0F;

         for (int var20 = 0; var20 < var17.length; var20++) {
            float var21 = this.chipX[var20];
            float var22 = var19 + this.chipY[var20];
            boolean var23 = var17[var20].equalsIgnoreCase(var10);
            boolean var24 = this.animation > 0.9F && NwDraw.hovered((double)var2, (double)var3, var21, var22, this.chipW[var20], 11.0F);
            if (var24) {
               NwEl.hand();
            }

            this.chipHover[var20] = NwDraw.approach(this.chipHover[var20], var24 ? 1.0F : 0.0F, 18.0F);
            int var25 = var23
               ? NwDraw.withAlpha(NwDraw.mix(-13946792, var6, 0.55F), 230)
               : NwDraw.withAlpha(-1, Math.round(14.0F + 14.0F * this.chipHover[var20]));
            if (var23) {
               NwDraw.shadow(var5, var21, var22, this.chipW[var20], 11.0F, 3.5F, 4.0F, NwDraw.withAlpha(var6, 50));
            }

            NwDraw.round(var5, var21, var22, this.chipW[var20], 11.0F, 3.5F, var25);
            if (var23) {
               NwDraw.border(var5, var21, var22, this.chipW[var20], 11.0F, 3.5F, 0.5F, 822083583);
            }

            String var26 = NwDraw.trim(var11, var17[var20], this.chipW[var20] - 8.0F);
            NwDraw.textCentered(
               var11, var5, var26, var21 + this.chipW[var20] / 2.0F, NwEl.textY(var22 + 5.5F), var23 ? -1 : NwDraw.mix(-8552041, -1, this.chipHover[var20])
            );
         }

         NwDraw.alpha = var18;
      }
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) {
      if (NwDraw.hovered((double)var1, (double)var2, this.x, this.y, this.width, 14.0F)) {
         if (var3 == 0) {
            this.prevMode = this.setting2.currentEnumName();
            this.animation2 = 0.0F;
            this.setting2.increaseEnum();
            playBoolean();
         } else if (var3 == 1) {
            this.open = !this.open;

            try {
               if (this.open) {
                  Managers.SOUND.playSwipeIn();
               } else {
                  Managers.SOUND.playSwipeOut();
               }
            } catch (Throwable var7) {
            }
         }
      } else if (this.open && this.animation > 0.9F && var3 == 0) {
         String[] var4 = this.modes();
         float var5 = this.y + 14.0F + 1.0F;

         for (int var6 = 0; var6 < var4.length && var6 < this.chipX.length; var6++) {
            if (NwDraw.hovered((double)var1, (double)var2, this.chipX[var6], var5 + this.chipY[var6], this.chipW[var6], 11.0F)) {
               this.prevMode = this.setting2.currentEnumName();
               this.animation2 = 0.0F;
               this.setting2.setEnumByNumber(var6);
               playBoolean();
               break;
            }
         }
      }

      super.mouseClicked(var1, var2, var3);
   }

   private static void playBoolean() {
      try {
         Managers.SOUND.playBoolean();
      } catch (Throwable var1) {
      }
   }

   public void setWHeight(double var1) {
      this.wheight = var1;
   }

   public boolean isOpen() {
      return this.open;
   }
}
