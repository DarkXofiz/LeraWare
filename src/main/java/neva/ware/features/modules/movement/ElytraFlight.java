package neva.ware.features.modules.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2708;
import net.minecraft.class_3532;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.PacketEvent;
import neva.ware.events.impl.PlayerUpdateEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.AttackAura;
import neva.ware.gui.notification.Notification;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;

/**
 * ElytraFlight
 *
 * Mode Boost  : elytra ile ucarken bakilan yone belirli araliklarla hiz verir. Ayarlari vardir.
 *               Aura hedefi varsa hedefe dogru yaklasir, hedefi gectikten sonra ivmeyi korur.
 *               LookBack acikken Aura hedefin ilerisine gecince ters donup vurur.
 * Mode Bypass : istemci hicbir hiz/pozisyon uydurmaz, hareketi vanilla elytra fizigi uretir; modul sadece
 *               yaw/pitch'i secer. Ayar YOK: sunucunun geri cekme (rubberband) tepkisinden ogrenip kendini
 *               STRICT / NORMAL / RELAXED profilleri arasinda otomatik ayarlar.
 *               Hitbox acikken (varsayilan kapali) ve Aura hedefi varsa: rakibin onunda / arkasinda / kafasinda bir
 *               nokta otomatik secilir, ucus o noktadan gecer. Yon sessiz uygulanir, kamera oynamaz.
 */
public class ElytraFlight extends Module {
   public enum Mode {
      Boost,
      Bypass
   }

   /** Bypass hitbox sistemi: rakibe gore otomatik secilen gecis noktasi. */
   private enum Anchor {
      FRONT,
      BEHIND,
      HEAD
   }

   private enum Phase {
      DIVE,
      CLIMB
   }

   /** Otomatik secilen bypass profilleri. Siralama siki -> gevsek. */
   private enum Profile {
      SAFE(28.0F, 18.0F, 24.0F, 26.0F, 12, 3.5F, 16),
      STRICT(36.0F, 22.0F, 30.0F, 32.0F, 16, 5.0F, 14),
      NORMAL(52.0F, 28.0F, 36.0F, 38.0F, 22, 9.0F, 12),
      RELAXED(70.0F, 35.0F, 40.0F, 42.0F, 28, 14.0F, 12);

      final float maxSpeed;
      final float minSpeed;
      final float dive;
      final float climb;
      final int band;
      final float pitchStep;
      final int groundGuard;

      Profile(float maxSpeed, float minSpeed, float dive, float climb, int band, float pitchStep, int groundGuard) {
         this.maxSpeed = maxSpeed;
         this.minSpeed = minSpeed;
         this.dive = dive;
         this.climb = climb;
         this.band = band;
         this.pitchStep = pitchStep;
         this.groundGuard = groundGuard;
      }
   }

   public final Setting<Mode> mode = new Setting<>("Mode", Mode.Boost);

   // ---- Boost mode ----
   public final Setting<Integer> delay = new Setting<>("Delay", 100, 0, 300, v -> this.mode.is(Mode.Boost));
   public final Setting<Float> boost = new Setting<>("Boost", 0.05F, 0.01F, 0.3F, v -> this.mode.is(Mode.Boost));
   public final Setting<Float> maxSpeed = new Setting<>("MaxSpeed", 1.6F, 0.5F, 3.0F, v -> this.mode.is(Mode.Boost));
   public final Setting<Boolean> onlySpace = new Setting<>("OnlyWhileSpace", false, v -> this.mode.is(Mode.Boost));

   // ---- Sadece ac / kapat anahtarlari (ayar degeri yok) ----
   public final Setting<Boolean> lookBack = new Setting<>("LookBack", true);
   public final Setting<Boolean> hitbox = new Setting<>("Hitbox", true, v -> this.mode.is(Mode.Bypass));
   public boolean steering;
   public float flightYaw;
   public float flightPitch;
   private float lastBypassPitch = Float.NaN;
   private long manualUntilMs;

   private static final Map<String, Profile> SERVER_PROFILE = new HashMap<>();
   private static final Map<String, Profile> SERVER_CEILING = new HashMap<>();

   private final Timer boostTimer = new Timer();
   private double lastX;
   private double lastY;
   private double lastZ;
   private boolean hasLast;

