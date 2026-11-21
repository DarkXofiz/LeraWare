package neva.ware.utility.bypass;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_746;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.AttackAura;

public final class SharpAura {
    private static float yawVel;
    private static float pitchVel;
    private static float remYaw;
    private static float remPitch;
    private static long lastApply;
    private static long reactUntil;
    private static float fx;
    private static float fy = 0.6F;
    private static float fz;
    private static float gx;
    private static float gy = 0.6F;
    private static float gz;
    private static int retarget;
    private static double phase = ThreadLocalRandom.current().nextDouble() * 100.0;

    private SharpAura() {
    }

    public static void reset() {
        yawVel = 0.0F;
        pitchVel = 0.0F;
        remYaw = 0.0F;
        remPitch = 0.0F;
        lastApply = 0L;
        retarget = 0;
    }

    private static double gcd() {
        try {
            double var0 = (Double)Module.mc.field_1690.method_42495().method_41753();
            double var2 = var0 * 0.6 + 0.2;
            return var2 * var2 * var2 * 1.2;
        } catch (Throwable var4) {
            return 0.0;
        }
    }

    private static float wrap(float var0) {
        var0 %= 360.0F;
        if (var0 >= 180.0F) {
            var0 -= 360.0F;
        }

        if (var0 < -180.0F) {
            var0 += 360.0F;
        }

        return var0;
    }

    private static float clamp(float var0, float var1) {
        return Math.max(-var1, Math.min(var1, var0));
    }

    private static float quant(float var0, double var1, boolean var3) {
        if (var1 <= 1.0E-6) {
            return var0;
        } else {
            float var4 = var0 + (var3 ? remYaw : remPitch);
            float var5 = (float)(Math.round(var4 / var1) * var1);
            if (var3) {
                remYaw = var4 - var5;
            } else {
                remPitch = var4 - var5;
            }

            return var5;
        }
    }

    private static class_243 aimPoint(class_1297 var0, boolean var1) {
        ThreadLocalRandom var2 = ThreadLocalRandom.current();
        if (var1) {
            fx = gx = (var2.nextFloat() - 0.5F) * 0.4F;
            fz = gz = (var2.nextFloat() - 0.5F) * 0.4F;
            fy = gy = 0.5F + var2.nextFloat() * 0.3F;
            retarget = 2 + var2.nextInt(4);
        }

        if (--retarget <= 0) {
            gx = (var2.nextFloat() - 0.5F) * 0.7F;
            gz = (var2.nextFloat() - 0.5F) * 0.7F;
            gy = 0.35F + var2.nextFloat() * 0.55F;
            retarget = 3 + var2.nextInt(6);
        }

        float var3 = 0.35F + var2.nextFloat() * 0.15F;
        fx = fx + (gx - fx) * var3;
        fy = fy + (gy - fy) * var3;
        fz = fz + (gz - fz) * var3;
        class_238 var4 = var0.method_5829();
        double var5 = var4.method_17939();
        double var7 = var4.method_17940();
        double var9 = var4.method_17941();
        return new class_243(var0.method_23317() + fx * var5, var0.method_23318() + fy * var7, var0.method_23321() + fz * var9);
    }

    public static void apply(AttackAura var0, boolean var1, int var2) {
        class_746 var3 = Module.mc.field_1724;
        class_1297 var4 = AttackAura.target;
        if (var3 != null && var4 != null) {
            boolean var5 = var3.method_6128() || ModuleManager.elytraPlus.isEnabled();
            class_243 var6 = var3.method_33571();
            if (var6 != null) {
                ThreadLocalRandom var7 = ThreadLocalRandom.current();
                long var8 = System.nanoTime();
                boolean var10 = var8 - lastApply > 250000000L;
                class_243 var11 = var5 ? var4.method_33571() : aimPoint(var4, var10);
                if (var11 != null) {
                    if (var10) {
                        var0.rotationYaw = var3.method_36454();
                        var0.rotationPitch = var3.method_36455();
                        yawVel = 0.0F;
                        pitchVel = 0.0F;
                        remYaw = 0.0F;
                        remPitch = 0.0F;
                        reactUntil = var8 + (50L + var7.nextInt(60)) * 1000000L;
                    }

                    lastApply = var8;
                    double var12 = var11.field_1352 - var6.field_1352;
                    double var14 = var11.field_1351 - var6.field_1351;
                    double var16 = var11.field_1350 - var6.field_1350;
                    float var18 = (float)(Math.toDegrees(Math.atan2(var16, var12)) - 90.0);
                    float var19 = (float)(-Math.toDegrees(Math.atan2(var14, Math.hypot(var12, var16))));
                    phase = phase + (0.55 + var7.nextDouble() * 0.35);
                    float var20 = var1 ? 0.35F : 0.6F;
                    var18 += (float)(Math.sin(phase) * 0.6 + Math.sin(phase * 2.7 + 1.3) * 0.4 + (var7.nextDouble() - 0.5) * 0.5) * var20;
                    var19 += (float)(Math.sin(phase * 1.3 + 0.7) * 0.6 + Math.sin(phase * 3.1 + 2.2) * 0.4 + (var7.nextDouble() - 0.5) * 0.4) * var20 * 0.7F;
                    var19 = Math.max(-90.0F, Math.min(90.0F, var19));
                    float var21 = wrap(var18 - var0.rotationYaw);
                    float var22 = var19 - var0.rotationPitch;
                    float var23 = (var1 ? 0.86F : 0.74F) * (0.92F + var7.nextFloat() * 0.12F);
                    float var24 = (var1 ? 80.0F : 62.0F) * (0.9F + var7.nextFloat() * 0.2F) * (var5 ? 1.5F : 1.0F);
                    float var25 = var24 * 0.6F;
                    float var26 = clamp(var21 * var23, var24);
                    float var27 = clamp(var22 * var23 * 0.9F, var25);
                    if (var8 < reactUntil) {
                        var26 = 0.0F;
                        var27 = 0.0F;
                    }

                    float var28 = 38.0F * (var5 ? 1.6F : 1.0F);
                    yawVel = yawVel + clamp(var26 - yawVel, var28);
                    pitchVel = pitchVel + clamp(var27 - pitchVel, var28 * 0.65F);
                    if (yawVel * var21 > 0.0F && Math.abs(yawVel) > Math.abs(var21)) {
                        yawVel = var21;
                    }

                    if (pitchVel * var22 > 0.0F && Math.abs(pitchVel) > Math.abs(var22)) {
                        pitchVel = var22;
                    }

                    double var29 = gcd();
                    float var31 = var0.rotationYaw + quant(yawVel, var29, true);
                    float var32 = Math.max(-90.0F, Math.min(90.0F, var0.rotationPitch + quant(pitchVel, var29, false)));
                    var0.rotationYaw = var31;
                    var0.rotationPitch = var32;
                    ModuleManager.rotations.fixRotation = var31;
                }
            }
        }
    }
}
