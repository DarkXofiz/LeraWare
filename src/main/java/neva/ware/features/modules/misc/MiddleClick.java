package neva.ware.features.modules.misc;

import java.util.function.Consumer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_239;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_2886;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import net.minecraft.class_2828.class_2831;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.AsyncManager;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventSync;
import neva.ware.features.cmd.Command;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.combat.Aura;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.BooleanSettingGroup;
import neva.ware.utility.Timer;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.PlayerUtility;

public class MiddleClick extends Module {
   private final Setting<MiddleClick.Action> onBlock = new Setting<>("OnBlock", MiddleClick.Action.Xp);
   private final Setting<MiddleClick.Action> onAir = new Setting<>("OnAir", MiddleClick.Action.Pearl);
   private final Setting<MiddleClick.Action> onEntity = new Setting<>("OnEntity", MiddleClick.Action.Friend);
   private final Setting<MiddleClick.Action> onFlying = new Setting<>("OnElytra", MiddleClick.Action.Firework);
   private final Setting<Boolean> silent = new Setting<>("Silent", true);
   private final Setting<Boolean> inventory = new Setting<>("Inventory", true);
   private final Setting<Integer> swapDelay = new Setting<>("SwapDelay", 100, 0, 1000, v -> !this.silent.getValue());
   private final Setting<BooleanSettingGroup> antiWaste = new Setting<>("AntiWaste", new BooleanSettingGroup(true));
   private final Setting<Integer> durability = new Setting<>("StopOn", 90, 0, 100).addToGroup(this.antiWaste);
   public final Setting<Boolean> antiPickUp = new Setting<>("AntiPickUp", true);
   private final Setting<Boolean> feetExp = new Setting<>("FeetXP", false);
   private static final Timer timer = new Timer();
   private String state = "none";

   public MiddleClick() {
      super("MiddleClick", Module.Category.MISC);
   }

   @Override
   public String getDisplayInfo() {
      return this.state;
   }

   @EventHandler
   private void onSync(EventSync event) {
      if (mc.field_1755 == null) {
         class_239 target = mc.field_1765;
         if (mc.field_1724.method_6128()) {
            if (mc.field_1690.field_1871.method_1434()) {
               this.onFlying.getValue().doAction(event);
            }

            this.state = this.onFlying.getValue().toString();
            return;
         }

         if (target instanceof class_3966 ehr && ehr.method_17782() instanceof class_1657) {
            if (mc.field_1690.field_1871.method_1434()) {
               this.onEntity.getValue().doAction(event);
            }

            this.state = this.onEntity.getValue().toString();
            return;
         }

         if (target instanceof class_3965 bhr) {
            if (mc.field_1687.method_22347(bhr.method_17777())) {
               if (mc.field_1690.field_1871.method_1434()) {
                  this.onAir.getValue().doAction(event);
               }

               this.state = this.onAir.getValue().toString();
            } else {
               if (mc.field_1690.field_1871.method_1434()) {
                  this.onBlock.getValue().doAction(event);
               }

               this.state = this.onBlock.getValue().toString();
            }

            return;
         }

         if (mc.field_1690.field_1871.method_1434()) {
            this.onAir.getValue().doAction(event);
         }

         this.state = this.onAir.getValue().toString();
      }
   }

   private static boolean needXp() {
      for (class_1799 stack : mc.field_1724.method_5661()) {
         if (PlayerUtility.calculatePercentage(stack) < (float)ModuleManager.middleClick.durability.getValue().intValue()) {
            return true;
         }
      }

      return false;
   }

   private static int getHpSlot() {
      for (int i = 0; i < 9; i++) {
         if (isStackPotion(mc.field_1724.method_31548().method_5438(i))) {
            return i;
         }
      }

      return -1;
   }

   private static int findHpInInventory() {
      for (int i = 36; i >= 0; i--) {
         if (isStackPotion(mc.field_1724.method_31548().method_5438(i))) {
            return i < 9 ? i + 36 : i;
         }
      }

      return -1;
   }

