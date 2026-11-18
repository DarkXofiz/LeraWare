package neva.ware.features.modules.combat;

import io.netty.buffer.Unpooled;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_2540;
import net.minecraft.class_2824;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.injection.accesors.IClientPlayerEntity;
import neva.ware.setting.Setting;
import org.jetbrains.annotations.NotNull;

public final class Criticals extends Module {
   public final Setting<Criticals.Mode> mode = new Setting<>("Mode", Criticals.Mode.UpdatedNCP);
   public static boolean cancelCrit;

   public Criticals() {
      super("Criticals", Module.Category.COMBAT);
   }

   @EventHandler
   public void onPacketSend(@NotNull PacketEvent.Send event) {
      if (event.getPacket() instanceof class_2824 && getInteractType(event.getPacket()) == Criticals.InteractType.ATTACK) {
         class_1297 ent = getEntity(event.getPacket());
         if (ent == null || ent instanceof class_1511 || cancelCrit) {
            return;
         }

         this.doCrit();
      }
   }

   public void doCrit() {
      if (!this.isDisabled() && mc.field_1724 != null && mc.field_1687 != null) {
         if ((mc.field_1724.method_24828() || mc.field_1724.method_31549().field_7479 || this.mode.is(Criticals.Mode.Grim))
            && !mc.field_1724.method_5771()
            && !mc.field_1724.method_5869()) {
            switch ((Criticals.Mode)this.mode.getValue()) {
               case Ncp:
                  this.critPacket(0.0625, false);
                  this.critPacket(0.0, false);
                  break;
               case Strict:
                  this.critPacket(0.062600301692775, false);
                  this.critPacket(0.07260029960661, false);
                  this.critPacket(0.0, false);
                  this.critPacket(0.0, false);
                  break;
               case OldNCP:
                  this.critPacket(1.058293536E-5, false);
                  this.critPacket(9.16580235E-6, false);
                  this.critPacket(1.0371854E-7, false);
                  break;
               case UpdatedNCP:
                  this.critPacket(2.71875E-7, false);
                  this.critPacket(0.0, false);
                  break;
               case Grim:
                  if (!mc.field_1724.method_24828()) {
                     this.critPacket(-1.0E-6, true);
                  }
            }
         }
      }
   }

   private void critPacket(double yDelta, boolean full) {
      if (!full) {
         this.sendPacket(new class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318() + yDelta, mc.field_1724.method_23321(), false));
      } else {
         this.sendPacket(
            new class_2830(
               mc.field_1724.method_23317(),
               mc.field_1724.method_23318() + yDelta,
               mc.field_1724.method_23321(),
               ((IClientPlayerEntity)(Object)mc.field_1724).getLastYaw(),
               ((IClientPlayerEntity)(Object)mc.field_1724).getLastPitch(),
               false
            )
         );
      }
   }

   public static class_1297 getEntity(@NotNull class_2824 packet) {
      class_2540 packetBuf = new class_2540(Unpooled.buffer());
      packet.method_55976(packetBuf);
      return mc.field_1687.method_8469(packetBuf.method_10816());
   }

   public static Criticals.InteractType getInteractType(@NotNull class_2824 packet) {
      class_2540 packetBuf = new class_2540(Unpooled.buffer());
      packet.method_55976(packetBuf);
      packetBuf.method_10816();
      return (Criticals.InteractType)packetBuf.method_10818(Criticals.InteractType.class);
   }

   public static enum InteractType {
      INTERACT,
      ATTACK,
      INTERACT_AT;
   }

   public static enum Mode {
      Ncp,
      Strict,
      OldNCP,
      UpdatedNCP,
      Grim;
   }
}
