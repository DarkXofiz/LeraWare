package neva.ware.features.modules.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1511;
import net.minecraft.class_1541;
import net.minecraft.class_1548;
import net.minecraft.class_1657;
import net.minecraft.class_1701;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1819;
import net.minecraft.class_1829;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2604;
import net.minecraft.class_2626;
import net.minecraft.class_2815;
import net.minecraft.class_2838;
import net.minecraft.class_2846;
import net.minecraft.class_2848;
import net.minecraft.class_2868;
import net.minecraft.class_476;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_2848.class_2849;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventSync;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.movement.Blink;
import neva.ware.injection.accesors.IMinecraftClient;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.Bind;
import neva.ware.setting.impl.BooleanSettingGroup;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.Timer;
import neva.ware.utility.math.PredictUtility;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.SearchInvResult;
import neva.ware.utility.world.ExplosionUtility;
import org.jetbrains.annotations.NotNull;

public final class AutoTotem extends Module {
    private final Setting<AutoTotem.Mode> mode = new Setting<>("Mode", AutoTotem.Mode.Matrix);
    private final Setting<AutoTotem.OffHand> offhand = new Setting<>("Item", AutoTotem.OffHand.Totem);
    private final Setting<BooleanSettingGroup> bindSwap = new Setting<>(
        "BindSwap", new BooleanSettingGroup(false), v -> this.offhand.is(AutoTotem.OffHand.Totem)
    );
    private final Setting<Bind> swapButton = new Setting<>("SwapButton", new Bind(280, false, false)).addToGroup(this.bindSwap);
    private final Setting<AutoTotem.Swap> swapMode = new Setting<>("Swap", AutoTotem.Swap.GappleShield).addToGroup(this.bindSwap);
    private final Setting<Boolean> ncpStrict = new Setting<>("NCPStrict", false);
    private final Setting<Float> healthF = new Setting<>("HP", 16.0F, 0.0F, 36.0F);
    private final Setting<Float> healthS = new Setting<>("ShieldGappleHp", 16.0F, 0.0F, 20.0F, v -> this.offhand.getValue() == AutoTotem.OffHand.Shield);
    private final Setting<Boolean> calcAbsorption = new Setting<>("CalcAbsorption", true);
    private final Setting<Boolean> stopMotion = new Setting<>("StopMotion", false);
    private final Setting<Boolean> resetAttackCooldown = new Setting<>("ResetAttackCooldown", false);
    private final Setting<SettingGroup> safety = new Setting<>("Safety", new SettingGroup(false, 0));
    private final Setting<Boolean> hotbarFallBack = new Setting<>("HotbarFallback", false).addToGroup(this.safety);
    private final Setting<Boolean> fallBackCalc = new Setting<>("FallBackCalc", true, v -> this.hotbarFallBack.getValue()).addToGroup(this.safety);
    private final Setting<Boolean> onElytra = new Setting<>("OnElytra", true).addToGroup(this.safety);
    private final Setting<Boolean> onFall = new Setting<>("OnFall", true).addToGroup(this.safety);
    private final Setting<Boolean> onCrystal = new Setting<>("OnCrystal", true).addToGroup(this.safety);
    private final Setting<Boolean> onObsidianPlace = new Setting<>("OnObsidianPlace", false).addToGroup(this.safety);
    private final Setting<Boolean> onCrystalInHand = new Setting<>("OnCrystalInHand", false).addToGroup(this.safety);
    private final Setting<Boolean> onMinecartTnt = new Setting<>("OnMinecartTNT", true).addToGroup(this.safety);
    private final Setting<Boolean> onCreeper = new Setting<>("OnCreeper", true).addToGroup(this.safety);
    private final Setting<Boolean> onAnchor = new Setting<>("OnAnchor", true).addToGroup(this.safety);
    private final Setting<Boolean> onTnt = new Setting<>("OnTNT", true).addToGroup(this.safety);
    public final Setting<AutoTotem.RCGap> rcGap = new Setting<>("RightClickGapple", AutoTotem.RCGap.Off);
    private final Setting<Boolean> crappleSpoof = new Setting<>("CrappleSpoof", true, v -> this.offhand.getValue() == AutoTotem.OffHand.GApple);
    private int delay;
    private Timer bindDelay = new Timer();
    private class_1792 prevItem;

