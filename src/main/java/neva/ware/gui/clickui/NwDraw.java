package neva.ware.gui.clickui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.lang.reflect.Method;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.gui.font.FontRenderer;
import neva.ware.gui.font.FontRenderers;
import neva.ware.utility.render.Render2DEngine;
import org.joml.Matrix4f;

public final class NwDraw {
    public static float alpha = 1.0F;
    public static float dt = 0.016666668F;
    private static long lastFrame;
    public static final int PANEL_TOP = -266855897;
    public static final int PANEL_BOTTOM = -233828071;
    public static final int PANEL_LINE = 318767103;
    public static final int ROW = 218103807;
    public static final int ROW_HOVER = 402653183;
    public static final int TEXT = -1;
    public static final int TEXT_DIM = -3946796;
    public static final int TEXT_MUTED = -8552041;
    private static FontRenderer title;
    private static FontRenderer module;
    private static FontRenderer small;
    private static FontRenderer setting;
    private static boolean fontsFailed;
    private static Method flushMethod;
    private static boolean flushLookedUp;

    private NwDraw() {
    }

    private static void initFonts() {
        if (title == null && !fontsFailed) {
            try {
                title = FontRenderers.create(19.0F, "sf_bold");
                module = FontRenderers.create(15.0F, "sf_medium");
                small = FontRenderers.create(12.5F, "sf_medium");
                setting = FontRenderers.create(13.5F, "sf_medium");
            } catch (Throwable var1) {
                fontsFailed = true;
            }
        }
    }

    public static FontRenderer titleFont() {
        initFonts();
        return title != null ? title : FontRenderers.categories;
    }

    public static FontRenderer moduleFont() {
        initFonts();
        return module != null ? module : FontRenderers.sf_medium_modules;
    }

    public static FontRenderer smallFont() {
        initFonts();
        return small != null ? small : FontRenderers.sf_medium_mini;
    }

    public static float titleSize() {
        return title != null ? 9.5F : 9.0F;
    }

    public static float moduleSize() {
        return module != null ? 7.5F : 7.0F;
    }

    public static FontRenderer settingFont() {
        initFonts();
        return setting != null ? setting : FontRenderers.sf_medium_mini;
    }

    public static float settingSize() {
        return setting != null ? 6.75F : 6.0F;
    }

    public static float smallSize() {
        return small != null ? 6.25F : 6.0F;
    }

    public static void prepareScreen(class_332 var0) {
        try {
            if (!flushLookedUp) {
                flushLookedUp = true;

                try {
                    flushMethod = var0.getClass().getMethod("method_51452");
                } catch (Throwable var3) {
                    flushMethod = null;
                }
            }

            if (flushMethod != null) {
                flushMethod.invoke(var0);
            }
        } catch (Throwable var4) {
        }

        try {
            RenderSystem.clear(256, class_310.field_1703);
        } catch (Throwable var2) {
        }
    }

    public static void frame() {
        long var0 = System.nanoTime();
        if (lastFrame == 0L) {
            lastFrame = var0;
        }

        float var2 = (float)(var0 - lastFrame) / 1.0E9F;
        lastFrame = var0;
        if (var2 < 0.0F) {
            var2 = 0.0F;
        }

        if (var2 > 0.1F) {
            var2 = 0.1F;
        }

        dt = var2;
    }

    public static float approach(float var0, float var1, float var2) {
        float var3 = 1.0F - (float)Math.exp(-var2 * dt);
        float var4 = var0 + (var1 - var0) * var3;
        if (Math.abs(var1 - var4) < 0.001F) {
            var4 = var1;
        }

        return var4;
    }

    public static float clamp(float var0, float var1, float var2) {
        return var0 < var1 ? var1 : (var0 > var2 ? var2 : var0);
    }

    public static float easeOutCubic(float var0) {
        var0 = clamp(var0, 0.0F, 1.0F);
        float var1 = 1.0F - var0;
        return 1.0F - var1 * var1 * var1;
    }

    public static float easeInOutCubic(float var0) {
        var0 = clamp(var0, 0.0F, 1.0F);
        return var0 < 0.5F ? 4.0F * var0 * var0 * var0 : 1.0F - (float)Math.pow(-2.0F * var0 + 2.0F, 3.0) / 2.0F;
    }

