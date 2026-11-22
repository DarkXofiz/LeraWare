package neva.ware.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1531;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2815;
import net.minecraft.class_2828;
import net.minecraft.class_2886;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_2828.class_2829;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventSync;
import neva.ware.events.impl.EventTravel;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.injection.accesors.IPlayerMoveC2SPacket;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.ElytraMatch;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.SearchInvResult;
import neva.ware.utility.render.Render3DEngine;

public class ElytraTarget extends Module {
   public final Setting<Float> targetRange = new Setting<>("Target Range", 64.0F, 6.0F, 100.0F);
   public final Setting<Float> overrun = new Setting<>("Predict", 8.0F, 0.0F, 8.0F);
   public final Setting matchSpeed;
   public final Setting matchDecel;
   public final Setting<Boolean> smartPrediction = new Setting<>("Smart Prediction", true);
   public final Setting<Float> speedLimit = new Setting<>("Speed Limit", 10.0F, 0.0F, 50.0F);
   public final Setting<Boolean> instantBoost = new Setting<>("Instant Boost", false);
   public final Setting<Boolean> grimBypass = new Setting<>("Grim Bypass", true);
   public final Setting<ElytraTarget.RotationMode> rotationMode = new Setting<>("Rotation Mode", ElytraTarget.RotationMode.Snap);
   public final Setting<Float> maxRotationStep = new Setting<>("Max Rotation Step", 180.0F, 1.0F, 360.0F);
   public final Setting<Boolean> lockTarget = new Setting<>("Lock Target", true);
   public final Setting<Float> rotationJitter = new Setting<>("Rotation Jitter", 0.5F, 0.0F, 5.0F);
   public final Setting<Float> interceptionCutoff = new Setting<>("Interception Cutoff", 1.5F, 0.0F, 5.0F);
   public final Setting<Boolean> velocityCompensation = new Setting<>("Velocity Compensator", true);
   public final Setting<Boolean> centripetalCorrection = new Setting<>("Centripetal Correction", true);
   public final Setting<Boolean> gcdRotationFix = new Setting<>("GCD Rotation Fix", true);
   public final Setting<Float> rotationSpeed = new Setting<>("Rotation Speed", 10.0F, 1.0F, 20.0F);
   public final Setting<Integer> targetMemoryTicks = new Setting<>("Target Memory", 10, 0, 40);
   public final Setting<SettingGroup> visuals = new Setting<>("Visuals", new SettingGroup(false, 0));
   public final Setting<Boolean> predictionLine = new Setting<>("Prediction Line", true).addToGroup(this.visuals);
   public final Setting<Boolean> targetTracers = new Setting<>("Target Tracers", true).addToGroup(this.visuals);
   public final Setting predictBox;
   public final Setting<Boolean> autoFirework = new Setting<>("Auto Firework", false);
   public final Setting<Float> fireworkDelay = new Setting<>("Firework Delay", 500.0F, 10.0F, 1500.0F, v -> this.autoFirework.getValue());
   public final Setting<ElytraTarget.FireworkMode> fireworkMode = new Setting<>(
      "Firework Mode", ElytraTarget.FireworkMode.Silent, v -> this.autoFirework.getValue()
   );
   public final Setting<Float> boostFactor = new Setting<>("Boost Factor", 1.5F, 0.8F, 3.0F, v -> this.autoFirework.getValue());
   public final Setting<Boolean> speedBasedPrediction = new Setting<>("Speed Based Prediction", true);
   public final Setting<Float> minPredictionSpeed = new Setting<>("Min Prediction Speed", 2.0F, 0.0F, 50.0F, v -> this.speedBasedPrediction.getValue());
   public long lastFireworkTime = 0L;
   private int bypassTicks = 0;
   private class_243 boostVector = class_243.field_1353;
   public boolean shouldApplyBoost = false;
   private long lastDelayRandomizeTime = 0L;
   private int fireworkDelayDirection = 1;
   private float currentRandomizedDelay = 0.0F;
   public static class_1297 target;
   public float targetYaw;
   public float targetPitch;
   public static class_243 currentPredictedPos = null;
   private final List<class_243> predictionPath;
   private int memoryTicks = 0;
   private class_1297 lastTarget = null;
   private class_243 prevTargetVelocity = class_243.field_1353;
   private class_243 targetAcceleration = class_243.field_1353;
   private float lastYaw;
   private float lastPitch;
   private int hitCount = 0;
   private long lastHitTime = 0L;
   private boolean aggressiveMode = false;
   public final Setting<Integer> predictLength = new Setting<>("Predict Length", 2, 1, 10);
   private int driveBoostOverrunTick = 0;
   private int fireworkSwapTick = 0;
   private int fireworkSwapSlot = -1;
   private int fireworkReturnSlot = -1;
   private boolean pendingFireworkSwapBack = false;
   public static Object predictBoxStatic;
   public final Setting<Boolean> catchBoostEnabled;
   public final Setting<Float> catchBoost;

