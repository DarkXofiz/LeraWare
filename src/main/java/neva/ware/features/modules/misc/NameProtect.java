package neva.ware.features.modules.misc;

import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class NameProtect extends Module {
   public static Setting<String> newName = new Setting<>("name", "Hell_Raider");
   public static Setting<Boolean> hideFriends = new Setting<>("Hide friends", true);

   public NameProtect() {
      super("NameProtect", Module.Category.MISC);
   }

   public static String getCustomName() {
      return ModuleManager.nameProtect.isEnabled() ? newName.getValue().replaceAll("&", "§") : mc.method_53462().getName();
   }
}
