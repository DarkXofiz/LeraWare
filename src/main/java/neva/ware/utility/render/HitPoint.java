package neva.ware.utility.render;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.combat.AttackAura;

public final class HitPoint {
    private static int lastId = Integer.MIN_VALUE;
    private static long lastTime;
    private static double hx;
    private static double hy;
    private static double hz;

    private HitPoint() {
    }

    private static void calc(class_1297 var0) {
        long var1 = System.currentTimeMillis();
        if (var0.method_5628() != lastId || var1 - lastTime >= 40L) {
            lastId = var0.method_5628();
            lastTime = var1;
            class_238 var3 = var0.method_5829();
            class_310 var4 = class_310.method_1551();
            class_746 var5 = var4.field_1724;
            if (var5 != null && var0 != var5) {
                class_243 var6 = var5.method_33571();
                float var7 = var5.method_36454();
                float var8 = var5.method_36455();

                try {
                    AttackAura var9 = ModuleManager.aura;
                    if (var9 != null && var9.isEnabled() && AttackAura.target == var0) {
                        var7 = var9.rotationYaw;
                        var8 = var9.rotationPitch;
                    }
                } catch (Throwable var55) {
                }

                double var56 = Math.toRadians(var7);
                double var11 = Math.toRadians(var8);
                double var13 = -Math.sin(var56) * Math.cos(var11);
                double var15 = -Math.sin(var11);
                double var17 = Math.cos(var56) * Math.cos(var11);
                double var19 = var6.field_1352;
                double var21 = var6.field_1351;
                double var23 = var6.field_1350;
                double var25 = 0.05;
                double var27 = var3.field_1323 - var25;
                double var29 = var3.field_1322 - var25;
                double var31 = var3.field_1321 - var25;
                double var33 = var3.field_1320 + var25;
                double var35 = var3.field_1325 + var25;
                double var37 = var3.field_1324 + var25;
                double var39 = 0.0;
                double var41 = 12.0;
                double[] var43 = new double[]{var19, var21, var23};
                double[] var44 = new double[]{var13, var15, var17};
                double[] var45 = new double[]{var27, var29, var31};
                double[] var46 = new double[]{var33, var35, var37};
                boolean var47 = true;

                for (int var48 = 0; var48 < 3 && var47; var48++) {
                    if (Math.abs(var44[var48]) < 1.0E-9) {
                        if (var43[var48] < var45[var48] || var43[var48] > var46[var48]) {
                            var47 = false;
                        }
                    } else {
                        double var49 = (var45[var48] - var43[var48]) / var44[var48];
                        double var51 = (var46[var48] - var43[var48]) / var44[var48];
                        if (var49 > var51) {
                            double var53 = var49;
                            var49 = var51;
                            var51 = var53;
                        }

                        var39 = Math.max(var39, var49);
                        var41 = Math.min(var41, var51);
                        if (var39 > var41) {
                            var47 = false;
                        }
                    }
                }

                if (var47) {
                    hx = var19 + var13 * var39;
                    hy = var21 + var15 * var39;
                    hz = var23 + var17 * var39;
                } else {
                    hx = Math.max(var3.field_1323, Math.min(var3.field_1320, var19));
                    hy = Math.max(var3.field_1322, Math.min(var3.field_1325, var21));
                    hz = Math.max(var3.field_1321, Math.min(var3.field_1324, var23));
                }
            } else {
                hx = (var3.field_1323 + var3.field_1320) * 0.5;
                hy = var3.field_1322 + (var3.field_1325 - var3.field_1322) * 0.55;
                hz = (var3.field_1321 + var3.field_1324) * 0.5;
            }
        }
    }

    private static float jit() {
        return (float)((ThreadLocalRandom.current().nextDouble() - 0.5) * 0.08);
    }

    public static float x(class_1297 var0) {
        calc(var0);
        return (float)hx + jit();
    }

    public static float y(class_1297 var0) {
        calc(var0);
        return (float)hy + jit();
    }

    public static float z(class_1297 var0) {
        calc(var0);
        return (float)hz + jit();
    }
}
