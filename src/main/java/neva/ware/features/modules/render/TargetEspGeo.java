package neva.ware.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import org.joml.Matrix4f;

final class TargetEspGeo {
    private static final double TAU = Math.PI * 2;
    private static double clk;
    private static long lastNs;
    private static float[] vx = new float[16384];
    private static float[] vy = new float[16384];
    private static float[] vz = new float[16384];
    private static int[] vc = new int[16384];
    private static int vn;
    private static TargetESP M;
    private static float alphaS;
    private static double bx;
    private static double by;
    private static double bz;
    private static double W;
    private static double fillS;

    private TargetEspGeo() {
    }

    private static void ensure(int var0) {
        if (var0 > vx.length) {
            int var1 = Math.max(var0, vx.length * 2);
            vx = Arrays.copyOf(vx, var1);
            vy = Arrays.copyOf(vy, var1);
            vz = Arrays.copyOf(vz, var1);
            vc = Arrays.copyOf(vc, var1);
        }
    }

    private static void v(double var0, double var2, double var4, int var6) {
        ensure(vn + 1);
        vx[vn] = (float)var0;
        vy[vn] = (float)var2;
        vz[vn] = (float)var4;
        vc[vn] = var6;
        vn++;
    }

    private static void quad(
        double var0,
        double var2,
        double var4,
        int var6,
        double var7,
        double var9,
        double var11,
        int var13,
        double var14,
        double var16,
        double var18,
        int var20,
        double var21,
        double var23,
        double var25,
        int var27
    ) {
        v(var0, var2, var4, var6);
        v(var7, var9, var11, var13);
        v(var14, var16, var18, var20);
        v(var21, var23, var25, var27);
    }

    private static void tri(
        double var0, double var2, double var4, int var6, double var7, double var9, double var11, int var13, double var14, double var16, double var18, int var20
    ) {
        quad(var0, var2, var4, var6, var7, var9, var11, var13, var14, var16, var18, var20, var14, var16, var18, var20);
    }

    private static int C(float var0, int var1, int var2, double var3) {
        int var5 = (int)Math.max(0.0, Math.min(255.0, 255.0 * var3 * alphaS));
        return var5 << 24 | TargetEspFx.rgb(M, var0, var1, var2, clk);
    }

    private static int shade(int var0, double var1) {
        int var3 = (int)Math.min(255.0, (var0 >> 16 & 0xFF) * var1);
        int var4 = (int)Math.min(255.0, (var0 >> 8 & 0xFF) * var1);
        int var5 = (int)Math.min(255.0, (var0 & 0xFF) * var1);
        return var0 & 0xFF000000 | var3 << 16 | var4 << 8 | var5;
    }

    private static void line(double var0, double var2, double var4, double var6, double var8, double var10, double var12, int var14, int var15) {
        double var16 = var6 - var0;
        double var18 = var8 - var2;
        double var20 = var10 - var4;
        double var22 = (var0 + var6) * 0.5;
        double var24 = (var2 + var8) * 0.5;
        double var26 = (var4 + var10) * 0.5;
        double var28 = var18 * var26 - var20 * var24;
        double var30 = var20 * var22 - var16 * var26;
        double var32 = var16 * var24 - var18 * var22;
        double var34 = Math.sqrt(var28 * var28 + var30 * var30 + var32 * var32);
        if (!(var34 < 1.0E-9)) {
            double var36 = var12 / var34;
            var28 *= var36;
            var30 *= var36;
            var32 *= var36;
            double var38 = 0.35;
            int var40 = var14 & 16777215;
            int var41 = var15 & 16777215;
            quad(
                var0 - var28,
                var2 - var30,
                var4 - var32,
                var40,
                var0 - var28 * var38,
                var2 - var30 * var38,
                var4 - var32 * var38,
                var14,
                var6 - var28 * var38,
                var8 - var30 * var38,
                var10 - var32 * var38,
                var15,
                var6 - var28,
                var8 - var30,
                var10 - var32,
                var41
            );
            quad(
                var0 - var28 * var38,
                var2 - var30 * var38,
                var4 - var32 * var38,
                var14,
                var0 + var28 * var38,
                var2 + var30 * var38,
                var4 + var32 * var38,
                var14,
                var6 + var28 * var38,
                var8 + var30 * var38,
                var10 + var32 * var38,
                var15,
                var6 - var28 * var38,
                var8 - var30 * var38,
                var10 - var32 * var38,
                var15
            );
            quad(
                var0 + var28 * var38,
                var2 + var30 * var38,
                var4 + var32 * var38,
                var14,
                var0 + var28,
                var2 + var30,
                var4 + var32,
                var40,
                var6 + var28,
                var8 + var30,
                var10 + var32,
                var41,
                var6 + var28 * var38,
                var8 + var30 * var38,
                var10 + var32 * var38,
                var15
            );
        }
    }

