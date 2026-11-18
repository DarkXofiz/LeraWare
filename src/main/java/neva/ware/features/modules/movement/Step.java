package neva.ware.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_5134;
import net.minecraft.class_2828.class_2829;
import neva.ware.NevaWare;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventSync;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import neva.ware.utility.world.HoleUtility;

public class Step extends Module {
   private final Setting<Boolean> strict = new Setting<>("Strict", false);
   private final Setting<Float> height = new Setting<>("Height", 2.0F, 1.0F, 2.5F, v -> !this.strict.getValue());
   private final Setting<Boolean> useTimer = new Setting<>("Timer", true);
   private final Setting<Boolean> pauseIfShift = new Setting<>("PauseIfShift", false);
   private final Setting<Integer> stepDelay = new Setting<>("StepDelay", 200, 0, 1000);
   private final Setting<Boolean> holeDisable = new Setting<>("HoleDisable", false);
   private final Setting<Step.Mode> mode = new Setting<>("Mode", Step.Mode.NCP);
   private final neva.ware.utility.Timer stepTimer = new neva.ware.utility.Timer();
   private boolean alreadyInHole;
   private boolean timer;

   public Step() {
      super("Step", Module.Category.MOVEMENT);
   }

   @Override
   public void onEnable() {
      this.alreadyInHole = mc.field_1724 != null && HoleUtility.isHole(mc.field_1724.method_24515());
   }

   @Override
   public void onDisable() {
      NevaWare.TICK_TIMER = 1.0F;
      this.setStepHeight(0.6F);
   }

   @Override
   public void onUpdate() {
      if (this.holeDisable.getValue() && HoleUtility.isHole(mc.field_1724.method_24515()) && !this.alreadyInHole) {
         this.disable("Player in hole... Disabling...");
      } else {
         this.alreadyInHole = mc.field_1724 != null && HoleUtility.isHole(mc.field_1724.method_24515());
         if (this.pauseIfShift.getValue() && mc.field_1690.field_1832.method_1434()) {
            this.setStepHeight(0.6F);
         } else if (!mc.field_1724.method_31549().field_7479 && !ModuleManager.freeCam.isOn() && !mc.field_1724.method_3144() && !mc.field_1724.method_5799()) {
            if (this.timer && mc.field_1724.method_24828()) {
               NevaWare.TICK_TIMER = 1.0F;
               this.timer = false;
            }

            if (mc.field_1724.method_24828() && this.stepTimer.passedMs((long)this.stepDelay.getValue().intValue())) {
               this.setStepHeight(this.height.getValue());
            } else {
               this.setStepHeight(0.6F);
            }
         } else {
            this.setStepHeight(0.6F);
         }
      }
   }

   @EventHandler
   public void onStep(EventSync event) {
      if (this.mode.getValue() == Step.Mode.NCP) {
         double stepHeight = mc.field_1724.method_23318() - mc.field_1724.field_6036;
         if (stepHeight <= 0.75 || stepHeight > (double)this.height.getValue().floatValue() || this.strict.getValue() && stepHeight > 1.0) {
            return;
         }

         double[] offsets = this.getOffset(stepHeight);
         if (offsets != null && offsets.length > 1) {
            if (this.useTimer.getValue()) {
               NevaWare.TICK_TIMER = 1.0F / (float)offsets.length;
               this.timer = true;
            }

            for (double offset : offsets) {
               this.sendPacket(new class_2829(mc.field_1724.field_6014, mc.field_1724.field_6036 + offset, mc.field_1724.field_5969, false));
            }

            if (this.strict.getValue()) {
               this.sendPacket(new class_2829(mc.field_1724.field_6014, mc.field_1724.field_6036 + stepHeight, mc.field_1724.field_5969, false));
            }
         }

         this.stepTimer.reset();
      }
   }

   public double[] getOffset(double h) {
      return switch ((int)(h * 10000.0)) {
         case 7500, 10000 -> new double[]{0.42, 0.753};
         case 8125, 8750 -> new double[]{0.39, 0.7};
         case 15000 -> new double[]{0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
         case 20000 -> new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
         case 250000 -> new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
         default -> null;
      };
   }

   private void setStepHeight(float v) {
      mc.field_1724.method_5996(class_5134.field_47761).method_6192((double)v);
   }

   public static enum Mode {
      NCP,
      VANILLA;
   }
}
