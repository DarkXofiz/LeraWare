package neva.ware.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2828;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2828.class_5911;
import neva.ware.events.impl.EventSync;
import neva.ware.events.impl.EventTick;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.injection.accesors.IPlayerMoveC2SPacket;
import neva.ware.setting.Setting;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.SearchInvResult;

public class NoFall extends Module {
   public final Setting<NoFall.Mode> mode = new Setting<>("Mode", NoFall.Mode.Rubberband);
   public final Setting<NoFall.FallDistance> fallDistance = new Setting<>("FallDistance", NoFall.FallDistance.Calc);
   public final Setting<Integer> fallDistanceValue = new Setting<>(
      "FallDistanceVal", 10, 2, 100, v -> this.fallDistance.getValue() == NoFall.FallDistance.Custom
   );
   private final Setting<Boolean> powderSnowBucket = new Setting<>("PowderSnowBucket", true, v -> this.mode.getValue() == NoFall.Mode.Items);
   private final Setting<Boolean> waterBucket = new Setting<>("WaterBucket", true, v -> this.mode.getValue() == NoFall.Mode.Items);
   private final Setting<Boolean> retrieve = new Setting<>("Retrieve", true, v -> this.mode.getValue() == NoFall.Mode.Items && this.waterBucket.getValue());
   private final Setting<Boolean> enderPearl = new Setting<>("EnderPearl", true, v -> this.mode.getValue() == NoFall.Mode.Items);
   private final Setting<Boolean> cobweb = new Setting<>("Cobweb", true, v -> this.mode.getValue() == NoFall.Mode.Items);
   private final Setting<Boolean> twistingVines = new Setting<>("TwistingVines", true, v -> this.mode.getValue() == NoFall.Mode.Items);
   private neva.ware.utility.Timer pearlCooldown = new neva.ware.utility.Timer();
   private boolean retrieveFlag;
   private boolean cancelGround = false;

   public NoFall() {
      super("NoFall", Module.Category.MOVEMENT);
   }

   @EventHandler
   public void onSync(EventSync e) {
      if (!fullNullCheck()) {
         if (this.isFalling()) {
            switch ((NoFall.Mode)this.mode.getValue()) {
               case Rubberband:
                  this.sendPacket(new class_5911(true));
                  break;
               case Items:
                  class_2338 playerPos = class_2338.method_49638(mc.field_1724.method_19538());
                  SearchInvResult snowResult = InventoryUtility.findItemInHotBar(class_1802.field_27876);
                  SearchInvResult pearlResult = InventoryUtility.findItemInHotBar(class_1802.field_8634);
                  SearchInvResult webResult = InventoryUtility.findItemInHotBar(class_1802.field_8786);
                  SearchInvResult vinesResult = InventoryUtility.findItemInHotBar(class_1802.field_23070);
                  SearchInvResult waterResult = InventoryUtility.findItemInHotBar(class_1802.field_8705);
                  if (waterResult.found() && this.waterBucket.getValue()) {
                     mc.field_1724.method_36457(90.0F);
                     this.doWaterDrop(waterResult, playerPos);
                  } else if (pearlResult.found() && this.enderPearl.getValue()) {
                     mc.field_1724.method_36457(90.0F);
                     this.doPearlDrop(pearlResult);
                  } else if (webResult.found() && this.cobweb.getValue()) {
                     mc.field_1724.method_36457(90.0F);
                     this.doWebDrop(webResult, playerPos);
                  } else if (vinesResult.found() && this.twistingVines.getValue()) {
                     mc.field_1724.method_36457(90.0F);
                     this.doVinesDrop(vinesResult, playerPos);
                  } else if (snowResult.found() && this.powderSnowBucket.getValue()) {
                     mc.field_1724.method_36457(90.0F);
                     this.doSnowDrop(snowResult, playerPos);
                  }
                  break;
               case MatrixOffGround:
               case Vanilla:
                  this.cancelGround = true;
            }
         } else if (this.retrieveFlag) {
            InventoryUtility.saveSlot();
            SearchInvResult waterResult = InventoryUtility.findItemInHotBar(class_1802.field_8550);
            waterResult.switchTo();
            mc.field_1724.method_36457(90.0F);
            mc.field_1761.method_2919(mc.field_1724, class_1268.field_5808);
            mc.field_1724.method_6104(class_1268.field_5808);
            InventoryUtility.returnSlot();
            this.retrieveFlag = false;
         }
      }
   }

   @EventHandler
   public void onTick(EventTick e) {
      if (this.mode.is(NoFall.Mode.Grim2b2t) && this.isFalling()) {
         this.sendPacket(
            new class_2830(
               mc.field_1724.method_23317(),
               mc.field_1724.method_23318() + 1.0E-9,
               mc.field_1724.method_23321(),
               mc.field_1724.method_36454(),
               mc.field_1724.method_36455(),
               false
            )
         );
         mc.field_1724.method_38785();
      }
   }

   private void doWaterDrop(SearchInvResult waterResult, class_2338 playerPos) {
      if (mc.field_1687.method_8320(playerPos.method_10074()).method_51367()
         || mc.field_1687.method_8320(playerPos.method_10074().method_10074()).method_51367()) {
         InventoryUtility.saveSlot();
         waterResult.switchTo();
         mc.field_1761.method_2919(mc.field_1724, class_1268.field_5808);
         mc.field_1724.method_6104(class_1268.field_5808);
         InventoryUtility.returnSlot();
         this.retrieveFlag = this.retrieve.getValue();
      }
   }

