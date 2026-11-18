package neva.ware.features.modules.client;

import net.minecraft.class_243;
import net.minecraft.class_3532;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventFixVelocity;
import neva.ware.events.impl.EventKeyboardInput;
import neva.ware.events.impl.EventPlayerJump;
import neva.ware.events.impl.EventPlayerTravel;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.Aura;
import neva.ware.setting.Setting;

public class Rotations extends Module {
   private final Setting<Rotations.MoveFix> moveFix = new Setting<>("MoveFix", Rotations.MoveFix.Off);
   public final Setting<Boolean> clientLook = new Setting<>("ClientLook", false);
   public float fixRotation;
   private float prevYaw;
   private float prevPitch;

   public Rotations() {
      super("Rotations", Module.Category.CLIENT);
   }

   public void onJump(EventPlayerJump e) {
      if (!Float.isNaN(this.fixRotation) && this.moveFix.getValue() != Rotations.MoveFix.Off && !mc.field_1724.method_3144()) {
         if (e.isPre()) {
            this.prevYaw = mc.field_1724.method_36454();
            mc.field_1724.method_36456(this.fixRotation);
         } else {
            mc.field_1724.method_36456(this.prevYaw);
         }
      }
   }

   public void onPlayerMove(EventFixVelocity event) {
      if (this.moveFix.getValue() == Rotations.MoveFix.Free) {
         if (Float.isNaN(this.fixRotation) || mc.field_1724.method_3144()) {
            return;
         }

         event.setVelocity(this.fix(this.fixRotation, event.getMovementInput(), event.getSpeed()));
      }
   }

   public void modifyVelocity(EventPlayerTravel e) {
      if (ModuleManager.aura.isEnabled()
         && Aura.target != null
         && ModuleManager.aura.rotationMode.not(null)
         && ModuleManager.aura.elytraTarget.getValue()
         && Managers.PLAYER.ticksElytraFlying > 5) {
         if (e.isPre()) {
            this.prevYaw = mc.field_1724.method_36454();
            this.prevPitch = mc.field_1724.method_36455();
            mc.field_1724.method_36456(this.fixRotation);
            mc.field_1724.method_36457(ModuleManager.aura.rotationPitch);
         } else {
            mc.field_1724.method_36456(this.prevYaw);
            mc.field_1724.method_36457(this.prevPitch);
         }
      } else {
         if (this.moveFix.getValue() == Rotations.MoveFix.Focused && !Float.isNaN(this.fixRotation) && !mc.field_1724.method_3144()) {
            if (e.isPre()) {
               this.prevYaw = mc.field_1724.method_36454();
               mc.field_1724.method_36456(this.fixRotation);
            } else {
               mc.field_1724.method_36456(this.prevYaw);
            }
         }
      }
   }

   public void onKeyInput(EventKeyboardInput e) {
      if (this.moveFix.getValue() == Rotations.MoveFix.Free) {
         if (Float.isNaN(this.fixRotation) || mc.field_1724.method_3144()) {
            return;
         }

         float mF = mc.field_1724.field_3913.field_3905;
         float mS = mc.field_1724.field_3913.field_3907;
         float delta = (mc.field_1724.method_36454() - this.fixRotation) * (float) (Math.PI / 180.0);
         float cos = class_3532.method_15362(delta);
         float sin = class_3532.method_15374(delta);
         mc.field_1724.field_3913.field_3907 = (float)Math.round(mS * cos - mF * sin);
         mc.field_1724.field_3913.field_3905 = (float)Math.round(mF * cos + mS * sin);
      }
   }

   private class_243 fix(float yaw, class_243 movementInput, float speed) {
      double d = movementInput.method_1027();
      if (d < 1.0E-7) {
         return class_243.field_1353;
      } else {
         class_243 vec3d = (d > 1.0 ? movementInput.method_1029() : movementInput).method_1021((double)speed);
         float f = class_3532.method_15374(yaw * (float) (Math.PI / 180.0));
         float g = class_3532.method_15362(yaw * (float) (Math.PI / 180.0));
         return new class_243(
            vec3d.field_1352 * (double)g - vec3d.field_1350 * (double)f, vec3d.field_1351, vec3d.field_1350 * (double)g + vec3d.field_1352 * (double)f
         );
      }
   }

   @Override
   public boolean isToggleable() {
      return false;
   }

   private static enum MoveFix {
      Off,
      Focused,
      Free;
   }
}
