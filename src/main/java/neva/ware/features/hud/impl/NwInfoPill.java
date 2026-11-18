package neva.ware.features.hud.impl;

import java.util.List;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;

final class NwInfoPill {
   private NwInfoPill() {
   }

   static void render(HudElement var0, class_332 var1, NwHud.Anim var2, NwHud.Icon var3, String var4, String var5) {
      class_4587 var6 = var1.method_51448();
      float var7 = var0.getPosX();
      float var8 = var0.getPosY();
      float var9 = NwHud.segBar(var6, var7, var8, List.of(new NwHud.Seg(var3, var4, var5, false)), var2);
      var0.setBounds(var7, var8, var9, 15.0F);
   }
}
