package neva.ware.features.modules.render;

import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class Fullbright extends Module {
   public static Setting<Float> minBright = new Setting<>("MinBright", 0.5F, 0.0F, 1.0F);

   public Fullbright() {
      super("Fullbright", Module.Category.RENDER);
   }
}