    public static float easeOutBack(float var0) {
        var0 = clamp(var0, 0.0F, 1.0F);
        float var1 = 1.70158F;
        float var2 = var1 + 1.0F;
        return 1.0F + var2 * (float)Math.pow(var0 - 1.0F, 3.0) + var1 * (float)Math.pow(var0 - 1.0F, 2.0);
    }

    public static int rgba(int var0, int var1, int var2, int var3) {
        return clampI(var3) << 24 | clampI(var0) << 16 | clampI(var1) << 8 | clampI(var2);
    }

    private static int clampI(int var0) {
        return var0 < 0 ? 0 : Math.min(255, var0);
    }

    public static int alphaOf(int var0) {
        return var0 >>> 24 & 0xFF;
    }

    public static int mulAlpha(int var0, float var1) {
        int var2 = Math.round(alphaOf(var0) * clamp(var1, 0.0F, 1.0F));
        return var2 << 24 | var0 & 16777215;
    }

    public static int withAlpha(int var0, int var1) {
        return clampI(var1) << 24 | var0 & 16777215;
    }

    public static int mix(int var0, int var1, float var2) {
        var2 = clamp(var2, 0.0F, 1.0F);
        int var3 = var0 >>> 24 & 0xFF;
        int var4 = var0 >> 16 & 0xFF;
        int var5 = var0 >> 8 & 0xFF;
        int var6 = var0 & 0xFF;
        int var7 = var1 >>> 24 & 0xFF;
        int var8 = var1 >> 16 & 0xFF;
        int var9 = var1 >> 8 & 0xFF;
        int var10 = var1 & 0xFF;
        return rgba(
            Math.round(var4 + (var8 - var4) * var2),
            Math.round(var5 + (var9 - var5) * var2),
            Math.round(var6 + (var10 - var6) * var2),
            Math.round(var3 + (var7 - var3) * var2)
        );
    }

    public static int accent(int var0) {
        try {
            Color var1 = HudEditor.getColor(var0);
            return 0xFF000000 | var1.getRGB() & 16777215;
        } catch (Throwable var2) {
            return -8614657;
        }
    }

    private static float feather() {
        try {
            double var0 = Module.mc.method_22683().method_4495();
            return (float)(1.0 / Math.max(1.0, var0));
        } catch (Throwable var2) {
            return 0.5F;
        }
    }

    private static int segments(float var0) {
        return Math.max(3, Math.min(18, (int)Math.ceil(var0 * 1.6F)));
    }

    private static float[] outline(float var0, float var1, float var2, float var3, float var4, float var5) {
        return outline(var0, var1, var2, var3, var4, var5, segments(Math.max(var4, 1.0F)));
    }

    private static float[] outline(float var0, float var1, float var2, float var3, float var4, float var5, int var6) {
        float[] var7 = new float[4 * (var6 + 1) * 2];
        float var8 = var0 - var5;
        float var9 = var1 - var5;
        float var10 = var2 + var5 * 2.0F;
        float var11 = var3 + var5 * 2.0F;
        float var12 = Math.max(0.0F, var4 + var5);
        float[][] var13 = new float[][]{
            {var8 + var12, var9 + var12, 180.0F},
            {var8 + var10 - var12, var9 + var12, 270.0F},
            {var8 + var10 - var12, var9 + var11 - var12, 0.0F},
            {var8 + var12, var9 + var11 - var12, 90.0F}
        };
        int var14 = 0;

        for (float[] var18 : var13) {
            for (int var19 = 0; var19 <= var6; var19++) {
                double var20 = Math.toRadians(var18[2] + 90.0 * var19 / var6);
                var7[var14++] = var18[0] + (float)Math.cos(var20) * var12;
                var7[var14++] = var18[1] + (float)Math.sin(var20) * var12;
            }
        }

        return var7;
    }

    private static void begin() {
        Render2DEngine.setupRender();
        RenderSystem.disableCull();
        RenderSystem.setShader(class_757::method_34540);
    }

    private static void end() {
        RenderSystem.enableCull();
        Render2DEngine.endRender();
    }

