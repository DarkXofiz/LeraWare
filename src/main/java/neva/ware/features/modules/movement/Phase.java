package neva.ware.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2846;
import net.minecraft.class_2848;
import net.minecraft.class_2879;
import net.minecraft.class_2886;
import net.minecraft.class_3959;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_2848.class_2849;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventBreakBlock;
import neva.ware.events.impl.EventCollision;
import neva.ware.events.impl.EventPostSync;
import neva.ware.events.impl.EventSync;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.player.AutoTool;
import neva.ware.setting.Setting;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.MovementUtility;

public class Phase extends Module {
    private final Setting<Phase.Mode> mode = new Setting<>("Mode", Phase.Mode.Vanilla);
    private final Setting<Boolean> silent = new Setting<>("Silent", false, v -> this.mode.getValue() == Phase.Mode.Sunrise);
    private final Setting<Boolean> waitBreak = new Setting<>("WaitBreak", true, v -> this.mode.getValue() == Phase.Mode.Sunrise);
    private final Setting<Boolean> onlyOnGround = new Setting<>("OnlyOnGround", false, v -> this.mode.is(Phase.Mode.Pearl));
    private final Setting<Boolean> autoDisable = new Setting<>("AutoDisable", false, v -> this.mode.getValue() == Phase.Mode.Pearl);
    private final Setting<Integer> afterBreak = new Setting<>(
        "BreakTimeout", 4, 1, 20, v -> this.mode.getValue() == Phase.Mode.Sunrise && this.waitBreak.getValue()
    );
    private final Setting<Integer> afterPearl = new Setting<>("PearlTimeout", 0, 0, 60, v -> this.mode.getValue() == Phase.Mode.Pearl);
    private final Setting<Float> pitch = new Setting<>("Pitch", 80.0F, 0.0F, 90.0F, v -> this.mode.getValue() == Phase.Mode.Pearl);
    private final Setting<Boolean> strict = new Setting<>("Strict", false, v -> this.mode.is(Phase.Mode.ForceMine));
    public int clipTimer;
    public int afterPearlTime;

    public Phase() {
        super("Phase", Module.Category.MOVEMENT);
    }

