package neva.ware.features.modules.client;

import neva.ware.features.modules.Module;
import neva.ware.gui.windows.WindowsScreen;
import neva.ware.gui.windows.impl.ConfigWindow;
import neva.ware.gui.windows.impl.FriendsWindow;
import neva.ware.gui.windows.impl.MacroWindow;
import neva.ware.gui.windows.impl.ProxyWindow;
import neva.ware.gui.windows.impl.WaypointWindow;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.PositionSetting;

public class Windows extends Module {
   public final Setting<PositionSetting> macroPos = new Setting<>("macroPos", new PositionSetting(0.3F, 0.3F), v -> false);
   public final Setting<PositionSetting> configPos = new Setting<>("configPos", new PositionSetting(0.35F, 0.35F), v -> false);
   public final Setting<PositionSetting> friendPos = new Setting<>("friendPos", new PositionSetting(0.4F, 0.4F), v -> false);
   public final Setting<PositionSetting> waypointPos = new Setting<>("waypointPos", new PositionSetting(0.45F, 0.45F), v -> false);
   public final Setting<PositionSetting> proxyPos = new Setting<>("proxyPos", new PositionSetting(0.5F, 0.5F), v -> false);

   public Windows() {
      super("Windows", Module.Category.CLIENT);
   }

   @Override
   public void onEnable() {
      mc.method_1507(
         new WindowsScreen(
            MacroWindow.get(
               this.macroPos.getValue().getX() * (float)mc.method_22683().method_4486(),
               this.macroPos.getValue().getY() * (float)mc.method_22683().method_4502(),
               this.macroPos
            ),
            ConfigWindow.get(
               this.configPos.getValue().getX() * (float)mc.method_22683().method_4486(),
               this.configPos.getValue().getY() * (float)mc.method_22683().method_4502(),
               this.configPos
            ),
            FriendsWindow.get(
               this.friendPos.getValue().getX() * (float)mc.method_22683().method_4486(),
               this.friendPos.getValue().getY() * (float)mc.method_22683().method_4502(),
               this.friendPos
            ),
            WaypointWindow.get(
               this.waypointPos.getValue().getX() * (float)mc.method_22683().method_4486(),
               this.waypointPos.getValue().getY() * (float)mc.method_22683().method_4502(),
               this.waypointPos
            ),
            ProxyWindow.get(
               this.proxyPos.getValue().getX() * (float)mc.method_22683().method_4486(),
               this.proxyPos.getValue().getY() * (float)mc.method_22683().method_4502(),
               this.proxyPos
            )
         )
      );
      this.disable();
   }
}
