package neva.ware.features.modules.combat;

import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class AuraHvh {
    private AuraHvh() {
    }

    public static float[] compute(class_310 var0, class_1297 var1, boolean var2) {
        class_746 var3 = var0.field_1724;
        if (var3 != null && var1 != null) {
            class_243 var4 = var3.method_33571();
            class_238 var5 = var1.method_5829();
            class_243 var6 = closestPointOnBox(var4, var5);
            if (!var2 && !isPointVisible(var0, var4, var6)) {
                class_243 var7 = getVisiblePointOnBox(var0, var4, var5);
                if (var7 != null) {
                    var6 = var7;
                }
            }

            double var23 = var6.field_1352 - var4.field_1352;
            double var9 = var4.field_1351 - var6.field_1351;
            double var11 = var6.field_1350 - var4.field_1350;
            float var13 = var3.method_36454();
            float var14 = var3.method_36455();
            float var15 = var13 + class_3532.method_15393((float)(Math.toDegrees(Math.atan2(var11, var23)) - 90.0) - var13);
            float var16 = class_3532.method_15363((float)Math.toDegrees(Math.atan2(var9, Math.sqrt(var23 * var23 + var11 * var11))), -90.0F, 90.0F);
            double var17 = (Double)var0.field_1690.method_42495().method_41753();
            double var19 = Math.pow(var17 * 0.6 + 0.2, 3.0) * 1.2;
            float var21 = (float)(var15 - (var15 - var13) % var19);
            float var22 = class_3532.method_15363((float)(var16 - (var16 - var14) % var19), -90.0F, 90.0F);
            return new float[]{var21, var22};
        } else {
            return new float[]{var3 == null ? 0.0F : var3.method_36454(), var3 == null ? 0.0F : var3.method_36455()};
        }
    }

    private static class_243 closestPointOnBox(class_243 var0, class_238 var1) {
        double var2 = class_3532.method_15350(var0.field_1352, var1.field_1323, var1.field_1320);
        double var4 = class_3532.method_15350(var0.field_1351, var1.field_1322, var1.field_1325);
        double var6 = class_3532.method_15350(var0.field_1350, var1.field_1321, var1.field_1324);
        double var8 = var2 - var0.field_1352;
        double var10 = var4 - var0.field_1351;
        double var12 = var6 - var0.field_1350;
        return var8 * var8 + var10 * var10 + var12 * var12 < 1.0E-12 ? var1.method_1005() : new class_243(var2, var4, var6);
    }

    private static boolean isPointVisible(class_310 var0, class_243 var1, class_243 var2) {
        if (var0.field_1687 != null && var0.field_1724 != null) {
            class_3965 var3 = var0.field_1687.method_17742(new class_3959(var1, var2, class_3960.field_17558, class_242.field_1348, var0.field_1724));
            return var3 == null || var3.method_17783() == class_240.field_1333;
        } else {
            return false;
        }
    }

    private static class_243 getVisiblePointOnBox(class_310 var0, class_243 var1, class_238 var2) {
        class_243 var3 = closestPointOnBox(var1, var2);
        if (isPointVisible(var0, var1, var3)) {
            return var3;
        } else {
            class_243 var4 = var2.method_1005();
            if (isPointVisible(var0, var1, var4)) {
                return var4;
            } else {
                class_243 var5 = new class_243(var4.field_1352, var2.field_1325 - 0.05, var4.field_1350);
                if (isPointVisible(var0, var1, var5)) {
                    return var5;
                } else {
                    class_243 var6 = new class_243(var4.field_1352, var2.field_1322 + 0.05, var4.field_1350);
                    if (isPointVisible(var0, var1, var6)) {
                        return var6;
                    } else {
                        double[] var7 = new double[]{var2.field_1323 + 0.05, var2.field_1320 - 0.05};
                        double[] var8 = new double[]{var2.field_1322 + 0.05, var4.field_1351, var2.field_1325 - 0.05};
                        double[] var9 = new double[]{var2.field_1321 + 0.05, var2.field_1324 - 0.05};

                        for (double var13 : var8) {
                            for (double var18 : var7) {
                                for (double var23 : var9) {
                                    class_243 var25 = new class_243(var18, var13, var23);
                                    if (isPointVisible(var0, var1, var25)) {
                                        return var25;
                                    }
                                }
                            }
                        }

                        return null;
                    }
                }
            }
        }
    }
}
