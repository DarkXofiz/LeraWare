package neva.ware.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_243;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.Timer;
import neva.ware.utility.render.TextureStorage;
import org.joml.Matrix4f;

public final class JumpCircleFx {
    static final Set<String> FX = Set.of("Ring", "Ripple", "Shockwave", "Hexagon", "Star", "Spiral", "Pulse", "Sparks", "Flower", "Vortex");
    public static Setting<?> MODE;
    public static final Setting<JumpCircleFx.ColorMode> colorMode = new Setting<>("Color Mode", JumpCircleFx.ColorMode.Sync, var0 -> all());
    public static final Setting<ColorSetting> color = new Setting<>(
        "Color",
        new ColorSetting(new Color(-6595329, true)),
        var0 -> all() && (colorMode.is(JumpCircleFx.ColorMode.Custom) || colorMode.is(JumpCircleFx.ColorMode.Gradient))
    );
    public static final Setting<ColorSetting> color2 = new Setting<>(
        "Second Color", new ColorSetting(new Color(-12793601, true)), var0 -> all() && colorMode.is(JumpCircleFx.ColorMode.Gradient)
    );
    public static final Setting<Integer> lifetime = new Setting<>("Lifetime", 1200, 300, 4000, var0 -> all());
    public static final Setting<Float> size = new Setting<>("Size", 1.4F, 0.3F, 5.0F, var0 -> all());
    public static final Setting<Float> thickness = new Setting<>("Thickness", 3.0F, 0.5F, 10.0F, var0 -> fxVis());
    public static final Setting<Boolean> glow = new Setting<>("Glow", Boolean.TRUE, var0 -> all());
    public static final Setting<Float> glowSize = new Setting<>("Glow Size", 3.0F, 1.0F, 8.0F, var0 -> all() && glow.getValue());
    public static final Setting<Boolean> fill = new Setting<>("Fill", Boolean.FALSE, var0 -> fxVis());
    public static final Setting<Integer> fillAlpha = new Setting<>("Fill Alpha", 35, 1, 100, var0 -> fxVis() && fill.getValue());
    public static final Setting<Integer> rings = new Setting<>("Rings", 3, 1, 6, var0 -> fxVis() && (is("Ripple") || is("Pulse")));
    public static final Setting<Integer> points = new Setting<>(
        "Points", 5, 3, 12, var0 -> fxVis() && (is("Star") || is("Flower") || is("Spiral") || is("Vortex"))
    );
    public static final Setting<Integer> sparkCount = new Setting<>("Spark Count", 24, 4, 80, var0 -> fxVis() && is("Sparks"));
    public static final Setting<Float> spin = new Setting<>("Spin Speed", 1.5F, -10.0F, 10.0F, var0 -> all());
    public static final Setting<Float> rise = new Setting<>("Rise", 0.0F, 0.0F, 2.0F, var0 -> all());
    public static final Setting<Boolean> bounce = new Setting<>("Bounce", Boolean.FALSE, var0 -> all());
    public static final Setting<Boolean> throughWalls = new Setting<>("Through Walls", Boolean.FALSE, var0 -> all());
    private static class_2960 GLOW;
    private static Matrix4f mat;
    private static class_287 buf;
    private static double ox;
    private static double oy;
    private static double oz;
    private static float alpha;
    private static double timeSec;
    private static boolean noFill;
    private static int vcount;
    private static final double[] PX = new double[512];
    private static final double[] PZ = new double[512];

    private static boolean all() {
        return MODE != null;
    }

    private static boolean fxVis() {
        return MODE != null && FX.contains(String.valueOf(MODE.getValue()));
    }

    private JumpCircleFx() {
    }

    private static boolean is(String var0) {
        return MODE != null && String.valueOf(MODE.getValue()).equals(var0);
    }

    public static boolean handles() {
        return MODE != null;
    }

    public static boolean expired(Object var0) {
        try {
            return ((Timer)var0).getPassedTimeMs() > lifetime.getValue().intValue();
        } catch (Throwable var2) {
            return true;
        }
    }