   public ElytraTarget() {
      super("ElytraTarget", Module.Category.COMBAT);
      this.predictionPath = new ArrayList<>();
      this.predictBox = new Setting<>("Predict Box", true).addToGroup(this.visuals);
      predictBoxStatic = this.predictBox;
      this.matchSpeed = ElytraMatch.matchSetting(this);
      this.matchDecel = ElytraMatch.decelSetting(this);
      this.catchBoostEnabled = new Setting<>("Catch Boost Enabled", true);
      this.catchBoost = new Setting<>("Catch Boost", 1.07F, 1.0F, 1.5F, new ElytraCatch.Visible(this));
   }

   @Override
   public void onUpdate() {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         if (this.pendingFireworkSwapBack) {
            this.fireworkSwapTick--;
            if (this.fireworkSwapTick <= 0) {
               mc.field_1761
                  .method_2906(mc.field_1724.field_7512.field_7763, this.fireworkSwapSlot, this.fireworkReturnSlot, class_1713.field_7791, mc.field_1724);
               this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
               this.pendingFireworkSwapBack = false;
               this.fireworkSwapSlot = -1;
               this.fireworkReturnSlot = -1;
            }
         }

         if (this.autoFirework.getValue() && mc.field_1724.method_6128()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastDelayRandomizeTime >= 1500L) {
               this.currentRandomizedDelay = this.fireworkDelay.getValue() + 5.0F * (float)this.fireworkDelayDirection;
               this.fireworkDelayDirection *= -1;
               this.lastDelayRandomizeTime = currentTime;
            }

            float effectiveDelay = this.currentRandomizedDelay > 0.0F ? this.currentRandomizedDelay : this.fireworkDelay.getValue();
            if ((float)(currentTime - this.lastFireworkTime) >= effectiveDelay) {
               this.useFirework();
               this.lastFireworkTime = currentTime;
               this.shouldApplyBoost = true;
               if (!this.instantBoost.getValue()) {
                  this.bypassTicks = 3;
                  class_243 lookDirection = mc.field_1724.method_5720();
                  this.boostVector = new class_243(
                     lookDirection.field_1352 * (double)this.boostFactor.getValue().floatValue() / 3.0,
                     lookDirection.field_1351 * (double)this.boostFactor.getValue().floatValue() / 3.0,
                     lookDirection.field_1350 * (double)this.boostFactor.getValue().floatValue() / 3.0
                  );
               }
            }
         }

