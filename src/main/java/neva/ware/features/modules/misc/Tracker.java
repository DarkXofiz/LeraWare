package neva.ware.features.modules.misc;

import io.netty.util.internal.ConcurrentSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1683;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2885;
import net.minecraft.class_2886;
import net.minecraft.class_7439;
import neva.ware.events.impl.EventEntitySpawn;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.setting.Setting;
import neva.ware.utility.math.MathUtility;

public class Tracker extends Module {
   protected final Setting<Boolean> only1v1 = new Setting<>("1v1-Only", true);
   protected final Set<class_2338> placed = new ConcurrentSet();
   protected final AtomicInteger awaitingExp = new AtomicInteger();
   protected static final AtomicInteger crystals = new AtomicInteger();
   protected static final AtomicInteger exp = new AtomicInteger();
   protected static class_1657 trackedPlayer;
   protected boolean awaiting;
   protected int crystalStacks;
   protected int expStacks;

   public Tracker() {
      super("Tracker", Module.Category.MISC);
   }

   @Override
   public void onEnable() {
      this.awaiting = false;
      trackedPlayer = null;
      this.awaitingExp.set(0);
      crystals.set(0);
      exp.set(0);
      this.crystalStacks = 0;
      this.expStacks = 0;
   }

   @Override
   public String getDisplayInfo() {
      return trackedPlayer == null ? null : trackedPlayer.method_5477().getString();
   }

   @EventHandler
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof class_7439 pac) {
         String s = pac.comp_763().getString();
         if (!s.contains("<") && (s.contains("has accepted your duel request") || s.contains("Accepted the duel request from"))) {
            this.sendMessage(ClientSettings.isRu() ? "Дуель принята! Обновляю цель..." : "Duel accepted! Resetting target...");
            trackedPlayer = null;
            this.awaitingExp.set(0);
            crystals.set(0);
            exp.set(0);
            this.crystalStacks = 0;
            this.expStacks = 0;
         }
      }
   }

   @EventHandler
   public void onEntitySpawn(EventEntitySpawn e) {
      if (e.getEntity() instanceof class_1511
         && !this.placed.remove(class_2338.method_49637(e.getEntity().method_23317(), e.getEntity().method_23318() - 1.0, e.getEntity().method_23321()))) {
         crystals.incrementAndGet();
      }

      if (e.getEntity() instanceof class_1683) {
         if (this.awaitingExp.get() > 0) {
            if (mc.field_1724.method_5858(e.getEntity()) < 16.0) {
               this.awaitingExp.decrementAndGet();
            } else {
               exp.incrementAndGet();
            }
         } else {
            exp.incrementAndGet();
         }
      }
   }

   @Override
   public void onUpdate() {
      boolean found = false;

      for (class_1657 player : mc.field_1687.method_18456()) {
         if (player != null && !player.equals(mc.field_1724)) {
            if (found && this.only1v1.getValue()) {
               this.disable(ClientSettings.isRu() ? "Ты не в дуели! Отключаю.." : "Disabled, you are not in a 1v1! Disabling...");
               return;
            }

            if (trackedPlayer == null) {
               this.sendMessage(
                  class_124.field_1076
                     + (ClientSettings.isRu() ? "Следим за " : "Now tracking ")
                     + class_124.field_1064
                     + player.method_5477().getString()
                     + class_124.field_1076
                     + "!"
               );
            }

            trackedPlayer = player;
            found = true;
         }
      }

      if (trackedPlayer != null) {
         int exp = Tracker.exp.get() / 64;
         if (this.expStacks != exp) {
            this.expStacks = exp;
            if (ClientSettings.isRu()) {
               this.sendMessage(
                  class_124.field_1064
                     + trackedPlayer.method_5477().getString()
                     + class_124.field_1076
                     + " использовал "
                     + class_124.field_1068
                     + exp
                     + class_124.field_1076
                     + (exp == 1 ? " стак" : " стаков")
                     + " Пузырьков опыта!"
               );
            } else {
               this.sendMessage(
                  class_124.field_1064
                     + trackedPlayer.method_5477().getString()
                     + class_124.field_1076
                     + " used "
                     + class_124.field_1068
                     + exp
                     + class_124.field_1076
                     + (exp == 1 ? " stack" : " stacks")
                     + " of XP Bottles!"
               );
            }
         }

         int crystals = Tracker.crystals.get() / 64;
         if (this.crystalStacks != crystals) {
            this.crystalStacks = crystals;
            if (!ClientSettings.isRu()) {
               this.sendMessage(
                  class_124.field_1064
                     + trackedPlayer.method_5477().getString()
                     + class_124.field_1076
                     + " used "
                     + class_124.field_1068
                     + crystals
                     + class_124.field_1076
                     + (crystals == 1 ? " stack" : " stacks")
                     + " of Crystals!"
               );
            } else {
               this.sendMessage(
                  class_124.field_1064
                     + trackedPlayer.method_5477().getString()
                     + class_124.field_1076
                     + " использовал "
                     + class_124.field_1068
                     + crystals
                     + class_124.field_1076
                     + (crystals == 1 ? " стак" : " стаков")
                     + " Кристаллов!"
               );
            }
         }
      }
   }

   public void sendTrack() {
      if (trackedPlayer != null) {
         int c = crystals.get();
         int e = exp.get();
         StringBuilder builder;
         if (ClientSettings.isRu()) {
            builder = new StringBuilder()
               .append(trackedPlayer.method_5477().getString())
               .append(class_124.field_1076)
               .append(" использовал ")
               .append(class_124.field_1068)
               .append(c)
               .append(class_124.field_1076)
               .append(" (")
               .append(class_124.field_1068);
         } else {
            builder = new StringBuilder()
               .append(trackedPlayer.method_5477().getString())
               .append(class_124.field_1076)
               .append(" has used ")
               .append(class_124.field_1068)
               .append(c)
               .append(class_124.field_1076)
               .append(" (")
               .append(class_124.field_1068);
         }

         if (c % 64 == 0) {
            builder.append(c / 64);
         } else {
            builder.append(MathUtility.round((double)c / 64.0, 1));
         }

         if (ClientSettings.isRu()) {
            builder.append(class_124.field_1076)
               .append(") кристаллов и ")
               .append(class_124.field_1068)
               .append(e)
               .append(class_124.field_1076)
               .append(" (")
               .append(class_124.field_1068);
         } else {
            builder.append(class_124.field_1076)
               .append(") crystals and ")
               .append(class_124.field_1068)
               .append(e)
               .append(class_124.field_1076)
               .append(" (")
               .append(class_124.field_1068);
         }

         if (e % 64 == 0) {
            builder.append(e / 64);
         } else {
            builder.append(MathUtility.round((double)e / 64.0, 1));
         }

         if (ClientSettings.isRu()) {
            builder.append(class_124.field_1076).append(") пузырьков опыта.");
         } else {
            builder.append(class_124.field_1076).append(") bottles of experience.");
         }

         this.sendMessage(builder.toString());
      }
   }

   @EventHandler
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getPacket() instanceof class_2886
         && (mc.field_1724.method_6047().method_7909() == class_1802.field_8287 || mc.field_1724.method_6079().method_7909() == class_1802.field_8287)) {
         this.awaitingExp.incrementAndGet();
      }

      if (event.getPacket() instanceof class_2885 pac
         && (mc.field_1724.method_6047().method_7909() == class_1802.field_8301 || mc.field_1724.method_6079().method_7909() == class_1802.field_8301)) {
         this.placed.add(pac.method_12543().method_17777());
      }
   }
}