    public AutoTotem() {
        super("AutoTotem", Module.Category.COMBAT);
    }

    @EventHandler
    public void onSync(EventSync e) {
        this.swapTo(this.getItemSlot());
        if (this.rcGap.not(AutoTotem.RCGap.Off)
            && mc.field_1724.method_6047().method_7909() instanceof class_1829
            && mc.field_1690.field_1904.method_1434()
            && !mc.field_1724.method_6115()) {
            ((IMinecraftClient)(Object)mc).idoItemUse();
        }

        this.delay--;
    }

    @EventHandler
    public void onPacketReceive(@NotNull PacketEvent.Receive e) {
        if (e.getPacket() instanceof class_2604 spawn
            && spawn.method_11169() == class_1299.field_6110
            && this.getPlayerPos().method_1028(spawn.method_11175(), spawn.method_11174(), spawn.method_11176()) < 36.0) {
            if (this.hotbarFallBack.getValue()) {
                if (this.fallBackCalc.getValue()
                    && ExplosionUtility.getExplosionDamageWPredict(
                            new class_243(spawn.method_11175(), spawn.method_11174(), spawn.method_11176()),
                            mc.field_1724,
                            PredictUtility.createBox(this.getPlayerPos(), mc.field_1724),
                            false
                        )
                        < this.getTriggerHealth() + 4.0F) {
                    return;
                }

                this.runInstant();
            }

            if (this.onCrystal.getValue()
                && this.getTriggerHealth()
                        - ExplosionUtility.getExplosionDamageWPredict(
                            new class_243(spawn.method_11175(), spawn.method_11174(), spawn.method_11176()),
                            mc.field_1724,
                            PredictUtility.createBox(this.getPlayerPos(), mc.field_1724),
                            false
                        )
                    < 0.5) {
                int slot = -1;

                for (int i = 9; i < 45; i++) {
                    if (mc.field_1724.method_31548().method_5438(i >= 36 ? i - 36 : i).method_7909().equals(class_1802.field_8288)) {
                        slot = i >= 36 ? i - 36 : i;
                        break;
                    }
                }

                this.swapTo(slot);
                this.debug("spawn switch");
            }
        }

        if (e.getPacket() instanceof class_2626 blockUpdate
            && blockUpdate.method_11308().method_26204() == class_2246.field_10540
            && this.onObsidianPlace.getValue()
            && this.getPlayerPos().method_1025(blockUpdate.method_11309().method_46558()) < 36.0
            && this.delay <= 0) {
            this.runInstant();
        }
    }

    private float getTriggerHealth() {
        return mc.field_1724.method_6032() + (this.calcAbsorption.getValue() ? mc.field_1724.method_6067() : 0.0F);
    }

    private void runInstant() {
        SearchInvResult hotbarResult = InventoryUtility.findItemInHotBar(class_1802.field_8288);
        SearchInvResult invResult = InventoryUtility.findItemInInventory(class_1802.field_8288);
        if (hotbarResult.found()) {
            hotbarResult.switchTo();
            this.delay = 20;
        } else if (invResult.found()) {
            int slot = invResult.slot() >= 36 ? invResult.slot() - 36 : invResult.slot();
            if (!this.hotbarFallBack.getValue()) {
                this.swapTo(slot);
            } else {
                mc.field_1761.method_2916(slot);
            }

            this.delay = 20;
        }
    }

