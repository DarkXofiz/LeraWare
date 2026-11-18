package neva.ware.gui.clickui.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.NevaWare;
import neva.ware.core.Managers;
import neva.ware.gui.clickui.AbstractElement;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwEl;
import neva.ware.gui.clickui.NwSized;
import neva.ware.gui.font.FontRenderer;
import neva.ware.setting.Setting;
import neva.ware.utility.math.MathUtility;

public class SliderElement extends AbstractElement implements NwSized {
   private final float min;
   private final float max;
   private float animation;
   private float prevValue;
   private boolean dragging;
   private boolean listening;
   public String Stringnumber = "";
   private float hoverAnim;
   private float dragAnim;
   private float listenAnim;
   private long caret = System.nanoTime();

   public SliderElement(Setting var1) {
      super(var1);
      this.min = ((Number)var1.getMin()).floatValue();
      this.max = ((Number)var1.getMax()).floatValue();
      this.animation = this.fraction();
   }

   @Override
   public float prefHeight() {
      return 21.0F;
   }

   public boolean isListening() {
      return this.listening;
   }

   private float value() {
      return ((Number)this.setting.getValue()).floatValue();
   }

   private float fraction() {
      return this.max - this.min == 0.0F ? 0.0F : NwDraw.clamp((this.value() - this.min) / (this.max - this.min), 0.0F, 1.0F);
   }

   private String format() {
      Object var1 = this.setting.getValue();
      if (var1 instanceof Integer var2) {
         return String.valueOf(var2);
      } else {
         try {
            return new BigDecimal(((Number)var1).doubleValue()).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
         } catch (Throwable var3) {
            return String.valueOf(var1);
         }
      }
   }

   private float trackX() {
      return this.x + 5.0F + 1.0F + NwEl.indent(this.setting);
   }

   private float trackW() {
      return this.width - 12.0F - NwEl.indent(this.setting);
   }

   @Override
   public void render(class_332 var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      class_4587 var5 = var1.method_51448();
      int var6 = NwDraw.accent(0);
      int var7 = NwDraw.accent(180);
      if (this.dragging) {
         this.setValue(var2, (double)this.trackX(), (double)this.trackW());
      }

      this.animation = NwDraw.approach(this.animation, this.fraction(), 20.0F);
      this.hoverAnim = NwDraw.approach(this.hoverAnim, !this.hovered && !this.dragging ? 0.0F : 1.0F, 18.0F);
      this.dragAnim = NwDraw.approach(this.dragAnim, this.dragging ? 1.0F : 0.0F, 18.0F);
      this.listenAnim = NwDraw.approach(this.listenAnim, this.listening ? 1.0F : 0.0F, 16.0F);
      if (this.hovered) {
         NwEl.hand();
      }

      float var8 = NwEl.indent(this.setting);
      NwEl.guides(var5, this.x, this.y, this.height, this.setting);
      NwEl.hoverBg(var5, this.x + 2.0F + var8, this.y + 0.5F, this.width - 4.0F - var8, this.height - 1.0F, this.hoverAnim * 0.8F);
      float var9 = this.y + 6.5F;
      FontRenderer var10 = NwDraw.settingFont();
      String var11 = this.listening ? this.Stringnumber : this.format();
      float var12 = NwDraw.width(var10, var11);
      float var13 = this.x + this.width - 5.0F - 1.0F;
      if (this.listenAnim > 0.02F) {
         float var14 = Math.max(var12 + 8.0F, 18.0F);
         NwDraw.round(var5, var13 - var14 + 2.0F, var9 - 5.0F, var14, 10.0F, 3.0F, NwDraw.withAlpha(var6, Math.round(60.0F * this.listenAnim)));
         NwDraw.border(var5, var13 - var14 + 2.0F, var9 - 5.0F, var14, 10.0F, 3.0F, 0.5F, NwDraw.withAlpha(var6, Math.round(160.0F * this.listenAnim)));
      }

      NwDraw.text(var10, var5, var11, var13 - var12, NwEl.textY(var9), this.listening ? -1 : NwDraw.mix(-8552041, -1, this.hoverAnim));
      if (this.listening && (System.nanoTime() - this.caret) / 530000000L % 2L == 0L) {
         NwDraw.round(var5, var13 + 0.6F, var9 - 3.5F, 0.7F, 7.0F, 0.3F, -570425345);
      }

      NwEl.label(var5, this.setting.getName(), this.x + 5.0F + 1.0F + var8, var9, this.width - var12 - 20.0F - var8, this.hoverAnim);
      float var21 = this.trackX();
      float var15 = this.trackW();
      float var16 = this.y + 14.5F;
      float var17 = 3.0F;
      NwDraw.round(var5, var21, var16, var15, var17, 1.5F, 654311423);
      float var18 = Math.max(var17, var15 * this.animation);
      if (this.animation > 0.001F) {
         NwDraw.shadow(var5, var21, var16, var18, var17, 1.5F, 3.0F, NwDraw.withAlpha(var6, Math.round(40.0F + 40.0F * this.hoverAnim)));
         NwDraw.roundQuad(var5, var21, var16, var18, var17, 1.5F, var6, var7, var7, var6);
      }

      float var19 = var21 + var15 * this.animation;
      float var20 = 2.6F + 0.7F * this.hoverAnim + 0.3F * this.dragAnim;
      NwDraw.circle(var5, var19, var16 + var17 / 2.0F, var20 + 1.2F, NwDraw.withAlpha(var6, Math.round(70.0F * this.hoverAnim)));
      NwDraw.circle(var5, var19, var16 + var17 / 2.0F, var20, -1);
   }

