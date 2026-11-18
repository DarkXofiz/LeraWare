package neva.ware.gui.clickui.impl;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.gui.clickui.AbstractElement;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwEl;
import neva.ware.gui.clickui.NwSized;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.Bind;

public class BindElement extends AbstractElement implements NwSized {
   public boolean isListening;
   private float hover;
   private float listen;

   public BindElement(Setting var1) {
      super(var1);
   }

   @Override
   public float prefHeight() {
      return 14.0F;
   }

   private String keyName() {
      if (this.setting.getValue() instanceof Bind var2) {
         String var3 = var2.getBind();
         if (var3 != null && !var3.equalsIgnoreCase("none")) {
            return switch (var3) {
               case "LEFT_CONTROL" -> "LCtrl";
               case "RIGHT_CONTROL" -> "RCtrl";
               case "LEFT_SHIFT" -> "LShift";
               case "RIGHT_SHIFT" -> "RShift";
               case "LEFT_ALT" -> "LAlt";
               case "RIGHT_ALT" -> "RAlt";
               default -> var3.length() > 10 ? var3.substring(0, 10) : var3;
            };
         } else {
            return "None";
         }
      } else {
         return "None";
      }
   }

   @Override
   public void render(class_332 var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      class_4587 var5 = var1.method_51448();
      int var6 = NwDraw.accent(0);
      this.hover = NwDraw.approach(this.hover, this.hovered ? 1.0F : 0.0F, 18.0F);
      this.listen = NwDraw.approach(this.listen, this.isListening ? 1.0F : 0.0F, 16.0F);
      if (this.hovered) {
         NwEl.hand();
      }

      float var7 = NwEl.indent(this.setting);
      NwEl.guides(var5, this.x, this.y, this.height, this.setting);
      NwEl.hoverBg(var5, this.x + 2.0F + var7, this.y + 0.5F, this.width - 4.0F - var7, this.height - 1.0F, this.hover);
      float var8 = this.y + this.height / 2.0F;
      String var9 = this.isListening ? "..." : this.keyName();
      int var10 = NwDraw.mix(
         NwDraw.withAlpha(-1, Math.round(18.0F + 12.0F * this.hover)), NwDraw.withAlpha(NwDraw.mix(-13946792, var6, 0.55F), 230), this.listen
      );
      float var11 = NwEl.chip(var5, var9, this.x + this.width - 5.0F - 1.0F, var8, var10, NwDraw.mix(-3946796, -1, Math.max(this.hover, this.listen)));
      NwEl.label(var5, this.setting.getName(), this.x + 5.0F + 1.0F + var7, var8, this.width - var11 - 16.0F - var7, this.hover);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) {
      if (!this.isListening) {
         if (this.hovered && var3 == 0) {
            this.isListening = true;
         }

         super.mouseClicked(var1, var2, var3);
      } else {
         if (!this.hovered || var3 != 0) {
            this.setting.setValue(new Bind(var3, true, false));
         }

         this.isListening = false;
      }
   }

   @Override
   public void keyTyped(int var1) {
      if (this.isListening) {
         if (var1 != 256 && var1 != 261) {
            this.setting.setValue(new Bind(var1, false, false));
         } else {
            this.setting.setValue(new Bind(-1, false, false));
         }

         this.isListening = false;
      }
   }
}
