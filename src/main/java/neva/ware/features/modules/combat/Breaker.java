package neva.ware.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2480;
import net.minecraft.class_2680;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.core.manager.player.CombatManager;
import neva.ware.events.impl.EventSync;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.player.SpeedMine;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;
import neva.ware.utility.world.ExplosionUtility;
import neva.ware.utility.world.HoleUtility;

public final class Breaker extends Module {
    private final Setting<Breaker.Target> targetMode = new Setting<>("Target", Breaker.Target.AutoCrystal);
    private final Setting<Boolean> onlyIfHole = new Setting<>("OnlyIfHole", false);
    private final Setting<CombatManager.TargetBy> targetBy = new Setting<>(
        "TargetBy", CombatManager.TargetBy.Distance, v -> this.targetMode.is(Breaker.Target.Breaker)
    );
    private final Setting<Integer> range = new Setting<>("Range", 5, 1, 7, v -> this.targetMode.is(Breaker.Target.Breaker));
    private final Setting<Float> minDamage = new Setting<>("MinDamage", 7.0F, 0.0F, 36.0F);
    private final Setting<Float> maxSelfDamage = new Setting<>("MaxSelfDamage", 4.0F, 0.0F, 36.0F);
    private final Setting<Boolean> cevPriority = new Setting<>("CevPriority", true);
    private final Setting<Boolean> antiShulker = new Setting<>("AntiShulker", true);
    private class_2338 blockPos;
    private final Timer pause = new Timer();

    public Breaker() {
        super("Breaker", Module.Category.COMBAT);
    }