    private static int rgb(double var0, double var2) {
        double var4 = var0 - Math.floor(var0);
        switch ((JumpCircleFx.ColorMode)colorMode.getValue()) {
            case Custom:
                ColorSetting var13 = color.getValue();
                return var13.getRed() << 16 | var13.getGreen() << 8 | var13.getBlue();
            case Gradient:
                ColorSetting var6 = color.getValue();
                ColorSetting var7 = color2.getValue();
                double var8 = 0.5 + 0.5 * Math.sin((var4 + var2) * Math.PI * 2.0);
                int var10 = (int)(var6.getRed() + (var7.getRed() - var6.getRed()) * var8);
                int var11 = (int)(var6.getGreen() + (var7.getGreen() - var6.getGreen()) * var8);
                int var12 = (int)(var6.getBlue() + (var7.getBlue() - var6.getBlue()) * var8);
                return var10 << 16 | var11 << 8 | var12;
            case Rainbow:
                return Color.HSBtoRGB((float)(var4 + var2), 0.75F, 1.0F) & 16777215;
            default:
                return HudEditor.getColor((int)(var4 * 360.0 + var2 * 360.0)).getRGB() & 16777215;
        }
    }

    private static int argb(int var0, float var1) {
        int var2 = Math.max(0, Math.min(255, (int)(var1 * 255.0F)));
        return var2 << 24 | var0 & 16777215;
    }

    private static void vtx(double var0, double var2, double var4, float var6, int var7) {
        vcount++;
        b().method_22918(mat, (float)(ox + var0), (float)(oy + var2), (float)(oz + var4)).method_22913(var6, 0.5F).method_39415(var7);
    }

    private static void band(double[] var0, double[] var1, double var2, int var4, boolean var5, double var6, float var8, double var9) {
        if (var4 >= 2) {
            int var11 = var5 ? var4 : var4 - 1;
            double var12 = var6 * 0.5;

            for (int var14 = 0; var14 < var11; var14++) {
                int var16 = (var14 + 1) % var4;
                double var17 = var0[var16] - var0[var14];
                double var19 = var1[var16] - var1[var14];
                double var21 = Math.sqrt(var17 * var17 + var19 * var19);
                if (!(var21 < 1.0E-6)) {
                    double var23 = -var19 / var21 * var12;
                    double var25 = var17 / var21 * var12;
                    int var27 = argb(rgb((double)var14 / var4, var9), var8);
                    int var28 = argb(rgb((double)var16 / var4, var9), var8);
                    vtx(var0[var14] - var23, var2, var1[var14] - var25, 0.0F, var27);
                    vtx(var0[var14] + var23, var2, var1[var14] + var25, 1.0F, var27);
                    vtx(var0[var16] + var23, var2, var1[var16] + var25, 1.0F, var28);
                    vtx(var0[var16] - var23, var2, var1[var16] - var25, 0.0F, var28);
                }
            }
        }
    }

    private static void disk(double[] var0, double[] var1, double var2, int var4, float var5, double var6) {
        for (int var8 = 0; var8 < var4; var8++) {
            int var9 = (var8 + 1) % var4;
            int var10 = argb(rgb((double)var8 / var4, var6), var5);
            int var11 = argb(rgb((double)var9 / var4, var6), var5);
            int var12 = argb(rgb((double)var8 / var4, var6), var5 * 0.15F);
            vtx(0.0, var2, 0.0, 0.5F, var12);
            vtx(var0[var8], var2, var1[var8], 0.5F, var10);
            vtx(var0[var9], var2, var1[var9], 0.5F, var11);
            vtx(0.0, var2, 0.0, 0.5F, var12);
        }
    }

    private static int circlePts(double[] var0, double[] var1, int var2, double var3, double var5) {
        for (int var7 = 0; var7 < var2; var7++) {
            double var8 = var5 + var7 * Math.PI * 2.0 / var2;
            var0[var7] = Math.cos(var8) * var3;
            var1[var7] = Math.sin(var8) * var3;
        }

        return var2;
    }

