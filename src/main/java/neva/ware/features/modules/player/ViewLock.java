package neva.ware.features.modules.player;

import net.minecraft.class_4587;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class ViewLock extends Module {
   private final Setting<Boolean> lockCurrent = new Setting<>("LockCurrent", true);
   public final Setting<Boolean> pitch = new Setting<>("Pitch", true);
   public final Setting<Float> pitchValue = new Setting<>("PitchValue", 0.0F, -90.0F, 90.0F, v -> this.pitch.getValue());
   public final Setting<Boolean> yaw = new Setting<>("Yaw", true);
   public final Setting<Float> yawValue = new Setting<>("YawValue", 0.0F, -180.0F, 180.0F, v -> this.yaw.getValue());

   public ViewLock() {
      super("ViewLock", Module.Category.PLAYER);
   }

   @Override
   public void onEnable() {
      if (this.lockCurrent.getValue()) {
         this.yawValue.setValue(mc.field_1724.method_36454());
         this.pitchValue.setValue(mc.field_1724.method_36455());
      }
   }

   @Override
   public void onRender3D(class_4587 m) {
      if (this.pitch.getValue()) {
         mc.field_1724.method_36457(this.pitchValue.getValue());
      }

      if (this.yaw.getValue()) {
         mc.field_1724.method_36456(this.yawValue.getValue());
      }
   }
}
