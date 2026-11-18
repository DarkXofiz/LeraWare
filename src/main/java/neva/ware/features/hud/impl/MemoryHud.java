package neva.ware.features.hud.impl;

import net.minecraft.class_332;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;

public class MemoryHud extends HudElement {
   private final NwHud.Anim anim = new NwHud.Anim();

   public MemoryHud() {
      super("MemoryHud", 100, 10);
   }

   private long toMiB(long var1) {
      return var1 / 1024L / 1024L;
   }

   @Override
   public void onRender2D(class_332 var1) {
      super.onRender2D(var1);
      Runtime var2 = Runtime.getRuntime();
      long var3 = var2.totalMemory() - var2.freeMemory();
      NwInfoPill.render(this, var1, this.anim, NwHud.Icon.MEMORY, this.toMiB(var3) + " / " + this.toMiB(var2.maxMemory()), "MB");
   }
}