    private static void arc(
        double var0,
        double var2,
        double var4,
        double var6,
        double var8,
        double var10,
        int var12,
        double var13,
        double var15,
        double var17,
        int var19,
        int var20
    ) {
        double var21 = var8;
        double var23 = var0 + Math.cos(var8) * var6;
        double var25 = var4 + Math.sin(var8) * var6;

        for (int var27 = 1; var27 <= var12; var27++) {
            double var28 = (double)var27 / var12;
            double var30 = var8 + (var10 - var8) * var28;
            double var32 = var0 + Math.cos(var30) * var6;
            double var34 = var4 + Math.sin(var30) * var6;
            float var36 = (float)((var21 % (Math.PI * 2) + (Math.PI * 2)) % (Math.PI * 2) / (Math.PI * 2));
            float var37 = (float)((var30 % (Math.PI * 2) + (Math.PI * 2)) % (Math.PI * 2) / (Math.PI * 2));
            double var38 = var15 + (var17 - var15) * ((double)(var27 - 1) / var12);
            double var40 = var15 + (var17 - var15) * var28;
            line(var23, var2, var25, var32, var2, var34, var13, C(var36, var19, var20, var38), C(var37, var19, var20, var40));
            var21 = var30;
            var23 = var32;
            var25 = var34;
        }
    }

    private static void disc(double var0, double var2, double var4, double var6, int var8, double var9, double var11, int var13) {
        for (int var14 = 0; var14 < var8; var14++) {
            double var15 = var14 * (Math.PI * 2) / var8;
            double var17 = (var14 + 1) * (Math.PI * 2) / var8;
            tri(
                var0,
                var2,
                var4,
                C(0.5F, var13, 1, var9),
                var0 + Math.cos(var15) * var6,
                var2,
                var4 + Math.sin(var15) * var6,
                C((float)((double)var14 / var8), var13, 1, var11),
                var0 + Math.cos(var17) * var6,
                var2,
                var4 + Math.sin(var17) * var6,
                C((float)((double)(var14 + 1) / var8), var13, 1, var11)
            );
        }
    }

    private static void rotX(double[] var0, double var1) {
        double var3 = Math.cos(var1);
        double var5 = Math.sin(var1);
        double var7 = var0[1] * var3 - var0[2] * var5;
        double var9 = var0[1] * var5 + var0[2] * var3;
        var0[1] = var7;
        var0[2] = var9;
    }

    private static void rotY(double[] var0, double var1) {
        double var3 = Math.cos(var1);
        double var5 = Math.sin(var1);
        double var7 = var0[0] * var3 + var0[2] * var5;
        double var9 = -var0[0] * var5 + var0[2] * var3;
        var0[0] = var7;
        var0[2] = var9;
    }