    @EventHandler
    private void onSync(EventSync event) {
        class_1657 target;
        if (this.targetMode.is(Breaker.Target.Breaker)) {
            target = Managers.COMBAT.getTarget(this.range.getValue().intValue(), this.targetBy.getValue());
        } else {
            target = AutoCrystal.target;
        }

        if (target != null) {
            class_2338 burrow = class_2338.method_49638(target.method_19538());
            class_2680 burrowState = mc.field_1687.method_8320(burrow);
            if (this.pause.passedMs(600L)) {
                if (this.blockPos != null) {
                    if (mc.field_1687.method_22347(this.blockPos)
                        || mc.field_1724.method_5707(this.blockPos.method_46558())
                            > (
                                ModuleManager.speedMine.isEnabled()
                                    ? ModuleManager.speedMine.range.getPow2Value()
                                    : (ModuleManager.reach.isEnabled() ? ModuleManager.reach.blocksRange.getPow2Value() : 9.0F)
                            )) {
                        this.blockPos = null;
                        return;
                    }

                    if (!ModuleManager.speedMine.isEnabled()) {
                        mc.field_1761.method_2902(this.blockPos, class_2350.field_11036);
                    } else {
                        if (ModuleManager.speedMine.alreadyActing(this.blockPos)) {
                            return;
                        }

                        for (SpeedMine.MineAction action : ModuleManager.speedMine.actions) {
                            if (action.instantBreaking()) {
                                return;
                            }
                        }

                        mc.field_1761.method_2910(this.blockPos, class_2350.field_11036);
                    }

                    mc.field_1724.method_6104(class_1268.field_5808);
                }

                ArrayList<Breaker.BreakData> list = new ArrayList();
                if (this.cevPriority.getValue()) {
                    for (int y = 2; y <= 3; y++) {
                        class_2338 bp = class_2338.method_49637(target.method_23317(), target.method_23318() + y, target.method_23321());
                        if (mc.field_1687.method_8320(bp).method_26204() == class_2246.field_10540
                            && !bp.equals(class_2338.method_49638(target.method_19538()).method_10074())
                            && ModuleManager.autoCrystal
                                    .getInteractResult(bp, new class_243(0.5F + bp.method_10263(), 1.0F + bp.method_10264(), 0.5F + bp.method_10260()))
                                != null) {
                            class_2680 currentState = mc.field_1687.method_8320(bp);
                            mc.field_1687.method_8501(bp, class_2246.field_10124.method_9564());
                            float damage = ExplosionUtility.getExplosionDamage(bp.method_46558().method_1031(0.0, -0.5, 0.0), target, false);
                            float selfDamage = ExplosionUtility.getExplosionDamage(bp.method_46558().method_1031(0.0, -0.5, 0.0), mc.field_1724, false);
                            mc.field_1687.method_8501(bp, currentState);
                            if ((Float.isNaN(ModuleManager.autoCrystal.renderDamage) || ModuleManager.autoCrystal.renderDamage < damage)
                                && selfDamage < this.maxSelfDamage.getValue()
                                && damage >= this.minDamage.getValue()) {
                                list.add(new Breaker.BreakData(bp, damage));
                            }
                        }
                    }

                    Breaker.BreakData best = (Breaker.BreakData)list.stream().max(Comparator.comparing(Breaker.BreakData::damage)).orElse(null);
                    if (best != null && this.cevPriority.getValue()) {
                        list.add(new Breaker.BreakData(best.blockPos, 999.0F));
                    }
                }

                boolean inBurrow = burrowState.method_26204() == class_2246.field_10540 || burrowState.method_26204() == class_2246.field_10443;
                if (inBurrow) {
                    list.add(new Breaker.BreakData(burrow, 995.0F));
                    mc.field_1687.method_8501(burrow, class_2246.field_10124.method_9564());
                } else if (this.onlyIfHole.getValue() && !HoleUtility.isHole(class_2338.method_49638(target.method_19538()))) {
                    return;
                }

                for (int x = -2; x <= 2; x++) {
                    for (int yx = 0; yx <= 3; yx++) {
                        for (int z = -2; z <= 2; z++) {
                            if (yx <= 1 || x != -2 && z != -2 && x != 2 && z != 2) {
                                class_2338 bp = class_2338.method_49637(target.method_23317() + x, target.method_23318() + yx, target.method_23321() + z);
                                if (mc.field_1687.method_8320(bp).method_26204() instanceof class_2480 && this.antiShulker.getValue()) {
                                    list.add(new Breaker.BreakData(burrow, 990.0F));
                                }

                                if ((
                                        mc.field_1687.method_8320(bp).method_26204() == class_2246.field_10540
                                            || mc.field_1687.method_8320(bp).method_26204() == class_2246.field_10443
                                    )
                                    && (
                                        mc.field_1687.method_8320(bp.method_10074()).method_26204() == class_2246.field_10540
                                            || mc.field_1687.method_8320(bp.method_10074()).method_26204() == class_2246.field_9987
                                    )
                                    && !bp.equals(class_2338.method_49638(target.method_19538()).method_10074())
                                    && ModuleManager.autoCrystal
                                            .getInteractResult(bp, new class_243(0.5F + bp.method_10263(), 1.0F + bp.method_10264(), 0.5F + bp.method_10260()))
                                        != null) {
                                    class_2680 currentState = mc.field_1687.method_8320(bp);
                                    mc.field_1687.method_8501(bp, class_2246.field_10124.method_9564());
                                    float damage = ExplosionUtility.getExplosionDamage(bp.method_46558().method_1031(0.0, -0.5, 0.0), target, false);
                                    float selfDamage = ExplosionUtility.getExplosionDamage(bp.method_46558().method_1031(0.0, -0.5, 0.0), mc.field_1724, false);
                                    mc.field_1687.method_8501(bp, currentState);
                                    if (ModuleManager.autoCrystal.renderDamage < damage
                                        && selfDamage <= this.maxSelfDamage.getValue()
                                        && damage >= this.minDamage.getValue()
                                        && bp != this.blockPos) {
                                        list.add(new Breaker.BreakData(bp, damage));
                                    }
                                }
                            }
                        }
                    }
                }

                if (inBurrow) {
                    mc.field_1687.method_8501(burrow, burrowState);
                }

                Breaker.BreakData best = (Breaker.BreakData)list.stream().max(Comparator.comparing(Breaker.BreakData::damage)).orElse(null);
                Breaker.BreakData secondBest = ModuleManager.speedMine.doubleMine.getValue()
                    ? (Breaker.BreakData)list.stream().sorted(Comparator.comparing(Breaker.BreakData::damage).reversed()).skip(1L).findFirst().orElse(null)
                    : null;
                this.blockPos = best == null
                    ? null
                    : (ModuleManager.speedMine.alreadyActing(best.blockPos()) && secondBest != null ? secondBest.blockPos() : best.blockPos());
            }
        }
    }

    public void pause() {
        this.pause.reset();
    }

    private record BreakData(class_2338 blockPos, float damage) {
    }

    private static enum Target {
        AutoCrystal,
        Breaker;
    }
}
