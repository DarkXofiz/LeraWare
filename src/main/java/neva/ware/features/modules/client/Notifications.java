package neva.ware.features.modules.client;

import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public final class Notifications extends Module {
   public final Setting<Notifications.Mode> mode = new Setting<>("Mode", Notifications.Mode.Default);

   public Notifications() {
      super("Notifications", Module.Category.CLIENT);
   }

   public static enum Mode {
      Default,
      CrossHair,
      Text;
   }
}