    private static void vertex(class_287 var0, Matrix4f var1, float var2, float var3, int var4) {
        var0.method_22918(var1, var2, var3, 0.0F)
            .method_22915((var4 >> 16 & 0xFF) / 255.0F, (var4 >> 8 & 0xFF) / 255.0F, (var4 & 0xFF) / 255.0F, (var4 >>> 24 & 0xFF) / 255.0F);
    }

    private static int colorAt(int var0, int var1, float var2, float var3, float var4) {
        return var0 != var1 && !(var4 <= 0.0F) ? mix(var0, var1, (var2 - var3) / var4) : var0;
    }

    public static void round(class_4587 var0, float var1, float var2, float var3, float var4, float var5, int var6, int var7) {
        if (!(var3 <= 0.01F) && !(var4 <= 0.01F)) {
            var6 = mulAlpha(var6, alpha);
            var7 = mulAlpha(var7, alpha);
            if (alphaOf(var6) != 0 || alphaOf(var7) != 0) {
                var5 = clamp(var5, 0.0F, Math.min(var3, var4) / 2.0F);
                float var8 = feather();
                float var9 = var8 / 2.0F;
                Matrix4f var10 = var0.method_23760().method_23761();
                float[] var11 = outline(var1, var2, var3, var4, var5, -var9);
                float[] var12 = outline(var1, var2, var3, var4, var5, var9);
                begin();
                class_287 var13 = class_289.method_1348().method_60827(class_5596.field_27381, class_290.field_1576);
                vertex(var13, var10, var1 + var3 / 2.0F, var2 + var4 / 2.0F, colorAt(var6, var7, var2 + var4 / 2.0F, var2, var4));

                for (byte var14 = 0; var14 < var11.length; var14 += 2) {
                    vertex(var13, var10, var11[var14], var11[var14 + 1], colorAt(var6, var7, var11[var14 + 1], var2, var4));
                }

                vertex(var13, var10, var11[0], var11[1], colorAt(var6, var7, var11[1], var2, var4));
                class_286.method_43433(var13.method_60800());
                class_287 var21 = class_289.method_1348().method_60827(class_5596.field_27380, class_290.field_1576);

                for (byte var15 = 0; var15 <= var11.length; var15 += 2) {
                    int var16 = var15 % var11.length;
                    int var17 = colorAt(var6, var7, var11[var16 + 1], var2, var4);
                    vertex(var21, var10, var11[var16], var11[var16 + 1], var17);
                    vertex(var21, var10, var12[var16], var12[var16 + 1], var17 & 16777215);
                }

                class_286.method_43433(var21.method_60800());
                end();
            }
        }
    }

    public static void roundQuad(class_4587 var0, float var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9) {
        if (!(var3 <= 0.01F) && !(var4 <= 0.01F)) {
            var6 = mulAlpha(var6, alpha);
            var7 = mulAlpha(var7, alpha);
            var8 = mulAlpha(var8, alpha);
            var9 = mulAlpha(var9, alpha);
            var5 = clamp(var5, 0.0F, Math.min(var3, var4) / 2.0F);
            float var10 = feather() / 2.0F;
            Matrix4f var11 = var0.method_23760().method_23761();
            float[] var12 = outline(var1, var2, var3, var4, var5, -var10);
            float[] var13 = outline(var1, var2, var3, var4, var5, var10);
            begin();
            class_287 var14 = class_289.method_1348().method_60827(class_5596.field_27381, class_290.field_1576);
            vertex(var14, var11, var1 + var3 / 2.0F, var2 + var4 / 2.0F, bilinear(var6, var7, var8, var9, 0.5F, 0.5F));

            for (byte var15 = 0; var15 < var12.length; var15 += 2) {
                vertex(
                    var14,
                    var11,
                    var12[var15],
                    var12[var15 + 1],
                    bilinear(var6, var7, var8, var9, (var12[var15] - var1) / var3, (var12[var15 + 1] - var2) / var4)
                );
            }

            vertex(var14, var11, var12[0], var12[1], bilinear(var6, var7, var8, var9, (var12[0] - var1) / var3, (var12[1] - var2) / var4));
            class_286.method_43433(var14.method_60800());
            class_287 var24 = class_289.method_1348().method_60827(class_5596.field_27380, class_290.field_1576);

            for (byte var16 = 0; var16 <= var12.length; var16 += 2) {
                int var17 = var16 % var12.length;
                int var18 = bilinear(var6, var7, var8, var9, (var12[var17] - var1) / var3, (var12[var17 + 1] - var2) / var4);
                vertex(var24, var11, var12[var17], var12[var17 + 1], var18);
                vertex(var24, var11, var13[var17], var13[var17 + 1], var18 & 16777215);
            }

            class_286.method_43433(var24.method_60800());
            end();
        }
    }

