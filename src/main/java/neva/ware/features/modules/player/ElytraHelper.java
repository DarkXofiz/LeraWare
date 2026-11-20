package neva.ware.features.modules.player;

import java.util.Random;
import net.minecraft.class_1268;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2815;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_2886;
import net.minecraft.class_2846.class_2847;
import neva.ware.core.Managers;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.combat.Aura;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.Bind;
import neva.ware.utility.Timer;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.SearchInvResult;

public class ElytraHelper extends Module {
    private static ElytraHelper INSTANCE = new ElytraHelper();
    private final Setting<Bind> switchButton = new Setting<>("SwitchButton", new Bind(-1, false, false));
    private final Setting<Bind> fireWorkButton = new Setting<>("FireWorkButton", new Bind(-1, false, false));
    private final Setting<ElytraHelper.FireWorkMode> fireWorkMode;
    private final Setting<Boolean> autoFirework;
    private final Setting<Bind> legitFireworkKey;
    private final Setting<Boolean> slotReturn;
    private final Setting<Integer> delay;
    private final Setting<Boolean> returnDelay;
    private final Setting<Integer> returnDelayMs;
    private final Setting<Boolean> fireworkRandomize;
    private final Setting<Integer> fireworkRandomMin;
    private final Setting<Integer> fireworkRandomMax;
    private long currentFireworkDelayMs = -1L;
    private final Setting<Boolean> swing;
    private final Setting<Boolean> swapBeforeFirework;
    private final Setting<Integer> legit3Delay;
    private final Setting<Boolean> legit3Randomize;
    private final Setting<Integer> bypass1Delay;
    private final Setting<Boolean> bypass1Randomize;
    private final Setting<Boolean> bypass1ItemReset;
    private final Setting<Integer> bypass2TickDelay;
    private final Setting<Boolean> bypass2SyncWait;
    private final Timer switchTimer = new Timer();
    private final Timer fireworkTimer = new Timer();
    private final Timer returnTimer = new Timer();
    private final Timer returnDelayTimer = new Timer();
    private int returnSlot = -1;
    private int returnInvSlot = -1;
    private boolean returnDelayPending = false;
    private int legit2Ticks = -1;
    public static boolean swapping = false;
    private int legit3Stage = 0;
    private int legit3TickCounter = 0;
    private int legit3TargetDelay = 0;
    private int legit3OriginalSlot = -1;
    private int legit3FireworkSlot = -1;
    private final Random legit3Random = new Random();
    private boolean bypass1Active = false;
    private int bypass1Stage = 0;
    private int bypass1TargetSlot = -1;
    private int bypass1OriginalSlot = -1;
    private final Timer bypass1Timer = new Timer();
    private long bypass1HumanDelayMs = 0L;
    private final Random bypass1Random = new Random();
    private boolean bypass2Active = false;
    private int bypass2Stage = 0;
    private int bypass2TickCounter = 0;
    private int bypass2TargetSlot = -1;
    private int bypass2OriginalSlot = -1;
    public static boolean fireworkActive = false;
    private int swapBfStage = 0;
    private final Timer swapBfTimer = new Timer();
    private int swapBfOriginalSlot = -1;
    private int swapBfFireworkSlot = -1;

