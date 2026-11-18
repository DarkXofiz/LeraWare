package neva.ware.features.modules.combat;

import baritone.api.BaritoneAPI;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1451;
import net.minecraft.class_1531;
import net.minecraft.class_1588;
import net.minecraft.class_1621;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1674;
import net.minecraft.class_1676;
import net.minecraft.class_1678;
import net.minecraft.class_1743;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_1835;
import net.minecraft.class_1839;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2663;
import net.minecraft.class_2708;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_2848;
import net.minecraft.class_2886;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_5134;
import net.minecraft.class_745;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_2848.class_2849;
import neva.ware.NevaWare;
import neva.ware.core.Core;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.core.manager.player.PlayerManager;
import neva.ware.events.impl.EventSync;
import neva.ware.events.impl.PacketEvent;
import neva.ware.events.impl.PlayerUpdateEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.features.modules.render.TargetESP;
import neva.ware.gui.notification.Notification;
import neva.ware.injection.accesors.ILivingEntity;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.BooleanSettingGroup;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.Timer;
import neva.ware.utility.bypass.BypassAura;
import neva.ware.utility.bypass.SharpAura;
import neva.ware.utility.interfaces.IOtherClientPlayerEntity;
import neva.ware.utility.math.MathUtility;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.PlayerUtility;
import neva.ware.utility.player.SearchInvResult;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.Render3DEngine;
import org.jetbrains.annotations.NotNull;

public class Aura extends Module {
   public final Setting<Float> attackRange = new Setting<>("Range", 3.1F, 1.0F, 6.0F);
   public final Setting<Float> wallRange = new Setting<>("WallsRange", 3.1F, 0.0F, 6.0F);
   public final Setting<Float> aimRange;
   public final Setting<Float> elytraAimRange;
   public final Setting<Boolean> elytra = new Setting<>("ElytraOverride", false);
   public final Setting<Float> elytraAttackRange = new Setting<>("ElytraRange", 3.1F, 1.0F, 6.0F, v -> this.elytra.getValue());
   public final Setting<Float> elytraWallRange = new Setting<>("ElytraWallsRange", 3.1F, 0.0F, 6.0F, v -> this.elytra.getValue());
   public final Setting<Aura.WallsBypass> wallsBypass = new Setting<>("WallsBypass", Aura.WallsBypass.Off, v -> this.getWallRange() > 0.0F);
   public final Setting<Integer> fov = new Setting<>("FOV", 180, 1, 180);
   public final Setting<Aura.Mode> rotationMode = new Setting<>("Rotation", Aura.Mode.TrackV2);
   public final Setting<Integer> interactTicks = new Setting<>("InteractTicks", 3, 1, 10, v -> this.rotationMode.getValue() == null);
   public final Setting<Aura.Switch> switchMode = new Setting<>("AutoWeapon", Aura.Switch.None);
   public final Setting<Boolean> onlyWeapon = new Setting<>("OnlyWeapon", false, v -> this.switchMode.getValue() != Aura.Switch.Silent);
   public final Setting<BooleanSettingGroup> smartCrit = new Setting<>("CritMode", new BooleanSettingGroup(true));
   public final Setting<Boolean> onlySpace = new Setting<>("OnlyCrit", false).addToGroup(this.smartCrit);
   public final Setting<Boolean> autoJump = new Setting<>("AutoJump", false).addToGroup(this.smartCrit);
   public final Setting<Boolean> shieldBreaker = new Setting<>("ShieldBreaker", true);
   public final Setting<Boolean> pauseWhileEating = new Setting<>("PauseWhileEating", false);
   public final Setting<Boolean> tpsSync = new Setting<>("TPSSync", false);
   public final Setting<Boolean> clientLook = new Setting<>("ClientLook", false);
   public final Setting<Boolean> pauseBaritone = new Setting<>("PauseBaritone", false);
   public final Setting<BooleanSettingGroup> oldDelay = new Setting<>("OldDelay", new BooleanSettingGroup(false));
   public final Setting<Integer> minCPS = new Setting<>("MinCPS", 7, 1, 20).addToGroup(this.oldDelay);
   public final Setting<Integer> maxCPS = new Setting<>("MaxCPS", 12, 1, 20).addToGroup(this.oldDelay);
   public final Setting<Aura.Sort> sort = new Setting<>("Sort", Aura.Sort.LowestDistance);
   public final Setting<Boolean> lockTarget = new Setting<>("LockTarget", true);
   public final Setting<Boolean> elytraTarget = new Setting<>("ElytraTarget", true);
   public final Setting<SettingGroup> advanced = new Setting<>("Advanced", new SettingGroup(false, 0));
   public final Setting<Boolean> randomHitDelay;
   public final Setting<Boolean> pauseInInventory;
   public final Setting<Boolean> dropSprint;
   public final Setting<Boolean> returnSprint;
   public final Setting<Aura.RayTrace> rayTrace;
   public final Setting<Boolean> grimRayTrace;
   public final Setting<Boolean> unpressShield;
   public final Setting<Boolean> deathDisable;
   public final Setting<Boolean> tpDisable;
   public final Setting<Boolean> pullDown;
   public final Setting<Boolean> onlyJumpBoost;
   public final Setting<Float> pullValue;
   public final Setting<Aura.AttackHand> attackHand;
   public final Setting<Aura.Resolver> resolver;
   public final Setting<Integer> backTicks;
   public final Setting<Boolean> resolverVisualisation;
   public final Setting<Aura.AccelerateOnHit> accelerateOnHit;
   public final Setting<Integer> minYawStep;
   public final Setting<Integer> maxYawStep;
   public final Setting<Float> aimedPitchStep;
   public final Setting<Float> maxPitchStep;
   public final Setting<Float> pitchAccelerate;
   public final Setting<Float> attackCooldown;
   public final Setting<Float> attackBaseTime;
   public final Setting<Integer> attackTickLimit;
   public final Setting<Float> critFallDistance;
   public final Setting<SettingGroup> targets;
   public final Setting<Boolean> Players;
   public final Setting<Boolean> Mobs;
   public final Setting<Boolean> Animals;
   public final Setting<Boolean> Villagers;
   public final Setting<Boolean> Slimes;
   public final Setting<Boolean> hostiles;
   public final Setting<Boolean> onlyAngry;
   public final Setting<Boolean> Projectiles;
   public final Setting<Boolean> ignoreInvisible;
   public final Setting<Boolean> ignoreNamed;
   public final Setting<Boolean> ignoreTeam;
   public final Setting<Boolean> ignoreCreative;
   public final Setting<Boolean> ignoreNaked;
   public final Setting<Boolean> ignoreShield;
   public static class_1297 target;
   public float rotationYaw;
   public float rotationPitch;
   public float pitchAcceleration;
   private class_243 rotationPoint;
   private class_243 rotationMotion;
   private int hitTicks;
   private int trackticks;
   private boolean lookingAtHitbox;
   private final Timer delayTimer;
   private final Timer pauseTimer;
   public class_238 resolvedBox;
   static boolean wasTargeted = false;
   private int sbState;
   private int sbPrev;

