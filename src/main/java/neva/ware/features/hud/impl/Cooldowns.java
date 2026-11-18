package neva.ware.features.hud.impl;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;

public class Cooldowns extends HudElement {
   private final NwPanel panel = new NwPanel();
   private final NwHud.Anim attack = new NwHud.Anim();
   private final NwHud.Anim hurt = new NwHud.Anim();

   public Cooldowns() {
      super("Cooldowns", 100, 100);
   }

   @Override
   public void onRender2D(class_332 var1) {
      super.onRender2D(var1);
      class_4587 var2 = var1.method_51448();
      class_746 var3 = mc.field_1724;
      float var4 = var3 == null ? 1.0F : var3.method_7261(0.5F);
      float var5 = var3 == null ? 0.0F : (float)var3.field_6235 / 10.0F;
      float var6 = this.attack.to(var4, 16.0F);
      float var7 = this.hurt.to(var5, 16.0F);
      if (this.panel.begin(this, var2, NwHud.Icon.TIMER, "Cooldowns", null, 120.0F, 24.0F, true)) {
         float var8 = this.panel.rowsY;
         this.row(var2, "Attack", var6, var8);
         this.row(var2, "Hurt", var7, var8 + 12.0F);
         this.panel.end();
      }
   }

   private void row(class_4587 var1, String var2, float var3, float var4) {
      float var5 = var4 + 6.0F;
      NwHud.text(var1, var2, this.panel.x + 6.0F, var5, NwHud.textColor());
      String var6 = Math.round(var3 * 100.0F) + "%";
      float var7 = this.panel.x + this.panel.w - 6.0F;
      NwHud.textSmall(var1, var6, var7 - NwHud.ws(var6), var5, NwHud.textColor2());
      float var8 = 46.0F;
      NwHud.bar(var1, var7 - NwHud.ws("100%") - 4.0F - var8, var5 - 1.5F, var8, 3.0F, var3);
   }
}
