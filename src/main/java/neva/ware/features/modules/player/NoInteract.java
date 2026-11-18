package neva.ware.features.modules.player;

import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class NoInteract extends Module {
   public static Setting<Boolean> onlyAura = new Setting<>("OnlyAura", false);

   public NoInteract() {
      super("NoInteract", Module.Category.PLAYER);
   }
}