   public Aura() {
      super("Attack Aura", Module.Category.COMBAT);
      this.aimRange = new Setting<>("AimRange", 3.1F, 0.0F, 50.0F);
      this.elytraAimRange = new Setting<>("ElytraAimRange", 3.1F, 0.0F, 100.0F);
      this.randomHitDelay = new Setting<>("RandomHitDelay", false).addToGroup(this.advanced);
      this.pauseInInventory = new Setting<>("PauseInInventory", true).addToGroup(this.advanced);
      this.dropSprint = new Setting<>("DropSprint", true).addToGroup(this.advanced);
      this.returnSprint = new Setting<>("ReturnSprint", true, v -> this.dropSprint.getValue()).addToGroup(this.advanced);
      this.rayTrace = new Setting<>("RayTrace", Aura.RayTrace.OnlyTarget).addToGroup(this.advanced);
      this.grimRayTrace = new Setting<>("GrimRayTrace", true).addToGroup(this.advanced);
      this.unpressShield = new Setting<>("UnpressShield", true).addToGroup(this.advanced);
      this.deathDisable = new Setting<>("DisableOnDeath", true).addToGroup(this.advanced);
      this.tpDisable = new Setting<>("TPDisable", false).addToGroup(this.advanced);
      this.pullDown = new Setting<>("FastFall", false).addToGroup(this.advanced);
      this.onlyJumpBoost = new Setting<>("OnlyJumpBoost", false, v -> this.pullDown.getValue()).addToGroup(this.advanced);
      this.pullValue = new Setting<>("PullValue", 3.0F, 0.0F, 20.0F, v -> this.pullDown.getValue()).addToGroup(this.advanced);
      this.attackHand = new Setting<>("AttackHand", Aura.AttackHand.MainHand).addToGroup(this.advanced);
      this.resolver = new Setting<>("Resolver", Aura.Resolver.Advantage).addToGroup(this.advanced);
      this.backTicks = new Setting<>("BackTicks", 4, 1, 20, v -> this.resolver.is(Aura.Resolver.BackTrack)).addToGroup(this.advanced);
      this.resolverVisualisation = new Setting<>("ResolverVisualisation", false, v -> !this.resolver.is(Aura.Resolver.Off)).addToGroup(this.advanced);
      this.accelerateOnHit = new Setting<>("AccelerateOnHit", Aura.AccelerateOnHit.Off).addToGroup(this.advanced);
      this.minYawStep = new Setting<>("MinYawStep", 65, 1, 180).addToGroup(this.advanced);
      this.maxYawStep = new Setting<>("MaxYawStep", 75, 1, 180).addToGroup(this.advanced);
      this.aimedPitchStep = new Setting<>("AimedPitchStep", 1.0F, 0.0F, 90.0F).addToGroup(this.advanced);
      this.maxPitchStep = new Setting<>("MaxPitchStep", 8.0F, 1.0F, 90.0F).addToGroup(this.advanced);
      this.pitchAccelerate = new Setting<>("PitchAccelerate", 1.65F, 1.0F, 10.0F).addToGroup(this.advanced);
      this.attackCooldown = new Setting<>("AttackCooldown", 0.9F, 0.5F, 1.0F).addToGroup(this.advanced);
      this.attackBaseTime = new Setting<>("AttackBaseTime", 0.5F, 0.0F, 2.0F).addToGroup(this.advanced);
      this.attackTickLimit = new Setting<>("AttackTickLimit", 11, 0, 20).addToGroup(this.advanced);
      this.critFallDistance = new Setting<>("CritFallDistance", 0.0F, 0.0F, 1.0F).addToGroup(this.advanced);
      this.targets = new Setting<>("Targets", new SettingGroup(false, 0));
      this.Players = new Setting<>("Players", true).addToGroup(this.targets);
      this.Mobs = new Setting<>("Mobs", true).addToGroup(this.targets);
      this.Animals = new Setting<>("Animals", true).addToGroup(this.targets);
      this.Villagers = new Setting<>("Villagers", true).addToGroup(this.targets);
      this.Slimes = new Setting<>("Slimes", true).addToGroup(this.targets);
      this.hostiles = new Setting<>("Hostiles", true).addToGroup(this.targets);
      this.onlyAngry = new Setting<>("OnlyAngryHostiles", true, v -> this.hostiles.getValue()).addToGroup(this.targets);
      this.Projectiles = new Setting<>("Projectiles", true).addToGroup(this.targets);
      this.ignoreInvisible = new Setting<>("IgnoreInvisibleEntities", false).addToGroup(this.targets);
      this.ignoreNamed = new Setting<>("IgnoreNamed", false).addToGroup(this.targets);
      this.ignoreTeam = new Setting<>("IgnoreTeam", false).addToGroup(this.targets);
      this.ignoreCreative = new Setting<>("IgnoreCreative", true).addToGroup(this.targets);
      this.ignoreNaked = new Setting<>("IgnoreNaked", false).addToGroup(this.targets);
      this.ignoreShield = new Setting<>("AttackShieldingEntities", true).addToGroup(this.targets);
      this.pitchAcceleration = 1.0F;
      this.rotationPoint = class_243.field_1353;
      this.rotationMotion = class_243.field_1353;
      this.delayTimer = new Timer();
      this.pauseTimer = new Timer();
   }

