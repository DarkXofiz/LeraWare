package neva.ware.features.modules.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1451;
import net.minecraft.class_1531;
import net.minecraft.class_1646;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1839;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2815;
import net.minecraft.class_2846;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_2848;
import net.minecraft.class_2848.class_2849;
import net.minecraft.class_2868;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_465;
import net.minecraft.class_1713;
import neva.ware.core.Core;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventSync;
import neva.ware.events.impl.PlayerUpdateEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.render.TargetESP;
import neva.ware.gui.notification.Notification;
import net.minecraft.class_239;
import net.minecraft.class_2708;
import neva.ware.events.impl.PacketEvent;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.MouseQuantizer;
import neva.ware.utility.player.SearchInvResult;

/**
 * AttackAura: the MiranWare Aura. It replaces the old Netsuki based Aura.
 * HvH / Smooth rotation, Smart / Only critical gate, sprint drop, shield breaker, pause and auto-disable rules,
 * target filters, randomized attack delay, GCD-quantized rotation with tremor noise and human-like yaw / pitch animation.
 *
 * The rotation is silent: it is applied to the player only for the movement packet and restored afterwards,
 * and the attack is made right after that packet so the server sees the aim before the hit.
 *
 * Other modules read the public surface of this class: target, rotationYaw, rotationPitch, pause(),
 * and the Players / Mobs / Animals / Villagers / ignoreCreative / ignoreInvisible settings.
 */
public class AttackAura extends Module {
   /** Sunucu kritik / tam hasar icin saldiri cooldown > 0.9 ister; 0.86 esigi vuruslarin yarisinda kritigi kaciriyordu. */
   private static final float ATTACK_COOLDOWN = 0.95F;

   public final Setting<Float> attackRange = new Setting<>("Range", 3.1F, 1.0F, 6.0F);
   public final Setting<Float> aimRange = new Setting<>("AimRange", 3.1F, 0.0F, 6.0F);
   public final Setting<Float> elytraAimRange = new Setting<>("ElytraAimRange", 25.0F, 10.0F, 100.0F);
   public final Setting<AttackAura.Mode> rotationMode = new Setting<>("Rotation", AttackAura.Mode.HvH);
   /** Acikken: sunucunun anticheat tepkisine gore menzil / donus hizi / duvar kontrolu otomatik ayarlanir. */
   public final Setting<Boolean> bypass = new Setting<>("Bypass", true);
   public final Setting<AttackAura.CritMode> crit = new Setting<>("Crit", AttackAura.CritMode.Smart);
   public final Setting<Boolean> dropSprint = new Setting<>("DropSprint", true);
   /** Yerdeyken kritik icin otomatik zipla (gercek fizik kritigi, paket sahteciligi yok). */
   public final Setting<Boolean> critJump = new Setting<>("CritJump", true);
   public final Setting<AttackAura.Switch> switchMode = new Setting<>("AutoWeapon", AttackAura.Switch.None);
   public final Setting<Boolean> shieldBreaker = new Setting<>("ShieldBreaker", true);
   public final Setting<Boolean> unpressShield = new Setting<>("UnpressShield", true);
   public final Setting<Boolean> pauseWhileEating = new Setting<>("Pause: Eating", false);
   public final Setting<Boolean> pauseInInventory = new Setting<>("Pause: Inventory", true);
   public final Setting<Boolean> deathDisable = new Setting<>("Disable: Death", true);
   public final Setting<Boolean> killDisable = new Setting<>("Disable: Kill", false);
   public final Setting<Boolean> elytraTarget = new Setting<>("ElytraTarget", true);
   public final Setting<Boolean> Players = new Setting<>("Players", true);
   public final Setting<Boolean> Animals = new Setting<>("Animals", true);
   public final Setting<Boolean> Mobs = new Setting<>("Monsters", true);
   public final Setting<Boolean> Villagers = new Setting<>("Villagers", true);
   public final Setting<Boolean> ignoreInvisible = new Setting<>("IgnoreInvisibles", false);
   public final Setting<Boolean> ignoreCreative = new Setting<>("IgnoreCreative", true);

   public static class_1297 target;
   public float rotationYaw;
   public float rotationPitch;
   public class_238 resolvedBox;

   private boolean lookingAtHitbox;
   private boolean rotationCommitted;
   private long lastAttackMs;
   private long attackDelayMs;
   private float cooldownGate = ATTACK_COOLDOWN;
   private float tickFallDistance;
   private boolean tickOnGround;
   private boolean jumpedThisTick;
   private class_1297 notifiedKill;
   private float desiredYaw;
   private float desiredPitch;
   private final Timer pauseTimer = new Timer();
   private final YawAnim yawAnim = new YawAnim(1.0F);
   private final YawAnim pitchAnim = new YawAnim(0.65F);
   private final MouseQuantizer quantizer = new MouseQuantizer();
   private final PitchTremor yawTremor = new PitchTremor();
   private final PitchTremor pitchTremor = new PitchTremor();
   /**
    * NavelyaAC ElytraTargetA (lock-error ~0.8-1.2, ayni zamanda GCD check) ve ElmasOyuncu AntiElytraTarget
    * (AimLock err<=1.2, Melee strict<=1.0, ultra<=0.35) hedefe KUSURSUZ (hata ~0) kilitlenmeyi tespit ediyor.
    * Daireye yakin, yavasca donen bir sapma ekleyerek aninlik hatayi genelde bu esiklerin USTUNDE tutar;
    * genlik hitbox toleransinin (birkac derece) icinde kaldigi icin vurus oranini dusurmez.
    */
   private final OrbitJitter aimJitter = new OrbitJitter();
   private final long aimNoiseSeed = ThreadLocalRandom.current().nextLong();

   // ------------------------------------------------------------ bypass
   /** Sunucunun anticheat tepkisine gore otomatik secilen seviye. Siralama siki -> gevsek. */
   private enum AcLevel {
      STRICT(2.9F, 55.0F, 100.0F, 30.0F, 70.0F, true, 60L),
      NORMAL(3.0F, 90.0F, 150.0F, 50.0F, 100.0F, true, 0L),
      RELAXED(Float.MAX_VALUE, 180.0F, 180.0F, 90.0F, 90.0F, false, 0L);