   private Anchor anchor = Anchor.FRONT;
   private int anchorLock;
   private int passHold;
   private class_1297 trackedTarget;
   private double lastTx;
   private double lastTz;
   private double tVelX;
   private double tVelZ;

   private Phase phase = Phase.DIVE;
   private double holdY;
   private boolean bypassActive;

   private Profile profile = Profile.NORMAL;
   private Profile ceiling = Profile.RELAXED;
   private String serverKey = "unknown";
   private volatile int setbackCount;
   private volatile long windowStartMs;
   private volatile long lastFlyingMs;
   private long cleanSinceMs;
   private long relaxedAtMs;

   // ---- [BYPASS+] sakin sure + desen kirici titresim ----
   private long calmUntilMs;
   private float jMax = 1.0F;
   private float jMin = 1.0F;
   private float jStep = 1.0F;
   private int jitterTicks;

   public ElytraFlight() {
      super("ElytraFlight", Module.Category.MOVEMENT);
   }

   @Override
   public void onEnable() {
      this.hasLast = false;
      this.bypassActive = false;
      this.boostTimer.reset();
      this.loadServerProfile();
   }

   @Override
   public void onDisable() {
      this.bypassActive = false;
      this.steering = false;
      this.lastBypassPitch = Float.NaN;
   }

   private static String currentServerKey() {
      try {
         if (mc.method_1562() != null && mc.method_1562().method_45734() != null) {
            return mc.method_1562().method_45734().field_3761.toLowerCase();
         }
      } catch (Throwable ignored) {
      }
      return "unknown";
   }

   private void loadServerProfile() {
      this.serverKey = currentServerKey();
      this.profile = SERVER_PROFILE.getOrDefault(this.serverKey, Profile.NORMAL);
      this.ceiling = SERVER_CEILING.getOrDefault(this.serverKey, Profile.RELAXED);
      long now = System.currentTimeMillis();
      this.setbackCount = 0;
      this.windowStartMs = now;
      this.cleanSinceMs = now;
      this.relaxedAtMs = 0L;
      this.calmUntilMs = 0L;
      this.jitterTicks = 0;
   }

   /** Sunucu bizi geri cektiyse (rubberband) ucus sirasinda sayilir. */
   @EventHandler
   public void onPacketReceive(PacketEvent.Receive e) {
      if (!this.mode.is(Mode.Bypass) || !(e.getPacket() instanceof class_2708)) {
         return;
      }
      long now = System.currentTimeMillis();
      if (now - this.lastFlyingMs < 600L) {
         this.setbackCount++;
         this.cleanSinceMs = now;
      }
   }

   /** AttackAura anticheat tepkisi gorurse ucus profilini de bir kademe sikilastirir. */
   public void externalTighten() {
      long now = System.currentTimeMillis();
      if (this.profile.ordinal() > 0) {
         this.profile = Profile.values()[this.profile.ordinal() - 1];
         SERVER_PROFILE.put(this.serverKey, this.profile);
         this.notifyProfile("Aura tepkisi algilandi, profil: " + this.profile.name());
      }
      this.calmUntilMs = now + 2500L;
      this.cleanSinceMs = now;
   }

   /** Her 30-70 tickte hiz esikleri ve donus adimi %6-%15 oraninda yeniden secilir: sabit periyodik desen olusmaz. */
   private void rollJitter() {
      if (--this.jitterTicks > 0) {
         return;
      }
      ThreadLocalRandom r = ThreadLocalRandom.current();
      this.jitterTicks = 30 + r.nextInt(40);
      this.jMax = 0.94F + r.nextFloat() * 0.12F;
      this.jMin = 0.94F + r.nextFloat() * 0.12F;
      this.jStep = 0.85F + r.nextFloat() * 0.30F;
   }

   private void notifyProfile(String text) {
      try {
         Managers.NOTIFICATION.publicity("ElytraFlight", text, 3, Notification.Type.INFO);
      } catch (Throwable ignored) {
      }
   }