    public static ElytraHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ElytraHelper();
        }

        return INSTANCE;
    }

    public ElytraHelper() {
        super("ElytraHelper", Module.Category.PLAYER);
        this.fireWorkMode = new Setting<>("FireWorkMode", ElytraHelper.FireWorkMode.Normal);
        this.autoFirework = new Setting<>("AutoFirework", false);
        this.legitFireworkKey = new Setting<>("Legit Key", new Bind(-1, false, false), v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Legit);
        this.slotReturn = new Setting<>("SlotReturn", true, v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Normal);
        this.delay = new Setting<>(
            "Delay",
            100,
            1,
            1000,
            v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Normal && this.slotReturn.getValue() || this.autoFirework.getValue()
        );
        this.returnDelay = new Setting<>(
            "ReturnDelay", false, v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Normal && this.slotReturn.getValue()
        );
        this.returnDelayMs = new Setting<>(
            "ReturnDelayMs",
            50,
            1,
            1000,
            v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Normal && this.slotReturn.getValue() && this.returnDelay.getValue()
        );
        this.swing = new Setting<>("Swing", true);
        this.swapBeforeFirework = new Setting<>("SwapBeforeFW", false, v -> this.autoFirework.getValue());
        this.fireworkRandomize = new Setting<>("FW-Randomize", false, v -> this.autoFirework.getValue());
        this.fireworkRandomMin = new Setting<>("FW-RandomMin", 1, 1, 2000, v -> this.autoFirework.getValue() && this.fireworkRandomize.getValue());
        this.fireworkRandomMax = new Setting<>("FW-RandomMax", 500, 1, 2000, v -> this.autoFirework.getValue() && this.fireworkRandomize.getValue());
        this.legit3Delay = new Setting<>("Legit3Delay", 3, 2, 8, v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Legit3);
        this.legit3Randomize = new Setting<>("Legit3Randomize", true, v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Legit3);
        this.bypass1Delay = new Setting<>("B1-HumanDelay", 150, 50, 400, v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Bypass1);
        this.bypass1Randomize = new Setting<>("B1-Randomize", true, v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Bypass1);
        this.bypass1ItemReset = new Setting<>("B1-ItemReset", true, v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Bypass1);
        this.bypass2TickDelay = new Setting<>("B2-TickDelay", 2, 1, 5, v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Bypass2);
        this.bypass2SyncWait = new Setting<>("B2-SyncWait", true, v -> this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Bypass2);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.legit3Stage = 0;
        this.legit3TickCounter = 0;
        this.legit3OriginalSlot = -1;
        this.legit3FireworkSlot = -1;
        this.resetBypass1();
        this.resetBypass2();
        fireworkActive = false;
        this.returnDelayPending = false;
        this.swapBfStage = 0;
        this.swapBfOriginalSlot = -1;
        this.invalidateFireworkDelay();
    }

    @Override
    public void onDisable() {
        if (this.legit3Stage > 0 && this.legit3OriginalSlot != -1 && mc.field_1724 != null && mc.method_1562() != null) {
            mc.method_1562().method_52787(new class_2868(this.legit3OriginalSlot));
            mc.field_1724.method_31548().field_7545 = this.legit3OriginalSlot;
        }

        if (this.bypass1Active && this.bypass1OriginalSlot != -1 && mc.field_1724 != null && mc.method_1562() != null) {
            mc.method_1562().method_52787(new class_2868(this.bypass1OriginalSlot));
            mc.field_1724.method_31548().field_7545 = this.bypass1OriginalSlot;
        }

        if (this.bypass2Active && this.bypass2OriginalSlot != -1 && mc.field_1724 != null && mc.method_1562() != null) {
            mc.method_1562().method_52787(new class_2868(this.bypass2OriginalSlot));
            mc.field_1724.method_31548().field_7545 = this.bypass2OriginalSlot;
        }

        this.resetBypass1();
        this.resetBypass2();
        fireworkActive = false;
        this.returnDelayPending = false;
        if (this.swapBfStage > 0 && this.swapBfOriginalSlot != -1 && mc.field_1724 != null && mc.method_1562() != null) {
            mc.method_1562().method_52787(new class_2868(this.swapBfOriginalSlot));
            mc.field_1724.method_31548().field_7545 = this.swapBfOriginalSlot;
        }

        this.swapBfStage = 0;
        this.swapBfOriginalSlot = -1;
    }

    private void resetBypass1() {
        this.bypass1Active = false;
        this.bypass1Stage = 0;
        this.bypass1TargetSlot = -1;
        this.bypass1OriginalSlot = -1;
    }

    private void resetBypass2() {
        this.bypass2Active = false;
        this.bypass2Stage = 0;
        this.bypass2TickCounter = 0;
        this.bypass2TargetSlot = -1;
        this.bypass2OriginalSlot = -1;
    }

    private int calculateLegit3Delay() {
        int baseDelay = this.legit3Delay.getValue();
        if (this.legit3Randomize.getValue()) {
            int variance = this.legit3Random.nextInt(3) - 1;
            return Math.max(1, baseDelay + variance);
        } else {
            return baseDelay;
        }
    }

    private long rollAutoFireworkDelay() {
        if (!this.fireworkRandomize.getValue()) {
            return this.delay.getValue().intValue();
        } else {
            int min = Math.min(this.fireworkRandomMin.getValue(), this.fireworkRandomMax.getValue());
            int max = Math.max(this.fireworkRandomMin.getValue(), this.fireworkRandomMax.getValue());
            return this.legit3Random.nextInt(max - min + 1) + min;
        }
    }

    private long getCachedFireworkDelay() {
        if (this.currentFireworkDelayMs < 0L) {
            this.currentFireworkDelayMs = this.rollAutoFireworkDelay();
        }

        return this.currentFireworkDelayMs;
    }

    private void invalidateFireworkDelay() {
        this.currentFireworkDelayMs = -1L;
    }

    private long calculateBypass1Delay() {
        long base = this.bypass1Delay.getValue().intValue();
        if (this.bypass1Randomize.getValue()) {
            long variance = (long)(base * 0.3);
            long offset = this.bypass1Random.nextLong() % (variance * 2L + 1L) - variance;
            return Math.max(30L, base + offset);
        } else {
            return base;
        }
    }

    private void processBypass1() {
        if (this.bypass1Active && this.bypass1Stage != 0) {
            switch (this.bypass1Stage) {
                case 1:
                    if (this.bypass1ItemReset.getValue() && mc.field_1724 != null && mc.method_1562() != null && mc.field_1724.method_6115()) {
                        mc.method_1562().method_52787(new class_2846(class_2847.field_12974, class_2338.field_10980, class_2350.field_11033));
                    }

                    this.bypass1HumanDelayMs = this.calculateBypass1Delay();
                    this.bypass1Timer.reset();
                    this.bypass1Stage = 2;
                    break;
                case 2:
                    if (this.bypass1Timer.passedMs(this.bypass1HumanDelayMs)) {
                        this.bypass1Stage = 3;
                    }
                    break;
                case 3:
                    if (mc.field_1724 != null && mc.method_1562() != null && this.bypass1TargetSlot != -1) {
                        if (this.swing.getValue()) {
                            mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
                        }

                        mc.method_1562().method_52787(new class_2868(this.bypass1TargetSlot));
                        mc.field_1724.method_31548().field_7545 = this.bypass1TargetSlot;
                    }

                    this.resetBypass1();
            }
        }
    }

    public void triggerBypass1(int targetSlot) {
        if (mc.field_1724 != null && mc.method_1562() != null) {
            this.bypass1OriginalSlot = mc.field_1724.method_31548().field_7545;
            this.bypass1TargetSlot = targetSlot;
            this.bypass1Active = true;
            this.bypass1Stage = 1;
        }
    }

    public boolean isBypass1Active() {
        return this.bypass1Active;
    }

    private void processBypass2() {
        if (this.bypass2Active && this.bypass2Stage != 0) {
            switch (this.bypass2Stage) {
                case 1:
                    boolean usingDone = mc.field_1724 == null || !mc.field_1724.method_6115();
                    if (!usingDone && this.bypass2TickCounter < 5) {
                        this.bypass2TickCounter++;
                    } else {
                        this.bypass2TickCounter = 0;
                        this.bypass2Stage = 2;
                    }
                    break;
                case 2:
                    this.bypass2TickCounter++;
                    if (this.bypass2TickCounter >= this.bypass2TickDelay.getValue()) {
                        this.bypass2Stage = 3;
                    }
                    break;
                case 3:
                    if (mc.field_1724 != null && mc.method_1562() != null && this.bypass2TargetSlot != -1) {
                        if (this.swing.getValue()) {
                            mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
                        }

                        mc.method_1562().method_52787(new class_2868(this.bypass2TargetSlot));
                        mc.field_1724.method_31548().field_7545 = this.bypass2TargetSlot;
                    }

                    this.resetBypass2();
            }
        }
    }

    public void triggerBypass2(int targetSlot) {
        if (mc.field_1724 != null && mc.method_1562() != null) {
            this.bypass2OriginalSlot = mc.field_1724.method_31548().field_7545;
            this.bypass2TargetSlot = targetSlot;
            this.bypass2Active = true;
            this.bypass2Stage = 1;
            this.bypass2TickCounter = 0;
        }
    }

    public boolean isBypass2Active() {
        return this.bypass2Active;
    }

    private void processLegit3() {
        if (this.legit3Stage != 0) {
            this.legit3TickCounter++;
            switch (this.legit3Stage) {
                case 1:
                    SearchInvResult hotbarResult = InventoryUtility.findItemInHotBar(class_1802.field_8639);
                    if (!hotbarResult.found()) {
                        this.sendMessage(ClientSettings.isRu() ? "Firework hotbar'da olmali!" : "Firework must be in hotbar!");
                        this.legit3Stage = 0;
                        return;
                    }

                    this.legit3OriginalSlot = mc.field_1724.method_31548().field_7545;
                    this.legit3FireworkSlot = hotbarResult.slot();
                    mc.method_1562().method_52787(new class_2868(this.legit3FireworkSlot));
                    mc.field_1724.method_31548().field_7545 = this.legit3FireworkSlot;
                    this.legit3TargetDelay = this.calculateLegit3Delay();
                    this.legit3TickCounter = 0;
                    this.legit3Stage = 2;
                    break;
                case 2:
                    if (this.legit3TickCounter >= this.legit3TargetDelay) {
                        this.legit3Stage = 3;
                    }
                    break;
                case 3:
                    InteractionUtility.sendSequencedPacket(
                        id -> new class_2886(class_1268.field_5808, id, mc.field_1724.method_36454(), mc.field_1724.method_36455())
                    );
                    if (this.swing.getValue()) {
                        mc.field_1724.method_6104(class_1268.field_5808);
                    }

                    mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
                    this.legit3TargetDelay = this.calculateLegit3Delay() + 1;
                    if (this.legit3Randomize.getValue()) {
                        this.legit3TargetDelay = this.legit3TargetDelay + this.legit3Random.nextInt(2);
                    }

                    this.legit3TickCounter = 0;
                    this.legit3Stage = 4;
                    break;
                case 4:
                    if (this.legit3TickCounter >= this.legit3TargetDelay) {
                        this.legit3Stage = 5;
                    }
                    break;
                case 5:
                    if (this.legit3OriginalSlot != -1 && this.legit3OriginalSlot != this.legit3FireworkSlot) {
                        mc.method_1562().method_52787(new class_2868(this.legit3OriginalSlot));
                        mc.field_1724.method_31548().field_7545 = this.legit3OriginalSlot;
                    }

                    this.legit3Stage = 0;
                    this.legit3TickCounter = 0;
                    this.legit3OriginalSlot = -1;
                    this.legit3FireworkSlot = -1;
            }
        }
    }

    @Override
    public void onUpdate() {
        if (mc.field_1724 != null && mc.field_1687 != null) {
            if (this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Legit3) {
                this.processLegit3();
            }

            if (this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Bypass1) {
                this.processBypass1();
            }

            if (this.fireWorkMode.getValue() == ElytraHelper.FireWorkMode.Bypass2) {
                this.processBypass2();
            }

            if (this.swapBeforeFirework.getValue() && this.autoFirework.getValue()) {
                this.processSwapBeforeFirework();
            }

            if (mc.field_1755 == null) {
                if (this.switchButton.getValue().getKey() != -1 && this.isKeyPressed(this.switchButton.getValue().getKey())) {
                    this.swapChest(false);
                }

                if (this.fireWorkButton.getValue().getKey() != -1 && this.isKeyPressed(this.fireWorkButton.getValue().getKey()) && mc.field_1724.method_6128()) {
                    this.useFireWork();
                }

                if (this.legitFireworkKey.getValue().getKey() != -1
                    && this.isKeyPressed(this.legitFireworkKey.getValue().getKey())
                    && mc.field_1724.method_6128()) {
                    this.useFireWork();
                }

                if (this.autoFirework.getValue() && mc.field_1724.method_6128()) {
                    if (this.swapBeforeFirework.getValue() && this.swapBfStage == 0) {
                        float tps = Managers.SERVER.getTPS();
                        float tpsFactor = 20.0F / (tps <= 0.0F ? 20.0F : tps);
                        long syncedDelay = (long)((float)this.getCachedFireworkDelay() * tpsFactor);
                        if (this.fireworkTimer.passedMs(syncedDelay)) {
                            this.startSwapBeforeFirework();
                            this.fireworkTimer.reset();
                            this.invalidateFireworkDelay();
                        }
                    } else if (!this.swapBeforeFirework.getValue()) {
                        float tps = Managers.SERVER.getTPS();
                        float tpsFactor = 20.0F / (tps <= 0.0F ? 20.0F : tps);
                        long syncedDelay = (long)((float)this.getCachedFireworkDelay() * tpsFactor);
                        if (this.fireworkTimer.passedMs(syncedDelay)) {
                            this.useFireWork();
                            this.fireworkTimer.reset();
                            this.invalidateFireworkDelay();
                        }
                    }
                }

                if (this.returnSlot != -1 && this.returnTimer.passedMs(this.delay.getValue().intValue())) {
                    if (this.returnDelay.getValue()) {
                        if (!this.returnDelayPending) {
                            this.returnDelayPending = true;
                            this.returnDelayTimer.reset();
                        }

                        if (this.returnDelayTimer.passedMs(this.returnDelayMs.getValue().intValue())) {
                            if (this.returnInvSlot != -1) {
                                mc.field_1761
                                    .method_2906(
                                        mc.field_1724.field_7498.field_7763,
                                        this.returnInvSlot,
                                        mc.field_1724.method_31548().field_7545,
                                        class_1713.field_7791,
                                        mc.field_1724
                                    );
                                this.sendPacket(new class_2815(mc.field_1724.field_7498.field_7763));
                                this.returnInvSlot = -1;
                            }

                            this.returnSlot = -1;
                            this.returnDelayPending = false;
                        }
                    } else {
                        if (this.returnInvSlot != -1) {
                            mc.field_1761
                                .method_2906(
                                    mc.field_1724.field_7498.field_7763,
                                    this.returnInvSlot,
                                    mc.field_1724.method_31548().field_7545,
                                    class_1713.field_7791,
                                    mc.field_1724
                                );
                            this.sendPacket(new class_2815(mc.field_1724.field_7498.field_7763));
                            this.returnInvSlot = -1;
                        }

                        this.returnSlot = -1;
                    }
                }
            }
        }
    }

    private void startSwapBeforeFirework() {
        if (mc.field_1724 != null && mc.method_1562() != null) {
            this.swapBfOriginalSlot = mc.field_1724.method_31548().field_7545;
            SearchInvResult swordResult = InventoryUtility.getSwordHotBar();
            if (swordResult.found()) {
                swordResult.switchTo();
            }

            this.swapBfStage = 1;
            this.swapBfTimer.reset();
        }
    }

    private void processSwapBeforeFirework() {
        if (this.swapBfStage != 0) {
            switch (this.swapBfStage) {
                case 1:
                    if (this.swapBfTimer.passedMs(this.delay.getValue().intValue())) {
                        this.swapBfStage = 2;
                    }
                    break;
                case 2:
                    if (Aura.target != null && mc.field_1724 != null && mc.method_1562() != null) {
                        mc.field_1761.method_2918(mc.field_1724, Aura.target);
                        if (this.swing.getValue()) {
                            mc.field_1724.method_6104(class_1268.field_5808);
                        }
                    }

                    this.swapBfStage = 3;
                    break;
                case 3:
                    this.useFireWork();
                    if (this.swapBfOriginalSlot != -1) {
                        InventoryUtility.switchTo(this.swapBfOriginalSlot);
                    }

                    this.swapBfStage = 0;
                    this.swapBfOriginalSlot = -1;
            }
        }
    }

    public void useFireWork() {
        ElytraHelper.FireWorkMode mode = this.fireWorkMode.getValue();
        if (mode == ElytraHelper.FireWorkMode.Offhand) {
            this.useFireWorkOffhand();
        } else if (mode == ElytraHelper.FireWorkMode.Legit3) {
            this.useFireWorkLegit3();
        } else if (mode == ElytraHelper.FireWorkMode.Bypass1) {
            this.useFireWorkBypass1();
        } else if (mode == ElytraHelper.FireWorkMode.Bypass2) {
            this.useFireWorkBypass2();
        } else if (mode == ElytraHelper.FireWorkMode.Legit4) {
            this.useFireWorkLegit4();
        } else if (mode == ElytraHelper.FireWorkMode.Legit2) {
            this.useFireWorkLegit2();
        } else if (mode == ElytraHelper.FireWorkMode.Legit) {
            this.useFireWorkLegit();
        } else {
            this.useFireWorkNormalSilent(mode);
        }
    }

    private void useFireWorkOffhand() {
        int epSlot = InventoryUtility.findItemInInventory(class_1802.field_8639).slot();
        if (epSlot == -1 && mc.field_1724.method_6079().method_7909() != class_1802.field_8639) {
            this.sendMessage(ClientSettings.isRu() ? "Fisekin yok!" : "You've got no fireworks!");
        } else {
            boolean needsSwap = mc.field_1724.method_6079().method_7909() != class_1802.field_8639;
            if (needsSwap) {
                mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, epSlot, 40, class_1713.field_7791, mc.field_1724);
            }

            InteractionUtility.sendSequencedPacket(id -> new class_2886(class_1268.field_5810, id, mc.field_1724.method_36454(), mc.field_1724.method_36455()));
            if (needsSwap) {
                mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, epSlot, 40, class_1713.field_7791, mc.field_1724);
            }
        }
    }

    private void useFireWorkLegit3() {
        if (this.legit3Stage == 0) {
            SearchInvResult hotbarResult = InventoryUtility.findItemInHotBar(class_1802.field_8639);
            if (!hotbarResult.found()) {
                this.sendMessage(ClientSettings.isRu() ? "Firework hotbar'da olmali!" : "Firework must be in hotbar!");
            } else if (mc.field_1724.method_31548().field_7545 == hotbarResult.slot()) {
                InteractionUtility.sendSequencedPacket(
                    id -> new class_2886(class_1268.field_5808, id, mc.field_1724.method_36454(), mc.field_1724.method_36455())
                );
                if (this.swing.getValue()) {
                    mc.field_1724.method_6104(class_1268.field_5808);
                }
            } else {
                this.legit3Stage = 1;
                this.legit3TickCounter = 0;
            }
        }
    }

    private void useFireWorkBypass1() {
        if (!this.bypass1Active) {
            SearchInvResult fireWorkResult = InventoryUtility.findItemInInventory(class_1802.field_8639);
            if (!fireWorkResult.found()) {
                this.sendMessage(ClientSettings.isRu() ? "Fisekin yok!" : "You've got no fireworks!");
            } else {
                SearchInvResult hotbarResult = InventoryUtility.findItemInHotBar(class_1802.field_8639);
                int originalSlot = mc.field_1724.method_31548().field_7545;
                if (hotbarResult.found()) {
                    hotbarResult.switchTo();
                } else {
                    mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, fireWorkResult.slot(), originalSlot, class_1713.field_7791, mc.field_1724);
                    this.sendPacket(new class_2815(mc.field_1724.field_7498.field_7763));
                }

                InteractionUtility.sendSequencedPacket(
                    id -> new class_2886(class_1268.field_5808, id, mc.field_1724.method_36454(), mc.field_1724.method_36455())
                );
                if (this.swing.getValue()) {
                    mc.field_1724.method_6104(class_1268.field_5808);
                }

                this.triggerBypass1(originalSlot);
            }
        }
    }

    private void useFireWorkBypass2() {
        if (!this.bypass2Active) {
            SearchInvResult fireWorkResult = InventoryUtility.findItemInInventory(class_1802.field_8639);
            if (!fireWorkResult.found()) {
                this.sendMessage(ClientSettings.isRu() ? "Fisekin yok!" : "You've got no fireworks!");
            } else {
                SearchInvResult hotbarResult = InventoryUtility.findItemInHotBar(class_1802.field_8639);
                int originalSlot = mc.field_1724.method_31548().field_7545;
                if (hotbarResult.found()) {
                    hotbarResult.switchTo();
                } else {
                    mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, fireWorkResult.slot(), originalSlot, class_1713.field_7791, mc.field_1724);
                    this.sendPacket(new class_2815(mc.field_1724.field_7498.field_7763));
                }

                InteractionUtility.sendSequencedPacket(
                    id -> new class_2886(class_1268.field_5808, id, mc.field_1724.method_36454(), mc.field_1724.method_36455())
                );
                if (this.swing.getValue()) {
                    mc.field_1724.method_6104(class_1268.field_5808);
                }

                this.triggerBypass2(originalSlot);
            }
        }
    }

    private void useFireWorkLegit4() {
        SearchInvResult fireWorkResult = InventoryUtility.findItemInInventory(class_1802.field_8639);
        if (!fireWorkResult.found()) {
            this.sendMessage("Envanterinde Fisek Yok");
        } else {
            class_1799 mainHandStack = mc.field_1724.method_6047();
            if (mainHandStack.method_7909() instanceof class_1829) {
                SearchInvResult invFirework = SearchInvResult.notFound();

                for (int i = 9; i <= 35; i++) {
                    class_1799 stack = mc.field_1724.method_31548().method_5438(i);
                    if (stack.method_7909() == class_1802.field_8639) {
                        invFirework = new SearchInvResult(i, true, stack);
                        break;
                    }
                }

                if (!invFirework.found()) {
                    this.sendMessage("Envanterinde Fisek Yok");
                } else {
                    int currentSlot = mc.field_1724.method_31548().field_7545;
                    mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, invFirework.slot(), currentSlot, class_1713.field_7791, mc.field_1724);
                    InteractionUtility.sendSequencedPacket(
                        id -> new class_2886(class_1268.field_5808, id, mc.field_1724.method_36454(), mc.field_1724.method_36455())
                    );
                    if (this.swing.getValue()) {
                        mc.field_1724.method_6104(class_1268.field_5808);
                    }

                    mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, invFirework.slot(), currentSlot, class_1713.field_7791, mc.field_1724);
                    this.sendPacket(new class_2815(mc.field_1724.field_7498.field_7763));
                }
            }
        }
    }

    private void useFireWorkLegit2() {
        SearchInvResult hotbarFireWorkResult = InventoryUtility.findItemInHotBar(class_1802.field_8639);
        if (hotbarFireWorkResult.found()) {
            int currentSlot = mc.field_1724.method_31548().field_7545;
            int fireworkSlot = hotbarFireWorkResult.slot();
            mc.method_1562().method_52787(new class_2868(fireworkSlot));
            InteractionUtility.sendSequencedPacket(id -> new class_2886(class_1268.field_5808, id, mc.field_1724.method_36454(), mc.field_1724.method_36455()));
            mc.method_1562().method_52787(new class_2868(currentSlot));
            this.legit2Ticks = -1;
        } else {
            this.sendMessage(ClientSettings.isRu() ? "Firework hotbar'da olmali!" : "Firework must be in hotbar!");
        }
    }

    private void useFireWorkLegit() {
        SearchInvResult fireWorkResult = InventoryUtility.findItemInInventory(class_1802.field_8639);
        if (!fireWorkResult.found()) {
            this.sendMessage(ClientSettings.isRu() ? "Fisekin yok!" : "You've got no fireworks!");
        } else {
            int currentSlot = mc.field_1724.method_31548().field_7545;
            int fireworkSlot = fireWorkResult.slot();
            if (fireworkSlot == currentSlot + 36) {
                mc.field_1761.method_2919(mc.field_1724, class_1268.field_5808);
                if (this.swing.getValue()) {
                    mc.field_1724.method_6104(class_1268.field_5808);
                }
            } else {
                mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, fireworkSlot, currentSlot, class_1713.field_7791, mc.field_1724);
                InteractionUtility.sendSequencedPacket(
                    id -> new class_2886(class_1268.field_5808, id, mc.field_1724.method_36454(), mc.field_1724.method_36455())
                );
                if (this.swing.getValue()) {
                    mc.field_1724.method_6104(class_1268.field_5808);
                }

                mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, fireworkSlot, currentSlot, class_1713.field_7791, mc.field_1724);
            }
        }
    }

    private void useFireWorkNormalSilent(ElytraHelper.FireWorkMode mode) {
        SearchInvResult fireWorkResult = InventoryUtility.findItemInInventory(class_1802.field_8639);
        if (!fireWorkResult.found()) {
            this.sendMessage(ClientSettings.isRu() ? "Fisekin yok!" : "You've got no fireworks!");
        } else {
            SearchInvResult hotbarFireWorkResult = InventoryUtility.findItemInHotBar(class_1802.field_8639);
            if (mode == ElytraHelper.FireWorkMode.Normal && this.slotReturn.getValue()) {
                this.returnSlot = mc.field_1724.method_31548().field_7545;
                this.returnInvSlot = hotbarFireWorkResult.found() ? -1 : (fireWorkResult.found() ? fireWorkResult.slot() : -1);
                this.returnTimer.reset();
                this.returnDelayPending = false;
            }

            InventoryUtility.saveSlot();
            if (hotbarFireWorkResult.found()) {
                hotbarFireWorkResult.switchTo();
            } else {
                mc.field_1761
                    .method_2906(
                        mc.field_1724.field_7498.field_7763,
                        fireWorkResult.slot(),
                        mc.field_1724.method_31548().field_7545,
                        class_1713.field_7791,
                        mc.field_1724
                    );
                this.sendPacket(new class_2815(mc.field_1724.field_7498.field_7763));
            }

            mc.field_1761.method_2919(mc.field_1724, class_1268.field_5808);
            if (this.swing.getValue()) {
                mc.field_1724.method_6104(class_1268.field_5808);
            }

            if (mode == ElytraHelper.FireWorkMode.Silent) {
                InventoryUtility.returnSlot();
                if (!hotbarFireWorkResult.found()) {
                    mc.field_1761
                        .method_2906(
                            mc.field_1724.field_7498.field_7763,
                            fireWorkResult.slot(),
                            mc.field_1724.method_31548().field_7545,
                            class_1713.field_7791,
                            mc.field_1724
                        );
                    this.sendPacket(new class_2815(mc.field_1724.field_7498.field_7763));
                }
            }
        }
    }

    public static int getChestPlateSlot() {
        for (class_1792 item : new class_1792[]{
            class_1802.field_22028, class_1802.field_8058, class_1802.field_8523, class_1802.field_8678, class_1802.field_8873, class_1802.field_8577
        }) {
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
                String msg = ClientSettings.isRu() ? "Goguslugu yok!" : "You don't have a chestplate!";
                if (disable) {
                    this.disable(msg);
                } else {
                    this.sendMessage(msg);
                }

                return;
            }

            clickSlot(slot);
            clickSlot(6);
            clickSlot(slot);
            this.sendPacket(new class_2815(mc.field_1724.field_7498.field_7763));
        } else {
            if (!result.found()) {
                String msg = ClientSettings.isRu() ? "Elytra yok!" : "You don't have an elytra!";
                if (disable) {
                    this.disable(msg);
                } else {
                    this.sendMessage(msg);
                }

                return;
            }

            clickSlot(result.slot());
            clickSlot(6);
            clickSlot(result.slot());
            this.sendPacket(new class_2815(mc.field_1724.field_7498.field_7763));
        }

        if (disable) {
            this.disable(ClientSettings.isRu() ? "Swap tamamlandi!" : "Swapped! Disabling..");
        }
    }

    public String getFireWorkMode() {
        return this.fireWorkMode.getValue().name();
    }

    public boolean isSwingEnabled() {
        return this.swing.getValue();
    }

    @Override
    public String getDisplayInfo() {
        return this.fireWorkMode.getValue().name();
    }

    public static enum FireWorkMode {
        Silent,
        Normal,
        Legit,
        Legit2,
        Legit3,
        Legit4,
        Offhand,
        Bypass1,
        Bypass2;
    }
}
