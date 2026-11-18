package neva.ware.features.modules.render;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2761;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.BooleanSettingGroup;
import neva.ware.setting.impl.ColorSetting;

public class WorldTweaks extends Module {
   public static final Setting<BooleanSettingGroup> fogModify = new Setting<>("FogModify", new BooleanSettingGroup(true));
   public static final Setting<Integer> fogStart = new Setting<>("FogStart", 0, 0, 256).addToGroup(fogModify);
   public static final Setting<Integer> fogEnd = new Setting<>("FogEnd", 64, 10, 256).addToGroup(fogModify);
   public static final Setting<ColorSetting> fogColor = new Setting<>("FogColor", new ColorSetting(new Color(11075839))).addToGroup(fogModify);
   public final Setting<Boolean> ctime = new Setting<>("ChangeTime", false);
   public final Setting<Integer> ctimeVal = new Setting<>("Time", 21, 0, 23);
   long oldTime;

   public WorldTweaks() {
      super("WorldTweaks", Module.Category.RENDER);
   }

   @Override
   public void onEnable() {
      this.oldTime = mc.field_1687.method_8510();
   }

   @Override
   public void onDisable() {
      mc.field_1687.method_8435(this.oldTime);
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof class_2761 && this.ctime.getValue()) {
         this.oldTime = ((class_2761)event.getPacket()).method_11871();
         event.cancel();
      }
   }

   @Override
   public void onUpdate() {
      if (this.ctime.getValue()) {
         mc.field_1687.method_8435((long)(this.ctimeVal.getValue() * 1000));
      }
   }
}
