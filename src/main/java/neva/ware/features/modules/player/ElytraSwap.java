package neva.ware.features.modules.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2815;
import net.minecraft.class_2848;
import net.minecraft.class_2879;
import net.minecraft.class_2886;
import net.minecraft.class_2848.class_2849;
import neva.ware.core.Managers;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.Bind;
import neva.ware.utility.Timer;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.SearchInvResult;

public class ElytraSwap extends Module {
    private final Setting<Boolean> delay = new Setting<>("Delay", false);
    private final Setting<ElytraSwap.Mode> mode = new Setting<>("Mode", ElytraSwap.Mode.Enable);
    private final Setting<Bind> switchButton = new Setting<>("SwitchButton", new Bind(-1, false, false), v -> this.mode.getValue() == ElytraSwap.Mode.Bind);
    private final Setting<Bind> fireWorkButton = new Setting<>("FireWorkButton", new Bind(-1, false, false), v -> this.mode.getValue() == ElytraSwap.Mode.Bind);
    private final Setting<Boolean> startFireWork = new Setting<>("StartFireWork", true, v -> this.mode.getValue() == ElytraSwap.Mode.Bind);
    private final Setting<ElytraSwap.FireWorkMode> fireWorkMode = new Setting<>(
        "FireWorkMode", ElytraSwap.FireWorkMode.Normal, v -> this.mode.getValue() == ElytraSwap.Mode.Bind
    );
    private final Timer switchTimer = new Timer();
    private final Timer fireworkTimer = new Timer();
    public static boolean swapping = false;

    public ElytraSwap() {
        super("ElytraSwap", Module.Category.PLAYER);
    }

    @Override
    public void onEnable() {
        if (this.mode.getValue() == ElytraSwap.Mode.Enable) {
            this.swapChest(true);
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == ElytraSwap.Mode.Bind && mc.field_1755 == null) {
            if (this.switchButton.getValue().getKey() != -1 && this.isKeyPressed(this.switchButton.getValue().getKey()) && this.switchTimer.every(500L)) {
                this.swapChest(false);
            }

            if (this.fireWorkButton.getValue().getKey() != -1
                && this.isKeyPressed(this.fireWorkButton.getValue().getKey())
                && this.fireworkTimer.every(500L)
                && mc.field_1724.method_6128()) {
                this.useFireWork();
            }
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.SendPost e) {
        if (e.getPacket() instanceof class_2848 command
            && command.method_12365() == class_2849.field_12982
            && this.mode.getValue() == ElytraSwap.Mode.Bind
            && this.startFireWork.getValue()) {
            this.useFireWork();
        }
    }

    public void useFireWork() {
        SearchInvResult hotbarFireWorkResult = InventoryUtility.findItemInHotBar(class_1802.field_8639);
        SearchInvResult fireWorkResult = InventoryUtility.findItemInInventory(class_1802.field_8639);
        InventoryUtility.saveSlot();
        if (hotbarFireWorkResult.found()) {
            hotbarFireWorkResult.switchTo();
        } else {
            if (!fireWorkResult.found()) {
                this.sendMessage(ClientSettings.isRu() ? "У тебя нет фейерверков!" : "You've got no fireworks!");
                return;
            }

            mc.field_1761
                .method_2906(
                    mc.field_1724.field_7512.field_7763, fireWorkResult.slot(), mc.field_1724.method_31548().field_7545, class_1713.field_7791, mc.field_1724
                );
            this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
        }

        this.sendSequencedPacket(id -> new class_2886(class_1268.field_5808, id, mc.field_1724.method_36454(), mc.field_1724.method_36455()));
        this.sendPacket(new class_2879(class_1268.field_5808));
        if (this.fireWorkMode.getValue() == ElytraSwap.FireWorkMode.Silent) {
            InventoryUtility.returnSlot();
            if (!hotbarFireWorkResult.found()) {
                mc.field_1761
                    .method_2906(
                        mc.field_1724.field_7512.field_7763,
                        fireWorkResult.slot(),
                        mc.field_1724.method_31548().field_7545,
                        class_1713.field_7791,
                        mc.field_1724
                    );
                this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
            }
        }
    }

    public static int getChestPlateSlot() {
        class_1792[] items = new class_1792[]{
            class_1802.field_22028, class_1802.field_8058, class_1802.field_8873, class_1802.field_8523, class_1802.field_8678, class_1802.field_8577
        };

        for (class_1792 item : items) {
            SearchInvResult slot = InventoryUtility.findItemInInventory(item);
            if (slot.found()) {
                return slot.slot();
            }
        }

        return -1;
    }

    private void swapChest(boolean disable) {
        SearchInvResult result = InventoryUtility.findItemInInventory(class_1802.field_8833);
        if (mc.field_1724.method_31548().method_5438(38).method_7909() == class_1802.field_8833) {
            int slot = getChestPlateSlot();
            if (slot == -1) {
                if (disable) {
                    this.disable(ClientSettings.isRu() ? "У тебя нет нагрудника!" : "You don't have a chestplate!");
                } else {
                    this.sendMessage(ClientSettings.isRu() ? "У тебя нет нагрудника!" : "You don't have a chestplate!");
                }

                return;
            }

            if (this.delay.getValue()) {
                Managers.ASYNC.run(() -> {
                    swapping = true;
                    clickSlot(slot);

                    try {
                        Thread.sleep(200L);
                    } catch (Exception var4) {
                    }

                    clickSlot(6);

                    try {
                        Thread.sleep(200L);
                    } catch (Exception var3x) {
                    }

                    clickSlot(slot);
                    this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
                    swapping = false;
                });
            } else {
                clickSlot(slot);
                clickSlot(6);
                clickSlot(slot);
                this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
            }
        } else {
            if (!result.found()) {
                if (disable) {
                    this.disable(ClientSettings.isRu() ? "У тебя нет элитры!" : "You don't have an elytra!");
                } else {
                    this.sendMessage(ClientSettings.isRu() ? "У тебя нет элитры!" : "You don't have an elytra!");
                }

                return;
            }

            if (this.delay.getValue()) {
                new Thread(() -> {
                    swapping = true;
                    clickSlot(result.slot());

                    try {
                        Thread.sleep(200L);
                    } catch (Exception var4) {
                    }

                    clickSlot(6);

                    try {
                        Thread.sleep(200L);
                    } catch (Exception var3x) {
                    }

                    clickSlot(result.slot());
                    this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
                    if (this.startFireWork.getValue() && mc.field_1724.field_6017 > 0.0F) {
                        this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12982));
                    }

                    swapping = false;
                }).start();
            } else {
                clickSlot(result.slot());
                clickSlot(6);
                clickSlot(result.slot());
                this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
                if (this.startFireWork.getValue() && mc.field_1724.field_6017 > 0.0F) {
                    this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12982));
                }
            }
        }

        if (disable) {
            this.disable(ClientSettings.isRu() ? "Свапнул! Отключаю.." : "Swapped! Disabling..");
        }
    }

    private static enum FireWorkMode {
        Silent,
        Normal;
    }

    private static enum Mode {
        Enable,
        Bind;
    }
}