   private static boolean isStackPotion(class_1799 stack) {
      if (stack == null) {
         return false;
      } else {
         if (stack.method_7909() == class_1802.field_8436) {
            class_1844 potionContentsComponent = (class_1844)stack.method_57825(class_9334.field_49651, class_1844.field_49274);

            for (class_1293 effect : potionContentsComponent.method_57397()) {
               if (effect.method_5579().comp_349() == class_1294.field_5915.comp_349()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static enum Action {
      HealingPot(
         e -> {
            if (MiddleClick.timer.every(250L)) {
               if (MiddleClick.getHpSlot() != -1) {
                  int hpSlot = MiddleClick.getHpSlot();
                  int originalSlot = Module.mc.field_1724.method_31548().field_7545;
                  if (hpSlot != -1) {
                     InventoryUtility.switchTo(hpSlot);
                     InteractionUtility.sendSequencedPacket(
                        id -> new class_2886(class_1268.field_5808, id, Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455())
                     );
                     InventoryUtility.switchTo(originalSlot);
                  }
               } else {
                  int hpSlot = MiddleClick.findHpInInventory();
                  if (hpSlot != -1) {
                     Module.mc
                        .field_1761
                        .method_2906(
                           Module.mc.field_1724.field_7512.field_7763,
                           hpSlot,
                           Module.mc.field_1724.method_31548().field_7545,
                           class_1713.field_7791,
                           Module.mc.field_1724
                        );
                     InteractionUtility.sendSequencedPacket(
                        id -> new class_2886(class_1268.field_5808, id, Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455())
                     );
                     Module.mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
                     Module.mc
                        .field_1761
                        .method_2906(
                           Module.mc.field_1724.field_7512.field_7763,
                           hpSlot,
                           Module.mc.field_1724.method_31548().field_7545,
                           class_1713.field_7791,
                           Module.mc.field_1724
                        );
                  }
               }
            }
         }
      ),
      Firework(
         e -> {
            if (MiddleClick.timer.every(250L)) {
               int epSlot1 = InventoryUtility.findItemInHotBar(class_1802.field_8639).slot();
               if (ModuleManager.middleClick.silent.getValue()) {
                  if (!ModuleManager.middleClick.inventory.getValue() || ModuleManager.middleClick.inventory.getValue() && epSlot1 != -1) {
                     int originalSlot = Module.mc.field_1724.method_31548().field_7545;
                     if (epSlot1 != -1) {
                        Module.mc.field_1724.method_31548().field_7545 = epSlot1;
                        Module.mc.field_1724.field_3944.method_52787(new class_2868(epSlot1));
                        InteractionUtility.sendSequencedPacket(
                           id -> new class_2886(class_1268.field_5808, id, Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455())
                        );
                        Module.mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
                        Module.mc.field_1724.method_31548().field_7545 = originalSlot;
                        Module.mc.field_1724.field_3944.method_52787(new class_2868(originalSlot));
                     }
                  } else {
                     int epSlot = InventoryUtility.findItemInInventory(class_1802.field_8639).slot();
                     if (epSlot != -1) {
                        Module.mc
                           .field_1761
                           .method_2906(
                              Module.mc.field_1724.field_7512.field_7763,
                              epSlot,
                              Module.mc.field_1724.method_31548().field_7545,
                              class_1713.field_7791,
                              Module.mc.field_1724
                           );
                        InteractionUtility.sendSequencedPacket(
                           id -> new class_2886(class_1268.field_5808, id, Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455())
                        );
                        Module.mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
                        Module.mc
                           .field_1761
                           .method_2906(
                              Module.mc.field_1724.field_7512.field_7763,
                              epSlot,
                              Module.mc.field_1724.method_31548().field_7545,
                              class_1713.field_7791,
                              Module.mc.field_1724
                           );
                     }
                  }
               } else if (!ModuleManager.middleClick.inventory.getValue() || ModuleManager.middleClick.inventory.getValue() && epSlot1 != -1) {
                  if (epSlot1 != -1) {
                     new MiddleClick.PearlThread(
                           Module.mc.field_1724, epSlot1, Module.mc.field_1724.method_31548().field_7545, ModuleManager.middleClick.swapDelay.getValue(), false
                        )
                        .start();
                  }
               } else {
                  int epSlot = InventoryUtility.findItemInInventory(class_1802.field_8639).slot();
                  if (epSlot != -1) {
                     new MiddleClick.PearlThread(
                           Module.mc.field_1724, epSlot, Module.mc.field_1724.method_31548().field_7545, ModuleManager.middleClick.swapDelay.getValue(), true
                        )
                        .start();
                  }
               }
            }
         }
      ),
      Friend(
         e -> {
            if (MiddleClick.timer.every(800L) && Module.mc.field_1765 instanceof class_3966 ehr && ehr.method_17782() instanceof class_1657) {
               if (Managers.FRIEND.isFriend(ehr.method_17782().method_5477().getString())) {
                  Managers.FRIEND.removeFriend(ehr.method_17782().method_5477().getString());
                  Command.sendMessage(
                     ClientSettings.isRu()
                        ? "§b" + ehr.method_17782().method_5477().getString() + "§r удален из друзей!"
                        : "§b" + ehr.method_17782().method_5477().getString() + "§r removed from friends!"
                  );
               } else {
                  Managers.FRIEND.addFriend(ehr.method_17782().method_5477().getString());
                  Command.sendMessage(
                     ClientSettings.isRu()
                        ? "Добавлен друг §b" + ehr.method_17782().method_5477().getString()
                        : "Added friend §b" + ehr.method_17782().method_5477().getString()
                  );
               }
            }
         }
      ),
      Pearl(
         e -> {
            if (MiddleClick.timer.every(500L)) {
               if (ModuleManager.aura.isEnabled() && Aura.target != null) {
                  Module.mc
                     .field_1724
                     .field_3944
                     .method_52787(
                        new class_2831(Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455(), Module.mc.field_1724.method_24828())
                     );
               }

               int epSlot1 = InventoryUtility.findItemInHotBar(class_1802.field_8634).slot();
               if (ModuleManager.middleClick.silent.getValue()) {
                  if (!ModuleManager.middleClick.inventory.getValue() || ModuleManager.middleClick.inventory.getValue() && epSlot1 != -1) {
                     int originalSlot = Module.mc.field_1724.method_31548().field_7545;
                     if (epSlot1 != -1) {
                        Module.mc.field_1724.method_31548().field_7545 = epSlot1;
                        Module.mc.field_1724.field_3944.method_52787(new class_2868(epSlot1));
                        InteractionUtility.sendSequencedPacket(
                           id -> new class_2886(class_1268.field_5808, id, Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455())
                        );
                        Module.mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
                        Module.mc.field_1724.method_31548().field_7545 = originalSlot;
                        Module.mc.field_1724.field_3944.method_52787(new class_2868(originalSlot));
                     }
                  } else {
                     int epSlot = InventoryUtility.findItemInInventory(class_1802.field_8634).slot();
                     if (epSlot != -1) {
                        Module.mc
                           .field_1761
                           .method_2906(
                              Module.mc.field_1724.field_7512.field_7763,
                              epSlot,
                              Module.mc.field_1724.method_31548().field_7545,
                              class_1713.field_7791,
                              Module.mc.field_1724
                           );
                        InteractionUtility.sendSequencedPacket(
                           id -> new class_2886(class_1268.field_5808, id, Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455())
                        );
                        Module.mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
                        Module.mc
                           .field_1761
                           .method_2906(
                              Module.mc.field_1724.field_7512.field_7763,
                              epSlot,
                              Module.mc.field_1724.method_31548().field_7545,
                              class_1713.field_7791,
                              Module.mc.field_1724
                           );
                     }
                  }
               } else if (!ModuleManager.middleClick.inventory.getValue() || ModuleManager.middleClick.inventory.getValue() && epSlot1 != -1) {
                  if (epSlot1 != -1) {
                     new MiddleClick.PearlThread(
                           Module.mc.field_1724, epSlot1, Module.mc.field_1724.method_31548().field_7545, ModuleManager.middleClick.swapDelay.getValue(), false
                        )
                        .start();
                  }
               } else {
                  int epSlot = InventoryUtility.findItemInInventory(class_1802.field_8634).slot();
                  if (epSlot != -1) {
                     new MiddleClick.PearlThread(
                           Module.mc.field_1724, epSlot, Module.mc.field_1724.method_31548().field_7545, ModuleManager.middleClick.swapDelay.getValue(), true
                        )
                        .start();
                  }
               }
            }
         }
      ),
      Xp(
         e -> {
            if (ModuleManager.middleClick.feetExp.getValue() && (!ModuleManager.middleClick.antiWaste.getValue().isEnabled() || MiddleClick.needXp())) {
               Module.mc.field_1724.method_36457(90.0F);
            }

            e.addPostAction(
               () -> {
                  if (!ModuleManager.middleClick.antiWaste.getValue().isEnabled() || MiddleClick.needXp()) {
                     if (Module.mc.field_1690.field_1871.method_1434()) {
                        int slot = InventoryUtility.findItemInHotBar(class_1802.field_8287).slot();
                        if (slot != -1) {
                           int lastSlot = Module.mc.field_1724.method_31548().field_7545;
                           InventoryUtility.switchTo(slot);
                           InteractionUtility.sendSequencedPacket(
                              id -> new class_2886(class_1268.field_5808, id, Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455())
                           );
                           if (ModuleManager.middleClick.silent.getValue()) {
                              InventoryUtility.switchTo(lastSlot);
                           }
                        }
                     }
                  }
               }
            );
         }
      ),
      None(e -> {
      });

      private final Consumer<EventSync> r;

      private Action(Consumer<EventSync> r) {
         this.r = r;
      }

      public void doAction(EventSync e) {
         this.r.accept(e);
      }
   }

   public static class PearlThread extends Thread {
      public class_746 player;
      int epSlot;
      int originalSlot;
      int delay;
      boolean inv;

      public PearlThread(class_746 entityPlayer, int epSlot, int originalSlot, int delay, boolean inventory) {
         this.player = entityPlayer;
         this.epSlot = epSlot;
         this.originalSlot = originalSlot;
         this.delay = delay;
         this.inv = inventory;
      }

      @Override
      public void run() {
         if (!this.inv) {
            InventoryUtility.switchTo(this.epSlot);
            AsyncManager.sleep(this.delay);
            InteractionUtility.sendSequencedPacket(
               id -> new class_2886(class_1268.field_5808, id, Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455())
            );
            Module.mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
            AsyncManager.sleep(this.delay);
            InventoryUtility.switchTo(this.originalSlot);
         } else {
            Module.mc
               .field_1761
               .method_2906(Module.mc.field_1724.field_7512.field_7763, this.epSlot, this.originalSlot, class_1713.field_7791, Module.mc.field_1724);
            AsyncManager.sleep(this.delay);
            if (ModuleManager.aura.isEnabled() && Aura.target != null) {
               Module.mc
                  .field_1724
                  .field_3944
                  .method_52787(new class_2831(Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455(), Module.mc.field_1724.method_24828()));
            }

            InteractionUtility.sendSequencedPacket(
               id -> new class_2886(class_1268.field_5808, id, Module.mc.field_1724.method_36454(), Module.mc.field_1724.method_36455())
            );
            Module.mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
            AsyncManager.sleep(this.delay);
            Module.mc
               .field_1761
               .method_2906(Module.mc.field_1724.field_7512.field_7763, this.epSlot, this.originalSlot, class_1713.field_7791, Module.mc.field_1724);
         }

         super.run();
      }
   }
}