    static void render(TargetESP var0, class_1297 var1, class_4587 var2, float var3) {
        M = var0;
        class_310 var4 = class_310.method_1551();
        class_4184 var5 = var4.field_1773.method_19418();
        class_243 var6 = var5.method_19326();
        long var7 = System.nanoTime();
        double var9 = lastNs == 0L ? 0.0 : Math.min(0.1, (var7 - lastNs) / 1.0E9);
        lastNs = var7;
        clk = clk + var9 * var0.fxSpeed.getValue().floatValue();
        bx = var1.field_6014 + (var1.method_23317() - var1.field_6014) * var3 - var6.field_1352;
        by = var1.field_6036 + (var1.method_23318() - var1.field_6036) * var3 - var6.field_1351;
        bz = var1.field_5969 + (var1.method_23321() - var1.field_5969) * var3 - var6.field_1350;
        double var11 = var1.method_17682();
        if (var11 < 0.3) {
            var11 = 1.8;
        }

        alphaS = var0.alpha.getValue();
        W = var0.thickness.getValue().floatValue();
        fillS = var0.fill.getValue().floatValue();
        double var13 = var0.radius.getValue().floatValue();
        int var15 = var0.segments.getValue();
        int var16 = var0.count.getValue();
        double var17 = Math.toRadians(var0.tilt.getValue().floatValue());
        double var19 = var0.height.getValue().floatValue();
        boolean var21 = var0.pulse.getValue();
        vn = 0;
        switch ((TargetESP.Mode)var0.mode.getValue()) {
            case Reticle:
                reticle(var13, var15, var16, var21);
                break;
            case Magic:
                magic(var13, var15, var16, var11);
                break;
            case Scanner:
                scanner(var13, var15, var16, var11);
                break;
            case Gyro:
                gyro(var13, var15, var16, var11, var17, var19);
                break;
            case Dome:
                dome(var13, var15, var16, var11, var17, var19, var21);
                break;
            case Pillar:
                pillar(var13, var15, var16, var11, var19);
                break;
            case Marker:
                marker(var13, var11, var19);
                break;
            case Cube:
                cube(var13, var11, var21);
                break;
            default:
                return;
        }

        if (vn != 0) {
            RenderSystem.enableDepthTest();
            if (!var0.depthTest.getValue()) {
                RenderSystem.disableDepthTest();
            }

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.setShader(class_757::method_34540);
            Matrix4f var22 = var2.method_23760().method_23761();
            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
            flush(var22, 1.0F);
            float var23 = var0.bloom.getValue();
            if (var23 > 0.01F) {
                RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
                flush(var22, 0.3F * var23);
            }

            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void flush(Matrix4f var0, float var1) {
        boolean var2 = false;

        for (int var3 = 0; var3 < vn; var3++) {
            if ((int)((vc[var3] >>> 24) * var1) > 0) {
                var2 = true;
                break;
            }
        }

        if (var2) {
            class_287 var7 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);

            for (int var4 = 0; var4 < vn; var4++) {
                int var5 = vc[var4];
                int var6 = Math.min(255, (int)((var5 >>> 24) * var1));
                var7.method_22918(var0, vx[var4], vy[var4], vz[var4]).method_39415(var6 << 24 | var5 & 16777215);
            }

            class_286.method_43433(var7.method_60800());
        }
    }

    private static void reticle(double var0, int var2, int var3, boolean var4) {
        double var5 = by + 0.03;
        double var7 = clk;
        disc(bx, var5, bz, var0, var2, fillS * 0.02, fillS * 0.3, 0);
        double var9 = var7 * 2.2;
        double var11 = 1.3;
        byte var13 = 18;

        for (int var14 = 0; var14 < var13; var14++) {
            double var15 = (double)var14 / var13;
            double var17 = (double)(var14 + 1) / var13;
            double var19 = var9 - var15 * var11;
            double var21 = var9 - var17 * var11;
            double var23 = fillS * 0.9 * Math.pow(1.0 - var15, 1.6);
            double var25 = fillS * 0.9 * Math.pow(1.0 - var17, 1.6);
            tri(
                bx,
                var5,
                bz,
                C(0.5F, 0, 1, var23 * 0.2),
                bx + Math.cos(var19) * var0,
                var5,
                bz + Math.sin(var19) * var0,
                C(0.2F, 1, 2, var23),
                bx + Math.cos(var21) * var0,
                var5,
                bz + Math.sin(var21) * var0,
                C(0.2F, 1, 2, var25)
            );
        }

        line(bx, var5, bz, bx + Math.cos(var9) * var0, var5, bz + Math.sin(var9) * var0, W * 1.2, C(0.5F, 0, 2, 0.1), C(0.0F, 1, 2, 0.95));
        arc(bx, var5, bz, var0, 0.0, Math.PI * 2, var2, W * 1.3, 0.85, 0.85, 0, 1);
        int var27 = Math.max(3, var3 * 4);
        double var28 = (Math.PI * 2) / var27;
        double var29 = -var7 * 0.8;

        for (int var30 = 0; var30 < var27; var30++) {
            arc(bx, var5, bz, var0 * 0.8, var29 + var30 * var28, var29 + var30 * var28 + var28 * 0.55, 4, W, 0.9, 0.9, var30, var27);
        }

        for (int var31 = 0; var31 < 3; var31++) {
            arc(
                bx,
                var5,
                bz,
                var0 * 0.52,
                var7 * 1.3 + var31 * (Math.PI * 2) / 3.0,
                var7 * 1.3 + var31 * (Math.PI * 2) / 3.0 + 1.22,
                10,
                W * 1.6,
                1.0,
                0.4,
                var31,
                3
            );
        }

        for (int var32 = 0; var32 < 4; var32++) {
            double var20 = var7 * 0.4 + var32 * Math.PI / 2.0;
            line(
                bx + Math.cos(var20) * var0 * 1.04,
                var5,
                bz + Math.sin(var20) * var0 * 1.04,
                bx + Math.cos(var20) * var0 * 1.2,
                var5,
                bz + Math.sin(var20) * var0 * 1.2,
                W * 1.6,
                C(var32 / 4.0F, var32, 4, 0.95),
                C(var32 / 4.0F, var32, 4, 0.95)
            );
        }

        if (var4) {
            double var33 = var7 * 0.6 % 1.0;
            arc(bx, var5, bz, var0 * (0.35 + 0.85 * var33), 0.0, Math.PI * 2, var2, W, (1.0 - var33) * 0.8, (1.0 - var33) * 0.8, 0, 1);
        }
    }

    private static void magic(double var0, int var2, int var3, double var4) {
        double var6 = by + 0.03;
        double var8 = clk;
        disc(bx, var6, bz, var0, var2, fillS * 0.12, fillS * 0.04, 0);
        arc(bx, var6, bz, var0, 0.0, Math.PI * 2, var2, W * 1.4, 0.9, 0.9, 0, 1);
        arc(bx, var6, bz, var0 * 0.9, 0.0, Math.PI * 2, var2, W * 0.8, 0.7, 0.7, 0, 1);
        arc(bx, var6, bz, var0 * 0.4, 0.0, Math.PI * 2, var2 / 2, W, 0.8, 0.8, 0, 1);
        int var10 = var3 + 2;
        int var11 = var10 % 2 == 1 ? (var10 - 1) / 2 : Math.max(1, var10 / 2 - 1);

        for (int var12 = 0; var12 < 2; var12++) {
            double var13 = var12 == 0 ? var0 * 0.9 : var0 * 0.38;
            double var15 = var12 == 0 ? var8 * 0.5 : -var8 * 0.9;

            for (int var17 = 0; var17 < var10; var17++) {
                int var18 = (var17 + var11) % var10;
                double var19 = var15 + var17 * (Math.PI * 2) / var10;
                double var21 = var15 + var18 * (Math.PI * 2) / var10;
                line(
                    bx + Math.cos(var19) * var13,
                    var6,
                    bz + Math.sin(var19) * var13,
                    bx + Math.cos(var21) * var13,
                    var6,
                    bz + Math.sin(var21) * var13,
                    W * (var12 == 0 ? 1.1 : 0.9),
                    C((float)var17 / var10, var17, var10, 0.95),
                    C((float)var18 / var10, var18, var10, 0.95)
                );
            }
        }

        for (int var23 = 0; var23 < 48; var23++) {
            double var25 = var23 * (Math.PI * 2) / 48.0;
            double var27 = 0.2 + 0.8 * Math.pow(0.5 + 0.5 * Math.sin(var25 * 3.0 - var8 * 3.0), 2.0);
            line(
                bx + Math.cos(var25) * var0 * 0.93,
                var6,
                bz + Math.sin(var25) * var0 * 0.93,
                bx + Math.cos(var25) * var0 * 1.0,
                var6,
                bz + Math.sin(var25) * var0 * 1.0,
                W * 0.8,
                C(var23 / 48.0F, var23, 48, var27),
                C(var23 / 48.0F, var23, 48, var27)
            );
        }

        for (int var24 = 0; var24 < 12; var24++) {
            double var26 = var24 * (Math.PI * 2) / 12.0 + var8 * 0.2;
            double var28 = (var8 * 0.5 + var24 * 0.37) % 1.0;
            double var29 = (0.25 + 0.75 * var28) * var4 * 0.8;
            double var30 = bx + Math.cos(var26) * var0 * 0.9;
            double var31 = bz + Math.sin(var26) * var0 * 0.9;
            line(var30, var6, var31, var30, var6 + var29, var31, W * 1.2, C(var24 / 12.0F, var24, 12, (1.0 - var28) * 0.9), C(var24 / 12.0F, var24, 12, 0.0));
        }
    }

    private static void scanner(double var0, int var2, int var3, double var4) {
        double var6 = clk;
        double var8 = var0 * 0.8;
        int var10 = var3 * 3 + 3;

        for (int var11 = 0; var11 <= var10; var11++) {
            double var12 = var6 - var11 * 0.04;
            double var14 = by + (0.5 + 0.5 * Math.sin(var12 * 1.6)) * (var4 + 0.2) - 0.1;
            double var16 = Math.pow(1.0 - (double)var11 / (var10 + 1), 1.6);
            if (var11 == 0) {
                disc(bx, var14, bz, var8, var2, fillS * 0.05, fillS * 0.45, 0);
                arc(bx, var14, bz, var8, 0.0, Math.PI * 2, var2, W * 1.7, 1.0, 1.0, 0, 1);
            } else {
                arc(bx, var14, bz, var8, 0.0, Math.PI * 2, Math.max(12, var2 / 2), W, var16 * 0.7, var16 * 0.7, var11, var10);
            }
        }

        for (int var18 = 0; var18 < 8; var18++) {
            double var19 = var18 * (Math.PI * 2) / 8.0 + var6 * 0.3;
            double var20 = bx + Math.cos(var19) * var8;
            double var21 = bz + Math.sin(var19) * var8;
            line(var20, by - 0.1, var21, var20, by + var4 + 0.1, var21, W * 0.6, C(var18 / 8.0F, var18, 8, 0.05), C(var18 / 8.0F, var18, 8, 0.22));
        }
    }

    private static void gyro(double var0, int var2, int var3, double var4, double var6, double var8) {
        double var10 = clk;
        double var12 = by + var4 * (0.5 + 0.5 * var8);
        int var14 = Math.max(1, Math.min(6, var3));
        double[] var15 = new double[4];

        for (int var16 = 0; var16 < var14; var16++) {
            double var17 = var0 * (1.0 - 0.09 * var16);
            double var19 = var16 % 2 == 0 ? 1.0 : -1.0;
            double var21 = var10 * (0.9 + 0.35 * var16) * var19 + var16 * 1.1;
            double var23 = var6 + var16 * (Math.PI / var14) + Math.sin(var10 * 0.5 + var16) * 0.35;
            double var25 = var10 * 2.4 * var19 + var16;
            double var27 = 0.0;
            double var29 = 0.0;
            double var31 = 0.0;
            double var33 = 0.0;
            double var35 = 0.0;
            double var37 = 0.0;
            double var39 = 9.0;

            for (int var41 = 0; var41 <= var2; var41++) {
                double var42 = var41 * (Math.PI * 2) / var2;
                var15[0] = Math.cos(var42) * var17;
                var15[1] = 0.0;
                var15[2] = Math.sin(var42) * var17;
                rotX(var15, var23);
                rotY(var15, var21);
                double var44 = bx + var15[0];
                double var46 = var12 + var15[1];
                double var48 = bz + var15[2];
                double var50 = ((var25 - var42) * var19 % (Math.PI * 2) + (Math.PI * 2)) % (Math.PI * 2) / (Math.PI * 2);
                double var52 = 0.16 + 0.84 * Math.pow(1.0 - var50, 2.6);
                if (var41 > 0) {
                    float var54 = (float)var41 / var2;
                    line(
                        var27, var29, var31, var44, var46, var48, W * (1.0 + (1.0 - var50) * 0.8), C(var54, var16, var14, var52), C(var54, var16, var14, var52)
                    );
                }

                if (var50 < var39) {
                    var39 = var50;
                    var33 = var44;
                    var35 = var46;
                    var37 = var48;
                }

                var27 = var44;
                var29 = var46;
                var31 = var48;
            }

            line(var33, var35, var37, var33 + 0.001, var35 + 0.001, var37 + 0.001, W * 3.0, C(0.5F, var16, var14, 1.0), C(0.5F, var16, var14, 1.0));
        }
    }

    private static void dome(double var0, int var2, int var3, double var4, double var6, double var8, boolean var10) {
        double var11 = clk;
        double var13 = by + var4 * (0.5 + 0.5 * var8);
        int var15 = var3 * 2 + 3;
        byte var16 = 14;
        double var17 = Math.sin(var11 * 1.1);
        double var19 = var11 * 0.3;
        double var21 = var10 ? 1.0 + 0.03 * Math.sin(var11 * 2.0) : 1.0;
        double[] var23 = new double[4];
        double var24 = var0 * 1.15 * var21;
        int var26 = Math.max(16, var2 / 2);

        for (int var27 = 0; var27 < var15; var27++) {
            double var28 = -1.0 + 2.0 * (var27 + 1.0) / (var15 + 1);
            double var30 = Math.exp(-((var28 - var17) * (var28 - var17)) / 0.05);
            double var32 = 0.14 + 0.86 * var30;
            double var34 = Math.sqrt(Math.max(0.0, 1.0 - var28 * var28)) * var24;
            double var36 = 0.0;
            double var38 = 0.0;
            double var40 = 0.0;

            for (int var42 = 0; var42 <= var26; var42++) {
                double var43 = var42 * (Math.PI * 2) / var26;
                var23[0] = Math.cos(var43) * var34;
                var23[1] = var28 * var24;
                var23[2] = Math.sin(var43) * var34;
                rotX(var23, var6 * 0.5);
                rotY(var23, var19);
                double var45 = bx + var23[0];
                double var47 = var13 + var23[1];
                double var49 = bz + var23[2];
                if (var42 > 0) {
                    line(
                        var36,
                        var38,
                        var40,
                        var45,
                        var47,
                        var49,
                        W * (0.8 + var30),
                        C((float)(0.5 + 0.5 * var28), var27, var15, var32),
                        C((float)(0.5 + 0.5 * var28), var27, var15, var32)
                    );
                }

                var36 = var45;
                var38 = var47;
                var40 = var49;
            }
        }

        for (int var51 = 0; var51 < var16; var51++) {
            double var53 = var51 * (Math.PI * 2) / var16;
            double var55 = 0.0;
            double var57 = 0.0;
            double var59 = 0.0;

            for (int var61 = 0; var61 <= 24; var61++) {
                double var37 = -1.0 + 2.0 * var61 / 24.0;
                double var39 = Math.sqrt(Math.max(0.0, 1.0 - var37 * var37)) * var24;
                var23[0] = Math.cos(var53) * var39;
                var23[1] = var37 * var24;
                var23[2] = Math.sin(var53) * var39;
                rotX(var23, var6 * 0.5);
                rotY(var23, var19);
                double var41 = bx + var23[0];
                double var67 = var13 + var23[1];
                double var69 = bz + var23[2];
                double var71 = 0.16 + 0.5 * Math.exp(-((var37 - var17) * (var37 - var17)) / 0.08);
                if (var61 > 0) {
                    line(
                        var55,
                        var57,
                        var59,
                        var41,
                        var67,
                        var69,
                        W * 0.7,
                        C((float)(0.5 + 0.5 * var37), var51, var16, var71),
                        C((float)(0.5 + 0.5 * var37), var51, var16, var71)
                    );
                }

                var55 = var41;
                var57 = var67;
                var59 = var69;
            }
        }

        if (fillS > 0.01) {
            for (int var52 = 0; var52 <= var15; var52++) {
                double var54 = var52 == 0 ? -1.0 : -1.0 + 2.0 * var52 / (var15 + 1);
                double var56 = var52 == var15 ? 1.0 : -1.0 + 2.0 * (var52 + 1.0) / (var15 + 1);
                double var58 = (var54 + var56) * 0.5;
                double var60 = fillS * 0.22 * (0.25 + 0.75 * Math.exp(-((var58 - var17) * (var58 - var17)) / 0.15));
                int var62 = C((float)(0.5 + 0.5 * var58), var52, var15 + 1, var60);

                for (int var63 = 0; var63 < var16; var63++) {
                    double var64 = var63 * (Math.PI * 2) / var16;
                    double var65 = (var63 + 1) * (Math.PI * 2) / var16;
                    double[][] var66 = new double[][]{{var64, var54}, {var65, var54}, {var65, var56}, {var64, var56}};
                    double[] var68 = new double[12];

                    for (int var44 = 0; var44 < 4; var44++) {
                        double var70 = Math.sqrt(Math.max(0.0, 1.0 - var66[var44][1] * var66[var44][1])) * var24;
                        var23[0] = Math.cos(var66[var44][0]) * var70;
                        var23[1] = var66[var44][1] * var24;
                        var23[2] = Math.sin(var66[var44][0]) * var70;
                        rotX(var23, var6 * 0.5);
                        rotY(var23, var19);
                        var68[var44 * 3] = bx + var23[0];
                        var68[var44 * 3 + 1] = var13 + var23[1];
                        var68[var44 * 3 + 2] = bz + var23[2];
                    }

                    quad(
                        var68[0],
                        var68[1],
                        var68[2],
                        var62,
                        var68[3],
                        var68[4],
                        var68[5],
                        var62,
                        var68[6],
                        var68[7],
                        var68[8],
                        var62,
                        var68[9],
                        var68[10],
                        var68[11],
                        var62
                    );
                }
            }
        }
    }

    private static void pillar(double var0, int var2, int var3, double var4, double var6) {
        double var8 = clk;
        double var10 = var0 * 0.55;
        double var12 = by + var4 + 1.2 + 0.5 * var6;

        for (int var14 = 0; var14 < var2; var14++) {
            double var15 = var14 * (Math.PI * 2) / var2;
            double var17 = (var14 + 1) * (Math.PI * 2) / var2;
            double var19 = 0.65 + 0.35 * Math.sin(var15 * 5.0 + var8 * 3.0);
            double var21 = 0.65 + 0.35 * Math.sin(var17 * 5.0 + var8 * 3.0);
            float var23 = (float)var14 / var2;
            float var24 = (float)(var14 + 1) / var2;
            quad(
                bx + Math.cos(var15) * var10,
                by,
                bz + Math.sin(var15) * var10,
                C(var23, 0, 1, fillS * 0.85 * var19),
                bx + Math.cos(var17) * var10,
                by,
                bz + Math.sin(var17) * var10,
                C(var24, 0, 1, fillS * 0.85 * var21),
                bx + Math.cos(var17) * var10,
                var12,
                bz + Math.sin(var17) * var10,
                C(var24, 0, 1, 0.0),
                bx + Math.cos(var15) * var10,
                var12,
                bz + Math.sin(var15) * var10,
                C(var23, 0, 1, 0.0)
            );
        }

        disc(bx, by + 0.03, bz, var10 * 1.8, var2, fillS * 0.35, 0.0, 0);
        arc(bx, by + 0.03, bz, var10, 0.0, Math.PI * 2, var2, W * 1.5, 0.95, 0.95, 0, 1);
        arc(bx, var12, bz, var10, 0.0, Math.PI * 2, var2, W, 0.25, 0.25, 0, 1);
        int var33 = Math.max(1, var3);

        for (int var34 = 0; var34 < var33; var34++) {
            double var16 = 0.0;
            double var18 = 0.0;
            double var20 = 0.0;

            for (int var22 = 0; var22 <= 40; var22++) {
                double var35 = var22 / 40.0;
                double var25 = var35 * Math.PI * 4.0 + var8 * 1.5 + var34 * (Math.PI * 2) / var33;
                double var27 = bx + Math.cos(var25) * var10;
                double var29 = by + var35 * (var12 - by);
                double var31 = bz + Math.sin(var25) * var10;
                if (var22 > 0) {
                    line(
                        var16,
                        var18,
                        var20,
                        var27,
                        var29,
                        var31,
                        W * 1.2,
                        C((float)var35, var34, var33, (1.0 - var35) * 0.9),
                        C((float)var35, var34, var33, (1.0 - var35) * 0.9)
                    );
                }

                var16 = var27;
                var18 = var29;
                var20 = var31;
            }
        }
    }

    private static void marker(double var0, double var2, double var4) {
        double var6 = clk;
        double var8 = var0 * 0.34;
        double var10 = var8 * 1.6;
        double var12 = by + var2 + 0.55 + 0.06 * Math.sin(var6 * 2.0) + 0.35 * var4;
        double var14 = var6 * 1.8;
        double var16 = 0.12 * Math.sin(var6);
        double[][] var18 = new double[6][3];
        double[] var19 = new double[4];

        for (int var20 = 0; var20 < 6; var20++) {
            if (var20 == 0) {
                var19[0] = 0.0;
                var19[1] = var10;
                var19[2] = 0.0;
            } else if (var20 == 1) {
                var19[0] = 0.0;
                var19[1] = -var10;
                var19[2] = 0.0;
            } else {
                double var21 = (var20 - 2) * Math.PI / 2.0;
                var19[0] = Math.cos(var21) * var8;
                var19[1] = 0.0;
                var19[2] = Math.sin(var21) * var8;
            }

            rotX(var19, var16);
            rotY(var19, var14);
            var18[var20][0] = bx + var19[0];
            var18[var20][1] = var12 + var19[1];
            var18[var20][2] = bz + var19[2];
        }

        double var67 = 0.35;
        double var22 = 0.8;
        double var24 = 0.45;
        double var26 = Math.sqrt(var67 * var67 + var22 * var22 + var24 * var24);
        var67 /= var26;
        var22 /= var26;
        var24 /= var26;
        double var28 = 0.35 + 0.6 * fillS;

        for (int var30 = 0; var30 < 4; var30++) {
            int var31 = 2 + var30;
            int var32 = 2 + (var30 + 1) % 4;

            for (int var33 = 0; var33 < 2; var33++) {
                int var34 = var33 == 0 ? 0 : 1;
                double[] var35 = var18[var34];
                double[] var36 = var18[var31];
                double[] var37 = var18[var32];
                double var38 = var36[0] - var35[0];
                double var40 = var36[1] - var35[1];
                double var42 = var36[2] - var35[2];
                double var44 = var37[0] - var35[0];
                double var46 = var37[1] - var35[1];
                double var48 = var37[2] - var35[2];
                double var50 = var40 * var48 - var42 * var46;
                double var52 = var42 * var44 - var38 * var48;
                double var54 = var38 * var46 - var40 * var44;
                double var56 = (var35[0] + var36[0] + var37[0]) / 3.0 - bx;
                double var58 = (var35[1] + var36[1] + var37[1]) / 3.0 - var12;
                double var60 = (var35[2] + var36[2] + var37[2]) / 3.0 - bz;
                if (var50 * var56 + var52 * var58 + var54 * var60 < 0.0) {
                    var50 = -var50;
                    var52 = -var52;
                    var54 = -var54;
                }

                double var62 = Math.sqrt(var50 * var50 + var52 * var52 + var54 * var54);
                if (!(var62 < 1.0E-9)) {
                    double var64 = Math.max(0.0, (var50 * var67 + var52 * var22 + var54 * var24) / var62);
                    int var66 = shade(C(var30 / 4.0F + var33 * 0.5F, var30 + var33 * 4, 8, var28), 0.4 + 0.75 * var64);
                    tri(var35[0], var35[1], var35[2], var66, var36[0], var36[1], var36[2], var66, var37[0], var37[1], var37[2], var66);
                }
            }
        }

        for (int var71 = 0; var71 < 4; var71++) {
            int var72 = 2 + var71;
            int var73 = 2 + (var71 + 1) % 4;
            int var74 = C(var71 / 4.0F, var71, 4, 1.0);
            line(var18[0][0], var18[0][1], var18[0][2], var18[var72][0], var18[var72][1], var18[var72][2], W * 1.3, var74, var74);
            line(var18[1][0], var18[1][1], var18[1][2], var18[var72][0], var18[var72][1], var18[var72][2], W * 1.3, var74, var74);
            line(var18[var72][0], var18[var72][1], var18[var72][2], var18[var73][0], var18[var73][1], var18[var73][2], W * 1.3, var74, var74);
        }

        line(bx, var12 - var10, bz, bx, by + var2 + 0.08, bz, W * 1.2, C(0.5F, 0, 1, 0.9), C(0.5F, 0, 1, 0.0));
    }

    private static void cube(double var0, double var2, boolean var4) {
        double var5 = clk;
        double var7 = var0 * 0.55;
        double var9 = var2 * 0.5 + 0.12;
        double var11 = by + var2 * 0.5;
        double var13 = var5 * 0.7;
        double var15 = var4 ? 1.0 + 0.04 * Math.sin(var5 * 3.0) : 1.0;
        double[][] var17 = new double[8][3];
        double[] var18 = new double[4];

        for (int var19 = 0; var19 < 8; var19++) {
            double var20 = (var19 & 1) == 0 ? -1.0 : 1.0;
            double var22 = (var19 & 2) == 0 ? -1.0 : 1.0;
            double var24 = (var19 & 4) == 0 ? -1.0 : 1.0;
            var18[0] = var20 * var7 * var15;
            var18[1] = var22 * var9 * var15;
            var18[2] = var24 * var7 * var15;
            rotY(var18, var13);
            var17[var19][0] = bx + var18[0];
            var17[var19][1] = var11 + var18[1];
            var17[var19][2] = bz + var18[2];
        }

        int[][] var26 = new int[][]{{0, 1, 3, 2}, {1, 5, 7, 3}, {5, 4, 6, 7}, {4, 0, 2, 6}};

        for (int var27 = 0; var27 < 4; var27++) {
            int[] var21 = var26[var27];
            double[] var31 = var17[var21[0]];
            double[] var23 = var17[var21[1]];
            double[] var36 = var17[var21[2]];
            double[] var25 = var17[var21[3]];
            quad(
                var31[0],
                var31[1],
                var31[2],
                C(var27 / 4.0F, var27, 4, fillS * 0.5),
                var23[0],
                var23[1],
                var23[2],
                C(var27 / 4.0F, var27, 4, fillS * 0.5),
                var36[0],
                var36[1],
                var36[2],
                C(var27 / 4.0F, var27, 4, fillS * 0.04),
                var25[0],
                var25[1],
                var25[2],
                C(var27 / 4.0F, var27, 4, fillS * 0.04)
            );
        }

        int[][] var28 = new int[][]{{0, 1, 5, 4}, {2, 3, 7, 6}};

        for (int var29 = 0; var29 < 2; var29++) {
            int[] var32 = var28[var29];
            int var34 = C(0.5F, var29, 2, fillS * 0.12);
            quad(
                var17[var32[0]][0],
                var17[var32[0]][1],
                var17[var32[0]][2],
                var34,
                var17[var32[1]][0],
                var17[var32[1]][1],
                var17[var32[1]][2],
                var34,
                var17[var32[2]][0],
                var17[var32[2]][1],
                var17[var32[2]][2],
                var34,
                var17[var32[3]][0],
                var17[var32[3]][1],
                var17[var32[3]][2],
                var34
            );
        }

        for (int var30 = 0; var30 < 8; var30++) {
            for (int var33 = 0; var33 < 3; var33++) {
                int var35 = var30 ^ 1 << var33;
                double[] var37 = var17[var30];
                double[] var38 = var17[var35];
                if (var30 < var35) {
                    line(var37[0], var37[1], var37[2], var38[0], var38[1], var38[2], W * 0.6, C(var30 / 8.0F, var30, 8, 0.3), C(var35 / 8.0F, var35, 8, 0.3));
                }

                line(
                    var37[0],
                    var37[1],
                    var37[2],
                    var37[0] + (var38[0] - var37[0]) * 0.34,
                    var37[1] + (var38[1] - var37[1]) * 0.34,
                    var37[2] + (var38[2] - var37[2]) * 0.34,
                    W * 1.6,
                    C(var30 / 8.0F, var30, 8, 1.0),
                    C(var30 / 8.0F, var30, 8, 1.0)
                );
            }
        }
    }
}
