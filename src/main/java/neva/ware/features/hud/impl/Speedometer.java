package neva.ware.features.hud.impl;

import net.minecraft.class_332;
import neva.ware.NevaWare;
import neva.ware.core.Managers;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.setting.Setting;

public class Speedometer extends HudElement {
   public float speed;
   private final Setting<Boolean> bps = new Setting<>("BPS", false);
   private final Setting<Boolean> average = new Setting<>("Average", false);
   private final NwHud.Anim anim = new NwHud.Anim();

   public Speedometer() {
      super("Speedometer", 50, 10);
   }

   @Override
   public void onRender2D(class_332 var1) {
      super.onRender2D(var1);
      boolean var2 = this.bps.getValue();
      float var3 = (var2 ? this.getSpeedMpS() : this.getSpeedKpH()) * NevaWare.TICK_TIMER;
      NwInfoPill.render(this, var1, this.anim, NwHud.Icon.SPEED, String.format("%.1f", var3), var2 ? "bps" : "km/h");
   }

   public float getSpeedKpH() {
      return (this.average.getValue() ? Managers.PLAYER.averagePlayerSpeed : Managers.PLAYER.currentPlayerSpeed) * 72.0F;
   }

   public float getSpeedMpS() {
      return (this.average.getValue() ? Managers.PLAYER.averagePlayerSpeed : Managers.PLAYER.currentPlayerSpeed) * 20.0F;
   }
}