    public void swapTo(int slot) {
        if (slot != -1 && this.delay <= 0) {
            if (mc.field_1755 instanceof class_476) {
                return;
            }

            if (this.stopMotion.getValue()) {
                mc.field_1724.method_18800(0.0, mc.field_1724.method_18798().method_10214(), 0.0);
            }

            int nearestSlot = findNearestCurrentItem();
            int prevCurrentItem = mc.field_1724.method_31548().field_7545;
            if (slot >= 9) {
                switch ((AutoTotem.Mode)this.mode.getValue()) {
                    case Default:
                        if (this.ncpStrict.getValue()) {
                            this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12985));
                        }

                        clickSlot(slot);
                        clickSlot(45);
                        clickSlot(slot);
                        this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
                        break;
                    case Alternative:
                        if (this.ncpStrict.getValue()) {
                            this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12985));
                        }

                        clickSlot(slot, nearestSlot, class_1713.field_7791);
                        clickSlot(45, nearestSlot, class_1713.field_7791);
                        clickSlot(slot, nearestSlot, class_1713.field_7791);
                        this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
                        break;
                    case Matrix:
                        if (this.ncpStrict.getValue()) {
                            this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12985));
                        }

                        mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, slot, nearestSlot, class_1713.field_7791, mc.field_1724);
                        this.debug(slot + " " + nearestSlot);
                        this.sendPacket(new class_2868(nearestSlot));
                        mc.field_1724.method_31548().field_7545 = nearestSlot;
                        class_1799 itemstack = mc.field_1724.method_6079();
                        mc.field_1724.method_6122(class_1268.field_5810, mc.field_1724.method_6047());
                        mc.field_1724.method_6122(class_1268.field_5808, itemstack);
                        this.sendPacket(new class_2846(class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
                        this.sendPacket(new class_2868(prevCurrentItem));
                        mc.field_1724.method_31548().field_7545 = prevCurrentItem;
                        mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, slot, nearestSlot, class_1713.field_7791, mc.field_1724);
                        this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
                        if (this.resetAttackCooldown.getValue()) {
                            mc.field_1724.method_7350();
                        }
                        break;
                    case MatrixPick:
                        this.debug(slot + " pick");
                        this.sendPacket(new class_2838(slot));
                        this.sendPacket(new class_2846(class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
                        int prevSlot = mc.field_1724.method_31548().field_7545;
                        Managers.ASYNC.run(() -> mc.field_1724.method_31548().field_7545 = prevSlot, 300L);
                        break;
                    case NewVersion:
                        this.debug(slot + " swap");
                        mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, slot, 40, class_1713.field_7791, mc.field_1724);
                        this.sendPacket(new class_2815(mc.field_1724.field_7512.field_7763));
                }
            } else {
                this.sendPacket(new class_2868(slot));
                mc.field_1724.method_31548().field_7545 = slot;
                this.debug(slot + " select");
                this.sendPacket(new class_2846(class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
                this.sendPacket(new class_2868(prevCurrentItem));
                mc.field_1724.method_31548().field_7545 = prevCurrentItem;
                if (this.resetAttackCooldown.getValue()) {
                    mc.field_1724.method_7350();
                }
            }

            this.delay = (int)(2.0F + Managers.SERVER.getPing() / 25.0F);
        }
    }

    public static int findNearestCurrentItem() {
        int i = mc.field_1724.method_31548().field_7545;
        if (i == 8) {
            return 7;
        } else {
            return i == 0 ? 1 : i - 1;
        }
    }

    public int getItemSlot() {
        if (mc.field_1724 != null && mc.field_1687 != null) {
            SearchInvResult gapple = InventoryUtility.findItemInInventory(class_1802.field_8367);
            SearchInvResult crapple = InventoryUtility.findItemInInventory(class_1802.field_8463);
            SearchInvResult shield = InventoryUtility.findItemInInventory(class_1802.field_8255);
            class_1792 offHandItem = mc.field_1724.method_6079().method_7909();
            int itemSlot = -1;
            class_1792 item = null;
            switch ((AutoTotem.OffHand)this.offhand.getValue()) {
                case Totem:
                    if (offHandItem != class_1802.field_8288 && !mc.field_1724.method_6079().method_7960()) {
                        this.prevItem = offHandItem;
                    }

                    item = this.prevItem;
                    if (this.bindSwap.getValue().isEnabled() && this.isKeyPressed(this.swapButton) && this.bindDelay.every(250L)) {
                        switch ((AutoTotem.Swap)this.swapMode.getValue()) {
                            case GappleShield:
                                if (!mc.field_1724.method_6079().method_7960() && offHandItem != class_1802.field_8255) {
                                    item = class_1802.field_8255;
                                    break;
                                }

                                item = class_1802.field_8463;
                                break;
                            case BallShield:
                                if (!mc.field_1724.method_6079().method_7960() && offHandItem != class_1802.field_8255) {
                                    item = class_1802.field_8255;
                                    break;
                                }

                                item = class_1802.field_8575;
                                break;
                            case GappleBall:
                                if (!mc.field_1724.method_6079().method_7960() && offHandItem != class_1802.field_8463) {
                                    item = class_1802.field_8463;
                                    break;
                                }

                                item = class_1802.field_8575;
                                break;
                            case BallTotem:
                                if (!mc.field_1724.method_6079().method_7960() && offHandItem != class_1802.field_8288) {
                                    item = class_1802.field_8288;
                                } else {
                                    item = class_1802.field_8575;
                                }
                        }

                        this.prevItem = item;
                    }
                    break;
                case Crystal:
                    item = class_1802.field_8301;
                    break;
                case GApple:
                    if (this.crappleSpoof.getValue()) {
                        if (mc.field_1724.method_6059(class_1294.field_5898) && mc.field_1724.method_6112(class_1294.field_5898).method_5578() > 2) {
                            if (crapple.found() || offHandItem == class_1802.field_8463) {
                                item = class_1802.field_8463;
                            } else if (gapple.found() || offHandItem == class_1802.field_8367) {
                                item = class_1802.field_8367;
                            }
                        } else if (gapple.found() || offHandItem == class_1802.field_8367) {
                            item = class_1802.field_8367;
                        } else if (crapple.found() || offHandItem == class_1802.field_8463) {
                            item = class_1802.field_8463;
                        }
                    } else if (crapple.found() || offHandItem == class_1802.field_8463) {
                        item = class_1802.field_8463;
                    } else if (gapple.found() || offHandItem == class_1802.field_8367) {
                        item = class_1802.field_8367;
                    }
                    break;
                case Shield:
                    if (shield.found() || offHandItem == class_1802.field_8255) {
                        if (this.getTriggerHealth() <= this.healthS.getValue()) {
                            if (crapple.found() || offHandItem == class_1802.field_8463) {
                                item = class_1802.field_8463;
                            } else if (gapple.found() || offHandItem == class_1802.field_8367) {
                                item = class_1802.field_8367;
                            }
                        } else if (!mc.field_1724.method_7357().method_7904(class_1802.field_8255)) {
                            item = class_1802.field_8255;
                        } else if (crapple.found() || offHandItem == class_1802.field_8463) {
                            item = class_1802.field_8463;
                        } else if (gapple.found() || offHandItem == class_1802.field_8367) {
                            item = class_1802.field_8367;
                        }
                    } else if (crapple.found() || offHandItem == class_1802.field_8463) {
                        item = class_1802.field_8463;
                    }
            }

            if (this.getTriggerHealth() <= this.healthF.getValue()
                && (InventoryUtility.findItemInInventory(class_1802.field_8288).found() || offHandItem == class_1802.field_8288)) {
                item = class_1802.field_8288;
            }

            if (!this.rcGap.is(AutoTotem.RCGap.Off)
                && mc.field_1724.method_6047().method_7909() instanceof class_1829
                && mc.field_1690.field_1904.method_1434()
                && !(offHandItem instanceof class_1819)
                && (this.rcGap.is(AutoTotem.RCGap.Always) || this.rcGap.is(AutoTotem.RCGap.OnlySafe) && this.getTriggerHealth() > this.healthF.getValue())) {
                if (crapple.found() || offHandItem == class_1802.field_8463) {
                    item = class_1802.field_8463;
                }

                if (gapple.found() || offHandItem == class_1802.field_8367) {
                    item = class_1802.field_8367;
                }
            }

            if (this.onFall.getValue() && this.getTriggerHealth() - ((mc.field_1724.field_6017 - 3.0F) / 2.0F + 3.5F) < 0.5) {
                item = class_1802.field_8288;
            }

            if (this.onElytra.getValue() && mc.field_1724.method_6128()) {
                item = class_1802.field_8288;
            }

            if (this.onCrystalInHand.getValue()) {
                for (class_1657 pl : Managers.ASYNC.getAsyncPlayers()) {
                    if (!Managers.FRIEND.isFriend(pl)
                        && pl != mc.field_1724
                        && this.getPlayerPos().method_1025(pl.method_19538()) < 36.0
                        && (
                            pl.method_6047().method_7909() == class_1802.field_8281
                                || pl.method_6047().method_7909() == class_1802.field_8301
                                || pl.method_6079().method_7909() == class_1802.field_8281
                                || pl.method_6079().method_7909() == class_1802.field_8301
                        )) {
                        item = class_1802.field_8288;
                    }
                }
            }

            for (class_1297 entity : mc.field_1687.method_18112()) {
                if (entity != null && entity.method_5805() && !(this.getPlayerPos().method_1025(entity.method_19538()) > 36.0)) {
                    if (this.onCrystal.getValue()
                        && entity instanceof class_1511
                        && this.getTriggerHealth()
                                - ExplosionUtility.getExplosionDamageWPredict(
                                    entity.method_19538(), mc.field_1724, PredictUtility.createBox(this.getPlayerPos(), mc.field_1724), false
                                )
                            < 0.5) {
                        item = class_1802.field_8288;
                        break;
                    }

                    if (this.onTnt.getValue() && entity instanceof class_1541) {
                        item = class_1802.field_8288;
                        break;
                    }

                    if (this.onMinecartTnt.getValue() && entity instanceof class_1701) {
                        item = class_1802.field_8288;
                        break;
                    }

                    if (this.onCreeper.getValue() && entity instanceof class_1548) {
                        item = class_1802.field_8288;
                        break;
                    }
                }
            }

            if (this.onAnchor.getValue()) {
                for (int x = -6; x <= 6; x++) {
                    for (int y = -6; y <= 6; y++) {
                        for (int z = -6; z <= 6; z++) {
                            class_2338 bp = new class_2338(x, y, z);
                            if (mc.field_1687.method_8320(bp).method_26204() == class_2246.field_23152) {
                                item = class_1802.field_8288;
                                break;
                            }
                        }
                    }
                }
            }

            for (int i = 9; i < 45; i++) {
                if (mc.field_1724.method_6079().method_7909() == item) {
                    return -1;
                }

                if (mc.field_1724.method_31548().method_5438(i >= 36 ? i - 36 : i).method_7909().equals(item)) {
                    itemSlot = i >= 36 ? i - 36 : i;
                    break;
                }
            }

            return item == mc.field_1724.method_6047().method_7909() && mc.field_1690.field_1904.method_1434() ? -1 : itemSlot;
        } else {
            return -1;
        }
    }

    private class_243 getPlayerPos() {
        return ModuleManager.blink.isEnabled() ? Blink.lastPos : mc.field_1724.method_19538();
    }

    private static enum Mode {
        Default,
        Alternative,
        Matrix,
        MatrixPick,
        NewVersion;
    }

    private static enum OffHand {
        Totem,
        Crystal,
        GApple,
        Shield;
    }

    public static enum RCGap {
        Off,
        Always,
        OnlySafe;
    }

    private static enum Swap {
        GappleShield,
        BallShield,
        GappleBall,
        BallTotem;
    }
}
