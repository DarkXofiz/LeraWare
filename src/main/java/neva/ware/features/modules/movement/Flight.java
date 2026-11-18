package neva.ware.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_2708;
import net.minecraft.class_2743;
import net.minecraft.class_2846;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2846.class_2847;
import neva.ware.events.impl.EventSync;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.setting.Setting;
import neva.ware.utility.player.MovementUtility;

public class Flight extends Module {
   private final Setting<Flight.Mode> mode = new Setting<>("Mode", Flight.Mode.Vanilla);
   private final Setting<Float> hSpeed = new Setting<>("Horizontal", 1.0F, 0.0F, 10.0F, v -> !this.mode.is(Flight.Mode.StormBreak));
   private final Setting<Float> vSpeed = new Setting<>("Vertical", 0.78F, 0.0F, 5.0F, v -> !this.mode.is(Flight.Mode.StormBreak));
   private final Setting<Float> boostValue = new Setting<>("Boost", 1.0F, 0.1F, 1.0F, v -> this.mode.is(Flight.Mode.StormBreak));
   private final Setting<Boolean> autoToggle = new Setting<>("AutoToggle", false, v -> this.mode.is(Flight.Mode.MatrixJump));
   private final Setting<Integer> boostTicks = new Setting<>("Ticks", 8, 0, 40, v -> this.mode.is(Flight.Mode.Damage));
   private final Setting<Boolean> antiKick = new Setting<>("AntiKick", false, v -> this.mode.is(Flight.Mode.Creative) || this.mode.is(Flight.Mode.Vanilla));
   private double prevX;
   private double prevY;
   private double prevZ;
   private double velocityMotion;
   public boolean onPosLook = false;
   private int flyTicks = 0;

   public Flight() {
      super("Flight", Module.Category.MOVEMENT);
   }

   @EventHandler
   public void onEventSync(EventSync event) {
      switch ((Flight.Mode)this.mode.getValue()) {
         case Vanilla:
            if (MovementUtility.isMoving()) {
               double[] dir = MovementUtility.forward((double)this.hSpeed.getValue().floatValue());
               mc.field_1724.method_18800(dir[0], 0.0, dir[1]);
            } else {
               mc.field_1724.method_18800(0.0, 0.0, 0.0);
            }

            if (mc.field_1690.field_1903.method_1434()) {
               mc.field_1724.method_18799(mc.field_1724.method_18798().method_1031(0.0, (double)this.vSpeed.getValue().floatValue(), 0.0));
            }

            if (mc.field_1690.field_1832.method_1434()) {
               mc.field_1724.method_18799(mc.field_1724.method_18798().method_1031(0.0, (double)(-this.vSpeed.getValue()), 0.0));
            }
         case MatrixJump:
         default:
            break;
         case AirJump:
            if (MovementUtility.isMoving()
               && mc.field_1687
                  .method_20812(mc.field_1724, mc.field_1724.method_5829().method_1009(0.5, 0.0, 0.5).method_989(0.0, -1.0, 0.0))
                  .iterator()
                  .hasNext()) {
               mc.field_1724.method_24830(true);
               mc.field_1724.method_6043();
            }
            break;
         case MatrixGlide:
            if (mc.field_1724.method_24828()) {
               mc.field_1724.method_6043();
               this.flyTicks = 5;
            } else if (this.flyTicks > 0) {
               if (MovementUtility.isMoving()) {
                  double[] dir = MovementUtility.forward((double)this.hSpeed.getValue().floatValue());
                  mc.field_1724.method_18800(dir[0], -0.04, dir[1]);
               } else {
                  mc.field_1724.method_18800(0.0, -0.04, 0.0);
               }

               this.flyTicks--;
            }
            break;
         case StormBreak:
            if (mc.field_1724.field_6012 % 60 == 0) {
               this.sendMessage(class_124.field_1061 + (ClientSettings.isRu() ? "В этом режиме нужно ломать блоки!" : "In this mode you need to break blocks!"));
            }
      }

      if (this.antiKick.getValue() && (this.mode.is(Flight.Mode.Creative) || this.mode.is(Flight.Mode.Vanilla))) {
         mc.field_1724.method_18799(mc.field_1724.method_18798().method_1031(0.0, -0.08, 0.0));
      }
   }