   /** Geri cekme sayisina gore profili siklastir, uzun temiz ucusta gevset. */
   private void adaptProfile() {
      long now = System.currentTimeMillis();
      if (now - this.windowStartMs > 10000L) {
         this.windowStartMs = now;
         this.setbackCount = 0;
      }

      if (this.setbackCount >= 2) {
         if (now - this.relaxedAtMs < 25000L) {
            // gevsettigimiz seviye geri cekme yedi: bu sunucuda tavan olarak bir alt seviyeyi kabul et
            this.ceiling = Profile.values()[Math.max(0, this.profile.ordinal() - 1)];
            SERVER_CEILING.put(this.serverKey, this.ceiling);
         }
         int drop = this.setbackCount >= 4 ? 2 : 1;
         int idx = Math.max(0, this.profile.ordinal() - drop);
         if (idx != this.profile.ordinal()) {
            this.profile = Profile.values()[idx];
            SERVER_PROFILE.put(this.serverKey, this.profile);
            this.notifyProfile("Anticheat tepkisi algilandi, profil: " + this.profile.name());
         }
         // geri cekmeden hemen sonra yon/pitch mudahalesini birakip vanilla fizige birkac saniye izin ver
         this.calmUntilMs = now + 2500L;
         this.setbackCount = 0;
         this.windowStartMs = now;
         this.cleanSinceMs = now;
      } else if (this.profile.ordinal() < this.ceiling.ordinal() && now - this.cleanSinceMs > 60000L) {
         this.profile = Profile.values()[this.profile.ordinal() + 1];
         SERVER_PROFILE.put(this.serverKey, this.profile);
         this.relaxedAtMs = now;
         this.cleanSinceMs = now;
         this.notifyProfile("Temiz ucus, profil: " + this.profile.name());
      }
   }

   @EventHandler
   public void onUpdate(PlayerUpdateEvent e) {
      if (mc.field_1724 == null || mc.field_1687 == null) {
         return;
      }

      double x = mc.field_1724.method_23317();
      double y = mc.field_1724.method_23318();
      double z = mc.field_1724.method_23321();
      double speed = 0.0;
      if (this.hasLast) {
         double dx = x - this.lastX;
         double dy = y - this.lastY;
         double dz = z - this.lastZ;
         speed = Math.sqrt(dx * dx + dy * dy + dz * dz);
      }
      this.lastX = x;
      this.lastY = y;
      this.lastZ = z;
      this.hasLast = true;

      if (!mc.field_1724.method_6128()) {
         this.bypassActive = false;
         this.steering = false;
         return;
      }
      this.lastFlyingMs = System.currentTimeMillis();

      if (this.mode.is(Mode.Bypass)) {
         this.adaptProfile();
         if (System.currentTimeMillis() < this.calmUntilMs) {
            this.bypassActive = false;
            this.steering = false;
            this.trackedTarget = null;
            return;
         }
         this.rollJitter();
         if (this.hitbox.getValue() && ModuleManager.aura.isEnabled() && AttackAura.target != null && AttackAura.target.method_5805()) {
            this.bypassActive = false;
            this.runEngage(AttackAura.target);
            return;
         }
         this.trackedTarget = null;
         this.steering = false;
         if (ModuleManager.aura.isEnabled() && AttackAura.target != null) {
            // Hitbox kapaliyken hedefteyken rotasyonu Aura yonetir. Bypass pitch'e karismaz.
            this.bypassActive = false;
            return;
         }
         this.runBypass();
         return;
      }

      this.bypassActive = false;
      this.trackedTarget = null;
      this.steering = false;
      this.runBoost(speed);
   }

