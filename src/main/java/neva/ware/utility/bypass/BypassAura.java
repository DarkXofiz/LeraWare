package neva.ware.utility.bypass;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_746;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.Aura;

public final class BypassAura {
    private static float yawVel;
    private static float pitchVel;
    private static float remYaw;
    private static float remPitch;
    private static double phase = ThreadLocalRandom.current().nextDouble() * 100.0;
    private static long lastApply;
    private static long reactUntil;
    private static float bias;
    private static float biasDir = 1.0F;
    private static int lastHitTicks;
    private static float fx;
    private static float fy = 0.65F;
    private static float fz;
    private static float gx;
    private static float gy = 0.65F;
    private static float gz;

    private BypassAura() {
    }

    public static void reset() {
        yawVel = 0.0F;
        pitchVel = 0.0F;
        remYaw = 0.0F;
        remPitch = 0.0F;
        lastApply = 0L;
        bias = 0.0F;
        lastHitTicks = 0;
    }

    private static class_243 aimPoint(class_1297 var0, boolean var1) {
        ThreadLocalRandom var2 = ThreadLocalRandom.current();
        if (var1) {
            fx = gx = (var2.nextFloat() - 0.5F) * 0.4F;
            fz = gz = (var2.nextFloat() - 0.5F) * 0.4F;
            fy = gy = 0.5F + var2.nextFloat() * 0.3F;
        }

        if (var2.nextFloat() < 0.05F) {
            gx = (var2.nextFloat() - 0.5F) * 0.5F;
            gz = (var2.nextFloat() - 0.5F) * 0.5F;
            gy = 0.45F + var2.nextFloat() * 0.4F;
        }

        fx = fx + (gx - fx) * 0.1F;
        fy = fy + (gy - fy) * 0.1F;
        fz = fz + (gz - fz) * 0.1F;
        class_238 var3 = var0.method_5829();
        double var4 = var3.method_17939();
        double var6 = var3.method_17940();
        double var8 = var3.method_17941();
        return new class_243(var0.method_23317() + fx * var4, var0.method_23318() + fy * var6, var0.method_23321() + fz * var8);
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

    public static void apply(Aura var0, boolean var1, int var2, int var3) {
        class_746 var4 = Module.mc.field_1724;
        class_1297 var5 = Aura.target;
        if (var4 != null && var5 != null) {
            boolean var6 = var4.method_6128() || ModuleManager.elytraPlus.isEnabled();
            class_243 var7 = var4.method_33571();
            if (var7 != null) {
                ThreadLocalRandom var8 = ThreadLocalRandom.current();
                long var9 = System.nanoTime();
                boolean var11 = var9 - lastApply > 250000000L;
                class_243 var12 = var6 ? var5.method_33571() : aimPoint(var5, var11);
                if (var12 != null) {
                    if (var11) {
                        var0.rotationYaw = var4.method_36454();
                        var0.rotationPitch = var4.method_36455();
                        yawVel = 0.0F;
                        pitchVel = 0.0F;
                        remYaw = 0.0F;
                        remPitch = 0.0F;
                        bias = var2 == 1 ? 1.5F + var8.nextFloat() * 2.0F : 0.0F;
                        reactUntil = var9 + (70L + var8.nextInt(80)) * 1000000L;
                    }

                    lastApply = var9;
                    double var13 = var12.field_1352 - var7.field_1352;
                    double var15 = var12.field_1351 - var7.field_1351;
                    double var17 = var12.field_1350 - var7.field_1350;
                    float var19 = (float)(Math.toDegrees(Math.atan2(var17, var13)) - 90.0);
                    float var20 = (float)(-Math.toDegrees(Math.atan2(var15, Math.hypot(var13, var17))));
                    phase = phase + (0.28 + var8.nextDouble() * 0.18);
                    float var21 = (float)(Math.sin(phase) * 0.6 + Math.sin(phase * 2.3 + 1.7) * 0.4);
                    float var22 = (float)(Math.sin(phase * 1.4 + 0.5) * 0.6 + Math.sin(phase * 2.9 + 2.1) * 0.4);
                    float var25;
                    float var26;
                    float var27;
                    float var28;
                    float var29;
                    if (var2 == 0) {
                        var25 = var1 ? 0.62F : 0.38F;
                        var26 = (var1 ? 46.0F : 34.0F) * (0.92F + var8.nextFloat() * 0.16F);
                        var27 = var26 * 0.55F;
                        var28 = 0.45F;
                        var29 = var1 ? 0.05F : 0.16F;
                    } else {
                        boolean var30 = var3 > lastHitTicks;
                        lastHitTicks = var3;
                        if (var30) {
                            biasDir = var8.nextBoolean() ? 1.0F : -1.0F;
                        }

                        boolean var31 = var3 <= 2;
                        if (!var31 && !var1) {
                            bias = bias + (2.6F - bias) * 0.18F;
                            var25 = 0.2F;
                            var26 = 16.0F * (0.9F + var8.nextFloat() * 0.2F);
                        } else {
                            bias *= 0.55F;
                            var25 = 0.78F;
                            var26 = 58.0F * (0.92F + var8.nextFloat() * 0.16F);
                        }

                        var27 = var26 * 0.5F;
                        var28 = var31 ? 0.6F : 0.4F;
                        var29 = !var31 && !var1 ? 0.14F : 0.04F;
                        var19 += biasDir * bias;
                        var20 += -biasDir * bias * 0.35F;
                    }

                    if (var6) {
                        var25 = Math.min(1.0F, var25 * 1.6F);
                        var26 *= 1.7F;
                        var27 *= 1.7F;
                    }

                    var20 = Math.max(-90.0F, Math.min(90.0F, var20 + var22 * var29));
                    var19 += var21 * var29;
                    float var23 = wrap(var19 - var0.rotationYaw);
                    float var24 = var20 - var0.rotationPitch;
                    float var38 = clamp(var23 * var25, var26);
                    float var39 = clamp(var24 * var25 * 0.85F, var27);
                    if (var9 < reactUntil) {
                        var38 = 0.0F;
                        var39 = 0.0F;
                    }

                    yawVel = yawVel + (var38 - yawVel) * var28;
                    pitchVel = pitchVel + (var39 - pitchVel) * var28;
                    double var32 = gcd();
                    float var34 = var0.rotationYaw + quant(yawVel, var32, true);
                    float var35 = Math.max(-90.0F, Math.min(90.0F, var0.rotationPitch + quant(pitchVel, var32, false)));
                    var0.rotationYaw = var34;
                    var0.rotationPitch = var35;
                    ModuleManager.rotations.fixRotation = var34;
                }
            }
        }
    }
}
