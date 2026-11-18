package neva.ware.features.modules.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1299;
import net.minecraft.class_1313;
import net.minecraft.class_1511;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2604;
import net.minecraft.class_2626;
import net.minecraft.class_2708;
import net.minecraft.class_3532;
import net.minecraft.class_2828.class_2829;
import neva.ware.NevaWare;
import neva.ware.events.impl.EventTick;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.base.PlaceModule;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.world.HoleUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Surround extends PlaceModule {
   private final Setting<Integer> blocksPerTick = new Setting<>("Blocks/Place", 8, 1, 12);
   private final Setting<Integer> placeDelay = new Setting<>("Delay/Place", 3, 0, 10);
   private final Setting<Surround.CenterMode> center = new Setting<>("Center", Surround.CenterMode.Disabled);
   private final Setting<SettingGroup> autoDisable = new Setting<>("Auto Disable", new SettingGroup(false, 0));
   private final Setting<Boolean> onYChange = new Setting<>("On Y Change", true).addToGroup(this.autoDisable);
   private final Setting<Surround.OnTpAction> onTp = new Setting<>("On Tp", Surround.OnTpAction.None).addToGroup(this.autoDisable);
   private final Setting<Boolean> onDeath = new Setting<>("On Death", false).addToGroup(this.autoDisable);
   private int delay;
   private double prevY;

   public Surround() {
      super("Surround", Module.Category.COMBAT);
   }

   @Override
   public void onEnable() {
      if (mc.field_1724 != null) {
         this.delay = 0;
         this.prevY = mc.field_1724.method_23318();
         if (this.center.getValue() == Surround.CenterMode.Teleport) {
            mc.field_1724
               .method_30634(
                  (double)class_3532.method_15357(mc.field_1724.method_23317()) + 0.5,
                  mc.field_1724.method_23318(),
                  (double)class_3532.method_15357(mc.field_1724.method_23321()) + 0.5
               );
            this.sendPacket(
               new class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), mc.field_1724.method_24828())
            );
         }
      }
   }

   @Override
   public void onUpdate() {
      if ((mc.field_1724.method_29504() || !mc.field_1724.method_5805() || mc.field_1724.method_6032() + mc.field_1724.method_6067() <= 0.0F)
         && this.onDeath.getValue()) {
         this.disable(ClientSettings.isRu() ? "Выключен из-за смерти." : "Disable because you died.");
      }
   }

   @EventHandler(
      priority = 200
   )
   private void onTick(EventTick event) {
      if (this.prevY != mc.field_1724.method_23318() && this.onYChange.getValue() && NevaWare.core.getSetBackTime() > 500L) {
         this.disable(ClientSettings.isRu() ? "Отключён из-за изменения Y!" : "Disabled due to Y change!");
      } else {
         this.prevY = mc.field_1724.method_23318();
         class_243 centerVec = new class_243(
            (double)class_3532.method_15357(mc.field_1724.method_23317()) + 0.5,
            mc.field_1724.method_23318(),
            (double)class_3532.method_15357(mc.field_1724.method_23321()) + 0.5
         );
         class_238 centerBox = new class_238(
            centerVec.method_10216() - 0.2,
            centerVec.method_10214() - 0.1,
            centerVec.method_10215() - 0.2,
            centerVec.method_10216() + 0.2,
            centerVec.method_10214() + 0.1,
            centerVec.method_10215() + 0.2
         );
         if (this.center.getValue() == Surround.CenterMode.Motion && !centerBox.method_1006(mc.field_1724.method_19538())) {
            mc.field_1724
               .method_5784(
                  class_1313.field_6308,
                  new class_243(
                     (centerVec.method_10216() - mc.field_1724.method_23317()) / 2.0, 0.0, (centerVec.method_10215() - mc.field_1724.method_23321()) / 2.0
                  )
               );
         } else {
            List<class_2338> blocks = this.getBlocks();
            if (!blocks.isEmpty()) {
               if (this.delay > 0) {
                  this.delay--;
               } else {
                  if (!this.getBlockResult().found()) {
                     this.disable(ClientSettings.isRu() ? "Нет блоков!" : "No blocks!");
                  }

                  int placed = 0;
                  if (this.delay <= 0) {
                     while (placed < this.blocksPerTick.getValue()) {
                        if (!this.getBlockResult().found()) {
                           this.disable(ClientSettings.isRu() ? "Нет блоков!" : "No blocks!");
                        }

                        class_2338 targetBlock = this.getSequentialPos();
                        if (targetBlock == null || !this.placeBlock(targetBlock, true)) {
                           break;
                        }

                        placed++;
                        this.delay = this.placeDelay.getValue();
                        this.inactivityTimer.reset();
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = 200
   )
   private void onPacketReceive(@NotNull PacketEvent.Receive event) {
      if (!this.getBlockResult().found()) {
         this.disable(ClientSettings.isRu() ? "Нет блоков!" : "No blocks!");
      }

      if (event.getPacket() instanceof class_2604 spawn && spawn.method_11169() == class_1299.field_6110) {
         class_1511 cr = new class_1511(mc.field_1687, spawn.method_11175(), spawn.method_11174(), spawn.method_11176());
         cr.method_5838(spawn.method_11167());
         if (this.crystalBreaker.getValue().isEnabled() && cr.method_5858(mc.field_1724) <= (double)this.remove.getPow2Value()) {
            this.handlePacket();
         }
      }

      if (event.getPacket() instanceof class_2626 pac
         && mc.field_1724.method_5707(pac.method_11309().method_46558()) < (double)this.range.getPow2Value()
         && pac.method_11308().method_45474()) {
         this.handlePacket();
      }

      if (event.getPacket() instanceof class_2708 && this.onTp.getValue() == Surround.OnTpAction.Disable) {
         this.disable(ClientSettings.isRu() ? "Выключен из-за руббербенда!" : "Disabled due to a rubberband!");
      }
   }

   private void handlePacket() {
      class_2338 bp = this.getSequentialPos();
      if (bp != null && this.placeBlock(bp, PlaceModule.InteractMode.Packet)) {
         this.inactivityTimer.reset();
      }
   }

   @Nullable
   private class_2338 getSequentialPos() {
      for (class_2338 bp : this.getBlocks()) {
         if (!new class_238(bp).method_994(mc.field_1724.method_5829())
            && InteractionUtility.canPlaceBlock(bp, this.interact.getValue(), true)
            && mc.field_1687.method_8320(bp).method_45474()) {
            return bp;
         }
      }

      return null;
   }

   @NotNull
   private List<class_2338> getBlocks() {
      class_2338 playerPos = this.getPlayerPos();
      List<class_2338> offsets = new ArrayList<>();
      if (this.center.getValue() == Surround.CenterMode.Disabled && mc.field_1724 != null) {
         double decimalX = Math.abs(mc.field_1724.method_23317()) - Math.floor(Math.abs(mc.field_1724.method_23317()));
         double decimalZ = Math.abs(mc.field_1724.method_23321()) - Math.floor(Math.abs(mc.field_1724.method_23321()));
         int lengthXPos = HoleUtility.calcLength(decimalX, false);
         int lengthXNeg = HoleUtility.calcLength(decimalX, true);
         int lengthZPos = HoleUtility.calcLength(decimalZ, false);
         int lengthZNeg = HoleUtility.calcLength(decimalZ, true);
         ArrayList<class_2338> tempOffsets = new ArrayList<>();
         offsets.addAll(this.getOverlapPos());

         for (int x = 1; x < lengthXPos + 1; x++) {
            tempOffsets.add(HoleUtility.addToPlayer(playerPos, (double)x, 0.0, (double)(1 + lengthZPos)));
            tempOffsets.add(HoleUtility.addToPlayer(playerPos, (double)x, 0.0, (double)(-(1 + lengthZNeg))));
         }

         for (int var19 = 0; var19 <= lengthXNeg; var19++) {
            tempOffsets.add(HoleUtility.addToPlayer(playerPos, (double)(-var19), 0.0, (double)(1 + lengthZPos)));
            tempOffsets.add(HoleUtility.addToPlayer(playerPos, (double)(-var19), 0.0, (double)(-(1 + lengthZNeg))));
         }

         for (int z = 1; z < lengthZPos + 1; z++) {
            tempOffsets.add(HoleUtility.addToPlayer(playerPos, (double)(1 + lengthXPos), 0.0, (double)z));
            tempOffsets.add(HoleUtility.addToPlayer(playerPos, (double)(-(1 + lengthXNeg)), 0.0, (double)z));
         }

         for (int var17 = 0; var17 <= lengthZNeg; var17++) {
            tempOffsets.add(HoleUtility.addToPlayer(playerPos, (double)(1 + lengthXPos), 0.0, (double)(-var17)));
            tempOffsets.add(HoleUtility.addToPlayer(playerPos, (double)(-(1 + lengthXNeg)), 0.0, (double)(-var17)));
         }

         for (class_2338 pos : tempOffsets) {
            if (this.getDown(pos)) {
               offsets.add(pos.method_10069(0, -1, 0));
            }

            offsets.add(pos);
         }
      } else {
         offsets.add(playerPos.method_10069(0, -1, 0));

         for (class_2382 surround : HoleUtility.VECTOR_PATTERN) {
            if (this.getDown(playerPos.method_10081(surround))) {
               offsets.add(playerPos.method_10069(surround.method_10263(), -1, surround.method_10260()));
            }

            offsets.add(playerPos.method_10081(surround));
         }
      }

      return offsets;
   }

   private boolean getDown(class_2338 pos) {
      for (class_2350 dir : class_2350.values()) {
         if (!mc.field_1687.method_8320(pos.method_10081(dir.method_10163())).method_45474()) {
            return false;
         }
      }

      return mc.field_1687.method_8320(pos).method_45474() && this.interact.getValue() != InteractionUtility.Interact.AirPlace;
   }

   @NotNull
   private List<class_2338> getOverlapPos() {
      List<class_2338> positions = new ArrayList<>();
      if (mc.field_1724 != null) {
         double decimalX = mc.field_1724.method_23317() - Math.floor(mc.field_1724.method_23317());
         double decimalZ = mc.field_1724.method_23321() - Math.floor(mc.field_1724.method_23321());
         int offX = HoleUtility.calcOffset(decimalX);
         int offZ = HoleUtility.calcOffset(decimalZ);
         positions.add(this.getPlayerPos());

         for (int x = 0; x <= Math.abs(offX); x++) {
            for (int z = 0; z <= Math.abs(offZ); z++) {
               int properX = x * offX;
               int properZ = z * offZ;
               positions.add(Objects.requireNonNull(this.getPlayerPos()).method_10069(properX, -1, properZ));
            }
         }
      }

      return positions;
   }

   @NotNull
   private class_2338 getPlayerPos() {
      return class_2338.method_49637(
         mc.field_1724.method_23317(),
         mc.field_1724.method_23318() - Math.floor(mc.field_1724.method_23318()) > 0.8
            ? Math.floor(mc.field_1724.method_23318()) + 1.0
            : Math.floor(mc.field_1724.method_23318()),
         mc.field_1724.method_23321()
      );
   }

   private static enum CenterMode {
      Teleport,
      Motion,
      Disabled;
   }

   private static enum OnTpAction {
      Disable,
      Stay,
      None;
   }
}