    @EventHandler
    public void onCollide(EventCollision e) {
        if (!fullNullCheck()) {
            class_2338 playerPos = class_2338.method_49638(mc.field_1724.method_19538());
            if ((
                    !this.mode.is(Phase.Mode.CCClip) && !this.mode.is(Phase.Mode.Pearl) && !this.mode.is(Phase.Mode.ForceMine) && this.canNoClip()
                        || this.afterPearlTime > 0
                )
                && (!e.getPos().equals(playerPos.method_10074()) || mc.field_1690.field_1832.method_1434())) {
                e.setState(class_2246.field_10124.method_9564());
            }

            if (this.mode.is(Phase.Mode.ForceMine)) {
                float xDelta = Math.abs(playerPos.method_10263() - e.getPos().method_10263());
                float zDelta = Math.abs(playerPos.method_10260() - e.getPos().method_10260());
                if (xDelta != 0.0F && zDelta != 0.0F && this.strict.getValue()) {
                    return;
                }

                if (!e.getPos().equals(playerPos.method_10074()) || mc.field_1690.field_1832.method_1434()) {
                    e.setState(class_2246.field_10124.method_9564());
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.afterPearlTime = 0;
        this.clipTimer = 0;
        if (mc.field_1724.method_24828() && this.mode.is(Phase.Mode.CCClip)) {
            double[] diagonalOffset = MovementUtility.forwardWithoutStrafe(0.44);
            boolean diagonal = mc.field_1724.method_36454() % 90.0F > 35.0F && mc.field_1724.method_36454() % 90.0F < 55.0F;
            this.sendPacket(new class_2848(mc.field_1724, class_2849.field_12981));
            if (diagonal) {
                double[] directionVec = MovementUtility.forwardWithoutStrafe(0.51);
                int height = mc.field_1687
                        .method_17742(
                            new class_3959(
                                mc.field_1724.method_33571(),
                                mc.field_1724.method_33571().method_1031(diagonalOffset[0], 0.0, diagonalOffset[1]),
                                class_3960.field_17558,
                                class_242.field_1348,
                                mc.field_1724
                            )
                        )
                        .method_17783()
                        .equals(class_240.field_1333)
                    ? 1
                    : 2;
                mc.field_1724
                    .method_5814(
                        mc.field_1724.method_23317() + directionVec[0], mc.field_1724.method_23318() + height, mc.field_1724.method_23321() + directionVec[1]
                    );
                this.sendPacket(new class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), true));
                height = mc.field_1687
                        .method_22347(class_2338.method_49638(mc.field_1724.method_19538().method_1031(diagonalOffset[0], -2.0, diagonalOffset[1])))
                    ? 2
                    : 1;
                mc.field_1724
                    .method_5814(
                        mc.field_1724.method_23317() + directionVec[0], mc.field_1724.method_23318() - height, mc.field_1724.method_23321() + directionVec[1]
                    );
                this.sendPacket(new class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), true));
                this.disable("diagonal");
            } else {
                double[] directionVec = MovementUtility.forwardWithoutStrafe(0.57);
                int height = mc.field_1687
                        .method_17742(
                            new class_3959(
                                mc.field_1724.method_33571(),
                                mc.field_1724.method_33571().method_1031(diagonalOffset[0], 0.0, diagonalOffset[1]),
                                class_3960.field_17558,
                                class_242.field_1348,
                                mc.field_1724
                            )
                        )
                        .method_17783()
                        .equals(class_240.field_1333)
                    ? 1
                    : 2;
                mc.field_1724
                    .method_5814(
                        mc.field_1724.method_23317() + directionVec[0], mc.field_1724.method_23318() + height, mc.field_1724.method_23321() + directionVec[1]
                    );
                this.sendPacket(new class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), true));
                mc.field_1724
                    .method_5814(mc.field_1724.method_23317() + directionVec[0], mc.field_1724.method_23318(), mc.field_1724.method_23321() + directionVec[1]);
                this.sendPacket(new class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), true));
                height = mc.field_1687
                        .method_22347(class_2338.method_49638(mc.field_1724.method_19538().method_1031(diagonalOffset[0], -2.0, diagonalOffset[1])))
                    ? 2
                    : 1;
                mc.field_1724
                    .method_5814(
                        mc.field_1724.method_23317() + directionVec[0], mc.field_1724.method_23318() - height, mc.field_1724.method_23321() + directionVec[1]
                    );
                this.sendPacket(new class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), true));
                this.disable("normal");
            }
        }
    }

    @EventHandler
    public void onSync(EventSync e) {
        if (!fullNullCheck()) {
            if (this.clipTimer > 0) {
                this.clipTimer--;
            }

            if (this.afterPearlTime > 0) {
                this.afterPearlTime--;
            }

            if (this.mode.getValue() == Phase.Mode.Sunrise
                && (mc.field_1724.field_5976 || this.playerInsideBlock())
                && !mc.field_1724.method_5869()
                && !mc.field_1724.method_5771()
                && this.clipTimer <= 0) {
                double[] dir = MovementUtility.forward(0.5);
                class_2338 blockToBreak = null;
                if (mc.field_1690.field_1903.method_1434()) {
                    blockToBreak = class_2338.method_49637(
                        mc.field_1724.method_23317() + dir[0], mc.field_1724.method_23318() + 2.0, mc.field_1724.method_23321() + dir[1]
                    );
                } else if (mc.field_1690.field_1832.method_1434()) {
                    blockToBreak = class_2338.method_49637(
                        mc.field_1724.method_23317() + dir[0], mc.field_1724.method_23318() - 1.0, mc.field_1724.method_23321() + dir[1]
                    );
                } else if (MovementUtility.isMoving()) {
                    blockToBreak = class_2338.method_49637(
                        mc.field_1724.method_23317() + dir[0], mc.field_1724.method_23318(), mc.field_1724.method_23321() + dir[1]
                    );
                }

                if (blockToBreak == null) {
                    return;
                }

                int best_tool = AutoTool.getTool(blockToBreak);
                if (best_tool == -1) {
                    return;
                }

                int prevItem = mc.field_1724.method_31548().field_7545;
                InventoryUtility.switchTo(best_tool);
                mc.field_1761.method_2902(blockToBreak, mc.field_1724.method_5735());
                mc.field_1724.method_6104(class_1268.field_5808);
                if (this.silent.getValue()) {
                    InventoryUtility.switchTo(prevItem);
                }
            }

            if (this.mode.getValue() == Phase.Mode.ForceMine
                && (mc.field_1724.field_5976 || this.playerInsideBlock())
                && !mc.field_1724.method_5869()
                && !mc.field_1724.method_5771()) {
                for (int x = -2; x < 2; x++) {
                    for (int y = -1; y < 3; y++) {
                        for (int z = -2; z < 2; z++) {
                            if ((x != 0 || y != 0 || z != 0) && (x != 0 || y != 1 || z != 0) || mc.field_1690.field_1832.method_1434()) {
                                class_2338 bp = class_2338.method_49638(mc.field_1724.method_19538()).method_10069(x, y, z);
                                if (mc.field_1724.method_5829().method_994(new class_238(bp)) && !mc.field_1687.method_22347(bp)) {
                                    this.sendPacket(new class_2846(class_2847.field_12973, bp, class_2350.field_11036));
                                }
                            }
                        }
                    }
                }
            }

            if (this.mode.getValue() == Phase.Mode.Pearl
                && (mc.field_1724.method_24828() || !this.onlyOnGround.getValue())
                && mc.field_1724.field_5976
                && !this.playerInsideBlock()
                && this.clipTimer <= 0
                && mc.field_1724.field_6012 > 60) {
                double[] dirx = MovementUtility.forward(0.5);
                class_2338 block = class_2338.method_49637(
                    mc.field_1724.method_23317() + dirx[0], mc.field_1724.method_23318(), mc.field_1724.method_23321() + dirx[1]
                );
                if (mc.field_1690.field_1832.method_1434()) {
                    return;
                }

                float[] angle = InteractionUtility.calculateAngle(block.method_46558());
                int epSlot = this.findEPSlot();
                if (epSlot != -1) {
                    ModuleManager.autoCrystal.pause();
                    ModuleManager.aura.pause();
                    mc.field_1724.method_36456(angle[0]);
                    mc.field_1724.method_36457(this.pitch.getValue());
                }
            }
        }
    }

    @EventHandler
    public void onPostSync(EventPostSync e) {
        if (this.mode.getValue() == Phase.Mode.Pearl
            && (mc.field_1724.method_24828() || !this.onlyOnGround.getValue())
            && mc.field_1724.field_5976
            && !this.playerInsideBlock()
            && this.clipTimer <= 0
            && mc.field_1724.field_6012 > 60) {
            if (mc.field_1690.field_1832.method_1434()) {
                return;
            }

            int epSlot = this.findEPSlot();
            int prevItem = mc.field_1724.method_31548().field_7545;
            if (epSlot != -1) {
                InventoryUtility.switchTo(epSlot);
                this.sendSequencedPacket(id -> new class_2886(class_1268.field_5808, id, mc.field_1724.method_36454(), mc.field_1724.method_36455()));
                this.sendPacket(new class_2879(class_1268.field_5808));
                InventoryUtility.switchTo(prevItem);
                if (this.autoDisable.getValue()) {
                    this.disable();
                }
            }

            this.clipTimer = 20;
            this.afterPearlTime = this.afterPearl.getValue();
        }
    }

    private int findEPSlot() {
        int epSlot = -1;
        if (mc.field_1724.method_6047().method_7909() == class_1802.field_8634) {
            epSlot = mc.field_1724.method_31548().field_7545;
        }

        if (epSlot == -1) {
            for (int l = 0; l < 9; l++) {
                if (mc.field_1724.method_31548().method_5438(l).method_7909() == class_1802.field_8634) {
                    epSlot = l;
                    break;
                }
            }
        }

        return epSlot;
    }

    public boolean canNoClip() {
        if (this.mode.is(Phase.Mode.Vanilla)) {
            return true;
        } else {
            return !this.waitBreak.getValue() ? true : this.clipTimer != 0;
        }
    }

    public boolean playerInsideBlock() {
        return !mc.field_1687.method_22347(class_2338.method_49638(mc.field_1724.method_19538()));
    }

    @EventHandler
    public void onBreakBlock(EventBreakBlock e) {
        this.clipTimer = this.afterBreak.getValue();
    }

    private static enum Mode {
        Vanilla,
        Pearl,
        Sunrise,
        ForceMine,
        CCClip;
    }
}