      final float reach;
      final float yawCap;
      final float yawCapElytra;
      final float pitchCap;
      final float pitchCapElytra;
      final boolean wallCheck;
      final long extraDelayMs;

      AcLevel(float reach, float yawCap, float yawCapElytra, float pitchCap, float pitchCapElytra, boolean wallCheck, long extraDelayMs) {
         this.reach = reach;
         this.yawCap = yawCap;
         this.yawCapElytra = yawCapElytra;
         this.pitchCap = pitchCap;
         this.pitchCapElytra = pitchCapElytra;
         this.wallCheck = wallCheck;
         this.extraDelayMs = extraDelayMs;
      }
   }

   private static final Map<String, AcLevel> SERVER_LEVEL = new HashMap<>();
   private static final Map<String, AcLevel> SERVER_CEIL = new HashMap<>();

   private AcLevel level = AcLevel.NORMAL;
   private AcLevel ceil = AcLevel.RELAXED;
   private String serverKey = "unknown";
   private volatile int signals;
   private volatile long signalWindowMs;
   private volatile long lastRealAttackMs;
   private long cleanSinceMs;
   private long relaxedAtMs;
   private long lastTickMs;
   private int keyTicks;
   private boolean prevCommitted;
   private boolean lastCommitted;
   private float baseYaw;
   private float basePitch;
   private class_1297 pendingTarget;
   private float pendingHealth;
   private int pendingTicks;
   private int ignoredStreak;

   private static String currentServerKey() {
      try {
         if (mc.method_1562() != null && mc.method_1562().method_45734() != null) {
            return mc.method_1562().method_45734().field_3761.toLowerCase();
         }
      } catch (Throwable ignored) {
      }

      return "unknown";
   }

   private void loadLevel() {
      this.serverKey = currentServerKey();
      this.level = SERVER_LEVEL.getOrDefault(this.serverKey, AcLevel.NORMAL);
      this.ceil = SERVER_CEIL.getOrDefault(this.serverKey, AcLevel.RELAXED);
      long now = System.currentTimeMillis();
      this.signals = 0;
      this.signalWindowMs = now;
      this.cleanSinceMs = now;
      this.relaxedAtMs = 0L;
   }

   private void notifyLevel(String text) {
      try {
         Managers.NOTIFICATION.publicity("Aura", text, 3, Notification.Type.INFO);
      } catch (Throwable ignored) {
      }
   }

   /** Anticheat tepkisi sinyali: geri cekme, yok sayilan vuruslar, vurus sonrasi baglanti kopmasi. */
   private void addSignal() {
      long now = System.currentTimeMillis();
      if (now - this.signalWindowMs > 15000L) {
         this.signalWindowMs = now;
         this.signals = 0;
      }

      this.signals++;
      this.cleanSinceMs = now;
   }

   private void tighten(long now) {
      if (now - this.relaxedAtMs < 30000L) {
         // gevsettigimiz seviye sinyal yedi: bu sunucuda tavan olarak bir alt seviyeyi kabul et
         this.ceil = AcLevel.values()[Math.max(0, this.level.ordinal() - 1)];
         SERVER_CEIL.put(this.serverKey, this.ceil);
      }

      if (this.level.ordinal() > 0) {
         this.level = AcLevel.values()[this.level.ordinal() - 1];
         SERVER_LEVEL.put(this.serverKey, this.level);
         this.notifyLevel("Anticheat tepkisi algilandi, seviye: " + this.level.name());
      }

      this.signals = 0;
      this.signalWindowMs = now;
      this.cleanSinceMs = now;
      if (ModuleManager.elytraFlight != null && ModuleManager.elytraFlight.isEnabled()) {
         ModuleManager.elytraFlight.externalTighten();
      }
   }

   /** Her tick (bypass acikken): sunucu degisimi, vurus sonrasi kopma, sinyal degerlendirme, vurus geri bildirimi. */
   private void bypassTick() {
      long now = System.currentTimeMillis();
      if (this.lastTickMs != 0L
         && now - this.lastTickMs > 4000L
         && this.lastRealAttackMs > 0L
         && this.lastTickMs - this.lastRealAttackMs < 2500L) {
         // vurustan hemen sonra baglanti koptu / dondu: bu sunucuyu tehlikeli say, bir kademe sikilastir
         this.loadLevel();
         this.tighten(now);
         this.lastRealAttackMs = 0L;
      }

      this.lastTickMs = now;
      if (this.keyTicks++ % 40 == 0 && !currentServerKey().equals(this.serverKey)) {
         this.loadLevel();
      }

      if (now - this.signalWindowMs > 15000L) {
         this.signalWindowMs = now;
         this.signals = 0;
      }

      if (this.signals >= 3) {
         this.tighten(now);
      } else if (this.level.ordinal() < this.ceil.ordinal()
         && now - this.cleanSinceMs > 90000L
         && now - this.lastRealAttackMs < 60000L) {
         this.level = AcLevel.values()[this.level.ordinal() + 1];
         SERVER_LEVEL.put(this.serverKey, this.level);
         this.relaxedAtMs = now;
         this.cleanSinceMs = now;
         this.notifyLevel("Temiz vurus, seviye: " + this.level.name());
      }

      this.trackHitFeedback();
   }

   /** Vurustan ~6 tick sonra hedefin cani dusmediyse vurus yok sayilmis demektir; ust uste 4 kez olursa sinyal. */
   private void trackHitFeedback() {
      if (this.pendingTarget == null) {
         return;
      }

      if (++this.pendingTicks < 6) {
         return;
      }

      class_1297 t = this.pendingTarget;
      this.pendingTarget = null;
      if (!(t instanceof class_1309 living) || !t.method_5805() || living.method_29504()) {
         this.ignoredStreak = 0;
         return;
      }

      float hp = living.method_6032() + living.method_6067();
      if (hp < this.pendingHealth - 0.001F) {
         this.ignoredStreak = 0;
      } else if (++this.ignoredStreak >= 4) {
         this.ignoredStreak = 0;
         this.addSignal();
      }
   }

   /** Geri cekme (rubberband) vurustan hemen sonra geldiyse sinyal say. */
   @EventHandler
   public void onPacketReceive(PacketEvent.Receive e) {
      if (!this.bypass.getValue() || !(e.getPacket() instanceof class_2708)) {
         return;
      }

      if (System.currentTimeMillis() - this.lastRealAttackMs < 1000L) {
         this.addSignal();
      }
   }