   private void runBoost(double speed) {
      if (this.onlySpace.getValue() && !mc.field_1690.field_1903.method_1434()) {
         return;
      }
      if (speed >= (double)this.maxSpeed.getValue()) {
         return;
      }
      if (!this.boostTimer.passedMs((long)this.delay.getValue().intValue())) {
         return;
      }

      float yaw = (float)Math.toRadians((double)mc.field_1724.method_36454());
      float pitch = (float)Math.toRadians((double)mc.field_1724.method_36455());
      double cosPitch = Math.cos((double)pitch);
      double dirX = -Math.sin((double)yaw) * cosPitch;
      double dirY = -Math.sin((double)pitch);
      double dirZ = Math.cos((double)yaw) * cosPitch;

      class_1297 t = ModuleManager.aura.isEnabled() ? AttackAura.target : null;
      if (t != null) {
         // Aura oyuncunun yaw/pitch'ini hedefe cevirir; hizi o yone vermek yerine kendi hesabimizi yapariz:
         //  - hedef onumuzdeyse ve uzaksa: hedefe dogru hizlan (yaklas)
         //  - hedefi gectiysek / yakindaysak: mevcut hareket yonunde ivmeyi koru (kolayca ilerisine gec)
         double tx = t.method_23317() - mc.field_1724.method_23317();
         double ty = t.method_23318() + (double)(t.method_18381(t.method_18376()) / 2.0F) - mc.field_1724.method_23318();
         double tz = t.method_23321() - mc.field_1724.method_23321();
         double tDist = Math.sqrt(tx * tx + ty * ty + tz * tz);
         double vx = mc.field_1724.method_18798().field_1352;
         double vy = mc.field_1724.method_18798().field_1351;
         double vz = mc.field_1724.method_18798().field_1350;
         double vLen = Math.sqrt(vx * vx + vy * vy + vz * vz);
         boolean ahead = vLen < 0.1 || vx * tx + vy * ty + vz * tz > 0.0;
         if (ahead && tDist > 3.0) {
            dirX = tx / tDist;
            dirY = ty / tDist;
            dirZ = tz / tDist;
         } else if (vLen > 0.1) {
            dirX = vx / vLen;
            dirY = vy / vLen;
            dirZ = vz / vLen;
         }
      }

      double b = (double)this.boost.getValue();
      double newVx = mc.field_1724.method_18798().field_1352 + dirX * b;
      double newVy = mc.field_1724.method_18798().field_1351 + dirY * b;
      double newVz = mc.field_1724.method_18798().field_1350 + dirZ * b;

      // RushAC "ElytraTargetS": yatay hiz, DALMIYORKEN (to.y >= from.y) 1.65 blok/tick'i gecerse flag atiyor.
      // Dalista bu kontrol devre disi, o yuzden sadece tirmanirken/duzdeyken sinirliyoruz.
      if (newVy >= 0.0) {
         double horiz = Math.sqrt(newVx * newVx + newVz * newVz);
         double cap = 1.55;
         if (horiz > cap) {
            double scale = cap / horiz;
            newVx *= scale;
            newVz *= scale;
         }
      }

      mc.field_1724.method_18800(newVx, newVy, newVz);
      this.boostTimer.reset();
   }