    private static int bilinear(int var0, int var1, int var2, int var3, float var4, float var5) {
        return mix(mix(var0, var1, var4), mix(var3, var2, var4), var5);
    }

    public static void ribbon(class_4587 var0, float[] var1, float[] var2, int var3, int var4, float var5, float var6) {
        if (var1.length >= 4) {
            int var7 = var1.length / 2;
            byte var8 = 6;
            float[] var9 = new float[(var7 + 2 * var8) * 2];
            float[] var10 = new float[var7 + 2 * var8];

            for (int var11 = 0; var11 < 2; var11++) {
                int var12 = var11 == 0 ? 0 : var7 - 1;
                int var13 = var11 == 0 ? 1 : var7 - 2;
                float var14 = var1[var12 * 2] - var1[var13 * 2];
                float var15 = var1[var12 * 2 + 1] - var1[var13 * 2 + 1];
                float var16 = (float)Math.sqrt(var14 * var14 + var15 * var15);
                if (var16 < 1.0E-5F) {
                    var16 = 1.0E-5F;
                }

                var14 /= var16;
                var15 /= var16;
                float var17 = var2[Math.min(var12, var2.length - 1)] / 2.0F;

                for (int var18 = 1; var18 <= var8; var18++) {
                    float var19 = (float)var18 / var8 * 0.985F;
                    int var20 = var11 == 0 ? var8 - var18 : var7 + var8 + var18 - 1;
                    var9[var20 * 2] = var1[var12 * 2] + var14 * var17 * var19;
                    var9[var20 * 2 + 1] = var1[var12 * 2 + 1] + var15 * var17 * var19;
                    var10[var20] = 2.0F * var17 * (float)Math.sqrt(1.0F - var19 * var19);
                }
            }

            for (int var29 = 0; var29 < var7; var29++) {
                var9[(var29 + var8) * 2] = var1[var29 * 2];
                var9[(var29 + var8) * 2 + 1] = var1[var29 * 2 + 1];
                var10[var29 + var8] = var2[Math.min(var29, var2.length - 1)];
            }

            var1 = var9;
            var2 = var10;
            var7 = var9.length / 2;
            var3 = mulAlpha(var3, alpha);
            var4 = mulAlpha(var4, alpha);
            if (alphaOf(var3) != 0 || alphaOf(var4) != 0) {
                float var26 = feather();
                var9 = new float[var7];
                var10 = new float[var7];

                for (int var30 = 0; var30 < var7; var30++) {
                    int var32 = Math.max(0, var30 - 1);
                    int var34 = Math.min(var7 - 1, var30 + 1);
                    float var37 = var1[var34 * 2] - var1[var32 * 2];
                    float var40 = var1[var34 * 2 + 1] - var1[var32 * 2 + 1];
                    float var42 = (float)Math.sqrt(var37 * var37 + var40 * var40);
                    if (var42 < 1.0E-5F) {
                        var42 = 1.0E-5F;
                    }

                    var9[var30] = -var40 / var42;
                    var10[var30] = var37 / var42;
                }

                Matrix4f var31 = var0.method_23760().method_23761();
                begin();

                for (int var33 = 0; var33 < 3; var33++) {
                    class_287 var35 = class_289.method_1348().method_60827(class_5596.field_27380, class_290.field_1576);

                    for (int var38 = 0; var38 < var7; var38++) {
                        float var41 = var1[var38 * 2];
                        float var43 = var1[var38 * 2 + 1];
                        float var44 = var2[Math.min(var38, var2.length - 1)] / 2.0F;
                        int var45 = colorAt(var3, var4, var43, var5, var6);
                        int var46 = var45 & 16777215;
                        if (var33 == 0) {
                            vertex(var35, var31, var41 + var9[var38] * var44, var43 + var10[var38] * var44, var45);
                            vertex(var35, var31, var41 - var9[var38] * var44, var43 - var10[var38] * var44, var45);
                        } else {
                            float var47 = var33 == 1 ? 1.0F : -1.0F;
                            vertex(var35, var31, var41 + var47 * var9[var38] * var44, var43 + var47 * var10[var38] * var44, var45);
                            vertex(var35, var31, var41 + var47 * var9[var38] * (var44 + var26), var43 + var47 * var10[var38] * (var44 + var26), var46);
                        }
                    }

                    class_286.method_43433(var35.method_60800());
                }

                end();
            }
        }
    }