   /** Bypass acikken seviyeye gore kisitlanmis vurus menzili. */
   private float getReach() {
      float r = this.attackRange.getValue();
      return this.bypass.getValue() ? Math.min(r, this.level.reach) : r;
   }

   /** Sunucunun gordugu bir onceki rotasyondan itibaren tek tick'te donulebilecek yaw sinirini uygular. */
   private float capYaw(float desired) {
      if (!this.bypass.getValue() || mc.field_1724 == null) {
         return desired;
      }

      float base = this.prevCommitted ? this.baseYaw : mc.field_1724.method_36454();
      float cap = mc.field_1724.method_6128() ? this.level.yawCapElytra : this.level.yawCap;
      float d = class_3532.method_15393(desired - base);
      return Math.abs(d) <= cap ? desired : base + Math.signum(d) * cap;
   }

   private float capPitch(float desired) {
      if (!this.bypass.getValue() || mc.field_1724 == null) {
         return desired;
      }

      float base = this.prevCommitted ? this.basePitch : mc.field_1724.method_36455();
      float cap = mc.field_1724.method_6128() ? this.level.pitchCapElytra : this.level.pitchCap;
      float d = desired - base;
      return Math.abs(d) <= cap ? desired : class_3532.method_15363(base + Math.signum(d) * cap, -90.0F, 90.0F);
   }

   /** Isinin duvara carpip carpmadigini kontrol eder: hedefe giris mesafesinden once blok varsa true. */
   private boolean wallBlocked(float yaw, float pitch, float reach) {
      double entry = this.rayEntry(yaw, pitch, target, reach);
      if (entry < 0.0) {
         return false;
      }

      class_239 hit = Managers.PLAYER.rayTrace((double)reach, yaw, pitch);
      if (hit == null) {
         return false;
      }

      double blockDist = Math.sqrt(mc.field_1724.method_33571().method_1025(hit.method_17784()));
      return blockDist + 0.02 < entry;
   }

   public AttackAura() {
      super("Attack Aura", Module.Category.COMBAT);
   }

   /** Other modules call this to stop attacks for one second (AutoBuff, Phase). */
   public void pause() {
      this.pauseTimer.reset();
   }

   private boolean pauseOver() {
      return this.pauseTimer.passedMs(1000L);
   }

   private float getRange() {
      return this.attackRange.getValue();
   }

   public float getAttackCooldown() {
      // sunucu saldiriyi 0.5 tick ileriye bakarak hesaplar (PlayerEntity.attack: getAttackCooldownProgress(0.5F))
      return mc.field_1724.method_7261(0.5F);
   }

   /**
    * Elytra "ters bakis": AttackAura + (ElytraFlight LookBack veya ElytraTarget) acik, elytra ile ucuyoruz ve hedef var.
    * Hedefin ilerisine gecince rotasyon aninda hedefe doner, vurus rotasyon paketinden hemen sonra yapilir.
    */
   public boolean elytraLook() {
      return target != null
         && mc.field_1724 != null
         && mc.field_1724.method_6128()
         && (ModuleManager.elytraFlight != null && ModuleManager.elytraFlight.isEnabled() && ModuleManager.elytraFlight.lookBack.getValue()
            || ModuleManager.elytratarget != null && ModuleManager.elytratarget.isEnabled());
   }

   @Override
   public void onEnable() {
      target = null;
      this.lookingAtHitbox = false;
      this.rotationCommitted = false;
      if (mc.field_1724 != null) {
         this.rotationYaw = mc.field_1724.method_36454();
         this.rotationPitch = mc.field_1724.method_36455();
         this.yawAnim.reset(mc.field_1724.method_36454());
         this.pitchAnim.reset(mc.field_1724.method_36455());
      }

      this.lastAttackMs = System.currentTimeMillis() - 2000L;
      this.attackDelayMs = 0L;
      this.cooldownGate = ATTACK_COOLDOWN;
      this.pauseTimer.reset();
      this.quantizer.reset();
      this.yawTremor.reset();
      this.pitchTremor.reset();
      this.loadLevel();
      this.lastTickMs = 0L;
      this.lastRealAttackMs = 0L;
      this.pendingTarget = null;
      this.ignoredStreak = 0;
      this.prevCommitted = false;
      this.lastCommitted = false;
   }

   @Override
   public void onDisable() {
      target = null;
      this.lookingAtHitbox = false;
      this.rotationCommitted = false;
      this.pendingTarget = null;
      this.lastCommitted = false;
   }

   // ------------------------------------------------------------------ tick

   @EventHandler
   public void onUpdate(PlayerUpdateEvent e) {
      this.rotationCommitted = false;
      this.jumpedThisTick = false;
      this.prevCommitted = this.lastCommitted;
      this.lastCommitted = false;
      if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
         target = null;
         return;
      }

      if (!mc.field_1724.method_5805()) {
         target = null;
         if (this.deathDisable.getValue()) {
            this.disable(ClientSettings.isRu() ? "Отключаю из-за смерти!" : "Disabling due to death!");
         }

         return;
      }

      if (this.bypass.getValue()) {
         this.bypassTick();
      }

      this.tickFallDistance = mc.field_1724.field_6017;
      this.tickOnGround = mc.field_1724.method_24828();
      if (mc.field_1755 != null && (this.pauseInInventory.getValue() || !(mc.field_1755 instanceof class_465<?>))) {
         this.lookingAtHitbox = false;
         return;
      }

      this.handleKill();
      this.updateTarget();
      if (target == null) {
         this.lookingAtHitbox = false;
         this.rotationYaw = mc.field_1724.method_36454();
         this.rotationPitch = mc.field_1724.method_36455();
         return;
      }

      if (!this.pauseOver() || mc.field_1724.method_6115() && this.pauseWhileEating.getValue()) {
         return;
      }