    private static void shape(double var0, double var2, float var4, double var5, boolean var7, int var8) {
        boolean var9 = fill.getValue() && var7 && !noFill;
        if (var9) {
            disk(PX, PZ, var0, var8, var4 * fillAlpha.getValue().intValue() / 100.0F, var5);
        }

        if (glow.getValue()) {
            band(PX, PZ, var0, var8, var7, var2 * glowSize.getValue().floatValue(), var4 * 0.28F, var5);
        }

        band(PX, PZ, var0, var8, var7, var2, var4, var5);
    }

    private static double easeOut(double var0) {
        double var2 = 1.0 - var0;
        if (bounce.getValue()) {
            double var4 = 1.70158;
            double var6 = var4 + 1.0;
            return 1.0 + var6 * Math.pow(var0 - 1.0, 3.0) + var4 * Math.pow(var0 - 1.0, 2.0);
        } else {
            return 1.0 - var2 * var2 * var2;
        }
    }

    public static void render(class_4587 var0, List<JumpCircle.Circle> var1, class_2960 var2, class_2960 var3) {
        if (!var1.isEmpty()) {
            class_310 var4 = class_310.method_1551();
            class_243 var5 = var4.field_1773.method_19418().method_19326();
            if (GLOW == null) {
                GLOW = class_2960.method_60655("nevaware", "textures/particles/ghost_glow.png");
            }

            timeSec = System.nanoTime() / 1.0E9 % 100000.0;
            if (throughWalls.getValue()) {
                RenderSystem.disableDepthTest();
            } else {
                RenderSystem.enableDepthTest();
            }

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            String var6 = String.valueOf(MODE.getValue());
            class_2960 var7 = GLOW;
            boolean var8 = true;
            switch (var6) {
                case "v1":
                    var7 = TextureStorage.default_circle;
                    break;
                case "Portal":
                    var7 = TextureStorage.bubble;
                    break;
                case "Custom":
                    var7 = var2 != null ? var2 : TextureStorage.default_circle;
                    break;
                case "NevaWare":
                    var7 = var3 != null ? var3 : TextureStorage.default_circle;
                    break;
                default:
                    var8 = false;
            }

            RenderSystem.setShaderTexture(0, var7);
            RenderSystem.setShader(class_757::method_34543);
            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
            mat = var0.method_23760().method_23761();
            buf = null;
            vcount = 0;
            long var9 = lifetime.getValue().intValue();

            for (int var11 = 0; var11 < var1.size(); var11++) {
                JumpCircle.Circle var12 = (JumpCircle.Circle)var1.get(var11);
                double var13 = Math.min(1.0, (double)var12.timer().getPassedTimeMs() / var9);
                if (!(var13 >= 1.0)) {
                    double var15 = easeOut(var13);
                    float var17 = (float)Math.min(1.0, var13 * 10.0);
                    alpha = (float)Math.pow(1.0 - var13, 1.3) * var17;
                    if (!(alpha <= 0.003F)) {
                        class_243 var18 = var12.pos();
                        ox = var18.field_1352 - var5.field_1352;
                        oy = var18.field_1351 - var5.field_1351 + 0.02 + rise.getValue().floatValue() * var15;
                        oz = var18.field_1350 - var5.field_1350;
                        int var19 = System.identityHashCode(var12);
                        double var20 = (var19 & 1023) / 1024.0 + timeSec * 0.15;
                        double var22 = size.getValue().floatValue() * var15;
                        double var24 = thickness.getValue().floatValue() * 0.03;
                        double var26 = timeSec * spin.getValue().floatValue() + (var19 & 0xFF) / 40.0;
                        if (var8) {
                            texQuad(var22, var26, var20);
                        } else {
                            drawMode(var6, var13, var15, var22, var24, var26, var20, var19);
                        }
                    }
                }
            }

            if (vcount > 0 && buf != null) {
                class_286.method_43433(buf.method_60800());
            }

            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            buf = null;
        }
    }

    private static class_287 b() {
        if (buf == null) {
            buf = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1575);
        }