   @Override
   public void onUpdate() {
      if (this.mode.is(Flight.Mode.Damage) && this.flyTicks-- > this.boostTicks.getValue()) {
         mc.field_1724.method_18800(mc.field_1724.method_18798().field_1352, this.velocityMotion, mc.field_1724.method_18798().method_10215());
      }

      if (this.mode.is(Flight.Mode.MatrixJump)) {
         if (mc.field_1724.field_6017 == 0.0F) {
            return;
         }

         mc.field_1724.method_31549().field_7479 = false;
         mc.field_1724.method_18800(0.0, 0.0, 0.0);
         if (mc.field_1690.field_1903.method_1434()) {
            mc.field_1724.method_18799(mc.field_1724.method_18798().method_1031(0.0, (double)this.vSpeed.getValue().floatValue(), 0.0));
         }

         if (mc.field_1690.field_1832.method_1434()) {
            mc.field_1724.method_18799(mc.field_1724.method_18798().method_1031(0.0, (double)(-this.vSpeed.getValue()), 0.0));
         }

         double[] dir = MovementUtility.forward((double)this.hSpeed.getValue().floatValue());
         mc.field_1724.method_18800(dir[0], mc.field_1724.method_18798().method_10214(), dir[1]);
      }

      if (this.mode.is(Flight.Mode.Creative)) {
         mc.field_1724.method_31549().field_7479 = true;
         mc.field_1724.method_31549().method_7248(this.hSpeed.getValue() / 10.0F);
      }
   }

   @EventHandler
   public void onPacketReceive(PacketEvent.Receive e) {
      if (this.mode.is(Flight.Mode.MatrixJump)) {
         if (fullNullCheck()) {
            return;
         }

         if (e.getPacket() instanceof class_2708) {
            this.onPosLook = true;
            this.prevX = mc.field_1724.method_18798().method_10216();
            this.prevY = mc.field_1724.method_18798().method_10214();
            this.prevZ = mc.field_1724.method_18798().method_10215();
         }
      }

      if (this.mode.is(Flight.Mode.Damage) && e.getPacket() instanceof class_2743 v && v.method_11816() / 8000.0 > 0.2) {
         this.velocityMotion = v.method_11816() / 8000.0;
         this.flyTicks = this.boostTicks.getValue();
      }
   }

   @EventHandler
   public void onPacketSend(PacketEvent.Send e) {
      if (this.mode.is(Flight.Mode.MatrixJump) && e.getPacket() instanceof class_2830 && this.onPosLook) {
         mc.field_1724.method_18800(this.prevX, this.prevY, this.prevZ);
         this.onPosLook = false;
         if (this.autoToggle.getValue()) {
            this.disable();
         }
      }

      if (this.mode.is(Flight.Mode.StormBreak)
         && e.getPacket() instanceof class_2846 pac
         && (
            pac.method_12363() == class_2847.field_12973
               || pac.method_12363() == class_2847.field_12968 && mc.field_1687.method_8320(pac.method_12362()).method_45474()
         )) {
         double[] dir = MovementUtility.forward((double)(2.0F * this.boostValue.getValue()));
         mc.field_1724.method_18800(dir[0], (double)(3.0F * this.boostValue.getValue()), dir[1]);
      }
   }

   @Override
   public void onDisable() {
      mc.field_1724.method_31549().field_7479 = false;
      mc.field_1724.method_31549().method_7248(0.05F);
   }

   private static enum Mode {
      Vanilla,
      MatrixJump,
      AirJump,
      MatrixGlide,
      StormBreak,
      Damage,
      Creative;
   }
}
