package neva.ware.utility;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_746;
import neva.ware.events.impl.EventTravel;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.ElytraTarget;
import neva.ware.setting.Setting;

public final class ElytraMatch {
    private static Setting<Boolean> sOn;
    private static Setting<Float> sDecel;
    private static boolean active;
    private static long lastTick;
    private static class_1297 lastTarget;
    private static final double[][] hist = new double[6][3];
    private static int hn;
    private static double tvx;
    private static double tvy;
    private static double tvz;
    private static double tSpeed;

    private ElytraMatch() {
    }

    public static Setting<Boolean> matchSetting(ElytraTarget var0) {
        sOn = new Setting<>("Match Speed", Boolean.TRUE);
        return sOn;
    }

    public static Setting<Float> decelSetting(ElytraTarget var0) {
        sDecel = new Setting<>("Match Decel", 3.0F, 0.5F, 30.0F, var0x -> sOn != null && Boolean.TRUE.equals(sOn.getValue()));
        return sDecel;
    }

    public static void reset() {
        active = false;
        lastTarget = null;
        hn = 0;
    }

    public static boolean isActive() {
        return active && System.nanoTime() - lastTick < 200000000L;
    }

    public static float jit(float var0) {
        return isActive() ? 0.0F : var0;
    }

    public static class_243 adjust(ElytraTarget var0, class_243 var1) {
        lastTick = System.nanoTime();
        class_1297 var2 = ElytraTarget.target;
        class_746 var3 = Module.mc.field_1724;
        if (var2 != null && var3 != null && sOn != null && Boolean.TRUE.equals(sOn.getValue())) {
            class_243 var4 = var2.method_19538();
            if (var4 == null) {
                return var1;
            } else {
                if (var2 != lastTarget) {
                    lastTarget = var2;
                    hn = 0;
                    active = false;
                }

                if (hn == hist.length) {
                    for (int var5 = 1; var5 < hn; var5++) {
                        hist[var5 - 1][0] = hist[var5][0];
                        hist[var5 - 1][1] = hist[var5][1];
                        hist[var5 - 1][2] = hist[var5][2];
                    }

                    hn--;
                }

                hist[hn][0] = var4.field_1352;
                hist[hn][1] = var4.field_1351;
                hist[hn][2] = var4.field_1350;
                hn++;
                if (hn < 3) {
                    active = false;
                    return var1;
                } else {
                    double var21 = 1.0 / (hn - 1);
                    tvx = (hist[hn - 1][0] - hist[0][0]) * var21;
                    tvy = (hist[hn - 1][1] - hist[0][1]) * var21;
                    tvz = (hist[hn - 1][2] - hist[0][2]) * var21;
                    double var7 = Math.sqrt(tvx * tvx + tvy * tvy + tvz * tvz);
                    tSpeed = var7;
                    if (var3.method_6128() && !(var7 * 20.0 < 8.0)) {
                        class_243 var9 = var3.method_19538();
                        double var10 = var9.field_1352 - var4.field_1352;
                        double var12 = var9.field_1351 - var4.field_1351;
                        double var14 = var9.field_1350 - var4.field_1350;
                        double var16 = (var10 * tvx + var12 * tvy + var14 * tvz) / var7;
                        double var18 = Math.sqrt(var10 * var10 + var12 * var12 + var14 * var14);
                        active = active ? var16 > -0.5 && var18 < 45.0 : var16 > 1.0 && var18 < 45.0;
                        if (!active) {
                            return var1;
                        } else {
                            class_243 var20 = var3.method_33571();
                            return new class_243(
                                var20.field_1352 + tvx / var7 * 32.0, var20.field_1351 + tvy / var7 * 32.0, var20.field_1350 + tvz / var7 * 32.0
                            );
                        }
                    } else {
                        active = false;
                        return var1;
                    }
                }
            }
        } else {
            active = false;
            lastTarget = null;
            hn = 0;
            return var1;
        }
    }

    public static void travel(EventTravel var0) {
        if (!var0.isPre() && isActive()) {
            class_746 var1 = Module.mc.field_1724;
            if (var1 != null && var1.method_6128()) {
                class_243 var2 = var1.method_18798();
                double var3 = Math.sqrt(var2.field_1352 * var2.field_1352 + var2.field_1351 * var2.field_1351 + var2.field_1350 * var2.field_1350);
                if (!(var3 < 1.0E-4)) {
                    double var5 = tSpeed / 0.99;
                    double var7 = sDecel != null ? sDecel.getValue().floatValue() : 3.0;
                    double var9 = var7 / 400.0;
                    double var11 = var3 + Math.max(-var9, Math.min(var9, var5 - var3));
                    if (!(Math.abs(var11 - var3) < 1.0E-9)) {
                        double var13 = var11 / var3;
                        var1.method_18799(new class_243(var2.field_1352 * var13, var2.field_1351 * var13, var2.field_1350 * var13));
                    }
                }
            }
        }
    }
}