        return buf;
    }

    private static void texQuad(double var0, double var2, double var4) {
        if (!(var0 <= 1.0E-4)) {
            if (glow.getValue()) {
                quad(var0 * (1.0 + glowSize.getValue().floatValue() * 0.12), var2, var4, alpha * 0.35F);
            }

            quad(var0, var2, var4, alpha);
        }
    }

    private static void quad(double var0, double var2, double var4, float var6) {
        double var7 = Math.cos(var2);
        double var9 = Math.sin(var2);
        double[][] var11 = new double[][]{{-var0, -var0, 0.0, 0.0}, {var0, -var0, 1.0, 0.0}, {var0, var0, 1.0, 1.0}, {-var0, var0, 0.0, 1.0}};

        for (int var12 = 0; var12 < 4; var12++) {
            double var13 = var11[var12][0] * var7 - var11[var12][1] * var9;
            double var15 = var11[var12][0] * var9 + var11[var12][1] * var7;
            vcount++;
            b()
                .method_22918(mat, (float)(ox + var13), (float)oy, (float)(oz + var15))
                .method_22913((float)var11[var12][2], (float)var11[var12][3])
                .method_39415(argb(rgb(var12 / 4.0, var4), var6));
        }
    }

    private static void drawMode(String var0, double var1, double var3, double var5, double var7, double var9, double var11, int var13) {
        switch (var0) {
            case "Ring":
                int var43 = circlePts(PX, PZ, 96, var5, var9);
                shape(0.0, var7, alpha, var11, true, var43);
                break;
            case "Ripple":
                int var50 = rings.getValue();

                for (int var56 = 0; var56 < var50; var56++) {
                    double var62 = var1 * (1.0 + var56 * 0.35) - var56 * 0.22;
                    if (!(var62 <= 0.0)) {
                        var62 = Math.min(1.0, var62);
                        double var68 = 1.0 - Math.pow(1.0 - var62, 3.0);
                        float var71 = alpha * (float)(1.0 - var62) * (1.0F - var56 * 0.12F);
                        if (!(var71 <= 0.003F)) {
                            int var42 = circlePts(PX, PZ, 80, size.getValue().floatValue() * var68, var9);
                            noFill = var56 > 0;
                            shape(0.0, var7 * (1.0 - var56 * 0.12), var71, var11 + var56 * 0.1, true, var42);
                            noFill = false;
                        }
                    }
                }
                break;
            case "Shockwave":
                double var49 = var7 * (1.0 + 4.0 * (1.0 - var3));
                int var40 = circlePts(PX, PZ, 96, var5, var9);
                shape(0.0, var49, alpha, var11, true, var40);
                var40 = circlePts(PX, PZ, 72, var5 * 0.55, -var9);
                band(PX, PZ, 0.0, var40, true, var7 * 0.6, alpha * 0.6F, var11 + 0.3);
                break;
            case "Hexagon":
                int var38 = circlePts(PX, PZ, 6, var5, var9);
                shape(0.0, var7, alpha, var11, true, var38);
                var38 = circlePts(PX, PZ, 6, var5 * 0.6, -var9 * 1.3);
                band(PX, PZ, 0.0, var38, true, var7 * 0.7, alpha * 0.7F, var11 + 0.25);
                break;
            case "Star":
                int var48 = points.getValue();
                int var37 = var48 * 2;

                for (int var55 = 0; var55 < var37; var55++) {
                    double var61 = var9 + var55 * Math.PI / var48;
                    double var67 = var55 % 2 == 0 ? var5 : var5 * 0.45;
                    PX[var55] = Math.cos(var61) * var67;
                    PZ[var55] = Math.sin(var61) * var67;
                }

                shape(0.0, var7, alpha, var11, true, var37);
                break;
            case "Flower":
                int var47 = points.getValue();
                short var36 = 160;

                for (int var54 = 0; var54 < var36; var54++) {
                    double var60 = var9 + var54 * Math.PI * 2.0 / var36;
                    double var66 = var5 * (0.72 + 0.28 * Math.cos(var47 * (var60 - var9)));
                    PX[var54] = Math.cos(var60) * var66;
                    PZ[var54] = Math.sin(var60) * var66;
                }

                shape(0.0, var7, alpha, var11, true, var36);
                break;
            case "Spiral":
                int var46 = Math.max(2, Math.min(8, points.getValue()));
                int var34 = 48;

                for (int var53 = 0; var53 < var46; var53++) {
                    double var59 = var9 + var53 * Math.PI * 2.0 / var46;

                    for (int var65 = 0; var65 < var34; var65++) {
                        double var70 = (double)var65 / (var34 - 1);
                        double var73 = var59 + var70 * Math.PI * 1.6;
                        PX[var65] = Math.cos(var73) * var5 * var70;
                        PZ[var65] = Math.sin(var73) * var5 * var70;
                    }

                    if (glow.getValue()) {
                        band(PX, PZ, 0.0, var34, false, var7 * glowSize.getValue().floatValue(), alpha * 0.28F, var11 + (double)var53 / var46);
                    }

                    band(PX, PZ, 0.0, var34, false, var7, alpha, var11 + (double)var53 / var46);
                }

                var34 = circlePts(PX, PZ, 72, var5, var9);
                band(PX, PZ, 0.0, var34, true, var7 * 0.5, alpha * 0.5F, var11);
                break;
            case "Vortex":
                int var45 = Math.max(2, Math.min(8, points.getValue()));
                byte var33 = 40;

                for (int var52 = 0; var52 < var45; var52++) {
                    double var58 = var9 * 2.0 + var52 * Math.PI * 2.0 / var45;

                    for (int var64 = 0; var64 < var33; var64++) {
                        double var69 = (double)var64 / (var33 - 1);
                        double var72 = var58 - var69 * Math.PI * 2.4;
                        double var74 = var5 * (1.0 - var69 * 0.85);
                        PX[var64] = Math.cos(var72) * var74;
                        PZ[var64] = Math.sin(var72) * var74;
                    }

                    if (glow.getValue()) {
                        band(PX, PZ, 0.0, var33, false, var7 * glowSize.getValue().floatValue(), alpha * 0.25F, var11 + (double)var52 / var45);
                    }

                    band(PX, PZ, 0.0, var33, false, var7 * 0.8, alpha, var11 + (double)var52 / var45);
                }
                break;
            case "Pulse":
                int var44 = rings.getValue();

                for (int var51 = 0; var51 < var44; var51++) {
                    double var57 = timeSec * 6.0 + var51 * 1.3;
                    double var21 = var5 * (0.35 + 0.65 * (var51 + 1) / var44) * (0.92 + 0.08 * Math.sin(var57));
                    int var32 = circlePts(PX, PZ, 80, var21, var9 * (var51 % 2 == 0 ? 1 : -1));
                    float var23 = alpha * (0.55F + 0.45F * (float)Math.abs(Math.sin(var57)));
                    noFill = var51 < var44 - 1;
                    shape(0.0, var7 * (0.6 + 0.4 * (var51 + 1) / var44), var23, var11 + var51 * 0.15, true, var32);
                    noFill = false;
                }
                break;
            case "Sparks":
                int var17 = sparkCount.getValue();
                Random var18 = new Random(var13);

                for (int var19 = 0; var19 < var17; var19++) {
                    double var20 = var18.nextDouble() * Math.PI * 2.0 + var9 * 0.2;
                    double var22 = 0.6 + var18.nextDouble() * 0.6;
                    double var24 = var5 * var22;
                    double var26 = Math.max(0.0, var24 - (0.25 + 0.35 * (1.0 - var1)) * size.getValue().floatValue() * 0.5);
                    PX[0] = Math.cos(var20) * var26;
                    PZ[0] = Math.sin(var20) * var26;
                    PX[1] = Math.cos(var20) * var24;
                    PZ[1] = Math.sin(var20) * var24;
                    double var28 = Math.sin(var1 * Math.PI) * 0.35 * var18.nextDouble();
                    double var30 = var11 + (double)var19 / var17;
                    if (glow.getValue()) {
                        band(PX, PZ, var28, 2, false, var7 * glowSize.getValue().floatValue() * 0.8, alpha * 0.3F, var30);
                    }

                    band(PX, PZ, var28, 2, false, var7 * 0.8, alpha, var30);
                }

                int var14 = circlePts(PX, PZ, 72, var5 * 0.5, var9);
                band(PX, PZ, 0.0, var14, true, var7 * 0.5, alpha * 0.45F, var11);
        }
    }

    public static enum ColorMode {
        Sync,
        Custom,
        Gradient,
        Rainbow;
    }
}