         if (!mc.field_1724.method_6128()) {
            target = null;
            currentPredictedPos = null;
            currentPredictedPos = ElytraMatch.adjust(this, currentPredictedPos);
            this.predictionPath.clear();
            this.memoryTicks = 0;
            this.aggressiveMode = false;
            this.hitCount = 0;
         } else {
            class_1297 candidate = this.findTarget();
            if (candidate != null) {
               if (target != candidate) {
                  this.targetYaw = mc.field_1724.method_36454();
                  this.targetPitch = mc.field_1724.method_36455();
                  this.prevTargetVelocity = new class_243(
                     candidate.method_23317() - candidate.field_6014,
                     candidate.method_23318() - candidate.field_6036,
                     candidate.method_23321() - candidate.field_5969
                  );
                  this.targetAcceleration = class_243.field_1353;
                  this.hitCount = 0;
                  this.aggressiveMode = false;
               }

               class_243 currentCandidateVel = new class_243(
                  candidate.method_23317() - candidate.field_6014,
                  candidate.method_23318() - candidate.field_6036,
                  candidate.method_23321() - candidate.field_5969
               );
               class_243 rawAccel = currentCandidateVel.method_1020(this.prevTargetVelocity);
               this.targetAcceleration = new class_243(
                  this.targetAcceleration.field_1352 * 0.8 + rawAccel.field_1352 * 0.2,
                  this.targetAcceleration.field_1351 * 0.8 + rawAccel.field_1351 * 0.2,
                  this.targetAcceleration.field_1350 * 0.8 + rawAccel.field_1350 * 0.2
               );
               this.prevTargetVelocity = currentCandidateVel;
               target = candidate;
               this.lastTarget = candidate;
               this.memoryTicks = this.targetMemoryTicks.getValue();
            } else {
               if (this.memoryTicks <= 0
                  || this.lastTarget == null
                  || !this.lastTarget.method_5805()
                  || (double)mc.field_1724.method_5739(this.lastTarget) > (double)this.targetRange.getValue().floatValue() * 1.5) {
                  target = null;
                  currentPredictedPos = null;
                  this.predictionPath.clear();
                  this.aggressiveMode = false;
                  return;
               }

               target = this.lastTarget;
               this.memoryTicks--;
            }

            double currentDistance = (double)mc.field_1724.method_5739(target);
            if (currentDistance > (double)this.targetRange.getValue().floatValue() * 1.2) {
               currentPredictedPos = null;
               this.predictionPath.clear();
               if (!this.lockTarget.getValue() || currentDistance > (double)this.targetRange.getValue().floatValue() * 1.5) {
                  target = null;
               }
            } else {
               class_243 targetPos = target.method_19538().method_1031(0.0, (double)target.method_17682() / 2.0, 0.0);
               class_243 targetVelocity = new class_243(
                  target.method_23317() - target.field_6014, target.method_23318() - target.field_6036, target.method_23321() - target.field_5969
               );
               double dist = (double)mc.field_1724.method_5739(target);
               double targetSpeedBps = targetVelocity.method_37267() * 20.0;
               float currentOverrun = this.overrun.getValue();
               if (this.speedBasedPrediction.getValue() && targetSpeedBps < (double)this.minPredictionSpeed.getValue().floatValue()) {
                  currentOverrun = 0.0F;
               }

               boolean usePrediction = !this.speedBasedPrediction.getValue() || targetSpeedBps >= (double)this.minPredictionSpeed.getValue().floatValue();
               if (usePrediction) {
                  currentPredictedPos = this.smartPrediction.getValue()
                     ? this.calculateSmartPrediction(targetPos, targetVelocity, dist, currentOverrun)
                     : this.calculateOvershootPosition(targetPos, targetVelocity, dist, currentOverrun);
               } else {
                  currentPredictedPos = targetPos;
               }

               this.driveBoostOverrunTick++;
               if (this.driveBoostOverrunTick >= this.predictLength.getValue()) {
                  this.driveBoostOverrunTick = 0;
                  currentPredictedPos = this.driveBoostOverrun(currentPredictedPos, targetVelocity);
               }

               this.predictionPath.clear();
               if (this.predictionLine.getValue()) {
                  class_243 tempPos = targetPos;
                  class_243 tempVel = targetVelocity;

                  for (int i = 0; i < 5; i++) {
                     tempPos = tempPos.method_1019(tempVel.method_1021((double)currentOverrun * 0.5));
                     this.predictionPath.add(tempPos);
                  }
               }
            }
         }
      }
   }

   private class_243 driveBoostOverrun(class_243 predictedPos, class_243 targetVelocity) {
      double driveBoostSpeed = mc.field_1724.method_18798().method_1033();
      double overrun = (double)this.overrun.getValue().floatValue() * (driveBoostSpeed / Math.max(0.1, (double)this.speedLimit.getValue().floatValue() / 20.0));
      overrun = Math.min(overrun, (double)this.overrun.getValue().floatValue() * 2.0);
      class_243 lookDir = target.method_5720();
      class_243 overrunDir;
      if (targetVelocity.method_1033() > 0.001) {
         overrunDir = targetVelocity.method_1029();
         if (overrunDir.method_1026(lookDir) < 0.0) {
            overrunDir = overrunDir.method_1021(-1.0);
         }
      } else {
         overrunDir = lookDir;
      }

      return predictedPos.method_1019(overrunDir.method_1021(overrun));
   }

   @EventHandler
   public void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck() && this.isEnabled()) {
         if (mc.field_1724.method_6128()) {
            if (event.getPacket() instanceof class_2828 packet) {
               if (this.grimBypass.getValue()) {
                  ((IPlayerMoveC2SPacket)(Object)packet).setOnGround(false);
               }
            }
         }
      }
   }

   @EventHandler
   public void onSync(EventSync e) {
      if (target == null || !mc.field_1724.method_6128()) {
         this.lastYaw = mc.field_1724.method_36454();
         this.lastPitch = mc.field_1724.method_36455();
      } else if (currentPredictedPos != null) {
         float[] rotations = InteractionUtility.calculateAngle(mc.field_1724.method_33571(), currentPredictedPos);
         float sensitivity = ((Double)mc.field_1690.method_42495().method_41753()).floatValue() * 0.6F + 0.2F;
         float gcd = sensitivity * sensitivity * sensitivity * 1.2F;
         float yawDiff = class_3532.method_15393(rotations[0] - this.lastYaw);
         float pitchDiff = rotations[1] - this.lastPitch;
         float finalYaw;
         float finalPitch;
         switch ((ElytraTarget.RotationMode)this.rotationMode.getValue()) {
            case Snap:
               float maxStep = this.maxRotationStep.getValue();
               finalYaw = this.lastYaw + class_3532.method_15363(yawDiff, -maxStep, maxStep);
               finalPitch = this.lastPitch + class_3532.method_15363(pitchDiff, -maxStep, maxStep);
               break;
            case Linear: {
               float step = this.rotationSpeed.getValue();
               finalYaw = this.lastYaw + class_3532.method_15363(yawDiff, -step, step);
               finalPitch = this.lastPitch + class_3532.method_15363(pitchDiff, -step, step);
               break;
            }
            case Soft:
               float t = class_3532.method_15363(this.rotationSpeed.getValue() / 20.0F, 0.05F, 1.0F);
               finalYaw = class_3532.method_16439(t, this.lastYaw, rotations[0]);
               finalPitch = class_3532.method_16439(t, this.lastPitch, rotations[1]);
               break;
            case Legit: {
               float step = this.rotationSpeed.getValue() * 0.6F;
               float jitter = (float)(
                  Math.random() * (double)ElytraMatch.jit(this.rotationJitter.getValue()) * 2.0 - (double)ElytraMatch.jit(this.rotationJitter.getValue())
               );
               finalYaw = this.lastYaw + class_3532.method_15363(yawDiff + jitter, -step, step);
               finalPitch = this.lastPitch + class_3532.method_15363(pitchDiff + jitter * 0.3F, -step, step);
               break;
            }
            case Flick:
               float threshold = this.rotationSpeed.getValue() * 5.0F;
               if (!(Math.abs(yawDiff) > threshold) && !(Math.abs(pitchDiff) > threshold * 0.5F)) {
                  finalYaw = this.lastYaw;
                  finalPitch = this.lastPitch;
               } else {
                  finalYaw = rotations[0];
                  finalPitch = rotations[1];
               }
               break;
            default:
               finalYaw = rotations[0];
               finalPitch = rotations[1];
         }

         if (this.gcdRotationFix.getValue()) {
            finalYaw -= (finalYaw - this.lastYaw) % gcd;
            finalPitch -= (finalPitch - this.lastPitch) % gcd;
         }

         e.setYaw(finalYaw);
         e.setPitch(finalPitch);
         this.lastYaw = finalYaw;
         this.lastPitch = finalPitch;
      }
   }

   @EventHandler
   public void onTravel(EventTravel event) {
      ElytraMatch.travel(event);
      if (event.isPre() && mc.field_1724.method_6128()) {
         if (this.shouldApplyBoost) {
            if (this.instantBoost.getValue()) {
               class_243 currentMotion = event.getmVec();
               class_243 lookDirection = mc.field_1724.method_5720();
               class_243 newMotion = new class_243(
                  currentMotion.field_1352 + lookDirection.field_1352 * (double)this.boostFactor.getValue().floatValue(),
                  currentMotion.field_1351 + lookDirection.field_1351 * (double)this.boostFactor.getValue().floatValue(),
                  currentMotion.field_1350 + lookDirection.field_1350 * (double)this.boostFactor.getValue().floatValue()
               );
               double maxSpeed = (double)this.speedLimit.getValue().floatValue() / 20.0;
               if (newMotion.method_1033() > maxSpeed) {
                  newMotion = newMotion.method_1029().method_1021(maxSpeed);
               }

               event.setmVec(newMotion);
               this.shouldApplyBoost = false;
            } else if (this.bypassTicks > 0) {
               class_243 currentMotion = event.getmVec();
               class_243 newMotion = currentMotion.method_1019(this.boostVector);
               double maxSpeed = (double)this.speedLimit.getValue().floatValue() / 20.0;
               if (newMotion.method_1033() > maxSpeed) {
                  newMotion = newMotion.method_1029().method_1021(maxSpeed);
               }

               event.setmVec(newMotion);
               this.bypassTicks--;
               if (this.bypassTicks <= 0) {
                  this.shouldApplyBoost = false;
               }
            }
         }
      }
   }

   public class_243 calculateOvershootPosition(class_243 targetPos, class_243 targetVelocity, double distance, float currentOverrun) {
      double leadTime = Math.min(1.2, Math.max(0.5, distance / 20.0));
      class_243 predictedPos = targetPos.method_1019(targetVelocity.method_1021(leadTime));
      double heightOffset = (double)target.method_17682() * 0.4;
      predictedPos = predictedPos.method_1031(0.0, heightOffset, 0.0);
      class_243 lookDir = target.method_5720();
      class_243 overrunDir;
      if (targetVelocity.method_1033() > 0.001) {
         overrunDir = targetVelocity.method_1029();
         if (overrunDir.method_1026(lookDir) < 0.0) {
            overrunDir = overrunDir.method_1021(-1.0);
         }
      } else {
         overrunDir = lookDir;
      }

      return predictedPos.method_1019(overrunDir.method_1021((double)currentOverrun));
   }

   public class_243 calculateSmartPrediction(class_243 targetPos, class_243 targetVelocity, double distance, float currentOverrun) {
      double aggressiveMultiplier = this.aggressiveMode ? 1.2 : 1.0;
      float pingSeconds = (float)Managers.SERVER.getPing() / 1000.0F;
      double mySpeed = mc.field_1724.method_18798().method_1033();
      if (this.shouldApplyBoost) {
         class_243 lookDir = mc.field_1724.method_5720();
         mySpeed = Math.max(
            mySpeed, mc.field_1724.method_18798().method_1019(lookDir.method_1021((double)this.boostFactor.getValue().floatValue())).method_1033()
         );
      } else if (System.currentTimeMillis() - this.lastFireworkTime < 600L) {
         double decay = 1.0 - (double)(System.currentTimeMillis() - this.lastFireworkTime) / 600.0;
         mySpeed = Math.max(mySpeed, mySpeed + decay * (double)this.boostFactor.getValue().floatValue() * 0.5);
      }

      if (mySpeed < 0.2) {
         mySpeed = (double)this.speedLimit.getValue().floatValue() / 20.0 * 0.5;
      }

      double timeToTarget = distance / (mySpeed * 20.0) + (double)pingSeconds;
      int simTicks = (int)class_3532.method_15350(timeToTarget * 20.0 * aggressiveMultiplier, 2.0, 20.0);
      class_243 simPos = targetPos;
      class_243 simVel = targetVelocity;
      class_243 lastSafePos = targetPos;

      for (int i = 0; i < simTicks; i++) {
         label60: {
            if (target instanceof class_1309 living && living.method_6128()) {
               simVel = new class_243(simVel.field_1352 * 0.99, simVel.field_1351 * 0.98, simVel.field_1350 * 0.99);
               break label60;
            }

            simVel = new class_243(simVel.field_1352 * 0.99, simVel.field_1351 * 0.98 - 0.08, simVel.field_1350 * 0.99);
         }

         class_243 nextPos = simPos.method_1019(simVel);
         class_2338 bp = class_2338.method_49638(nextPos);
         if (!mc.field_1687.method_8320(bp).method_26215()) {
            simPos = lastSafePos;
            break;
         }

         lastSafePos = simPos;
         simPos = nextPos;
         if (this.targetAcceleration.method_1033() > 0.002) {
            simPos = nextPos.method_1019(this.targetAcceleration.method_1021(0.3 * (1.0 - (double)i / (double)simTicks)));
         }
      }

      class_243 interceptPoint = simPos;
      double targetSpeed = targetVelocity.method_37267();
      if (currentOverrun > 0.0F) {
         class_243 lookDir = target.method_5720();
         class_243 overrunDir;
         if (targetVelocity.method_1033() > 0.001) {
            overrunDir = targetVelocity.method_1029();
            if (overrunDir.method_1026(lookDir) < 0.0) {
               overrunDir = overrunDir.method_1021(-1.0);
            }
         } else {
            overrunDir = lookDir;
         }

         interceptPoint = simPos.method_1019(overrunDir.method_1021((double)currentOverrun));
      }

      if (this.centripetalCorrection.getValue() && targetSpeed > 0.08) {
         class_243 turnVector = targetVelocity.method_1036(new class_243(0.0, 1.0, 0.0)).method_1029();
         double turnAmount = this.targetAcceleration.method_1026(turnVector);
         if (Math.abs(turnAmount) > 0.006) {
            interceptPoint = interceptPoint.method_1019(turnVector.method_1021(turnAmount * 15.0));
         }
      }

      double heightOffset = (double)target.method_17682() * 0.4;
      return new class_243(interceptPoint.field_1352, interceptPoint.field_1351 + heightOffset, interceptPoint.field_1350);
   }

   private class_243 calculateSmartPrediction_legacy(class_243 targetPos, class_243 targetVelocity, double distance) {
      double speed = targetVelocity.method_37267();
      double leadFactor = (double)this.overrun.getValue().floatValue() * 1.6;
      double timeToReach = distance / 18.0;
      float pingOffset = (float)Managers.SERVER.getPing() / 50.0F;
      class_243 relVel = targetVelocity.method_1020(mc.field_1724.method_18798());
      double aggressiveMultiplier = this.aggressiveMode ? 1.3 : 1.0;
      targetPos = targetPos.method_1019(targetVelocity.method_1021(Math.max(2.0, (timeToReach + (double)pingOffset) * leadFactor * aggressiveMultiplier)));
      if (this.targetAcceleration.method_1033() > 0.002) {
         targetPos = targetPos.method_1019(this.targetAcceleration.method_1021(0.8 * Math.pow(timeToReach, 2.0)));
      }

      class_243 playerPos = mc.field_1724.method_19538();
      class_243 toTarget = targetPos.method_1020(playerPos).method_1029();
      class_243 futurePos;
      if (speed > 0.08) {
         class_243 targetDir = targetVelocity.method_1029();
         double dot = toTarget.method_1026(targetDir);
         class_243 frontPosition;
         if (dot > 0.12) {
            double interceptDistance = Math.min((double)this.overrun.getValue().floatValue() * 1.4, distance * 0.7);
            class_243 turnVector = targetVelocity.method_1036(new class_243(0.0, 1.0, 0.0)).method_1029();
            double turnAmount = this.targetAcceleration.method_1026(turnVector);
            double relativeSpeed = Math.max(0.4, dot + 1.5) * (double)this.interceptionCutoff.getValue().floatValue() * speed * 2.2;
            frontPosition = targetPos.method_1019(targetDir.method_1021(interceptDistance + relativeSpeed));
            if (Math.abs(turnAmount) > 0.006) {
               frontPosition = frontPosition.method_1019(turnVector.method_1021(turnAmount * 22.0));
            }
         } else {
            class_243 predictedVelocity = targetDir.method_1021(-1.0).method_1029();
            double frontDistance = Math.max(2.8, (double)this.overrun.getValue().floatValue() * 1.3);
            frontPosition = targetPos.method_1019(predictedVelocity.method_1021(frontDistance));
         }

         double relativeSpeed = relVel.method_37267();
         if (relativeSpeed > 0.35) {
            class_243 interceptVector = relVel.method_1029().method_1021(relativeSpeed * 1.1);
            frontPosition = frontPosition.method_1019(interceptVector);
         }

         if (targetVelocity.field_1351 > 0.06) {
            frontPosition = frontPosition.method_1031(0.0, targetVelocity.field_1351 * 2.6, 0.0);
         }

         if (target.method_24828() || targetPos.field_1351 < playerPos.field_1351) {
            frontPosition = frontPosition.method_1031(0.0, 2.6, 0.0);
         } else if (targetPos.field_1351 > playerPos.field_1351 + 1.6) {
            frontPosition = frontPosition.method_1031(0.0, 3.2, 0.0);
         }

         futurePos = frontPosition;
      } else {
         class_243 targetDirx = toTarget.method_1021(Math.max(1.8, distance + (double)this.overrun.getValue().floatValue() * 0.8));
         futurePos = playerPos.method_1019(targetDirx);
      }

      return futurePos;
   }

   @Override
   public void onEnable() {
      ElytraMatch.reset();
      if (mc.field_1724 != null) {
         target = null;
         this.lastTarget = null;
         this.memoryTicks = 0;
         this.predictionPath.clear();
         currentPredictedPos = null;
         this.lastFireworkTime = 0L;
         this.shouldApplyBoost = false;
         this.lastYaw = mc.field_1724.method_36454();
         this.lastPitch = mc.field_1724.method_36455();
         this.hitCount = 0;
         this.aggressiveMode = false;
         this.lastDelayRandomizeTime = 0L;
         this.fireworkDelayDirection = 1;
         this.currentRandomizedDelay = 0.0F;
      }
   }

   @Override
   public void onDisable() {
      ElytraMatch.reset();
      target = null;
      this.lastTarget = null;
      this.memoryTicks = 0;
      this.predictionPath.clear();
      currentPredictedPos = null;
      this.lastFireworkTime = 0L;
      this.shouldApplyBoost = false;
      this.hitCount = 0;
      this.aggressiveMode = false;
      this.lastDelayRandomizeTime = 0L;
      this.fireworkDelayDirection = 1;
      this.currentRandomizedDelay = 0.0F;
      this.pendingFireworkSwapBack = false;
      this.fireworkSwapSlot = -1;
      this.fireworkReturnSlot = -1;
   }

   private void useFirework() {
      if (!ElytraMatch.isActive()) {
         if (mc.field_1724 != null && mc.field_1761 != null) {
            if (!mc.field_1724.method_7357().method_7904(class_1802.field_8639)) {
               if (!this.pendingFireworkSwapBack) {
                  SearchInvResult fireWorkResult = InventoryUtility.findItemInInventory(class_1802.field_8639);
                  if (!fireWorkResult.found()) {
                     this.sendMessage("You've got no fireworks!");
                  } else {
                     int currentSlot = mc.field_1724.method_31548().field_7545;
                     int hotbarSlot = currentSlot % 8 + 1;
                     int itemSlot = fireWorkResult.slot();
                     if (mc.field_1724.method_6115() && mc.field_1724.method_6058() == class_1268.field_5808) {
                        this.useFireworkWithOffhandSwap(itemSlot);
                     } else if (itemSlot >= 36) {
                        this.useFireworkFromHotbar(itemSlot - 36, currentSlot);
                     } else {
                        mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, itemSlot, hotbarSlot, class_1713.field_7791, mc.field_1724);
                        this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
                        this.useFireworkFromHotbar(hotbarSlot, currentSlot);
                        this.fireworkSwapSlot = itemSlot;
                        this.fireworkReturnSlot = hotbarSlot;
                        this.fireworkSwapTick = 2;
                        this.pendingFireworkSwapBack = true;
                     }
                  }
               }
            }
         }
      }
   }

   private void useFireworkFromHotbar(int fireworkSlot, int returnSlot) {
      InventoryUtility.switchToSilent(fireworkSlot);
      float[] rotation = this.getUseRotation();
      this.sendSequencedPacket(id -> new class_2886(class_1268.field_5808, id, rotation[0], rotation[1]));
      InventoryUtility.switchToSilent(returnSlot);
   }

   private void useFireworkWithOffhandSwap(int itemSlot) {
      int slotIndex = itemSlot <= 8 ? itemSlot + 36 : itemSlot;
      mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, slotIndex, 40, class_1713.field_7791, mc.field_1724);
      this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
      float[] rotation = this.getUseRotation();
      this.sendSequencedPacket(id -> new class_2886(class_1268.field_5810, id, rotation[0], rotation[1]));
      mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, slotIndex, 40, class_1713.field_7791, mc.field_1724);
      this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
   }

   private float[] getUseRotation() {
      float yaw = mc.field_1724.method_36454();
      float pitch = mc.field_1724.method_36455();
      if (ModuleManager.aura.isEnabled() && AttackAura.target != null) {
         yaw = ModuleManager.aura.rotationYaw;
         pitch = ModuleManager.aura.rotationPitch;
      }

      return new float[]{yaw, pitch};
   }

   public boolean shouldUseFirework() {
      if (ElytraMatch.isActive()) {
         return false;
      } else if (!this.autoFirework.getValue()) {
         return false;
      } else if (mc.field_1724.method_31549().field_7477) {
         return false;
      } else if (mc.field_1724.method_31548().method_18861(class_1802.field_8639) == 0) {
         return false;
      } else {
         return mc.field_1724.method_7357().method_7904(class_1802.field_8639)
            ? false
            : this.fireworkMode.getValue() == ElytraTarget.FireworkMode.Silent
               || this.fireworkMode.getValue() == ElytraTarget.FireworkMode.Legit && mc.field_1690.field_1904.method_1434();
      }
   }

   public class_1297 findTarget() {
      AttackAura aura = ModuleManager.aura;
      if (aura.isEnabled() && AttackAura.target != null && this.isValidTarget(AttackAura.target)) {
         double distanceToAuraTarget = (double)mc.field_1724.method_5739(AttackAura.target);
         if (distanceToAuraTarget <= (double)this.targetRange.getValue().floatValue() * 1.3) {
            return AttackAura.target;
         }
      }

      if (this.lockTarget.getValue() && target != null && this.isValidTarget(target)) {
         return target;
      } else {
         List<class_1309> targets = new CopyOnWriteArrayList<>();

         for (class_1297 ent : mc.field_1687.method_18112()) {
            if (ent instanceof class_1309) {
               class_1309 living = (class_1309)ent;
               if (this.isValidTarget(living)) {
                  targets.add(living);
               }
            }
         }

         return (class_1297)targets.stream().min(Comparator.comparingDouble(e -> (double)mc.field_1724.method_5739(e))).orElse(null);
      }
   }

   public boolean isValidTarget(class_1297 entity) {
      if (entity == mc.field_1724) {
         return false;
      } else if (entity instanceof class_1531) {
         return false;
      } else if (Managers.FRIEND.isFriend(entity.method_5477().getString())) {
         return false;
      } else if (!entity.method_5805()) {
         return false;
      } else {
         double distance = (double)mc.field_1724.method_5739(entity);
         double maxRange = (double)this.targetRange.getValue().floatValue();
         if (this.lockTarget.getValue() && target == entity) {
            maxRange *= 1.5;
         } else {
            maxRange *= 1.3;
         }

         if (distance > maxRange) {
            return false;
         } else {
            AttackAura aura = ModuleManager.aura;
            if (aura.isEnabled()) {
               if (entity instanceof class_1657 && !aura.Players.getValue()) {
                  return false;
               }

               if (entity instanceof class_1308 && !aura.Mobs.getValue()) {
                  return false;
               }

               if (entity instanceof class_1429 && !aura.Animals.getValue()) {
                  return false;
               }

               if (entity instanceof class_1646 && !aura.Villagers.getValue()) {
                  return false;
               }

               if (entity instanceof class_1657 player) {
                  if (player.method_7337() && aura.ignoreCreative.getValue()) {
                     return false;
                  }

                  if (player.method_5767() && aura.ignoreInvisible.getValue()) {
                     return false;
                  }
               }
            } else if (!(entity instanceof class_1657)) {
               return false;
            }

            return true;
         }
      }
   }

   @Override
   public void onRender3D(class_4587 stack) {
      if (target != null && mc.field_1724.method_6128()) {
         ElytraPredictBox.draw();
         if (this.predictionLine.getValue() && !this.predictionPath.isEmpty()) {
            class_243 var2 = null;

            for (int var3 = 0; var3 < this.predictionPath.size(); var3++) {
               class_243 var4 = this.predictionPath.get(var3);
               if (var2 != null) {
                  Render3DEngine.drawLine(var2, var4, new Color(255, 255, 255, 150 - var3 * 20));
               }

               var2 = var4;
            }
         }

         if (this.targetTracers.getValue()) {
            class_243 var5 = mc.field_1724.method_33571();
            class_243 var6 = target.method_5829().method_1005();
            Render3DEngine.drawLine(var5.method_1019(mc.field_1724.method_5828(Render3DEngine.getTickDelta())), var6, new Color(255, 255, 255, 50));
         }
      }
   }

   public void onTargetHit() {
      if (target != null) {
         this.memoryTicks = this.targetMemoryTicks.getValue();
         long now = System.currentTimeMillis();
         if (now - this.lastHitTime < 2000L) {
            this.hitCount++;
            if (this.hitCount >= 3) {
               this.aggressiveMode = true;
            }
         } else {
            this.hitCount = 1;
            this.aggressiveMode = false;
         }

         this.lastHitTime = now;
      }
   }

   public static enum FireworkMode {
      Legit,
      Silent;
   }

   private static enum RotationMode {
      Snap,
      Linear,
      Soft,
      Legit,
      Flick;
   }
}
