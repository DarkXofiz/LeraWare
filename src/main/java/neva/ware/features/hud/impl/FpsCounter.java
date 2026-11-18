package neva.ware.features.hud.impl;

import net.minecraft.class_332;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.utility.math.FrameRateCounter;

public class FpsCounter extends HudElement {
   private final NwHud.Anim anim = new NwHud.Anim();

   public FpsCounter() {
      super("Fps", 50, 10);
   }

   @Override
   public void onRender2D(class_332 var1) {
      super.onRender2D(var1);
      NwInfoPill.render(this, var1, this.anim, NwHud.Icon.FPS, String.valueOf(FrameRateCounter.INSTANCE.getFps()), "Fps");
   }
}
