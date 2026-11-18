package neva.ware.events.impl;

import neva.ware.events.Event;
import neva.ware.setting.Setting;

public class EventSetting extends Event {
   final Setting<?> setting;

   public EventSetting(Setting<?> setting) {
      this.setting = setting;
   }

   public Setting<?> getSetting() {
      return this.setting;
   }
}