   /**
    * Bypass + Aura: hitbox sistemi. Rakibin onunda, arkasinda veya kafasinda otomatik bir gecis noktasi secilir
    * ve ucus o noktadan gecer (ilerisine gecip ters donerek tekrar gecer); vurusu Aura ters bakisla yapar.
    * Hic bir zaman velocity/pozisyon yazmaz, sadece yaw/pitch secer.
    */
   private void runEngage(class_1297 t) {
      Profile p = this.profile;
      double tx = t.method_23317();
      double ty = t.method_23318();
      double tz = t.method_23321();
      class_238 box = t.method_5829();
      double height = box.field_1325 - box.field_1322;

      // rakibin yatay hizi (tick basina), yumusatilmis
      if (t != this.trackedTarget) {
         this.trackedTarget = t;
         this.passHold = 0;
         this.lastTx = tx;
         this.lastTz = tz;
         this.tVelX = 0.0;
         this.tVelZ = 0.0;
         this.anchorLock = 0;
      } else {
         this.tVelX = this.tVelX * 0.6 + (tx - this.lastTx) * 0.4;
         this.tVelZ = this.tVelZ * 0.6 + (tz - this.lastTz) * 0.4;
         this.lastTx = tx;
         this.lastTz = tz;
      }

      // rakibin "one" yonu: kacarken hareket yonu, dururken baktigi yon
      double tSpeed = Math.sqrt(this.tVelX * this.tVelX + this.tVelZ * this.tVelZ);
      boolean fleeing = tSpeed > 0.12;
      double fx;
      double fz;
      if (tSpeed > 0.06) {
         fx = this.tVelX / tSpeed;
         fz = this.tVelZ / tSpeed;
      } else {
         double rad = Math.toRadians((double)t.method_36454());
         fx = -Math.sin(rad);
         fz = Math.cos(rad);
      }

      double myX = mc.field_1724.method_23317();
      double myY = mc.field_1724.method_23318();
      double myZ = mc.field_1724.method_23321();
      double rx = myX - tx;
      double rz = myZ - tz;
      double hDist = Math.sqrt(rx * rx + rz * rz);
      double dot = rx * fx + rz * fz;

      // gecis noktasi secimi (kilitli, titremesin): yuksekteysek kafaya dal, rakibin gerisindeysek onune,
      // onundeysek arkasina gec
      if (this.anchorLock > 0) {
         this.anchorLock--;
      } else {
         Anchor pick;
         if (myY - ty > 7.0 && hDist < 14.0) {
            pick = Anchor.HEAD;
         } else {
            pick = dot < 0.0 ? Anchor.FRONT : Anchor.BEHIND;
         }
         if (pick != this.anchor) {
            if (hDist < 8.0 && this.anchor != Anchor.HEAD && pick != Anchor.HEAD) {
               // rakibin ilerisine gectik: hemen donme, ters donmeden once duz gidip mesafe ac
               this.passHold = 8;
            }
            this.anchor = pick;
            this.anchorLock = 12;
         }
      }

      double ax = tx;
      double ay = ty + height * 0.5;
      double az = tz;
      switch (this.anchor) {
         case FRONT:
            ax += fx * 1.0;
            az += fz * 1.0;
            if (fleeing && hDist > 5.0) {
               double vx0 = mc.field_1724.method_18798().field_1352;
               double vz0 = mc.field_1724.method_18798().field_1350;
               double mySpeed = Math.max(Math.sqrt(vx0 * vx0 + vz0 * vz0), 0.6);
               double lead = class_3532.method_15350(hDist / mySpeed, 0.0, 12.0);
               ax += this.tVelX * lead;
               az += this.tVelZ * lead;
            }
            break;
         case BEHIND:
            ax -= fx * 1.0;
            az -= fz * 1.0;
            break;
         default:
            ay = ty + height;
            break;
      }

      // acilar
      class_243 eye = mc.field_1724.method_33571();
      double dx = ax - eye.field_1352;
      double dy = ay - eye.field_1351;
      double dz = az - eye.field_1350;
      double hd = Math.sqrt(dx * dx + dz * dz);
      float curYaw = this.steering ? this.flightYaw : mc.field_1724.method_36454();
      float curPitch = this.steering ? this.flightPitch : mc.field_1724.method_36455();
      float wantYaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      float wantPitch = (float)(-Math.toDegrees(Math.atan2(dy, Math.max(hd, 0.001))));

      if (this.passHold > 0) {
         this.passHold--;
         double hv = Math.sqrt(mc.field_1724.method_18798().field_1352 * mc.field_1724.method_18798().field_1352
            + mc.field_1724.method_18798().field_1350 * mc.field_1724.method_18798().field_1350);
         if (hv > 0.2) {
            wantYaw = (float)(Math.toDegrees(Math.atan2(mc.field_1724.method_18798().field_1350, mc.field_1724.method_18798().field_1352)) - 90.0);
            wantPitch = curPitch;
         }
      }

      // hiz / yer korumasi: yavassak once dal, cok hizliysa tirmanma, yere yakinsa tirman
      double vx = mc.field_1724.method_18798().field_1352;
      double vz = mc.field_1724.method_18798().field_1350;
      double speed = Math.sqrt(vx * vx + vz * vz) * 20.0;
      wantPitch = class_3532.method_15363(wantPitch, -p.climb, p.dive);
      if (speed < (double)p.minSpeed && wantPitch < 0.0F) {
         wantPitch = p.dive * 0.5F;
      } else if (speed > (double)p.maxSpeed && wantPitch > 0.0F) {
         wantPitch = 0.0F;
      }
      if (this.groundDistance(p.groundGuard) <= p.groundGuard) {
         wantPitch = -p.climb;
      }

      float yawStep = p.pitchStep * 2.5F * this.jStep;
      float nextYaw = curYaw + class_3532.method_15363(class_3532.method_15393(wantYaw - curYaw), -yawStep, yawStep);
      float nextPitch = class_3532.method_15363(
         curPitch + class_3532.method_15363(wantPitch - curPitch, -p.pitchStep * this.jStep, p.pitchStep * this.jStep), -90.0F, 90.0F
      );

      // fare hassasiyet izgarasina oturt
      double gcd = Math.pow((Double)mc.field_1690.method_42495().method_41753() * 0.6 + 0.2, 3.0) * 1.2;
      if (gcd > 1.0E-6) {
         nextYaw = (float)((double)nextYaw - (double)(nextYaw - curYaw) % gcd);
         nextPitch = (float)((double)nextPitch - (double)(nextPitch - curPitch) % gcd);
      }
      nextPitch = class_3532.method_15363(nextPitch, -90.0F, 90.0F);

      // sessiz: sadece ucus hesabinda uygulanir (Rotations), kamera oynamaz
      this.flightYaw = nextYaw;
      this.flightPitch = nextPitch;
      this.steering = true;
   }

