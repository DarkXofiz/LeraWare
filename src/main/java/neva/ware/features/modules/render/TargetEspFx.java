package neva.ware.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Arrays;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.impl.ColorSetting;
import org.joml.Matrix4f;

public final class TargetEspFx {
    private static final String[] TEX = new String[]{"ghost_glow", "star", "circle", "snowflake", "firefly", "heart", "end_crystal2"};
    private static final class_2960[] IDS = new class_2960[TEX.length];
    private static double clock;
    private static long lastNs;
    private static float[] sx = new float[2048];
    private static float[] sy = new float[2048];
    private static float[] sz = new float[2048];
    private static float[] ss = new float[2048];
    private static int[] sc = new int[2048];
    private static int n;
    private static double rx;
    private static double ry;
    private static double rz;
    private static double ux;
    private static double uy;
    private static double uz;
    private static final int[][] EDGES = new int[][]{{0, 2}, {0, 3}, {0, 4}, {0, 5}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 4}, {2, 5}, {3, 4}, {3, 5}};

    private TargetEspFx() {
    }

    private static void ensure(int var0) {
        if (var0 > sx.length) {
            int var1 = Math.max(var0, sx.length * 2);
            sx = Arrays.copyOf(sx, var1);
            sy = Arrays.copyOf(sy, var1);
            sz = Arrays.copyOf(sz, var1);
            ss = Arrays.copyOf(ss, var1);
            sc = Arrays.copyOf(sc, var1);
        }
    }

    private static void add(double var0, double var2, double var4, float var6, int var7) {
        if (var7 >>> 24 > 0 && !(var6 <= 0.0F)) {
            ensure(n + 1);
            sx[n] = (float)var0;
            sy[n] = (float)var2;
            sz[n] = (float)var4;
            ss[n] = var6;
            sc[n] = var7;
            n++;
        }
    }

    private static int clamp255(double var0) {
        return var0 < 0.0 ? 0 : (var0 > 255.0 ? 255 : (int)var0);
    }

    static int rgb(TargetESP var0, float var1, int var2, int var3, double var4) {
        switch ((TargetESP.ColorMode)var0.colorMode.getValue()) {
            case Rainbow:
                float var14 = (float)((var4 * 40.0 + var1 * 60.0 + var2 * (360.0 / Math.max(1, var3))) % 360.0) / 360.0F;
                return Color.HSBtoRGB(var14 < 0.0F ? var14 + 1.0F : var14, 0.7F, 1.0F) & 16777215;
            case Custom:
                ColorSetting var13 = var0.color.getValue();
                return var13.getRed() << 16 | var13.getGreen() << 8 | var13.getBlue();
            case Gradient:
                ColorSetting var6 = var0.color.getValue();
                ColorSetting var7 = var0.color2.getValue();
                double var8 = 0.5 + 0.5 * Math.sin(var4 * 1.3 + var1 * 3.0 + var2 * 0.8);
                int var10 = (int)(var6.getRed() + (var7.getRed() - var6.getRed()) * var8);
                int var11 = (int)(var6.getGreen() + (var7.getGreen() - var6.getGreen()) * var8);
                int var12 = (int)(var6.getBlue() + (var7.getBlue() - var6.getBlue()) * var8);
                return var10 << 16 | var11 << 8 | var12;
            default:
                return HudEditor.getColor((int)(var1 * 52.0F * 5.0F) + var2 * 20).getRGB() & 16777215;
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

    private static void path(TargetESP.Mode var0, int var1, int var2, double var3, double var5, double var7, double var9, double[] var11) {
        var11[3] = 1.0;
        switch (var0) {
            case Orbit:
                double var21 = var1 % 2 == 0 ? 1.0 : -1.0;
                double var22 = var3 * (2.2 + 0.35 * var1) * var21 + var1 * 1.7;
                double var23 = var5 * (0.86 + 0.14 * Math.sin(var1 * 1.9 + 0.6));
                var11[0] = Math.cos(var22) * var23;
                var11[1] = 0.0;
                var11[2] = Math.sin(var22) * var23;
                rotX(var11, var9 + var1 * 0.55);
                rotY(var11, var1 * ((Math.PI * 2) / var2));
                break;
            case Helix:
                double var20 = var3 * 2.3 + var1 * ((Math.PI * 2) / var2);
                var11[0] = Math.cos(var20) * var5;
                var11[2] = Math.sin(var20) * var5;
                var11[1] = Math.sin(var3 * 0.95) * var7 * 0.55;
                break;
            case Halo:
                double var19 = var3 * 1.7 + var1 * ((Math.PI * 2) / var2);
                var11[0] = Math.cos(var19) * var5;
                var11[1] = 0.0;
                var11[2] = Math.sin(var19) * var5;
                rotX(var11, var9 * 0.5);
                var11[1] += Math.sin(var3 * 0.6) * var7 * 0.42;
                break;
            case Tornado:
                double var12 = var3 * 0.3 + (double)var1 / var2;
                var12 -= Math.floor(var12);
                double var14 = var3 * 3.1 + var12 * 5.2 + var1 * ((Math.PI * 2) / var2);
                double var16 = var5 * (0.28 + 0.72 * var12);
                var11[0] = Math.cos(var14) * var16;
                var11[2] = Math.sin(var14) * var16;
                var11[1] = -var7 * 0.5 + var12 * var7 * 1.3;
                var11[3] = Math.pow(Math.sin(Math.PI * var12), 0.6);
                break;
            default:
                var11[0] = var11[1] = var11[2] = 0.0;
        }
    }

    public static void render(TargetESP var0, class_1297 var1, class_4587 var2, float var3) {
        class_310 var4 = class_310.method_1551();
        class_757 var5 = var4.field_1773;
        class_4184 var6 = var5.method_19418();
        class_243 var7 = var6.method_19326();
        long var8 = System.nanoTime();
        double var10 = lastNs == 0L ? 0.0 : Math.min(0.1, (var8 - lastNs) / 1.0E9);
        lastNs = var8;
        double var12 = var0.fxSpeed.getValue().floatValue();
        clock += var10 * var12;
        double var14 = var1.field_6014 + (var1.method_23317() - var1.field_6014) * var3 - var7.field_1352;
        double var16 = var1.field_6036 + (var1.method_23318() - var1.field_6036) * var3 - var7.field_1351;
        double var18 = var1.field_5969 + (var1.method_23321() - var1.field_5969) * var3 - var7.field_1350;
        double var20 = var1.method_17682();
        if (var20 < 0.3) {
            var20 = 1.8;
        }

        double var22 = Math.toRadians(-var6.method_19330());
        double var24 = Math.toRadians(var6.method_19329());
        rx = Math.cos(var22);
        ry = 0.0;
        rz = -Math.sin(var22);
        ux = Math.sin(var24) * Math.sin(var22);
        uy = Math.cos(var24);
        uz = Math.sin(var24) * Math.cos(var22);
        TargetESP.Mode var26 = var0.mode.getValue();
        double var27 = var0.radius.getValue().floatValue();
        double var29 = var16 + var20 * (0.5 + 0.5 * var0.height.getValue().floatValue());
        double var31 = Math.toRadians(var0.tilt.getValue().floatValue());
        int var33 = var0.trail.getValue();
        float var34 = var0.fade.getValue();
        float var35 = var0.alpha.getValue();
        float var36 = var0.particleSize.getValue();
        boolean var37 = var0.pulse.getValue();
        int var38 = var0.count.getValue();
        double[] var39 = new double[4];
        n = 0;
        if (var26 != TargetESP.Mode.Orbit && var26 != TargetESP.Mode.Helix && var26 != TargetESP.Mode.Halo && var26 != TargetESP.Mode.Tornado) {
            if (var26 == TargetESP.Mode.Diamond) {
                buildDiamond(
                    var0,
                    var14,
                    var16 + var20 + 0.3 + var27 * 0.35 + 0.05 * Math.sin(clock * 1.5) + 0.35 * var0.height.getValue().floatValue(),
                    var18,
                    var27 * 0.45,
                    var31,
                    var38,
                    var33,
                    var34,
                    var35,
                    var36,
                    var37
                );
            } else if (var26 == TargetESP.Mode.Sphere) {
                buildSphere(var0, var14, var29, var18, var27, var31, var38, var33, var34, var35, var36, var37);
            }
        } else {
            int var40 = var26 == TargetESP.Mode.Halo ? var38 * 2 : var38;
            double var41 = 0.035 / Math.max(0.35, Math.sqrt(var12));
            ensure(var40 * var33 + 8);

            for (int var43 = 0; var43 < var40; var43++) {
                for (int var44 = 0; var44 < var33; var44++) {
                    float var45 = (float)var44 / var33;
                    double var46 = clock - var44 * var41;
                    path(var26, var43, var40, var46, var27, var20, var31, var39);
                    double var48 = Math.pow(1.0 - var45, var34) * var39[3];
                    int var50 = clamp255(255.0 * var48 * var35);
                    if (var50 > 0) {
                        double var51 = var37 ? 1.0 + 0.16 * Math.sin(var46 * 4.0 + var43) : 1.0;
                        float var53 = (float)(var36 * (0.22 + 0.78 * Math.pow(1.0 - var45, 0.8)) * var51 * 0.5);
                        add(var14 + var39[0], var29 + var39[1], var18 + var39[2], var53, var50 << 24 | rgb(var0, var45, var43, var40, clock));
                    }
                }
            }
        }

        if (n != 0) {
            RenderSystem.enableDepthTest();
            if (!var0.depthTest.getValue()) {
                RenderSystem.disableDepthTest();
            }

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            int var54 = var0.sprite.getValue().ordinal();
            if (IDS[var54] == null) {
                IDS[var54] = class_2960.method_60655("nevaware", "textures/particles/" + TEX[var54] + ".png");
            }

            RenderSystem.setShaderTexture(0, IDS[var54]);
            RenderSystem.setShader(class_757::method_34543);
            Matrix4f var55 = var2.method_23760().method_23761();
            float var42 = var0.bloom.getValue();
            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
            flush(var55, 0.75F, 1.0F);
            if (var42 > 0.01F) {
                RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
                flush(var55, 0.38F * var42, 1.0F);
                flush(var55, 0.11F * var42, 2.4F);
            }

            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void flush(Matrix4f var0, float var1, float var2) {
        boolean var3 = false;

        for (int var4 = 0; var4 < n; var4++) {
            if ((int)((sc[var4] >>> 24) * var1) > 0) {
                var3 = true;
                break;
            }
        }

        if (var3) {
            class_287 var19 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1575);

            for (int var5 = 0; var5 < n; var5++) {
                int var6 = sc[var5];
                int var7 = Math.min(255, (int)((var6 >>> 24) * var1));
                if (var7 > 0) {
                    int var8 = var7 << 24 | var6 & 16777215;
                    float var9 = ss[var5] * var2;
                    float var10 = sx[var5];
                    float var11 = sy[var5];
                    float var12 = sz[var5];
                    float var13 = (float)(rx * var9);
                    float var14 = (float)(ry * var9);
                    float var15 = (float)(rz * var9);
                    float var16 = (float)(ux * var9);
                    float var17 = (float)(uy * var9);
                    float var18 = (float)(uz * var9);
                    var19.method_22918(var0, var10 - var13 - var16, var11 - var14 - var17, var12 - var15 - var18).method_22913(0.0F, 1.0F).method_39415(var8);
                    var19.method_22918(var0, var10 + var13 - var16, var11 + var14 - var17, var12 + var15 - var18).method_22913(1.0F, 1.0F).method_39415(var8);
                    var19.method_22918(var0, var10 + var13 + var16, var11 + var14 + var17, var12 + var15 + var18).method_22913(1.0F, 0.0F).method_39415(var8);
                    var19.method_22918(var0, var10 - var13 + var16, var11 - var14 + var17, var12 - var15 + var18).method_22913(0.0F, 0.0F).method_39415(var8);
                }
            }

            class_286.method_43433(var19.method_60800());
        }
    }

    private static void buildDiamond(
        TargetESP var0,
        double var1,
        double var3,
        double var5,
        double var7,
        double var9,
        int var11,
        int var12,
        float var13,
        float var14,
        float var15,
        boolean var16
    ) {
        double[][] var17 = new double[][]{
            {var7, 0.0, 0.0}, {-var7, 0.0, 0.0}, {0.0, var7 * 1.45, 0.0}, {0.0, -var7 * 1.45, 0.0}, {0.0, 0.0, var7}, {0.0, 0.0, -var7}
        };
        int var18 = Math.max(1, Math.min(10, var12 / 8));
        int var19 = var11 + 2;
        ensure(var18 * (EDGES.length * var19 + 8) + 8);
        double[] var20 = new double[4];

        for (int var21 = 0; var21 < var18; var21++) {
            float var22 = (float)var21 / var18;
            double var23 = clock - var21 * 0.045;
            double var25 = var23 * 1.5;
            double var27 = Math.sin(var23 * 0.8) * 0.25 + var9 * 0.4;
            double var29 = Math.pow(1.0 - var22, var13);
            int var31 = clamp255(255.0 * var29 * var14);
            if (var31 > 0) {
                double var32 = var16 ? 1.0 + 0.12 * Math.sin(var23 * 3.0) : 1.0;
                double[][] var34 = new double[6][3];

                for (int var35 = 0; var35 < 6; var35++) {
                    var20[0] = var17[var35][0] * var32;
                    var20[1] = var17[var35][1] * var32;
                    var20[2] = var17[var35][2] * var32;
                    rotX(var20, var27);
                    rotY(var20, var25);
                    var34[var35][0] = var20[0];
                    var34[var35][1] = var20[1];
                    var34[var35][2] = var20[2];
                }

                for (int var49 = 0; var49 < EDGES.length; var49++) {
                    double[] var36 = var34[EDGES[var49][0]];
                    double[] var37 = var34[EDGES[var49][1]];

                    for (int var38 = 0; var38 < var19; var38++) {
                        double var39 = (var38 + 0.5) / var19;
                        double var41 = var36[0] + (var37[0] - var36[0]) * var39;
                        double var43 = var36[1] + (var37[1] - var36[1]) * var39;
                        double var45 = var36[2] + (var37[2] - var36[2]) * var39;
                        float var47 = (float)(var43 / (var7 * 2.9) + 0.5);
                        int var48 = rgb(var0, Math.max(0.0F, Math.min(1.0F, var47)) * 0.6F + var22 * 0.3F, var49, EDGES.length, var23);
                        add(var1 + var41, var3 + var43, var5 + var45, (float)(var15 * 0.28), clamp255(var31 * 0.85) << 24 | var48);
                    }
                }

                for (int var50 = 0; var50 < 6; var50++) {
                    int var51 = rgb(var0, var50 / 6.0F, var50, 6, var23);
                    add(var1 + var34[var50][0], var3 + var34[var50][1], var5 + var34[var50][2], (float)(var15 * 0.5), var31 << 24 | var51);
                }

                add(var1, var3, var5, (float)(var15 * 0.85 * var32), clamp255(var31 * 0.55) << 24 | rgb(var0, 0.5F, 0, 1, var23));
            }
        }
    }

    private static void buildSphere(
        TargetESP var0,
        double var1,
        double var3,
        double var5,
        double var7,
        double var9,
        int var11,
        int var12,
        float var13,
        float var14,
        float var15,
        boolean var16
    ) {
        int var17 = var11 * 14;
        int var18 = Math.max(1, Math.min(6, var12 / 12));
        ensure(var17 * var18 + 8);
        double[] var19 = new double[4];

        for (int var20 = 0; var20 < var18; var20++) {
            float var21 = (float)var20 / var18;
            double var22 = clock - var20 * 0.05;
            double var24 = var22 * 0.9;
            double var26 = Math.pow(1.0 - var21, var13);

            for (int var28 = 0; var28 < var17; var28++) {
                double var29 = 1.0 - 2.0 * (var28 + 0.5) / var17;
                double var31 = Math.sqrt(Math.max(0.0, 1.0 - var29 * var29));
                double var33 = var28 * 2.399963229728653;
                double var35 = var7 * (1.0 + (var16 ? 0.06 * Math.sin(var22 * 2.0 + var29 * 3.0) : 0.0));
                var19[0] = Math.cos(var33) * var31 * var35;
                var19[1] = var29 * var35;
                var19[2] = Math.sin(var33) * var31 * var35;
                rotX(var19, var9);
                rotY(var19, var24);
                double var37 = 0.55 + 0.45 * Math.sin(var22 * 3.0 + var28 * 1.3);
                int var39 = clamp255(255.0 * var26 * var14 * var37);
                if (var39 > 0) {
                    float var40 = (float)(var15 * 0.3 * (var16 ? 0.8 + 0.4 * var37 : 1.0));
                    add(
                        var1 + var19[0],
                        var3 + var19[1],
                        var5 + var19[2],
                        var40,
                        var39 << 24 | rgb(var0, (float)(0.5 + 0.5 * var29) * 0.7F + var21 * 0.3F, var28 % 7, 7, var22)
                    );
                }
            }
        }
    }
}
