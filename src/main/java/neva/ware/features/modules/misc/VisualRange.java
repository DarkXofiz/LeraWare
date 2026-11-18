package neva.ware.features.modules.misc;

import java.util.ArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventEntityRemoved;
import neva.ware.events.impl.EventEntitySpawn;
import neva.ware.features.modules.Module;
import neva.ware.gui.notification.Notification;
import neva.ware.setting.Setting;

public class VisualRange extends Module {
   private static final ArrayList<String> entities = new ArrayList<>();
   private final Setting<Boolean> leave = new Setting<>("Leave", true);
   private final Setting<Boolean> enter = new Setting<>("Enter", true);
   private final Setting<Boolean> friends = new Setting<>("Friends", true);
   private final Setting<Boolean> soundpl = new Setting<>("Sound", true);
   private final Setting<VisualRange.Mode> mode = new Setting<>("Mode", VisualRange.Mode.Notification);

   public VisualRange() {
      super("VisualRange", Module.Category.MISC);
   }

   @EventHandler
   public void onEntityAdded(EventEntitySpawn event) {
      if (this.isValid(event.getEntity())) {
         if (!entities.contains(event.getEntity().method_5477().getString())) {
            entities.add(event.getEntity().method_5477().getString());
            if (this.enter.getValue()) {
               this.notify(event.getEntity(), true);
            }
         }
      }
   }

   @EventHandler
   public void onEntityRemoved(EventEntityRemoved event) {
      if (this.isValid(event.entity)) {
         if (entities.contains(event.entity.method_5477().getString())) {
            entities.remove(event.entity.method_5477().getString());
            if (this.leave.getValue()) {
               this.notify(event.entity, false);
            }
         }
      }
   }

   public void notify(class_1297 entity, boolean enter) {
      String message = "";
      if (ModuleManager.nameProtect.isEnabled() && NameProtect.hideFriends.getValue()) {
         message = class_124.field_1075 + NameProtect.newName.getValue();
      }

      if (Managers.FRIEND.isFriend(entity.method_5477().getString())) {
         message = class_124.field_1075 + entity.method_5477().getString();
      } else {
         message = class_124.field_1080 + entity.method_5477().getString();
      }

      if (enter) {
         message = message + class_124.field_1060 + " was found!";
      } else {
         message = message + class_124.field_1061 + " left to X:" + (int)entity.method_23317() + " Z:" + (int)entity.method_23321();
      }

      if (this.mode.is(VisualRange.Mode.Chat) || this.mode.is(VisualRange.Mode.Both)) {
         this.sendMessage(message);
      }

      if (this.mode.is(VisualRange.Mode.Notification) || this.mode.is(VisualRange.Mode.Both)) {
         Managers.NOTIFICATION.publicity("VisualRange", message, 2, Notification.Type.WARNING);
      }

      if (this.soundpl.getValue()) {
         try {
            if (enter) {
               mc.field_1687.method_8396(mc.field_1724, mc.field_1724.method_24515(), class_3417.field_14709, class_3419.field_15245, 1.0F, 1.0F);
            } else {
               mc.field_1687.method_8396(mc.field_1724, mc.field_1724.method_24515(), class_3417.field_14627, class_3419.field_15245, 1.0F, 1.0F);
            }
         } catch (Exception var5) {
         }
      }
   }

   public boolean isValid(class_1297 entity) {
      return !(entity instanceof class_1657)
         ? false
         : entity != mc.field_1724 && (!Managers.FRIEND.isFriend(entity.method_5477().getString()) || this.friends.getValue());
   }

   public static enum Mode {
      Chat,
      Notification,
      Both;
   }
}
