package neva.ware.features.modules.movement;

import java.util.HashMap;
import java.util.Map;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
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
 * Mode Boost  : elytra ile ucarken bakilan yone belirli araliklarla hiz verir.
 *               Aura hedefi varsa hedefe dogru yaklasir, hedefi gectikten sonra ivmeyi korur.
 * Mode Bypass : istemci hicbir hiz/pozisyon uydurmaz, hareketi vanilla elytra fizigi uretir; modul sadece
 *               pitch'i secer. Ayar YOK: sunucunun geri cekme (rubberband) tepkisinden ogrenip kendini
 *               STRICT / NORMAL / RELAXED profilleri arasinda otomatik ayarlar.
 *
 * LookBack    : Aura'nin elytra "ters bakis" sistemini acar (hedefi gecince aninda hedefe donup vurur).
 *               Mantik Aura icinde, bu modul rotasyona dokunmaz.
 */
public class ElytraFlight extends Module {
   public enum Mode {
      Boost,
      Bypass
   }

   private enum Phase {
      DIVE,
      CLIMB
   }

   /** Otomatik secilen bypass profilleri. Siralama siki -> gevsek. */
   private enum Profile {
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

   // ---- Her iki mod: Aura entegre ters bakis ----
   public final Setting<Boolean> lookBack = new Setting<>("LookBack", true);

   private static final Map<String, Profile> SERVER_PROFILE = new HashMap<>();
   private static final Map<String, Profile> SERVER_CEILING = new HashMap<>();

   private final Timer boostTimer = new Timer();
   private double lastX;
   private double lastY;
   private double lastZ;
   private boolean hasLast;

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
         if (this.profile.ordinal() > 0) {
            this.profile = Profile.values()[this.profile.ordinal() - 1];
            SERVER_PROFILE.put(this.serverKey, this.profile);
            this.notifyProfile("Anticheat tepkisi algilandi, profil: " + this.profile.name());
         }
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
         return;
      }
      this.lastFlyingMs = System.currentTimeMillis();

      if (this.mode.is(Mode.Bypass)) {
         this.adaptProfile();
         if (ModuleManager.aura.isEnabled() && AttackAura.target != null) {
            // Hedefteyken rotasyonu Aura yonetir (hedefe yaklas, gec, ters don, vur). Bypass pitch'e karismaz.
            this.bypassActive = false;
            return;
         }
         this.runBypass();
         return;
      }

      this.bypassActive = false;
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
      mc.field_1724.method_5762(dirX * b, dirY * b, dirZ * b);
      this.boostTimer.reset();
   }

   /**
    * Vanilla-physics glide controller. Hic bir zaman velocity/pozisyon yazmaz, sadece bu tick'in pitch'ini secer.
    * DIVE = pitch asagi (irtifa -> hiz), CLIMB = pitch yukari (hiz -> irtifa). Parametreler aktif profilden gelir.
    */
   private void runBypass() {
      Profile p = this.profile;
      double vx = mc.field_1724.method_18798().field_1352;
      double vz = mc.field_1724.method_18798().field_1350;
      double speed = Math.sqrt(vx * vx + vz * vz) * 20.0;
      double y = mc.field_1724.method_23318();

      float maxS = p.maxSpeed;
      float minS = Math.min(p.minSpeed, maxS - 5.0F);
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
      float next = class_3532.method_15363(cur + class_3532.method_15363(target - cur, -p.pitchStep, p.pitchStep), -90.0F, 90.0F);

      // fare hassasiyet izgarasina oturt: rotasyon farki gercek fare girdisi gibi gorunur
      double gcd = Math.pow((Double)mc.field_1690.method_42495().method_41753() * 0.6 + 0.2, 3.0) * 1.2;
      if (gcd > 1.0E-6) {
         next = (float)((double)next - (double)(next - cur) % gcd);
      }
      next = class_3532.method_15363(next, -90.0F, 90.0F);

      mc.field_1724.method_36457(next);
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