      this.commitRotation();
      this.rotationCommitted = true;
      this.lastCommitted = true;
      this.baseYaw = this.rotationYaw;
      this.basePitch = this.rotationPitch;
      if (!this.elytraLook()) {
         // Rotations hareket duzeltmesi (jump / klavye / travel) bu tick'in HAREKETINDEN ONCE bu degeri okur;
         // sadece EventSync'te set etmek cok gecti ve duzeltme hic uygulanmiyordu (gonderilen rotasyon != hareket yonu).
         ModuleManager.rotations.fixRotation = this.rotationYaw;
      }
   }

   @EventHandler
   public void onSync(EventSync e) {
      if (!this.rotationCommitted || target == null || mc.field_1724 == null) {
         return;
      }

      this.rotationCommitted = false;
      float prevYaw = mc.field_1724.method_36454();
      float prevPitch = mc.field_1724.method_36455();
      mc.field_1724.method_36456(this.rotationYaw);
      mc.field_1724.method_36457(this.rotationPitch);
      if (!this.elytraLook()) {
         ModuleManager.rotations.fixRotation = this.rotationYaw;
      }

      Runnable previous = e.getPostAction();
      e.addPostAction(() -> {
         try {
            if (target != null && mc.field_1724 != null) {
               this.tryAttack();
            }
         } finally {
            if (mc.field_1724 != null) {
               mc.field_1724.method_36456(prevYaw);
               mc.field_1724.method_36457(prevPitch);
            }

            if (previous != null) {
               previous.run();
            }
         }
      });
   }

   @Override
   public void onRender3D(class_4587 stack) {
      if (target != null) {
         TargetESP.render(target, stack);
      }
   }

   private void updateTarget() {
      class_1297 previous = target;
      class_1297 candidate = this.findTarget();
      if (target == null) {
         target = candidate;
      } else if (this.skipEntity(target)) {
         target = null;
      }

      if (previous == null && target != null) {
         this.yawAnim.reset(mc.field_1724.method_36454());
         this.pitchAnim.reset(mc.field_1724.method_36455());
         this.quantizer.reset();
      }
   }

   private void handleKill() {
      if (target instanceof class_1309 living && (living.method_6032() <= 0.0F || living.method_29504())) {
         if (this.notifiedKill != target) {
            this.notifiedKill = target;
            Managers.NOTIFICATION
               .publicity("Aura", ClientSettings.isRu() ? "Цель успешно нейтрализована!" : "Target successfully neutralized!", 3, Notification.Type.SUCCESS);
            if (this.killDisable.getValue()) {
               this.disable();
            }
         }
      } else if (this.notifiedKill != null && this.notifiedKill != target) {
         this.notifiedKill = null;
      }
   }

   // -------------------------------------------------------------- rotation

   private class_243 closestPointOnBox(class_243 eye, class_238 box) {
      double cx = class_3532.method_15350(eye.field_1352, box.field_1323, box.field_1320);
      double cy = class_3532.method_15350(eye.field_1351, box.field_1322, box.field_1325);
      double cz = class_3532.method_15350(eye.field_1350, box.field_1321, box.field_1324);
      double dx = cx - eye.field_1352;
      double dy = cy - eye.field_1351;
      double dz = cz - eye.field_1350;
      if (dx * dx + dy * dy + dz * dz < 1.0E-12) {
         return box.method_1005();
      }

      return new class_243(cx, cy, cz);
   }

   private class_243 getAimPoint() {
      class_238 box = target.method_5829();
      if (this.rotationMode.getValue() == AttackAura.Mode.HvH) {
         return this.closestPointOnBox(mc.field_1724.method_33571(), box);
      }

      double t = (double)System.currentTimeMillis() / 900.0;
      double fracX = class_3532.method_15350(0.5 + YawAnim.fbm(this.aimNoiseSeed, t) * 0.2, 0.28, 0.72);
      double fracY = class_3532.method_15350(0.72 + YawAnim.fbm(this.aimNoiseSeed ^ 0xA5A5A5A5L, t * 0.8) * 0.17, 0.55, 0.9);
      double fracZ = class_3532.method_15350(0.5 + YawAnim.fbm(this.aimNoiseSeed ^ 0x5A5A5A5AL, t * 1.1) * 0.2, 0.28, 0.72);
      return new class_243(
         box.field_1323 + (box.field_1320 - box.field_1323) * fracX,
         box.field_1322 + (box.field_1325 - box.field_1322) * fracY,
         box.field_1321 + (box.field_1324 - box.field_1321) * fracZ
      );
   }

   private float[] anglesTo(class_243 point) {
      class_243 eye = mc.field_1724.method_33571();
      double difX = point.field_1352 - eye.field_1352;
      double difY = eye.field_1351 - point.field_1351;
      double difZ = point.field_1350 - eye.field_1350;
      float currentYaw = mc.field_1724.method_36454();
      float yaw = currentYaw + class_3532.method_15393((float)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0) - currentYaw);
      float pitch = class_3532.method_15363((float)Math.toDegrees(Math.atan2(difY, Math.sqrt(difX * difX + difZ * difZ))), -90.0F, 90.0F);
      return new float[]{yaw, pitch};
   }

   private void updateDesiredRotation() {
      float[] raw = this.anglesTo(this.getAimPoint());
      if (this.rotationMode.getValue() == AttackAura.Mode.Smooth) {
         long now = System.currentTimeMillis();
         this.desiredYaw = this.yawAnim.update(now, raw[0]);
         this.desiredPitch = class_3532.method_15363(this.pitchAnim.update(now, raw[1]), -90.0F, 90.0F);
      } else {
         this.desiredYaw = raw[0];
         this.desiredPitch = raw[1];
      }
   }

   private void applyRotation(float yaw, float pitch) {
      this.rotationYaw = yaw;
      this.rotationPitch = pitch;
   }

   private float[] jittered(float[] angles) {
      float[] off = this.aimJitter.next();
      return new float[]{angles[0] + off[0], class_3532.method_15363(angles[1] + off[1], -90.0F, 90.0F)};
   }

   private void commitRotation() {
      if (this.elytraLook()) {
         float[] direct = this.jittered(this.anglesTo(this.closestPointOnBox(mc.field_1724.method_33571(), target.method_5829())));
         this.applyRotation(
            this.quantizer.yaw(mc.field_1724.method_36454(), this.capYaw(direct[0])),
            this.quantizer.pitch(mc.field_1724.method_36455(), this.capPitch(direct[1]))
         );
         this.lookingAtHitbox = this.isLookingAtHitbox(this.rotationYaw, this.rotationPitch, target, this.getReach());
         if (!this.lookingAtHitbox) {
            float[] snap = this.jittered(this.anglesTo(target.method_5829().method_1005()));
            this.applyRotation(
               this.quantizer.yaw(mc.field_1724.method_36454(), this.capYaw(snap[0])),
               this.quantizer.pitch(mc.field_1724.method_36455(), this.capPitch(snap[1]))
            );
            this.lookingAtHitbox = this.isLookingAtHitbox(this.rotationYaw, this.rotationPitch, target, this.getReach());
         }

         return;
      }

      this.updateDesiredRotation();
      double gcd = this.quantizer.getGcd();
      double noiseScale = Math.max(gcd * 0.6, 0.55);
      float noisyYaw = (float)((double)this.desiredYaw + this.yawTremor.next(noiseScale));
      float noisyPitch = (float)((double)this.desiredPitch + this.pitchTremor.next(noiseScale * 0.6));
      this.applyRotation(
         this.quantizer.yaw(mc.field_1724.method_36454(), this.capYaw(noisyYaw)),
         this.quantizer.pitch(mc.field_1724.method_36455(), this.capPitch(noisyPitch))
      );
      this.lookingAtHitbox = this.isLookingAtHitbox(this.rotationYaw, this.rotationPitch, target, this.getReach());

      // elytra: the target moves too fast for the smoothed aim, snap onto its eye position
      if (!this.lookingAtHitbox && mc.field_1724.method_6128() && this.rotationMode.getValue() == AttackAura.Mode.HvH && this.autoCrit()) {
         float[] snap = this.jittered(this.anglesTo(target.method_33571()));
         this.applyRotation(
            this.quantizer.yaw(mc.field_1724.method_36454(), this.capYaw(snap[0])),
            this.quantizer.pitch(mc.field_1724.method_36455(), this.capPitch(snap[1]))
         );
         this.lookingAtHitbox = this.isLookingAtHitbox(this.rotationYaw, this.rotationPitch, target, this.getReach());
      }
   }

   /** Ray (from the eye along yaw / pitch, length distance) against the target hitbox, slab method. */
   private double rayEntry(float yaw, float pitch, class_1297 entity, float distance) {
      float yawRad = yaw * (float)(Math.PI / 180.0);
      float pitchRad = pitch * (float)(Math.PI / 180.0);
      float cosPitch = class_3532.method_15362(pitchRad);
      double[] dir = {-class_3532.method_15374(yawRad) * cosPitch, -class_3532.method_15374(pitchRad), class_3532.method_15362(yawRad) * cosPitch};
      class_243 eye = mc.field_1724.method_33571();
      double[] origin = {eye.field_1352, eye.field_1351, eye.field_1350};
      class_238 box = entity.method_5829();
      double[] min = {box.field_1323, box.field_1322, box.field_1321};
      double[] max = {box.field_1320, box.field_1325, box.field_1324};
      double tMin = 0.0;
      double tMax = distance;
      for (int axis = 0; axis < 3; axis++) {
         if (dir[axis] > -1.0E-9 && dir[axis] < 1.0E-9) {
            if (origin[axis] < min[axis] || origin[axis] > max[axis]) {
               return -1.0;
            }
         } else {
            double inv = 1.0 / dir[axis];
            double t1 = (min[axis] - origin[axis]) * inv;
            double t2 = (max[axis] - origin[axis]) * inv;
            if (t1 > t2) {
               double tmp = t1;
               t1 = t2;
               t2 = tmp;
            }

            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
               return -1.0;
            }
         }
      }

      return tMin;
   }

   private boolean isLookingAtHitbox(float yaw, float pitch, class_1297 entity, float distance) {
      return this.rayEntry(yaw, pitch, entity, distance) >= 0.0;
   }

   // ---------------------------------------------------------------- attack

   private void tryAttack() {
      if (target == null || !this.autoCrit() || !this.lookingAtHitbox) {
         return;
      }

      if (this.bypass.getValue() && this.level.wallCheck && this.wallBlocked(this.rotationYaw, this.rotationPitch, this.getReach())) {
         return;
      }

      if (this.shieldBreaker()) {
         return;
      }

      boolean[] state = this.preAttack();
      this.attack();
      this.postAttack(state[0], state[1]);
   }

   private void attack() {
      Criticals.cancelCrit = true;
      ModuleManager.criticals.doCrit();
      int prevSlot = this.switchMethod();
      mc.field_1761.method_2918(mc.field_1724, target);
      Criticals.cancelCrit = false;
      mc.field_1724.method_6104(class_1268.field_5808);
      this.lastRealAttackMs = System.currentTimeMillis();
      if (target instanceof class_1309 living && !living.method_6115()) {
         this.pendingTarget = target;
         this.pendingHealth = living.method_6032() + living.method_6067();
         this.pendingTicks = 0;
      }

      this.rollNextAttack();
      if (prevSlot != -1) {
         InventoryUtility.switchTo(prevSlot);
      }
   }

   private int switchMethod() {
      int prevSlot = -1;
      SearchInvResult swordResult = InventoryUtility.getSwordHotBar();
      if (swordResult.found() && this.switchMode.getValue() != AttackAura.Switch.None) {
         if (this.switchMode.getValue() == AttackAura.Switch.Silent) {
            prevSlot = mc.field_1724.method_31548().field_7545;
         }

         swordResult.switchTo();
      }

      return prevSlot;
   }

   /** Randomized gap between hits (450-550 ms, 7 % chance of an extra 150-400 ms) and a jittered charge gate. */
   private void rollNextAttack() {
      ThreadLocalRandom rnd = ThreadLocalRandom.current();
      this.lastAttackMs = System.currentTimeMillis();
      this.attackDelayMs = (long)rnd.nextDouble(450.0, 550.0);
      if (rnd.nextFloat() < 0.07F) {
         this.attackDelayMs += (long)rnd.nextDouble(150.0, 400.0);
      }

      if (this.bypass.getValue()) {
         this.attackDelayMs += this.level.extraDelayMs;
      }

      this.cooldownGate = 0.93F + rnd.nextFloat() * 0.05F;
   }

   public boolean isAboveWater() {
      return mc.field_1724.method_5869()
         || mc.field_1687.method_8320(class_2338.method_49638(mc.field_1724.method_19538().method_1031(0.0, -0.4, 0.0))).method_26204()
            == class_2246.field_10382;
   }

   private boolean willCrit() {
      return this.crit.getValue() != AttackAura.CritMode.Off
         && !this.tickOnGround
         && this.tickFallDistance > 0.0F
         && !mc.field_1724.method_6101()
         && !mc.field_1724.method_5799()
         && !mc.field_1724.method_5771()
         && !mc.field_1724.method_5869()
         && !this.isAboveWater()
         && !mc.field_1724.method_6059(class_1294.field_5919)
         && !mc.field_1724.method_6059(class_1294.field_5906)
         && !mc.field_1724.method_5765()
         && !mc.field_1724.method_31549().field_7479
         && !mc.field_1724.method_6128()
         && !Managers.PLAYER.isInWeb();
   }

   /** The critical / cooldown gate: true when a hit may be made this tick. */
   private boolean autoCrit() {
      boolean skipCrit = this.crit.getValue() == AttackAura.CritMode.Off
         || mc.field_1724.method_31549().field_7479
         || mc.field_1724.method_6128()
         || mc.field_1724.method_6059(class_1294.field_5919)
         || mc.field_1724.method_6059(class_1294.field_5906)
         || Managers.PLAYER.isInWeb();
      if (!this.elytraLook() && System.currentTimeMillis() - this.lastAttackMs < this.attackDelayMs) {
         return false;
      }

      if (this.pauseInInventory.getValue() && mc.field_1755 instanceof class_465<?>) {
         return false;
      }

      if (this.getAttackCooldown() < this.cooldownGate) {
         return false;
      }

      if (skipCrit) {
         return true;
      }

      // kritik mumkun olmayan ortamlar: normal vurus
      if (mc.field_1724.method_5771() || mc.field_1724.method_5869() || this.isAboveWater()) {
         return true;
      }

      // havada ve dusuyoruz: gercek kritik
      if (this.willCrit()) {
         return true;
      }

      if (this.waitingForCrit()) {
         return false;
      }

      return this.crit.getValue() != AttackAura.CritMode.Only;
   }

   /** Kritik penceresini beklerken true: havada yukseliyoruz ya da kritik icin simdi zipladik. */
   private boolean waitingForCrit() {
      if (mc.field_1724.method_6128()) {
         return false;
      }

      if (!this.tickOnGround) {
         // yukseliyorsak dusmeyi bekle; dusuyoruz ama kritik baska nedenle engelliyse bekleme
         return mc.field_1724.method_18798().field_1351 > 0.0;
      }

      if (mc.field_1690.field_1903.method_1434()) {
         return true;
      }

      if (this.jumpedThisTick) {
         return true;
      }

      if (!this.critJump.getValue() || !this.canCritJump()) {
         return false;
      }

      mc.field_1724.method_6043();
      this.jumpedThisTick = true;
      return true;
   }

   /** Yerdeyiz, hedef yakin ve zipalmaya engel yok: kritik icin ziplanabilir. */
   private boolean canCritJump() {
      if (target == null || !mc.field_1724.method_24828() || mc.field_1724.method_5765() || mc.field_1724.method_6101()) {
         return false;
      }

      if (mc.field_1724.method_5799() || mc.field_1724.method_5771() || mc.field_1724.method_5869()) {
         return false;
      }

      class_2338 bp = mc.field_1724.method_24515();
      if (!mc.field_1687.method_8320(new class_2338(bp.method_10263(), bp.method_10264() + 2, bp.method_10260())).method_26215()) {
         return false;
      }

      class_243 eye = mc.field_1724.method_33571();
      class_243 point = this.closestPointOnBox(eye, target.method_5829());
      return Math.sqrt(eye.method_1025(point)) <= (double)(Math.min(this.getReach(), 6.0F) + 1.0F);
   }

   // ---------------------------------------------------------- sprint / shield

   /** Returns {wasBlocking, droppedSprint}. */
   private boolean[] preAttack() {
      boolean blocking = mc.field_1724.method_6115()
         && mc.field_1724.method_6030().method_7909().method_7853(mc.field_1724.method_6030()) == class_1839.field_8949;
      if (blocking && this.unpressShield.getValue()) {
         this.sendPacket(new class_2846(class_2847.field_12974, class_2338.field_10980, class_2350.field_11033));
      }

      boolean drop = Core.serverSprint && this.dropSprint.getValue() && this.willCrit();
      if (drop) {
         mc.field_1724.method_5728(false);
         mc.field_1690.field_1867.method_23481(false);
         this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12985));
      }

      return new boolean[]{blocking, drop};
   }

   private void postAttack(boolean wasBlocking, boolean droppedSprint) {
      if (droppedSprint) {
         mc.field_1724.method_5728(true);
         mc.field_1690.field_1867.method_23481(true);
         this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12981));
      }

      if (wasBlocking && this.unpressShield.getValue()) {
         this.sendSequencedPacket(id -> new net.minecraft.class_2886(class_1268.field_5810, id, this.rotationYaw, this.rotationPitch));
      }
   }

   /** Inventory index (0-8 hotbar, 9-35 main inventory) of the axe with the most durability, or -1. */
   private int bestAxeSlot() {
      int best = -1;
      int bestDurability = 0;
      for (int i = 9; i < 45; i++) {
         int index = i >= 36 ? i - 36 : i;
         class_1799 stack = mc.field_1724.method_31548().method_5438(index);
         if (stack != null && stack.method_7909() instanceof class_1743 && stack.method_7936() > bestDurability) {
            best = index;
            bestDurability = stack.method_7936();
         }
      }

      return best;
   }

   private boolean shieldBreaker() {
      if (!this.shieldBreaker.getValue() || !(target instanceof class_1657 victim)) {
         return false;
      }

      int axeSlot = this.bestAxeSlot();
      if (axeSlot == -1) {
         return false;
      }

      if (!victim.method_6115()) {
         return false;
      }

      if (victim.method_6079().method_7909() != class_1802.field_8255 && victim.method_6047().method_7909() != class_1802.field_8255) {
         return false;
      }

      int selected = mc.field_1724.method_31548().field_7545;
      int syncId = mc.field_1724.field_7512.field_7763;
      if (axeSlot >= 9) {
         mc.field_1761.method_2906(syncId, axeSlot, selected, class_1713.field_7791, mc.field_1724);
         this.sendPacket(new class_2815(syncId));
         mc.field_1761.method_2918(mc.field_1724, target);
         mc.field_1724.method_6104(class_1268.field_5808);
         mc.field_1761.method_2906(syncId, axeSlot, selected, class_1713.field_7791, mc.field_1724);
         this.sendPacket(new class_2815(syncId));
      } else {
         this.sendPacket(new class_2868(axeSlot));
         mc.field_1761.method_2918(mc.field_1724, target);
         mc.field_1724.method_6104(class_1268.field_5808);
         this.sendPacket(new class_2868(selected));
      }

      this.rollNextAttack();
      return true;
   }

   // --------------------------------------------------------------- targeting

   private float getSquaredRotateDistance() {
      float dst = mc.field_1724.method_6128() ? this.elytraAimRange.getValue() : this.getRange() + this.aimRange.getValue();
      return dst * dst;
   }

   private boolean isInRange(class_1297 entity, float squaredRange) {
      class_243 center = entity.method_5829().method_1005();
      class_243 eye = mc.field_1724.method_33571();
      double dx = center.field_1352 - eye.field_1352;
      double dy = center.field_1351 - eye.field_1351;
      double dz = center.field_1350 - eye.field_1350;
      return dx * dx + dy * dy + dz * dz <= (double)squaredRange;
   }

   private class_1297 findTarget() {
      float squaredRange = this.getSquaredRotateDistance();
      class_1309 best = null;
      double bestScore = Double.MAX_VALUE;
      for (class_1297 entity : mc.field_1687.method_18112()) {
         if (!(entity instanceof class_1309 living) || this.skipEntity(living, squaredRange)) {
            continue;
         }

         double dx = living.method_23317() - mc.field_1724.method_23317();
         double dy = living.method_23318() - mc.field_1724.method_23318();
         double dz = living.method_23321() - mc.field_1724.method_23321();
         double score = dx * dx + dy * dy + dz * dz;
         if (best == null || score < bestScore) {
            best = living;
            bestScore = score;
         }
      }

      return best;
   }

   private boolean skipEntity(class_1297 entity) {
      return this.skipEntity(entity, this.getSquaredRotateDistance());
   }

   private boolean skipEntity(class_1297 entity, float squaredRange) {
      if (!(entity instanceof class_1309 living) || entity == mc.field_1724) {
         return true;
      }

      if (living.method_29504() || !entity.method_5805() || entity instanceof class_1531 || entity instanceof class_1451) {
         return true;
      }

      if (this.skipNotSelected(entity)) {
         return true;
      }

      if (entity.method_5767() && this.ignoreInvisible.getValue()) {
         return true;
      }

      if (entity instanceof class_1657 player) {
         if (ModuleManager.antiBot.isEnabled() && AntiBot.bots.contains(entity)) {
            return true;
         }

         if (Managers.FRIEND.isFriend(player)) {
            return true;
         }

         if (player.method_7337() && this.ignoreCreative.getValue()) {
            return true;
         }
      }

      return !this.isInRange(entity, squaredRange);
   }

   private boolean skipNotSelected(class_1297 entity) {
      if (entity instanceof class_1657) {
         return !this.Players.getValue();
      }

      if (entity instanceof class_1646) {
         return !this.Villagers.getValue();
      }

      if (entity instanceof class_1429) {
         return !this.Animals.getValue();
      }

      if (entity instanceof class_1308) {
         return !this.Mobs.getValue();
      }

      return false;
   }

   // ------------------------------------------------------- shared types
   // Position, Resolver and RayTrace are kept because the entity mixins and PlayerManager refer to them.

   public static enum Mode {
      HvH,
      Smooth;
   }

   public static enum CritMode {
      Off,
      Smart,
      Only;
   }

   public static enum Switch {
      Normal,
      None,
      Silent;
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

   public static class Position {
      private final double x;
      private final double y;
      private final double z;
      private int ticks;

      public Position(double x, double y, double z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public boolean shouldRemove() {
         return this.ticks++ > 4;
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

   /** Human-like angle animation (ease-out-back segments, saccades, bow, pauses, micro correction, idle jitter). */
   private static final class YawAnim {
      private float curveEnd;
      private long microDuration;
      private float retargetThreshold;
      private final float speedMul;
      private final long noiseSeed = ThreadLocalRandom.current().nextLong();
      private float from = Float.NaN;
      private float to = Float.NaN;
      private float c1;
      private long reactionUntil;
      private long segmentStart;
      private long duration;
      private boolean microCorrecting;
      private long microStart;
      private float microFrom;
      private float bowAmplitude;
      private float bowPhase;
      private boolean saccade;
      private boolean hasPause;
      private long pauseAtElapsedMs;
      private long pauseDurationMs;
      private boolean freshAcquisition = true;

      YawAnim(float speedMul) {
         this.speedMul = speedMul;
      }

      private static float random(float min, float max) {
         return (float)ThreadLocalRandom.current().nextDouble(min, max);
      }

      void reset(float current) {
         this.from = current;
         this.to = current;
         this.freshAcquisition = true;
         this.microCorrecting = false;
         this.saccade = false;
         this.hasPause = false;
         this.bowAmplitude = 0.0F;
         this.bowPhase = 0.5F;
         this.c1 = 0.5F;
         this.reactionUntil = 0L;
         this.segmentStart = 0L;
         this.duration = 60L;
         this.rollSegmentShape();
      }

      private void rollSegmentShape() {
         this.curveEnd = random(0.87F, 0.96F);
         this.microDuration = (long)random(55.0F, 95.0F);
         this.retargetThreshold = random(1.0F, 2.2F);
      }

      float update(long now, float destination) {
         if (Float.isNaN(this.to)) {
            this.reset(destination);
         }

         if (Math.abs(class_3532.method_15393(destination - this.to)) > this.retargetThreshold) {
            this.retarget(this.currentRaw(now), destination);
         }

         return this.currentRaw(now);
      }

      private void retarget(float currentAnimated, float destination) {
         this.from = currentAnimated;
         this.to = currentAnimated + class_3532.method_15393(destination - currentAnimated);
         this.c1 = random(0.0F, 1.0F) < 0.4F ? random(0.1F, 0.35F) : random(0.4F, 1.0F);
         long now = System.currentTimeMillis();
         if (this.freshAcquisition) {
            this.reactionUntil = now + (long)random(50.0F, 150.0F);
            this.freshAcquisition = false;
         } else {
            this.reactionUntil = now;
         }

         this.segmentStart = this.reactionUntil;
         float angularDist = Math.abs(this.to - this.from);
         float base = 45.0F + 26.0F * (float)Math.pow(angularDist, 0.55) / this.speedMul;
         float variance = 1.0F + random(-0.18F, 0.18F);
         long baseDuration = (long)(class_3532.method_15363(base, 60.0F, 560.0F) * variance);
         this.saccade = angularDist > 15.0F && random(0.0F, 1.0F) < 0.3F;
         if (this.saccade) {
            baseDuration = (long)((float)baseDuration * random(0.55F, 0.75F));
         }

         this.duration = Math.max(60L, baseDuration);
         float bowScale = class_3532.method_15363(angularDist / 6.0F, 0.0F, 1.0F);
         this.bowAmplitude = random(-1.4F, 1.4F) * bowScale;
         this.bowPhase = random(0.35F, 0.65F);
         this.hasPause = !this.saccade && random(0.0F, 1.0F) < 0.15F;
         if (this.hasPause) {
            this.pauseAtElapsedMs = (long)((float)this.duration * random(0.25F, 0.6F));
            this.pauseDurationMs = (long)random(20.0F, 40.0F);
         }

         this.microCorrecting = false;
         this.rollSegmentShape();
      }

      private long effectiveElapsed(long elapsed) {
         if (!this.hasPause || elapsed < this.pauseAtElapsedMs) {
            return elapsed;
         }

         if (elapsed < this.pauseAtElapsedMs + this.pauseDurationMs) {
            return this.pauseAtElapsedMs;
         }

         return elapsed - this.pauseDurationMs;
      }

      private float currentRaw(long now) {
         if (now < this.reactionUntil) {
            return this.from;
         }

         long elapsed = this.effectiveElapsed(now - this.segmentStart);
         if (elapsed >= this.duration) {
            if (!this.microCorrecting) {
               this.microCorrecting = true;
               this.microStart = now;
               this.microFrom = this.from + (this.to - this.from) * easeOutBack(this.curveEnd, this.c1);
            }

            long microElapsed = now - this.microStart;
            if (microElapsed >= this.microDuration) {
               return this.to + this.idleJitter(now);
            }

            float mp = (float)microElapsed / (float)this.microDuration;
            return this.microFrom + (this.to - this.microFrom) * easeOutQuad(mp);
         }

         float rawT = (float)elapsed / (float)this.duration;
         float speedNoise = 1.0F + 0.13F * (float)fbm(this.noiseSeed, (double)rawT * 6.0 + (double)now / 500.0);
         float warpedT = class_3532.method_15363(rawT * speedNoise, 0.0F, 1.0F);
         float t = warpedT * this.curveEnd;
         float eased = easeOutBack(t, this.c1);
         float bow = this.bowAmplitude * bowBump(rawT, this.bowPhase);
         return this.from + (this.to - this.from) * class_3532.method_15363(eased, -0.4F, 1.4F) + bow;
      }

      private static float bowBump(float t, float phase) {
         float remapped = t < phase ? 0.5F * (t / phase) : 0.5F + 0.5F * ((t - phase) / (1.0F - phase));
         return 4.0F * remapped * (1.0F - remapped);
      }

      private float idleJitter(long now) {
         double n = fbm(this.noiseSeed, (double)now / 260.0);
         float amplitude = 0.1F + (float)((fbm(this.noiseSeed ^ 0x5DEECE66DL, (double)now / 4000.0) + 1.0) * 0.5) * 0.4F;
         return (float)(n * (double)amplitude);
      }

      private static float easeOutBack(float t, float c1) {
         float c3 = c1 + 1.0F;
         float p = t - 1.0F;
         return 1.0F + c3 * p * p * p + c1 * p * p;
      }

      private static float easeOutQuad(float t) {
         return 1.0F - (1.0F - t) * (1.0F - t);
      }

      static double fbm(long seed, double t) {
         return noise1(seed, t) * 0.6 + noise1(seed ^ 0x9E3779B97F4A7C15L, t * 2.3) * 0.3 + noise1(seed ^ 0x632BE59BD9B4E019L, t * 5.1) * 0.1;
      }

      private static double noise1(long seed, double t) {
         long i = (long)Math.floor(t);
         double f = t - (double)i;
         double u = f * f * (3.0 - 2.0 * f);
         double a = hashUnit(seed, i);
         double b = hashUnit(seed, i + 1L);
         return a + (b - a) * u;
      }

      private static double hashUnit(long seed, long i) {
         long h = seed + i * -4417276706812531889L;
         h ^= h >>> 33;
         h *= -49064778989728563L;
         h ^= h >>> 33;
         h *= -4265267296055464877L;
         h ^= h >>> 33;
         return (double)(h >>> 11) * 2.220446049250313E-16 - 1.0;
      }
   }

   /** Smoothed gaussian hand tremor added on top of the aim. */
   /** Yavasca donen sabit yaricapli sapma: aninlik hata nadiren 0'a yaklasir, ama hitbox toleransi icinde kalir. */
   private static final class OrbitJitter {
      private double phase = ThreadLocalRandom.current().nextDouble(0.0, Math.PI * 2.0);
      private double amp = 1.4;
      private long nextReroll;

      float[] next() {
         long now = System.currentTimeMillis();
         if (now >= this.nextReroll) {
            this.amp = ThreadLocalRandom.current().nextDouble(1.0, 2.2);
            this.nextReroll = now + 400L + ThreadLocalRandom.current().nextLong(300L);
         }

         this.phase += ThreadLocalRandom.current().nextDouble(0.35, 0.65);
         double jitterAmp = this.amp + ThreadLocalRandom.current().nextDouble(-0.15, 0.15);
         return new float[]{(float)(Math.cos(this.phase) * jitterAmp), (float)(Math.sin(this.phase) * jitterAmp * 0.6)};
      }
   }

   private static final class PitchTremor {
      private double value;
      private double velocity;

      double next(double scale) {
         this.velocity = this.velocity * 0.75 + ThreadLocalRandom.current().nextGaussian() * scale * 0.3;
         this.value = this.value * 0.92 + this.velocity;
         return this.value;
      }

      void reset() {
         this.value = 0.0;
         this.velocity = 0.0;
      }
   }
}