   private void setValue(int var1, double var2, double var4) {
      float var6 = (float)((double)this.min + (double)(this.max - this.min) * Math.max(0.0, Math.min(1.0, ((double)var1 - var2) / var4)));
      Object var7 = this.setting.getValue();
      if (var7 instanceof Float) {
         this.setting.setValue(MathUtility.round2((double)var6));
      } else if (var7 instanceof Integer) {
         this.setting.setValue(Math.round(var6));
      } else if (var7 instanceof Double) {
         this.setting.setValue((double)MathUtility.round2((double)var6));
      }

      float var8 = this.value();
      if (var8 != this.prevValue) {
         try {
            Managers.SOUND.playSlider();
         } catch (Throwable var10) {
         }
      }

      this.prevValue = var8;
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) {
      if (var3 == 0 && this.hovered) {
         this.dragging = true;
         this.listening = false;
      } else if (this.hovered) {
         this.Stringnumber = "";
         this.listening = true;
         this.caret = System.nanoTime();
      } else if (this.listening) {
         this.listening = false;
         this.Stringnumber = "";
      }

      if (this.listening) {
         NevaWare.currentKeyListener = NevaWare.KeyListening.Sliders;
      }

      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      this.dragging = false;
   }

   @Override
   public void keyTyped(int var1) {
      if (NevaWare.currentKeyListener == NevaWare.KeyListening.Sliders) {
         if (this.listening) {
            switch (var1) {
               case 256:
               case 261:
                  this.listening = false;
                  this.Stringnumber = "";
                  break;
               case 257:
               case 335:
                  try {
                     this.searchNumber();
                  } catch (Exception var3) {
                     this.Stringnumber = "";
                     this.listening = false;
                  }
                  break;
               case 259:
                  this.Stringnumber = removeLastChar(this.Stringnumber);
            }
         }
      }
   }

   @Override
   public void charTyped(char var1, int var2) {
      if (this.listening) {
         if (Character.isDigit(var1) || var1 == '.' || var1 == '-') {
            if (this.Stringnumber.length() < 12) {
               this.Stringnumber = this.Stringnumber + var1;
            }

            this.caret = System.nanoTime();
         }
      }
   }

   public static String removeLastChar(String var0) {
      String var1 = "";
      if (var0 != null && !var0.isEmpty()) {
         var1 = var0.substring(0, var0.length() - 1);
      }

      return var1;
   }

   private void searchNumber() {
      Object var1 = this.setting.getValue();
      if (var1 instanceof Float) {
         this.setting.setValue(Float.valueOf(this.Stringnumber));
      } else if (var1 instanceof Integer) {
         this.setting.setValue(Integer.valueOf(this.Stringnumber));
      } else if (var1 instanceof Double) {
         this.setting.setValue(Double.valueOf(this.Stringnumber));
      }

      this.Stringnumber = "";
      this.listening = false;
   }
}