    public static void mesh(class_4587 var0, float[] var1, float[] var2, short[] var3, int[] var4) {
        int var5 = var1.length / 2;
        int[] var6 = new int[var5];
        boolean var7 = false;

        for (int var8 = 0; var8 < var5; var8++) {
            var6[var8] = mulAlpha(var4[var8], alpha);
            var7 |= alphaOf(var6[var8]) != 0;
        }

        if (var7) {
            float var16 = feather();
            float var9 = var16 * 0.5F;
            Matrix4f var10 = var0.method_23760().method_23761();
            begin();
            class_287 var11 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);

            for (byte var12 = 0; var12 + 2 < var3.length; var12 += 3) {
                short var13 = var3[var12];
                short var14 = var3[var12 + 1];
                short var15 = var3[var12 + 2];
                vertex(var11, var10, var1[var13 * 2] - var2[var13 * 2] * var9, var1[var13 * 2 + 1] - var2[var13 * 2 + 1] * var9, var6[var13]);
                vertex(var11, var10, var1[var14 * 2] - var2[var14 * 2] * var9, var1[var14 * 2 + 1] - var2[var14 * 2 + 1] * var9, var6[var14]);
                vertex(var11, var10, var1[var15 * 2] - var2[var15 * 2] * var9, var1[var15 * 2 + 1] - var2[var15 * 2 + 1] * var9, var6[var15]);
                vertex(var11, var10, var1[var15 * 2] - var2[var15 * 2] * var9, var1[var15 * 2 + 1] - var2[var15 * 2 + 1] * var9, var6[var15]);
            }

            class_286.method_43433(var11.method_60800());
            class_287 var17 = class_289.method_1348().method_60827(class_5596.field_27380, class_290.field_1576);

            for (int var18 = 0; var18 <= var5; var18++) {
                int var19 = var18 % var5;
                vertex(var17, var10, var1[var19 * 2] - var2[var19 * 2] * var9, var1[var19 * 2 + 1] - var2[var19 * 2 + 1] * var9, var6[var19]);
                vertex(
                    var17,
                    var10,
                    var1[var19 * 2] + var2[var19 * 2] * (var16 - var9),
                    var1[var19 * 2 + 1] + var2[var19 * 2 + 1] * (var16 - var9),
                    var6[var19] & 16777215
                );
            }

            class_286.method_43433(var17.method_60800());
            end();
        }
    }

    public static void line(class_4587 var0, float var1, float var2, float var3, float var4, float var5, int var6) {
        var6 = mulAlpha(var6, alpha);
        if (alphaOf(var6) != 0) {
            float var7 = var3 - var1;
            float var8 = var4 - var2;
            float var9 = (float)Math.sqrt(var7 * var7 + var8 * var8);
            if (!(var9 < 0.001F)) {
                float var10 = -var8 / var9;
                float var11 = var7 / var9;
                float var12 = feather();
                float var13 = var5 / 2.0F;
                float var14 = var5 / 2.0F + var12;
                int var15 = var6 & 16777215;
                Matrix4f var16 = var0.method_23760().method_23761();
                begin();
                class_287 var17 = class_289.method_1348().method_60827(class_5596.field_27380, class_290.field_1576);
                vertex(var17, var16, var1 + var10 * var14, var2 + var11 * var14, var15);
                vertex(var17, var16, var3 + var10 * var14, var4 + var11 * var14, var15);
                vertex(var17, var16, var1 + var10 * var13, var2 + var11 * var13, var6);
                vertex(var17, var16, var3 + var10 * var13, var4 + var11 * var13, var6);
                vertex(var17, var16, var1 - var10 * var13, var2 - var11 * var13, var6);
                vertex(var17, var16, var3 - var10 * var13, var4 - var11 * var13, var6);
                vertex(var17, var16, var1 - var10 * var14, var2 - var11 * var14, var15);
                vertex(var17, var16, var3 - var10 * var14, var4 - var11 * var14, var15);
                class_286.method_43433(var17.method_60800());
                end();
            }
        }
    }

    public static void chevron(class_4587 var0, float var1, float var2, float var3, float var4, int var5) {
        double var6 = Math.toRadians(var4);
        float var8 = (float)Math.cos(var6);
        float var9 = (float)Math.sin(var6);
        float[][] var10 = new float[][]{{-var3, -var3 / 2.0F}, {0.0F, var3 / 2.0F}, {var3, -var3 / 2.0F}};
        float[][] var11 = new float[3][2];

        for (int var12 = 0; var12 < 3; var12++) {
            var11[var12][0] = var1 + var10[var12][0] * var8 - var10[var12][1] * var9;
            var11[var12][1] = var2 + var10[var12][0] * var9 + var10[var12][1] * var8;
        }

        line(var0, var11[0][0], var11[0][1], var11[1][0], var11[1][1], 0.9F, var5);
        line(var0, var11[1][0], var11[1][1], var11[2][0], var11[2][1], 0.9F, var5);
    }

    public static void round(class_4587 var0, float var1, float var2, float var3, float var4, float var5, int var6) {
        round(var0, var1, var2, var3, var4, var5, var6, var6);
    }

    public static void border(class_4587 var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7) {
        if (!(var3 <= 0.01F) && !(var4 <= 0.01F)) {
            var7 = mulAlpha(var7, alpha);
            if (alphaOf(var7) != 0) {
                var5 = clamp(var5, 0.0F, Math.min(var3, var4) / 2.0F);
                float var8 = feather();
                Matrix4f var9 = var0.method_23760().method_23761();
                float[] var10 = outline(var1, var2, var3, var4, var5, var8 / 2.0F);
                float[] var11 = outline(var1, var2, var3, var4, var5, -var8 / 2.0F);
                float[] var12 = outline(var1, var2, var3, var4, var5, -var6 + var8 / 2.0F);
                float[] var13 = outline(var1, var2, var3, var4, var5, -var6 - var8 / 2.0F);
                int var14 = var7 & 16777215;
                begin();
                strip(var9, var13, var12, var14, var7);
                strip(var9, var12, var11, var7, var7);
                strip(var9, var11, var10, var7, var14);
                end();
            }
        }
    }

    private static void strip(Matrix4f var0, float[] var1, float[] var2, int var3, int var4) {
        class_287 var5 = class_289.method_1348().method_60827(class_5596.field_27380, class_290.field_1576);

        for (byte var6 = 0; var6 <= var1.length; var6 += 2) {
            int var7 = var6 % var1.length;
            vertex(var5, var0, var1[var7], var1[var7 + 1], var3);
            vertex(var5, var0, var2[var7], var2[var7 + 1], var4);
        }

        class_286.method_43433(var5.method_60800());
    }

    public static void shadow(class_4587 var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7) {
        var7 = mulAlpha(var7, alpha);
        if (alphaOf(var7) != 0 && !(var6 <= 0.0F)) {
            var5 = clamp(var5, 0.0F, Math.min(var3, var4) / 2.0F);
            Matrix4f var8 = var0.method_23760().method_23761();
            begin();
            int var9 = var6 > 8.0F ? 6 : 4;
            int var10 = segments(Math.max(var5 + var6, 1.0F));
            float[] var11 = outline(var1, var2, var3, var4, var5, 0.0F, var10);
            int var12 = var7;

            for (int var13 = 1; var13 <= var9; var13++) {
                float var14 = (float)var13 / var9;
                float[] var15 = outline(var1, var2, var3, var4, var5, var6 * var14, var10);
                float var16 = 1.0F - var14;
                int var17 = withAlpha(var7, Math.round(alphaOf(var7) * var16 * var16));
                strip(var8, var11, var15, var12, var17);
                var11 = var15;
                var12 = var17;
            }

            end();
        }
    }

    public static void circle(class_4587 var0, float var1, float var2, float var3, int var4) {
        round(var0, var1 - var3, var2 - var3, var3 * 2.0F, var3 * 2.0F, var3, var4);
    }

    public static void rect(class_4587 var0, float var1, float var2, float var3, float var4, int var5) {
        round(var0, var1, var2, var3, var4, 0.0F, var5);
    }

    public static void hGradient(class_4587 var0, float var1, float var2, float var3, float var4, int var5, int var6) {
        var5 = mulAlpha(var5, alpha);
        var6 = mulAlpha(var6, alpha);
        if (alphaOf(var5) != 0 || alphaOf(var6) != 0) {
            Matrix4f var7 = var0.method_23760().method_23761();
            begin();
            class_287 var8 = class_289.method_1348().method_60827(class_5596.field_27380, class_290.field_1576);
            vertex(var8, var7, var1, var2, var5);
            vertex(var8, var7, var1, var2 + var4, var5);
            vertex(var8, var7, var1 + var3, var2, var6);
            vertex(var8, var7, var1 + var3, var2 + var4, var6);
            class_286.method_43433(var8.method_60800());
            end();
        }
    }

    public static void dots(class_4587 var0, float var1, float var2, int var3) {
        float var4 = 2.6F;
        float var5 = 0.85F;
        circle(var0, var1 - var4, var2, var5, var3);
        circle(var0, var1, var2, var5, var3);
        circle(var0, var1 + var4, var2, var5, var3);
    }

    public static float textTop(float var0, float var1) {
        return var0 + 3.0F - 0.97F * var1 + 0.3525F * var1;
    }

    public static void text(FontRenderer var0, class_4587 var1, String var2, float var3, float var4, int var5) {
        var5 = mulAlpha(var5, alpha);
        if (alphaOf(var5) >= 4 && var2 != null && !var2.isEmpty()) {
            var0.drawString(var1, var2, var3, var4, var5);
        }
    }

    public static void textCentered(FontRenderer var0, class_4587 var1, String var2, float var3, float var4, int var5) {
        if (var2 != null) {
            text(var0, var1, var2, var3 - var0.getStringWidth(var2) / 2.0F, var4, var5);
        }
    }

    public static float width(FontRenderer var0, String var1) {
        return var1 == null ? 0.0F : var0.getStringWidth(var1);
    }

    public static String trim(FontRenderer var0, String var1, float var2) {
        if (var1 != null && !(var0.getStringWidth(var1) <= var2)) {
            String var3 = "...";
            float var4 = var0.getStringWidth(var3);
            StringBuilder var5 = new StringBuilder();

            for (char var9 : var1.toCharArray()) {
                if (var0.getStringWidth(var5.toString() + var9) + var4 > var2) {
                    break;
                }

                var5.append(var9);
            }

            return var5.toString().trim() + var3;
        } else {
            return var1;
        }
    }

    public static boolean hovered(double var0, double var2, float var4, float var5, float var6, float var7) {
        return var0 >= var4 && var0 <= var4 + var6 && var2 >= var5 && var2 <= var5 + var7;
    }

    public static String pretty(String var0) {
        if (var0 != null && !var0.isEmpty()) {
            if (var0.indexOf(32) >= 0) {
                return var0;
            } else {
                StringBuilder var1 = new StringBuilder();
                char[] var2 = var0.toCharArray();

                for (int var3 = 0; var3 < var2.length; var3++) {
                    char var4 = var2[var3];
                    if (var3 > 0 && Character.isUpperCase(var4)) {
                        char var5 = var2[var3 - 1];
                        boolean var6 = var3 + 1 < var2.length && Character.isLowerCase(var2[var3 + 1]);
                        int var7 = 0;

                        for (int var8 = var3 - 1; var8 >= 0 && (Character.isUpperCase(var2[var8]) || Character.isDigit(var2[var8])); var8--) {
                            var7++;
                        }

                        boolean var9 = Character.isUpperCase(var5) || Character.isDigit(var5);
                        if (Character.isLowerCase(var5) || var9 && var6 && var7 > 1) {
                            var1.append(' ');
                        }
                    }

                    var1.append(var4);
                }

                return var1.toString();
            }
        } else {
            return var0;
        }
    }
}
