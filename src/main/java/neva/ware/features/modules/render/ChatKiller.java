package neva.ware.features.modules.render;

import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;

public class ChatKiller extends Module {
   public Setting<String> text = new Setting<>("Text", "ChatKiller!");
   public Setting<Float> delay = new Setting<>("Delay", 3.0F, 0.1F, 20.0F);
   private final Timer timer = new Timer();

   public ChatKiller() {
      super("ChatKiller", Module.Category.RENDER);
   }

   @Override
   public void onUpdate() {
      if (this.timer.passedMs((long)(this.delay.getValue() * 1000.0F))) {
         String msg = this.text.getValue();
         if (msg != null && !msg.isEmpty()) {
            if (msg.startsWith("/")) {
               mc.field_1724.field_3944.method_45731(msg.substring(1));
            } else {
               mc.field_1724.field_3944.method_45729(msg);
            }
         }

         this.timer.reset();
      }
   }
}
