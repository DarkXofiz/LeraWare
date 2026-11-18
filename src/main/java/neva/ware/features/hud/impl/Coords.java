package neva.ware.features.hud.impl;

import net.minecraft.class_332;
import net.minecraft.class_746;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.setting.Setting;
import neva.ware.utility.player.PlayerUtility;

public class Coords extends HudElement {
   private final Setting<Coords.NetherCoords> netherCoords = new Setting<>("NetherCoords", Coords.NetherCoords.On);
   private final NwHud.Anim anim = new NwHud.Anim();

   public Coords() {
      super("Coords", 100, 10);
   }

   @Override
   public void onRender2D(class_332 var1) {
      super.onRender2D(var1);
      class_746 var2 = mc.field_1724;
      if (var2 != null) {
         double var3 = var2.method_23317();
         double var5 = var2.method_23318();
         double var7 = var2.method_23321();
         String var9 = (int)Math.floor(var3) + " " + (int)Math.floor(var5) + " " + (int)Math.floor(var7);
         Coords.NetherCoords var10 = this.netherCoords.getValue();
         boolean var11 = false;

         try {
            var11 = PlayerUtility.isInHell();
         } catch (Throwable var14) {
         }

         if (var10 == Coords.NetherCoords.On || var10 == Coords.NetherCoords.OnlyNether && var11) {
            double var12 = var11 ? 8.0 : 0.125;
            var9 = var9 + "  (" + (int)Math.floor(var3 * var12) + " " + (int)Math.floor(var7 * var12) + ")";
         }

         NwInfoPill.render(this, var1, this.anim, NwHud.Icon.PIN, var9, null);
      }
   }

   public static enum NetherCoords {
      Off,
      On,
      OnlyNether;
   }
}
