package neva.ware.features.hud.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_4587;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.utility.render.Render2DEngine;

final class NwPanel {
   final NwHud.Anim width = new NwHud.Anim();
   final NwHud.Anim height = new NwHud.Anim();
   final NwHud.Anim visible = new NwHud.Anim(0.0F);
   private final Map<String, NwHud.Anim> rows = new HashMap<>();
   float rowsY;
   float x;
   float y;
   float w;
   float h;
   float alpha;

   float row(String var1) {
      return this.rows.computeIfAbsent(var1, var0 -> new NwHud.Anim(0.0F)).to(1.0F, 12.0F);
   }

   void forget(Set<String> var1) {
      this.rows.keySet().removeIf(var1x -> !var1.contains(var1x));
   }

   boolean begin(HudElement var1, class_4587 var2, NwHud.Icon var3, String var4, String var5, float var6, float var7, boolean var8) {
      this.x = var1.getPosX();
      this.y = var1.getPosY();
      this.alpha = this.visible.to(var8 ? 1.0F : 0.0F, 12.0F);
      float var9 = Math.max(var6, 13.0F + NwHud.wb(var4) + (var5 == null ? 0.0F : NwHud.ws(var5) + 10.0F) + 12.0F);
      this.w = this.width.to(Math.max(92.0F, var9), 14.0F);
      this.h = this.height.to(16.0F + (var7 > 0.0F ? var7 + 4.0F : 0.0F), 14.0F);
      var1.setBounds(this.x, this.y, this.w, Math.max(this.h, 16.0F));
      if (this.alpha < 0.01F) {
         return false;
      } else {
         NwDraw.alpha = this.alpha;
         this.rowsY = NwHud.panel(var2, this.x, this.y, this.w, this.h, var3, var4, var5);
         Render2DEngine.addWindow(var2, this.x, this.y + 16.0F, this.x + this.w, this.y + this.h - 1.0F, 1.0);
         return true;
      }
   }

   void end() {
      Render2DEngine.popWindow();
      NwDraw.alpha = 1.0F;
   }
}