   private float getRange() {
      return this.elytra.getValue() && mc.field_1724.method_6128() ? this.elytraAttackRange.getValue() : this.attackRange.getValue();
   }

   private float getWallRange() {
      return this.elytra.getValue() && mc.field_1724 != null && mc.field_1724.method_6128() ? this.elytraWallRange.getValue() : this.wallRange.getValue();
   }

   public void auraLogic() {
      if (!this.haveWeapon()) {
         target = null;
      } else {
         this.handleKill();
         this.updateTarget();
         if (target != null) {
            if (!mc.field_1690.field_1903.method_1434() && mc.field_1724.method_24828() && this.autoJump.getValue()) {
               mc.field_1724.method_6043();
            }

            boolean readyForAttack;
            if (this.grimRayTrace.getValue()) {
               readyForAttack = this.autoCrit() && (this.lookingAtHitbox || this.skipRayTraceCheck());
               this.calcRotations(this.autoCrit());
            } else {
               this.calcRotations(this.autoCrit());
               readyForAttack = this.autoCrit() && (this.lookingAtHitbox || this.skipRayTraceCheck());
            }

            if (readyForAttack) {
               if (this.shieldBreaker(false)) {
                  return;
               }

               boolean[] playerState = this.preAttack();
               if (!(target instanceof class_1657 pl)
                  || !pl.method_6115()
                  || pl.method_6079().method_7909() != class_1802.field_8255
                  || this.ignoreShield.getValue()) {
                  this.attack();
               }

               this.postAttack(playerState[0], playerState[1]);
            }
         }
      }
   }

   private boolean haveWeapon() {
      class_1792 handItem = mc.field_1724.method_6047().method_7909();
      if (!this.onlyWeapon.getValue()) {
         return true;
      } else {
         return this.switchMode.getValue() == Aura.Switch.None
            ? handItem instanceof class_1829 || handItem instanceof class_1743 || handItem instanceof class_1835
            : InventoryUtility.getSwordHotBar().found() || InventoryUtility.getAxeHotBar().found();
      }
   }

   private boolean skipRayTraceCheck() {
      return this.rotationMode.getValue() == null
         || this.rayTrace.getValue() == Aura.RayTrace.OFF
         || this.rotationMode.is(null)
         || this.rotationMode.is(null)
            && (
               this.interactTicks.getValue() <= 1
                  || mc.field_1687
                     .method_20812(mc.field_1724, mc.field_1724.method_5829().method_1009(-0.25, 0.0, -0.25).method_989(0.0, 1.0, 0.0))
                     .iterator()
                     .hasNext()
            );
   }

   public void attack() {
      Criticals.cancelCrit = true;
      ModuleManager.criticals.doCrit();
      int prevSlot = this.switchMethod();
      mc.field_1761.method_2918(mc.field_1724, target);
      Criticals.cancelCrit = false;
      this.swingHand();
      this.hitTicks = this.getHitTicks();
      if (prevSlot != -1) {
         InventoryUtility.switchTo(prevSlot);
      }
   }

   @NotNull
   private boolean[] preAttack() {
      boolean blocking = mc.field_1724.method_6115()
         && mc.field_1724.method_6030().method_7909().method_7853(mc.field_1724.method_6030()) == class_1839.field_8949;
      if (blocking && this.unpressShield.getValue()) {
         this.sendPacket(new class_2846(class_2847.field_12974, class_2338.field_10980, class_2350.field_11033));
      }

      boolean sprint = Core.serverSprint;
      if (sprint && this.dropSprint.getValue()) {
         this.disableSprint();
      }

      if (this.rotationMode.is(null)) {
         this.sendPacket(
            new class_2830(
               mc.field_1724.method_23317(),
               mc.field_1724.method_23318(),
               mc.field_1724.method_23321(),
               this.rotationYaw,
               this.rotationPitch,
               mc.field_1724.method_24828()
            )
         );
      }

      return new boolean[]{blocking, sprint};
   }

   public void postAttack(boolean block, boolean sprint) {
      if (sprint && this.returnSprint.getValue() && this.dropSprint.getValue()) {
         this.enableSprint();
      }

      if (block && this.unpressShield.getValue()) {
         this.sendSequencedPacket(id -> new class_2886(class_1268.field_5810, id, this.rotationYaw, this.rotationPitch));
      }

      if (this.rotationMode.is(null)) {
         this.sendPacket(
            new class_2830(
               mc.field_1724.method_23317(),
               mc.field_1724.method_23318(),
               mc.field_1724.method_23321(),
               mc.field_1724.method_36454(),
               mc.field_1724.method_36455(),
               mc.field_1724.method_24828()
            )
         );
      }
   }