   private void doPearlDrop(SearchInvResult pearlResult) {
      if (this.pearlCooldown.passedMs(5000L)) {
         InventoryUtility.saveSlot();
         pearlResult.switchTo();
         mc.field_1761.method_2919(mc.field_1724, class_1268.field_5808);
         mc.field_1724.method_6104(class_1268.field_5808);
         InventoryUtility.returnSlot();
         this.pearlCooldown.reset();
      }
   }

   private void doWebDrop(SearchInvResult webResult, class_2338 playerPos) {
      if (mc.field_1687.method_8320(playerPos.method_10074()).method_51367()
         || mc.field_1687.method_8320(playerPos.method_10074().method_10074()).method_51367()) {
         InventoryUtility.saveSlot();
         if (mc.field_1687.method_8320(playerPos.method_10074()).method_51367()) {
            InteractionUtility.placeBlock(
               playerPos,
               InteractionUtility.Rotate.None,
               InteractionUtility.Interact.Vanilla,
               InteractionUtility.PlaceMode.Normal,
               webResult.slot(),
               false,
               true
            );
         } else {
            InteractionUtility.placeBlock(
               playerPos.method_10074(),
               InteractionUtility.Rotate.None,
               InteractionUtility.Interact.Vanilla,
               InteractionUtility.PlaceMode.Normal,
               webResult.slot(),
               false,
               true
            );
         }

         mc.field_1724.method_6104(class_1268.field_5808);
         InventoryUtility.returnSlot();
      }
   }

   private void doVinesDrop(SearchInvResult vinesResult, class_2338 playerPos) {
      if (mc.field_1687.method_8320(playerPos.method_10074()).method_51367()
         || mc.field_1687.method_8320(playerPos.method_10074().method_10074()).method_51367()) {
         InventoryUtility.saveSlot();
         if (mc.field_1687.method_8320(playerPos.method_10074()).method_51367()) {
            InteractionUtility.placeBlock(
               playerPos,
               InteractionUtility.Rotate.None,
               InteractionUtility.Interact.Vanilla,
               InteractionUtility.PlaceMode.Normal,
               vinesResult.slot(),
               false,
               true
            );
         } else {
            InteractionUtility.placeBlock(
               playerPos.method_10074(),
               InteractionUtility.Rotate.None,
               InteractionUtility.Interact.Vanilla,
               InteractionUtility.PlaceMode.Normal,
               vinesResult.slot(),
               false,
               true
            );
         }

         mc.field_1724.method_6104(class_1268.field_5808);
         InventoryUtility.returnSlot();
      }
   }

   private void doSnowDrop(SearchInvResult snowResult, class_2338 playerPos) {
      if (mc.field_1687.method_8320(playerPos.method_10074()).method_51367()
         || mc.field_1687.method_8320(playerPos.method_10074().method_10074()).method_51367()) {
         InventoryUtility.saveSlot();
         if (mc.field_1687.method_8320(playerPos.method_10074()).method_51367()) {
            InteractionUtility.placeBlock(
               playerPos,
               InteractionUtility.Rotate.None,
               InteractionUtility.Interact.Vanilla,
               InteractionUtility.PlaceMode.Normal,
               snowResult.slot(),
               false,
               true
            );
         } else {
            InteractionUtility.placeBlock(
               playerPos.method_10074(),
               InteractionUtility.Rotate.None,
               InteractionUtility.Interact.Vanilla,
               InteractionUtility.PlaceMode.Normal,
               snowResult.slot(),
               false,
               true
            );
         }

         mc.field_1724.method_6104(class_1268.field_5808);
         InventoryUtility.returnSlot();
      }
   }

   public boolean isFalling() {
      if (mc == null || mc.field_1724 == null || mc.field_1687 == null) {
         return false;
      } else if (mc.field_1724.method_6128()) {
         return false;
      } else if (this.mode.is(NoFall.Mode.Grim2b2t)) {
         return mc.field_1724.field_6017 > 3.0F;
      } else {
         switch ((NoFall.FallDistance)this.fallDistance.getValue()) {
            case Calc:
               return (mc.field_1724.field_6017 - 3.0F) / 2.0F + 3.5F > mc.field_1724.method_6032() / 3.0F;
            case Custom:
               return mc.field_1724.field_6017 > (float)this.fallDistanceValue.getValue().intValue();
            default:
               return false;
         }
      }
   }

   @Override
   public String getDisplayInfo() {
      return this.mode.getValue().toString() + " " + (this.isFalling() ? "Ready" : "");
   }

   @EventHandler
   public void onPacketSend(PacketEvent.Send e) {
      if (e.getPacket() instanceof class_2828 pac && this.cancelGround) {
         ((IPlayerMoveC2SPacket)pac).setOnGround(false);
      }
   }

   @Override
   public void onEnable() {
      this.cancelGround = false;
   }

   private static enum FallDistance {
      Calc,
      Custom;
   }

   private static enum Mode {
      Rubberband,
      Items,
      MatrixOffGround,
      Vanilla,
      Grim2b2t;
   }
}