   /** Rotations modulu ucus hesabinda flightYaw / flightPitch kullanir. */
   public boolean steerActive() {
      return this.steering && this.isEnabled() && this.mode.is(Mode.Bypass) && this.hitbox.getValue();
   }

   /**
    * Vanilla-physics glide controller. Hic bir zaman velocity/pozisyon yazmaz, sadece bu tick'in pitch'ini secer.
    * DIVE = pitch asagi (irtifa -> hiz), CLIMB = pitch yukari (hiz -> irtifa). Parametreler aktif profilden gelir.
    */
   private void runBypass() {
      long nowMs = System.currentTimeMillis();
      if (!Float.isNaN(this.lastBypassPitch) && Math.abs(mc.field_1724.method_36455() - this.lastBypassPitch) > 0.01F) {
         // oyuncu fareyle pitch'i degistirdi: 1.5 saniye kontrolu birak
         this.manualUntilMs = nowMs + 1500L;
         this.lastBypassPitch = Float.NaN;
         this.bypassActive = false;
      }

      if (nowMs < this.manualUntilMs) {
         return;
      }

      Profile p = this.profile;
      double vx = mc.field_1724.method_18798().field_1352;
      double vz = mc.field_1724.method_18798().field_1350;
      double speed = Math.sqrt(vx * vx + vz * vz) * 20.0;
      double y = mc.field_1724.method_23318();

      float maxS = p.maxSpeed * this.jMax;
      float minS = Math.min(p.minSpeed * this.jMin, maxS - 5.0F);
      double half = (double)p.band / 2.0;

      if (!this.bypassActive) {
         this.bypassActive = true;
         this.holdY = y;
         this.phase = speed >= (double)(minS + maxS) / 2.0 ? Phase.CLIMB : Phase.DIVE;
      }

      // Space irtifa bandini yukari, Shift asagi kaydirir
      if (mc.field_1690.field_1903.method_1434()) {
         this.holdY += 0.6;
      }
      if (mc.field_1690.field_1832.method_1434()) {
         this.holdY -= 0.6;
      }

      boolean danger = this.groundDistance(p.groundGuard) <= p.groundGuard;
      if (danger) {
         this.holdY = Math.max(this.holdY, y + half);
         this.phase = Phase.CLIMB;
      } else if (this.phase == Phase.DIVE) {
         if (speed >= (double)maxS || y <= this.holdY - half) {
            this.phase = Phase.CLIMB;
         }
      } else if (speed <= (double)minS || y >= this.holdY + half) {
         this.phase = Phase.DIVE;
      }

      float target = this.phase == Phase.DIVE ? p.dive : -p.climb;
      float cur = mc.field_1724.method_36455();
      float next = class_3532.method_15363(cur + class_3532.method_15363(target - cur, -p.pitchStep * this.jStep, p.pitchStep * this.jStep), -90.0F, 90.0F);

      // fare hassasiyet izgarasina oturt: rotasyon farki gercek fare girdisi gibi gorunur
      double gcd = Math.pow((Double)mc.field_1690.method_42495().method_41753() * 0.6 + 0.2, 3.0) * 1.2;
      if (gcd > 1.0E-6) {
         next = (float)((double)next - (double)(next - cur) % gcd);
      }
      next = class_3532.method_15363(next, -90.0F, 90.0F);

      mc.field_1724.method_36457(next);
      this.lastBypassPitch = next;
   }

   /** Oyuncunun altindaki ilk dolu blok mesafesi, guard + 1 = yok. */
   private int groundDistance(int guard) {
      if (guard <= 0) {
         return 999;
      }

      class_2338 base = mc.field_1724.method_24515();
      int bx = base.method_10263();
      int by = base.method_10264();
      int bz = base.method_10260();
      for (int i = 1; i <= guard; i++) {
         if (!mc.field_1687.method_8320(new class_2338(bx, by - i, bz)).method_26215()) {
            return i;
         }
      }
      return guard + 1;
   }
}
