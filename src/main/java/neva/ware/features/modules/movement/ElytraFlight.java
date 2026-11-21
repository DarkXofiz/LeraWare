package neva.ware.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.PlayerUpdateEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.AttackAura;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;

/**
 * ElytraFlight
 *
 * Elytra ile ucarken bakilan yone belirli araliklarla hiz verir.
 * AttackAura hedefi varsa hedefe dogru yaklasir, hedefi gectikten sonra ivmeyi korur.
 *
 * LookBack : AttackAura ile birlikte acikken, hedefin ilerisine gecince aninda hedefe ters donup vurur.
 *            Mantik AttackAura icinde, bu modul rotasyona dokunmaz.
 */
public class ElytraFlight extends Module {
   public final Setting<Integer> delay = new Setting<>("Delay", 100, 0, 300);
   public final Setting<Float> boost = new Setting<>("Boost", 0.05F, 0.01F, 0.3F);
   public final Setting<Float> maxSpeed = new Setting<>("MaxSpeed", 1.6F, 0.5F, 3.0F);
   public final Setting<Boolean> onlySpace = new Setting<>("OnlyWhileSpace", false);
   public final Setting<Boolean> lookBack = new Setting<>("LookBack", true);

   private final Timer boostTimer = new Timer();
   private double lastX;
   private double lastY;
   private double lastZ;
   private boolean hasLast;

   public ElytraFlight() {
      super("ElytraFlight", Module.Category.MOVEMENT);
   }

   @Override
   public void onEnable() {
      this.hasLast = false;
      this.boostTimer.reset();
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
         return;
      }

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
         // hedef onumuzdeyse ve uzaksa: hedefe dogru hizlan (yaklas)
         // hedefi gectiysek / yakindaysak: mevcut hareket yonunde ivmeyi koru (kolayca ilerisine gec)
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
}