   private void disableSprint() {
      mc.field_1724.method_5728(false);
      mc.field_1690.field_1867.method_23481(false);
      this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12985));
   }

   private void enableSprint() {
      mc.field_1724.method_5728(true);
      mc.field_1690.field_1867.method_23481(true);
      this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12981));
   }

   public void resolvePlayers() {
      if (this.resolver.not(Aura.Resolver.Off)) {
         for (class_1657 player : mc.field_1687.method_18456()) {
            if (player instanceof class_745) {
               ((IOtherClientPlayerEntity)player).resolve(this.resolver.getValue());
            }
         }
      }
   }

   public void restorePlayers() {
      if (this.resolver.not(Aura.Resolver.Off)) {
         for (class_1657 player : mc.field_1687.method_18456()) {
            if (player instanceof class_745) {
               ((IOtherClientPlayerEntity)player).releaseResolver();
            }
         }
      }
   }

   public void handleKill() {
      if (target instanceof class_1309 && (((class_1309)target).method_6032() <= 0.0F || ((class_1309)target).method_29504())) {
         Managers.NOTIFICATION
            .publicity("Aura", ClientSettings.isRu() ? "Цель успешно нейтрализована!" : "Target successfully neutralized!", 3, Notification.Type.SUCCESS);
      }
   }

   private int switchMethod() {
      int prevSlot = -1;
      SearchInvResult swordResult = InventoryUtility.getSwordHotBar();
      if (swordResult.found() && this.switchMode.getValue() != Aura.Switch.None) {
         if (this.switchMode.getValue() == Aura.Switch.Silent) {
            prevSlot = mc.field_1724.method_31548().field_7545;
         }

         swordResult.switchTo();
      }

      return prevSlot;
   }

   private int getHitTicks() {
      return this.oldDelay.getValue().isEnabled()
         ? 1 + (int)(20.0F / MathUtility.random((float)this.minCPS.getValue().intValue(), (float)this.maxCPS.getValue().intValue()))
         : (this.shouldRandomizeDelay() ? (int)MathUtility.random(11.0F, 13.0F) : this.attackTickLimit.getValue());
   }

   @EventHandler
   public void onUpdate(PlayerUpdateEvent e) {
      if (this.pauseTimer.passedMs(1000L)) {
         if (!mc.field_1724.method_6115() || !this.pauseWhileEating.getValue()) {
            if (this.pauseBaritone.getValue() && NevaWare.baritone) {
               boolean isTargeted = target != null;
               if (isTargeted && !wasTargeted) {
                  BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("pause");
                  wasTargeted = true;
               } else if (!isTargeted && wasTargeted) {
                  BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("resume");
                  wasTargeted = false;
               }
            }

            this.resolvePlayers();
            this.auraLogicSb();
            this.restorePlayers();
            this.hitTicks--;
         }
      }
   }

   @EventHandler
   public void onSync(EventSync e) {
      if (this.pauseTimer.passedMs(1000L)) {
         if (!mc.field_1724.method_6115() || !this.pauseWhileEating.getValue()) {
            if (this.haveWeapon()) {
               if (target != null && this.rotationMode.getValue() != null && this.rotationMode.getValue() != null) {
                  mc.field_1724.method_36456(this.rotationYaw);
                  mc.field_1724.method_36457(this.rotationPitch);
               } else {
                  this.rotationYaw = mc.field_1724.method_36454();
                  this.rotationPitch = mc.field_1724.method_36455();
               }

               if (this.oldDelay.getValue().isEnabled() && this.minCPS.getValue() > this.maxCPS.getValue()) {
                  this.minCPS.setValue(this.maxCPS.getValue());
               }

               if (target != null && this.pullDown.getValue() && (mc.field_1724.method_6059(class_1294.field_5913) || !this.onlyJumpBoost.getValue())) {
                  mc.field_1724.method_5762(0.0, (double)(-this.pullValue.getValue() / 1000.0F), 0.0);
               }
            }
         }
      }
   }

   @EventHandler
   public void onPacketSend(@NotNull PacketEvent.Send e) {
      if (e.getPacket() instanceof class_2824 pie && Criticals.getInteractType(pie) != Criticals.InteractType.ATTACK && target != null) {
         e.cancel();
      }
   }

   @EventHandler
   public void onPacketReceive(@NotNull PacketEvent.Receive e) {
      if (e.getPacket() instanceof class_2663 status
         && status.method_11470() == 30
         && status.method_11469(mc.field_1687) != null
         && target != null
         && status.method_11469(mc.field_1687) == target) {
         Managers.NOTIFICATION
            .publicity(
               "Aura",
               ClientSettings.isRu()
                  ? "Успешно сломали щит игроку " + target.method_5477().getString()
                  : "Succesfully destroyed " + target.method_5477().getString() + "'s shield",
               2,
               Notification.Type.SUCCESS
            );
      }

      if (e.getPacket() instanceof class_2708 && this.tpDisable.getValue()) {
         this.disable(ClientSettings.isRu() ? "Отключаю из-за телепортации!" : "Disabling due to tp!");
      }

      if (e.getPacket() instanceof class_2663 pac
         && pac.method_11470() == 3
         && pac.method_11469(mc.field_1687) == mc.field_1724
         && this.deathDisable.getValue()) {
         this.disable(ClientSettings.isRu() ? "Отключаю из-за смерти!" : "Disabling due to death!");
      }
   }

   @Override
   public void onEnable() {
      SharpAura.reset();
      BypassAura.reset();
      target = null;
      this.lookingAtHitbox = false;
      this.rotationPoint = class_243.field_1353;
      this.rotationMotion = class_243.field_1353;
      this.rotationYaw = mc.field_1724.method_36454();
      this.rotationPitch = mc.field_1724.method_36455();
      this.delayTimer.reset();
   }

   private boolean autoCrit() {
      boolean reasonForSkipCrit = !this.smartCrit.getValue().isEnabled()
         || mc.field_1724.method_31549().field_7479
         || mc.field_1724.method_6128()
         || ModuleManager.elytraPlus.isEnabled()
         || mc.field_1724.method_6059(class_1294.field_5919)
         || mc.field_1724.method_6059(class_1294.field_5906)
         || Managers.PLAYER.isInWeb();
      if (this.hitTicks > 0) {
         return false;
      } else if (this.pauseInInventory.getValue() && Managers.PLAYER.inInventory) {
         return false;
      } else if (this.getAttackCooldown() < this.attackCooldown.getValue() && !this.oldDelay.getValue().isEnabled()) {
         return false;
      } else if (ModuleManager.criticals.isEnabled() && ModuleManager.criticals.mode.is(Criticals.Mode.Grim)) {
         return true;
      } else {
         boolean mergeWithTargetStrafe = !ModuleManager.targetStrafe.isEnabled() || !ModuleManager.targetStrafe.jump.getValue();
         boolean mergeWithSpeed = !ModuleManager.speed.isEnabled() || mc.field_1724.method_24828();
         if (!mc.field_1690.field_1903.method_1434() && mergeWithTargetStrafe && mergeWithSpeed && !this.onlySpace.getValue() && !this.autoJump.getValue()) {
            return true;
         } else if (mc.field_1724.method_5771() || mc.field_1724.method_5869()) {
            return true;
         } else if (!mc.field_1690.field_1903.method_1434() && this.isAboveWater()) {
            return true;
         } else if (mc.field_1724.field_6017 > 1.0F && (double)mc.field_1724.field_6017 < 1.14) {
            return false;
         } else {
            return reasonForSkipCrit
               ? true
               : !mc.field_1724.method_24828()
                  && mc.field_1724.field_6017 > (this.shouldRandomizeFallDistance() ? MathUtility.random(0.15F, 0.7F) : this.critFallDistance.getValue());
         }
      }
   }

   private boolean shieldBreaker(boolean var1) {
      int var2 = InventoryUtility.getAxeHotBar().slot();
      if (var2 == -1) {
         return false;
      } else if (!this.shieldBreaker.getValue()) {
         return false;
      } else if (!(target instanceof class_1657)) {
         return false;
      } else if (!((class_1657)target).method_6115()) {
         return false;
      } else if (((class_1657)target).method_6048() < 5) {
         return false;
      } else if (((class_1657)target).method_6079().method_7909() != class_1802.field_8255
         && ((class_1657)target).method_6047().method_7909() != class_1802.field_8255) {
         return false;
      } else {
         int var3 = mc.field_1724.method_31548().field_7545;
         if (var2 != var3) {
            this.sbPrev = var3;
            mc.field_1724.method_31548().field_7545 = var2;
            this.sbState = 1;
         }

         mc.field_1761.method_2918(mc.field_1724, target);
         this.swingHand();
         this.hitTicks = 10;
         return true;
      }
   }

   private void swingHand() {
      switch ((Aura.AttackHand)this.attackHand.getValue()) {
         case MainHand:
            mc.field_1724.method_6104(class_1268.field_5808);
            break;
         case OffHand:
            mc.field_1724.method_6104(class_1268.field_5810);
      }
   }

   public boolean isAboveWater() {
      return mc.field_1724.method_5869()
         || mc.field_1687.method_8320(class_2338.method_49638(mc.field_1724.method_19538().method_1031(0.0, -0.4, 0.0))).method_26204()
            == class_2246.field_10382;
   }

   public float getAttackCooldownProgressPerTick() {
      return (float)(
         1.0
            / mc.field_1724.method_45325(class_5134.field_23723)
            * 20.0
            * (double)NevaWare.TICK_TIMER
            * (double)(this.tpsSync.getValue() ? Managers.SERVER.getTPSFactor() : 1.0F)
      );
   }

   public float getAttackCooldown() {
      return class_3532.method_15363(
         ((float)((ILivingEntity)mc.field_1724).getLastAttackedTicks() + this.attackBaseTime.getValue()) / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F
      );
   }

   private void updateTarget() {
      class_1297 candidat = this.findTarget();
      if (target == null) {
         target = candidat;
      } else {
         if (this.sort.getValue() == Aura.Sort.FOV || !this.lockTarget.getValue()) {
            target = candidat;
         }

         if (candidat instanceof class_1676) {
            target = candidat;
         }

         if (this.skipEntity(target)) {
            target = null;
         }
      }
   }

   private void calcRotations(boolean ready) {
      if (this.rotationMode.is(Aura.Mode.Sharp)) {
         this.calcSharp(ready);
      } else if (this.rotationMode.is(Aura.Mode.Snap)) {
         this.calcSnap(ready);
      } else if (this.rotationMode.is(Aura.Mode.HvH)) {
         this.calcHvhRotations(target, ready);
      } else if (this.rotationMode.is(Aura.Mode.SpookyTime)) {
         this.calcSpookyRotations(target, ready);
      } else {
         if (ready) {
            this.trackticks = mc.field_1687
                  .method_20812(mc.field_1724, mc.field_1724.method_5829().method_1009(-0.25, 0.0, -0.25).method_989(0.0, 1.0, 0.0))
                  .iterator()
                  .hasNext()
               ? 1
               : this.interactTicks.getValue();
         } else if (this.trackticks > 0) {
            this.trackticks--;
         }

         if (target != null) {
            class_243 targetVec;
            if (!mc.field_1724.method_6128() && !ModuleManager.elytraPlus.isEnabled()) {
               targetVec = this.getLegitLook(target);
            } else {
               targetVec = target.method_33571();
            }

            if (targetVec != null) {
               this.pitchAcceleration = Managers.PLAYER
                     .checkRtx(
                        this.rotationYaw,
                        this.rotationPitch,
                        this.getRange() + this.getAimRange(),
                        this.getRange() + this.getAimRange(),
                        this.rayTrace.getValue()
                     )
                  ? this.aimedPitchStep.getValue()
                  : (
                     this.pitchAcceleration < this.maxPitchStep.getValue()
                        ? this.pitchAcceleration * this.pitchAccelerate.getValue()
                        : this.maxPitchStep.getValue()
                  );
               float delta_yaw = class_3532.method_15393(
                     (float)class_3532.method_15338(
                           Math.toDegrees(Math.atan2(targetVec.field_1350 - mc.field_1724.method_23321(), targetVec.field_1352 - mc.field_1724.method_23317()))
                              - 90.0
                        )
                        - this.rotationYaw
                  )
                  + (float)(this.wallsBypass.is(Aura.WallsBypass.V2) && !ready && !mc.field_1724.method_6057(target) ? 20 : 0);
               float delta_pitch = (float)(
                     -Math.toDegrees(
                        Math.atan2(
                           targetVec.field_1351 - (mc.field_1724.method_19538().field_1351 + (double)mc.field_1724.method_18381(mc.field_1724.method_18376())),
                           Math.sqrt(
                              Math.pow(targetVec.field_1352 - mc.field_1724.method_23317(), 2.0)
                                 + Math.pow(targetVec.field_1350 - mc.field_1724.method_23321(), 2.0)
                           )
                        )
                     )
                  )
                  - this.rotationPitch;
               float yawStep = this.rotationMode.getValue() != Aura.Mode.TrackV2
                  ? 360.0F
                  : MathUtility.random((float)this.minYawStep.getValue().intValue(), (float)this.maxYawStep.getValue().intValue());
               float pitchStep = this.rotationMode.getValue() != Aura.Mode.TrackV2
                  ? 180.0F
                  : (Managers.PLAYER.ticksElytraFlying > 5 ? 180.0F : this.pitchAcceleration + MathUtility.random(-1.0F, 1.0F));
               if (ready) {
                  switch ((Aura.AccelerateOnHit)this.accelerateOnHit.getValue()) {
                     case Yaw:
                        yawStep = 180.0F;
                        break;
                     case Pitch:
                        pitchStep = 90.0F;
                        break;
                     case Both:
                        yawStep = 180.0F;
                        pitchStep = 90.0F;
                  }
               }

               if (delta_yaw > 180.0F) {
                  delta_yaw -= 180.0F;
               }

               float deltaYaw = class_3532.method_15363(class_3532.method_15379(delta_yaw), -yawStep, yawStep);
               float deltaPitch = class_3532.method_15363(delta_pitch, -pitchStep, pitchStep);
               float newYaw = this.rotationYaw + (delta_yaw > 0.0F ? deltaYaw : -deltaYaw);
               float newPitch = class_3532.method_15363(this.rotationPitch + deltaPitch, -90.0F, 90.0F);
               double gcdFix = Math.pow((Double)mc.field_1690.method_42495().method_41753() * 0.6 + 0.2, 3.0) * 1.2;
               if (this.trackticks <= 0 && this.rotationMode.getValue() != Aura.Mode.TrackV2) {
                  this.rotationYaw = mc.field_1724.method_36454();
                  this.rotationPitch = mc.field_1724.method_36455();
               } else {
                  this.rotationYaw = (float)((double)newYaw - (double)(newYaw - this.rotationYaw) % gcdFix);
                  this.rotationPitch = (float)((double)newPitch - (double)(newPitch - this.rotationPitch) % gcdFix);
               }

               if (!this.rotationMode.is(null)) {
                  ModuleManager.rotations.fixRotation = this.rotationYaw;
               }

               this.lookingAtHitbox = Managers.PLAYER
                  .checkRtx(this.rotationYaw, this.rotationPitch, this.getRange(), this.getWallRange(), this.rayTrace.getValue());
            }
         }
      }
   }

   @Override
   public void onRender3D(class_4587 stack) {
      if (this.haveWeapon() && target != null) {
         if ((this.resolver.is(Aura.Resolver.BackTrack) || this.resolverVisualisation.getValue()) && this.resolvedBox != null) {
            Render3DEngine.OUTLINE_QUEUE.add(new Render3DEngine.OutlineAction(this.resolvedBox, HudEditor.getColor(0), 1.0F));
         }

         TargetESP.render(target, stack);
         if (this.clientLook.getValue() && this.rotationMode.getValue() != null) {
            mc.field_1724
               .method_36456(
                  (float)Render2DEngine.interpolate((double)mc.field_1724.field_5982, (double)this.rotationYaw, (double)Render3DEngine.getTickDelta())
               );
            mc.field_1724
               .method_36457(
                  (float)Render2DEngine.interpolate((double)mc.field_1724.field_6004, (double)this.rotationPitch, (double)Render3DEngine.getTickDelta())
               );
         }
      }
   }

   @Override
   public void onDisable() {
      target = null;
   }

   public float getSquaredRotateDistance() {
      float dst = this.getRange();
      dst += this.getAimRange();
      if ((mc.field_1724.method_6128() || ModuleManager.elytraPlus.isEnabled()) && target != null) {
         dst += 4.0F;
      }

      if (ModuleManager.strafe.isEnabled()) {
         dst += 4.0F;
      }

      if (!this.isAimRotationMode() || this.rayTrace.getValue() == Aura.RayTrace.OFF) {
         dst = this.getRange();
      }

      return dst * dst;
   }

   public class_243 getLegitLook(class_1297 target) {
      float minMotionXZ = 0.003F;
      float maxMotionXZ = 0.03F;
      float minMotionY = 0.001F;
      float maxMotionY = 0.03F;
      double lenghtX = target.method_5829().method_17939();
      double lenghtY = target.method_5829().method_17940();
      double lenghtZ = target.method_5829().method_17941();
      if (this.rotationMotion.equals(class_243.field_1353)) {
         this.rotationMotion = new class_243(
            (double)MathUtility.random(-0.05F, 0.05F), (double)MathUtility.random(-0.05F, 0.05F), (double)MathUtility.random(-0.05F, 0.05F)
         );
      }

      this.rotationPoint = this.rotationPoint.method_1019(this.rotationMotion);
      if (this.rotationPoint.field_1352 >= (lenghtX - 0.05) / 2.0) {
         this.rotationMotion = new class_243(
            (double)(-MathUtility.random(minMotionXZ, maxMotionXZ)), this.rotationMotion.method_10214(), this.rotationMotion.method_10215()
         );
      }

      if (this.rotationPoint.field_1351 >= lenghtY) {
         this.rotationMotion = new class_243(
            this.rotationMotion.method_10216(), (double)(-MathUtility.random(minMotionY, maxMotionY)), this.rotationMotion.method_10215()
         );
      }

      if (this.rotationPoint.field_1350 >= (lenghtZ - 0.05) / 2.0) {
         this.rotationMotion = new class_243(
            this.rotationMotion.method_10216(), this.rotationMotion.method_10214(), (double)(-MathUtility.random(minMotionXZ, maxMotionXZ))
         );
      }

      if (this.rotationPoint.field_1352 <= -(lenghtX - 0.05) / 2.0) {
         this.rotationMotion = new class_243(
            (double)MathUtility.random(minMotionXZ, 0.03F), this.rotationMotion.method_10214(), this.rotationMotion.method_10215()
         );
      }

      if (this.rotationPoint.field_1351 <= 0.05) {
         this.rotationMotion = new class_243(
            this.rotationMotion.method_10216(), (double)MathUtility.random(minMotionY, maxMotionY), this.rotationMotion.method_10215()
         );
      }

      if (this.rotationPoint.field_1350 <= -(lenghtZ - 0.05) / 2.0) {
         this.rotationMotion = new class_243(
            this.rotationMotion.method_10216(), this.rotationMotion.method_10214(), (double)MathUtility.random(minMotionXZ, maxMotionXZ)
         );
      }

      this.rotationPoint.method_1031((double)MathUtility.random(-0.03F, 0.03F), 0.0, (double)MathUtility.random(-0.03F, 0.03F));
      if (!mc.field_1724.method_6057(target) && Objects.requireNonNull(this.wallsBypass.getValue()) == Aura.WallsBypass.V1) {
         return target.method_19538().method_1031(MathUtility.random(-0.15, 0.15), lenghtY, MathUtility.random(-0.15, 0.15));
      } else {
         if (!Managers.PLAYER.checkRtx(this.rotationYaw, this.rotationPitch, this.getRange(), this.getWallRange(), this.rayTrace.getValue())) {
            float[] rotation1 = PlayerManager.calcAngle(
               target.method_19538().method_1031(0.0, (double)(target.method_18381(target.method_18376()) / 2.0F), 0.0)
            );
            if (PlayerUtility.squaredDistanceFromEyes(target.method_19538().method_1031(0.0, (double)(target.method_18381(target.method_18376()) / 2.0F), 0.0))
                  <= this.attackRange.getPow2Value()
               && Managers.PLAYER.checkRtx(rotation1[0], rotation1[1], this.getRange(), 0.0F, this.rayTrace.getValue())) {
               this.rotationPoint = new class_243(
                  (double)MathUtility.random(-0.1F, 0.1F),
                  (double)(target.method_18381(target.method_18376()) / MathUtility.random(0.27F, 2.5F)),
                  (double)MathUtility.random(-0.1F, 0.1F)
               );
            } else {
               float halfBox = (float)(lenghtX / 2.0);

               for (float x1 = -halfBox; x1 <= halfBox; x1 += 0.05F) {
                  for (float z1 = -halfBox; z1 <= halfBox; z1 += 0.05F) {
                     for (float y1 = 0.05F; (double)y1 <= target.method_5829().method_17940(); y1 += 0.15F) {
                        class_243 v1 = new class_243(target.method_23317() + (double)x1, target.method_23318() + (double)y1, target.method_23321() + (double)z1);
                        if (!(PlayerUtility.squaredDistanceFromEyes(v1) > this.attackRange.getPow2Value())) {
                           float[] rotation = PlayerManager.calcAngle(v1);
                           if (Managers.PLAYER.checkRtx(rotation[0], rotation[1], this.getRange(), 0.0F, this.rayTrace.getValue())) {
                              this.rotationPoint = new class_243((double)x1, (double)y1, (double)z1);
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

         return target.method_19538().method_1019(this.rotationPoint);
      }
   }

   public boolean isInRange(class_1297 target) {
      if (PlayerUtility.squaredDistanceFromEyes(target.method_19538().method_1031(0.0, (double)target.method_18381(target.method_18376()), 0.0))
         > this.getSquaredRotateDistance() + 4.0F) {
         return false;
      } else {
         float halfBox = (float)(target.method_5829().method_17939() / 2.0);

         for (float x1 = -halfBox; x1 <= halfBox; x1 += 0.15F) {
            for (float z1 = -halfBox; z1 <= halfBox; z1 += 0.15F) {
               for (float y1 = 0.05F; (double)y1 <= target.method_5829().method_17940(); y1 += 0.25F) {
                  if (!(
                     PlayerUtility.squaredDistanceFromEyes(
                           new class_243(target.method_23317() + (double)x1, target.method_23318() + (double)y1, target.method_23321() + (double)z1)
                        )
                        > this.getSquaredRotateDistance()
                  )) {
                     float[] rotation = PlayerManager.calcAngle(
                        new class_243(target.method_23317() + (double)x1, target.method_23318() + (double)y1, target.method_23321() + (double)z1)
                     );
                     if (Managers.PLAYER
                        .checkRtx(
                           rotation[0], rotation[1], (float)Math.sqrt((double)this.getSquaredRotateDistance()), this.getWallRange(), this.rayTrace.getValue()
                        )) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   public class_1297 findTarget() {
      List<class_1309> first_stage = new CopyOnWriteArrayList<>();

      for (class_1297 ent : mc.field_1687.method_18112()) {
         if ((ent instanceof class_1678 || ent instanceof class_1674) && ent.method_5805() && this.isInRange(ent) && this.Projectiles.getValue()) {
            return ent;
         }

         if (!this.skipEntity(ent) && ent instanceof class_1309) {
            first_stage.add((class_1309)ent);
         }
      }
      return switch ((Aura.Sort)this.sort.getValue()) {
         case LowestDistance -> (class_1309)first_stage.stream().min(Comparator.comparing(e -> mc.field_1724.method_5707(e.method_19538()))).orElse(null);
         case HighestDistance -> (class_1309)first_stage.stream().max(Comparator.comparing(e -> mc.field_1724.method_5707(e.method_19538()))).orElse(null);
         case LowestHealth -> (class_1309)first_stage.stream().min(Comparator.comparing(e -> e.method_6032() + e.method_6067())).orElse(null);
         case HighestHealth -> (class_1309)first_stage.stream().max(Comparator.comparing(e -> e.method_6032() + e.method_6067())).orElse(null);
         case LowestDurability -> (class_1309)first_stage.stream().min(Comparator.comparing(e -> {
         float v = 0.0F;

         for (class_1799 armor : e.method_5661()) {
            if (armor != null && !armor.method_7909().equals(class_1802.field_8162)) {
               v += (float)(armor.method_7936() - armor.method_7919()) / (float)armor.method_7936();
            }
         }

         return v;
      })).orElse(null);
         case HighestDurability -> (class_1309)first_stage.stream().max(Comparator.comparing(e -> {
         float v = 0.0F;

         for (class_1799 armor : e.method_5661()) {
            if (armor != null && !armor.method_7909().equals(class_1802.field_8162)) {
               v += (float)(armor.method_7936() - armor.method_7919()) / (float)armor.method_7936();
            }
         }

         return v;
      })).orElse(null);
         case FOV -> (class_1309)first_stage.stream().min(Comparator.comparing(this::getFOVAngle)).orElse(null);
      };
   }

   private boolean skipEntity(class_1297 entity) {
      if (this.isBullet(entity)) {
         return false;
      } else if (!(entity instanceof class_1309 ent)) {
         return true;
      } else if (ent.method_29504() || !entity.method_5805()) {
         return true;
      } else if (entity instanceof class_1531) {
         return true;
      } else if (entity instanceof class_1451) {
         return true;
      } else if (this.skipNotSelected(entity)) {
         return true;
      } else if (!InteractionUtility.isVecInFOV(ent.method_19538(), this.fov.getValue())) {
         return true;
      } else {
         if (entity instanceof class_1657 player) {
            if (ModuleManager.antiBot.isEnabled() && AntiBot.bots.contains(entity)) {
               return true;
            }

            if (player == mc.field_1724 || Managers.FRIEND.isFriend(player)) {
               return true;
            }

            if (player.method_7337() && this.ignoreCreative.getValue()) {
               return true;
            }

            if (player.method_6096() == 0 && this.ignoreNaked.getValue()) {
               return true;
            }

            if (player.method_5767() && this.ignoreInvisible.getValue()) {
               return true;
            }

            if (player.method_22861() == mc.field_1724.method_22861() && this.ignoreTeam.getValue() && mc.field_1724.method_22861() != 16777215) {
               return true;
            }
         }

         return !this.isInRange(entity) || entity.method_16914() && this.ignoreNamed.getValue();
      }
   }

   private boolean isBullet(class_1297 entity) {
      return (entity instanceof class_1678 || entity instanceof class_1674)
         && entity.method_5805()
         && PlayerUtility.squaredDistanceFromEyes(entity.method_19538()) < this.getSquaredRotateDistance()
         && this.Projectiles.getValue();
   }

   private boolean skipNotSelected(class_1297 entity) {
      if (entity instanceof class_1621 && !this.Slimes.getValue()) {
         return true;
      } else {
         if (entity instanceof class_1588 he) {
            if (!this.hostiles.getValue()) {
               return true;
            }

            if (this.onlyAngry.getValue()) {
               return !he.method_7076(mc.field_1724);
            }
         }

         if (entity instanceof class_1657 && !this.Players.getValue()) {
            return true;
         } else if (entity instanceof class_1646 && !this.Villagers.getValue()) {
            return true;
         } else {
            return entity instanceof class_1308 && !this.Mobs.getValue() ? true : entity instanceof class_1429 && !this.Animals.getValue();
         }
      }
   }

   private float getFOVAngle(@NotNull class_1309 e) {
      double difX = e.method_23317() - mc.field_1724.method_23317();
      double difZ = e.method_23321() - mc.field_1724.method_23321();
      float yaw = (float)class_3532.method_15338(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0);
      return Math.abs(yaw - class_3532.method_15393(mc.field_1724.method_36454()));
   }

   public void pause() {
      this.pauseTimer.reset();
   }

   private boolean shouldRandomizeDelay() {
      return this.randomHitDelay.getValue()
         && (mc.field_1724.method_24828() || mc.field_1724.field_6017 < 0.12F || mc.field_1724.method_5681() || mc.field_1724.method_6128());
   }

   private boolean shouldRandomizeFallDistance() {
      return this.randomHitDelay.getValue() && !this.shouldRandomizeDelay();
   }

   private static void renderGhostsNextGen(int var0, int var1, float var2, float var3, class_1297 var4) {
      Render3DEngine.renderGhosts(var1, var2, var4);
   }

   private boolean isAimRotationMode() {
      if (this.rotationMode.is(Aura.Mode.Sharp)) {
         return true;
      } else {
         return this.rotationMode.is(Aura.Mode.Snap)
            ? true
            : this.rotationMode.is(Aura.Mode.TrackV2) || this.rotationMode.is(Aura.Mode.HvH) || this.rotationMode.is(Aura.Mode.SpookyTime);
      }
   }

   private void auraLogicSb() {
      if (this.sbState == 1) {
         mc.field_1724.method_31548().field_7545 = this.sbPrev;
         this.sbState = 0;
      }

      this.auraLogic();
   }

   private void calcHvhRotations(class_1297 var1, boolean var2) {
      float[] var3 = AuraHvh.compute(mc, var1, this.getWallRange() >= this.getRange());
      this.rotationYaw = var3[0];
      this.rotationPitch = var3[1];
      ModuleManager.rotations.fixRotation = this.rotationYaw;
      this.lookingAtHitbox = Managers.PLAYER.checkRtx(this.rotationYaw, this.rotationPitch, this.getRange(), this.getWallRange(), this.rayTrace.getValue());
   }

   private void calcSpookyRotations(class_1297 var1, boolean var2) {
      float[] var3 = AuraSpooky.compute(mc, var1, var2);
      this.rotationYaw = var3[0];
      this.rotationPitch = var3[1];
      ModuleManager.rotations.fixRotation = this.rotationYaw;
      this.lookingAtHitbox = Managers.PLAYER.checkRtx(this.rotationYaw, this.rotationPitch, this.getRange(), this.getWallRange(), this.rayTrace.getValue());
   }

   private void calcSnap(boolean var1) {
      BypassAura.apply(this, var1, 1, this.hitTicks);
      this.lookingAtHitbox = Managers.PLAYER.checkRtx(this.rotationYaw, this.rotationPitch, this.getRange(), this.getWallRange(), this.rayTrace.getValue());
   }

   private void calcSharp(boolean var1) {
      SharpAura.apply(this, var1, this.hitTicks);
      this.lookingAtHitbox = Managers.PLAYER.checkRtx(this.rotationYaw, this.rotationPitch, this.getRange(), this.getWallRange(), this.rayTrace.getValue());
   }

   public float getAimRange() {
      return (mc.field_1724 != null && mc.field_1724.method_6128() ? this.elytraAimRange : this.aimRange).getValue();
   }

   public static enum AccelerateOnHit {
      Off,
      Yaw,
      Pitch,
      Both;
   }

   public static enum AttackHand {
      MainHand,
      OffHand,
      None;
   }

   public static enum Mode {
      TrackV2,
      SpookyTime,
      HvH,
      Snap,
      Sharp;
      // $VF: synthetic field
      private static final Aura.Mode[] $VALUES = $values();
   }

   public static class Position {
      private double x;
      private double y;
      private double z;
      private int ticks;

      public Position(double x, double y, double z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public boolean shouldRemove() {
         return this.ticks++ > ModuleManager.aura.backTicks.getValue();
      }

      public double getX() {
         return this.x;
      }

      public double getY() {
         return this.y;
      }

      public double getZ() {
         return this.z;
      }
   }

   public static enum RayTrace {
      OFF,
      OnlyTarget,
      AllEntities;
   }

   public static enum Resolver {
      Off,
      Advantage,
      Predictive,
      BackTrack;
   }

   public static enum Sort {
      LowestDistance,
      HighestDistance,
      LowestHealth,
      HighestHealth,
      LowestDurability,
      HighestDurability,
      FOV;
   }

   public static enum Switch {
      Normal,
      None,
      Silent;
   }

   public static enum WallsBypass {
      Off,
      V1,
      V2;
   }
}
