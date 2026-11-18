package neva.ware.features.modules.combat;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_2620;
import neva.ware.events.impl.EventAttackBlock;
import neva.ware.events.impl.EventBreakBlock;
import neva.ware.events.impl.EventPlaceBlock;
import neva.ware.events.impl.EventPostSync;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.base.PlaceModule;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.player.PlayerUtility;
import neva.ware.utility.world.HoleUtility;
import org.jetbrains.annotations.NotNull;

public final class Blocker extends PlaceModule {
   private final Setting<Integer> actionShift = new Setting<>("Place Per Tick", 1, 1, 5);
   private final Setting<Integer> actionInterval = new Setting<>("Delay", 0, 0, 5);
   private final Setting<SettingGroup> logic = new Setting<>("Logic", new SettingGroup(false, 0));
   private final Setting<Boolean> antiCev = new Setting<>("Anti Cev", true).addToGroup(this.logic);
   private final Setting<Boolean> antiCiv = new Setting<>("Anti Civ", true).addToGroup(this.logic);
   private final Setting<Boolean> expand = new Setting<>("Expand", true).addToGroup(this.logic);
   private final Setting<Boolean> antiTntAura = new Setting<>("Anti TNT", false).addToGroup(this.logic);
   private final Setting<Boolean> antiAutoAnchor = new Setting<>("Anti Anchor", false).addToGroup(this.logic);
   private final Setting<SettingGroup> detect = new Setting<>("Detect", new SettingGroup(false, 1)).addToGroup(this.logic);
   private final Setting<Boolean> onPacket = new Setting<>("On Break Packet", true).addToGroup(this.detect);
   private final Setting<Boolean> onAttackBlock = new Setting<>("On Attack Block", false).addToGroup(this.detect);
   private final Setting<Boolean> onBreak = new Setting<>("On Break", true).addToGroup(this.detect);
   private final List<class_2338> placePositions = new CopyOnWriteArrayList<>();
   private int tickCounter = 0;

   public Blocker() {
      super("Blocker", Module.Category.COMBAT);
   }

   @Override
   public void onEnable() {
      this.tickCounter = 0;
      this.sendMessage(
         class_124.field_1061
            + (
               ClientSettings.isRu()
                  ? "ВНИМАНИЕ!!! "
                     + class_124.field_1070
                     + "Использование блокера на серверах осуждается игроками, а в некоторых странах карается набутыливанием!"
                  : "WARNING!!! "
                     + class_124.field_1070
                     + "The use of blocker on servers is condemned by players, and in some countries is punishable by jail!"
            )
      );
   }

   @EventHandler
   public void onPostSync(EventPostSync event) {
      if (this.tickCounter < this.actionInterval.getValue()) {
         this.tickCounter++;
      } else {
         if (this.tickCounter >= this.actionInterval.getValue()) {
            this.tickCounter = 0;
         }

         if (this.getBlockResult().found() && !this.placePositions.isEmpty()) {
            this.placePositions.removeIf(b -> PlayerUtility.squaredDistanceFromEyes(b.method_46558()) > this.range.getPow2Value());
            int blocksPlaced = 0;

            while (blocksPlaced < this.actionShift.getValue()) {
               class_2338 pos = this.placePositions
                  .stream()
                  .filter(p -> InteractionUtility.canPlaceBlock(p, this.interact.getValue(), true))
                  .min(Comparator.comparing(p -> mc.field_1724.method_19538().method_1022(p.method_46558())))
                  .orElse(null);
               if (pos == null || !mc.field_1724.method_24828() || !this.placeBlock(pos)) {
                  break;
               }

               blocksPlaced++;
               this.tickCounter = 0;
               this.placePositions.remove(pos);
               this.inactivityTimer.reset();
            }
         }
      }
   }

   @EventHandler
   private void onPacketReceive(@NotNull PacketEvent.Receive event) {
      if (event.getPacket() instanceof class_2620 && this.onPacket.getValue()) {
         class_2620 packet = event.getPacket();
         this.doLogic(packet.method_11277());
      }
   }

   @EventHandler
   private void onAttackBlock(EventAttackBlock event) {
      if (this.onAttackBlock.getValue()) {
         this.doLogic(event.getBlockPos());
      }
   }

   @EventHandler
   private void onBreak(EventBreakBlock event) {
      if (this.onBreak.getValue()) {
         this.doLogic(event.getPos());
      }
   }

   @EventHandler
   private void onPlaceBlock(@NotNull EventPlaceBlock event) {
      if (event.getBlockPos().equals(mc.field_1724.method_24515().method_10086(2))
         && event.getBlock().equals(class_2246.field_10375)
         && this.antiTntAura.getValue()) {
         this.placePositions.add(event.getBlockPos());
      }

      if (event.getBlockPos().equals(mc.field_1724.method_24515().method_10086(2))
         && event.getBlock().equals(class_2246.field_23152)
         && this.antiAutoAnchor.getValue()) {
         this.placePositions.add(event.getBlockPos());
      }
   }

   private void doLogic(class_2338 pos) {
      if (mc.field_1687 != null && mc.field_1724 != null && HoleUtility.isHole(mc.field_1724.method_24515())) {
         if (this.antiCev.getValue()) {
            for (class_2338 checkPos : HoleUtility.getHolePoses(mc.field_1724.method_19538())) {
               if (pos.equals(checkPos.method_10086(2))) {
                  this.placePositions.add(checkPos.method_10086(3));
                  return;
               }
            }
         }

         if (HoleUtility.getSurroundPoses(mc.field_1724.method_19538()).contains(pos)) {
            if (mc.field_1687.method_8320(pos).method_26204() != class_2246.field_9987 && !mc.field_1687.method_8320(pos).method_45474()) {
               this.placePositions.add(pos.method_10084());
               if (this.expand.getValue()) {
                  for (class_2382 vec : HoleUtility.VECTOR_PATTERN) {
                     class_2338 checkPosx = pos.method_10081(vec);
                     if (this.canPlaceBlock(checkPosx, true) && mc.field_1687.method_18467(class_1657.class, new class_238(checkPosx)).isEmpty()) {
                        this.placePositions.add(checkPosx);
                     }
                  }
               }
            }
         } else {
            if (this.antiCiv.getValue()) {
               for (class_2338 checkPosx : HoleUtility.getSurroundPoses(mc.field_1724.method_19538())) {
                  if (pos.equals(checkPosx.method_10084())) {
                     this.placePositions.add(checkPosx.method_10086(2));
                     return;
                  }
               }
            }
         }
      }
   }
}
