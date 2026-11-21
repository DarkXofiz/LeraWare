package neva.ware.features.modules.render;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import org.joml.Matrix4f;

public class CustomSky extends Module {
    public final Setting<CustomSky.Style> style = new Setting<>("Style", CustomSky.Style.Storm);
    public final Setting<Boolean> customColors = new Setting<>("Custom Colors", Boolean.FALSE);
    public final Setting<ColorSetting> topColor = new Setting<>("Top Color", new ColorSetting(new Color(-15920101, true)), var1 -> this.custom());
    public final Setting<ColorSetting> horizonColor = new Setting<>("Horizon Color", new ColorSetting(new Color(-8415586, true)), var1 -> this.custom());
    public final Setting<ColorSetting> bottomColor = new Setting<>("Bottom Color", new ColorSetting(new Color(-15064535, true)), var1 -> this.custom());
    public final Setting<ColorSetting> cloudColor = new Setting<>(
        "Cloud Color", new ColorSetting(new Color(-7693661, true)), var1 -> this.custom() && this.hasClouds()
    );
    public final Setting<Float> brightness = new Setting<>("Brightness", 1.0F, 0.3F, 2.0F);
    public final Setting<Float> opacity = new Setting<>("Opacity", 1.0F, 0.1F, 1.0F);
    public final Setting<Integer> detail = new Setting<>("Detail", 2, 1, 3);
    public final Setting<Float> cloudSpeed = new Setting<>("Cloud Speed", 1.0F, 0.0F, 6.0F, var1 -> this.hasClouds());
    public final Setting<Float> cloudScale = new Setting<>("Cloud Scale", 1.0F, 0.3F, 3.0F, var1 -> this.hasClouds());
    public final Setting<Float> cloudDensity = new Setting<>("Cloud Density", 0.6F, 0.0F, 1.0F, var1 -> this.hasClouds());
    public final Setting<Float> swirl = new Setting<>("Swirl", 0.7F, 0.0F, 2.0F, var1 -> this.hasClouds());
    public final Setting<Float> sunAngle = new Setting<>("Sun Angle", 200.0F, 0.0F, 360.0F, var1 -> this.style.is(CustomSky.Style.Sunset));
    public final Setting<Float> flowSpeed = new Setting<>(
        "Flow Speed",
        1.0F,
        0.0F,
        5.0F,
        var1 -> this.style.is(CustomSky.Style.Aurora) || this.style.is(CustomSky.Style.Rainbow) || this.style.is(CustomSky.Style.Gradient)
    );
    public final Setting<Boolean> stars = new Setting<>(
        "Stars", Boolean.TRUE, var1 -> !this.style.is(CustomSky.Style.Storm) && !this.style.is(CustomSky.Style.Rainbow)
    );
    public final Setting<Integer> starCount = new Setting<>(
        "Star Count", 800, 100, 2000, var1 -> !this.style.is(CustomSky.Style.Storm) && !this.style.is(CustomSky.Style.Rainbow) && this.stars.getValue()
    );
    public final Setting<Float> starSize = new Setting<>(
        "Star Size", 1.0F, 0.3F, 3.0F, var1 -> !this.style.is(CustomSky.Style.Storm) && !this.style.is(CustomSky.Style.Rainbow) && this.stars.getValue()
    );
    public final Setting<Boolean> lightning = new Setting<>("Lightning", Boolean.TRUE);
    public final Setting<CustomSky.Shape> lShape = new Setting<>("Lightning Shape", CustomSky.Shape.Random, var1 -> this.lightning.getValue());
    public final Setting<Float> lInterval = new Setting<>("Lightning Interval", 3.5F, 0.4F, 20.0F, var1 -> this.lightning.getValue());
    public final Setting<ColorSetting> lColor = new Setting<>("Lightning Color", new ColorSetting(new Color(-2956033, true)), var1 -> this.lightning.getValue());
    public final Setting<Float> lThickness = new Setting<>("Lightning Thickness", 1.0F, 0.3F, 4.0F, var1 -> this.lightning.getValue());
    public final Setting<Float> lGlow = new Setting<>("Lightning Glow", 1.0F, 0.0F, 2.5F, var1 -> this.lightning.getValue());
    public final Setting<Float> lFlash = new Setting<>("Sky Flash", 0.8F, 0.0F, 2.0F, var1 -> this.lightning.getValue());
    public final Setting<Integer> lBranches = new Setting<>("Branches", 3, 0, 8, var1 -> this.lightning.getValue());
    public final Setting<Float> lLength = new Setting<>("Bolt Length", 1.0F, 0.4F, 1.6F, var1 -> this.lightning.getValue());
    private static final double TAU = Math.PI * 2;
    private double clock;
    private long lastNs;
    private double lastBase = -10.0;
    private double nextStrike = 2.0;
    private final Random rnd = new Random();
    private int curDetail = -1;
    private int gN;
    private int gM;
    private float[] gx;
    private float[] gy;
    private float[] gz;
    private float[] ga;
    private float[] br;
    private float[] bg;
    private float[] bb;
    private float[] cov;
    private int[] fin;
    private static final int MAX_STARS = 2000;
    private float[] stx;
    private float[] sty;
    private float[] stz;
    private float[] ssz;
    private float[] sph;
    private float[] scr;
    private float[] scg;
    private float[] scb;
    private float[] sv;
    private final ArrayList<CustomSky.Bolt> bolts = new ArrayList();
    private float[] vx = new float[8192];
    private float[] vy = new float[8192];
    private float[] vz = new float[8192];
    private int[] vc = new int[8192];
    private int vn;

    public CustomSky() {
        super("CustomSky", Module.Category.RENDER);
    }

    private boolean custom() {
        return this.customColors.getValue();
    }

    private boolean hasClouds() {
        CustomSky.Style var1 = this.style.getValue();
        return var1 == CustomSky.Style.Storm || var1 == CustomSky.Style.Sunset || var1 == CustomSky.Style.Nebula || var1 == CustomSky.Style.Gradient;
    }

    private static int hash(int var0, int var1, int var2) {
        int var3 = var0 * 374761393 + var1 * 668265263 + var2 * 1274126177;
        var3 = (var3 ^ var3 >>> 13) * 1274126177;
        return var3 ^ var3 >>> 16;
    }

    private static double hv(int var0, int var1, int var2) {
        return (hash(var0, var1, var2) & 65535) / 65535.0;
    }

    private static double noise(double var0, double var2, double var4) {
        int var6 = (int)Math.floor(var0);
        int var7 = (int)Math.floor(var2);
        int var8 = (int)Math.floor(var4);
        double var9 = var0 - var6;
        double var11 = var2 - var7;
        double var13 = var4 - var8;
        double var15 = var9 * var9 * (3.0 - 2.0 * var9);
        double var17 = var11 * var11 * (3.0 - 2.0 * var11);
        double var19 = var13 * var13 * (3.0 - 2.0 * var13);
        double var21 = hv(var6, var7, var8);
        double var23 = hv(var6 + 1, var7, var8);
        double var25 = hv(var6, var7 + 1, var8);
        double var27 = hv(var6 + 1, var7 + 1, var8);
        double var29 = hv(var6, var7, var8 + 1);
        double var31 = hv(var6 + 1, var7, var8 + 1);
        double var33 = hv(var6, var7 + 1, var8 + 1);
        double var35 = hv(var6 + 1, var7 + 1, var8 + 1);
        double var37 = var21 + (var23 - var21) * var15;
        double var39 = var25 + (var27 - var25) * var15;
        double var41 = var29 + (var31 - var29) * var15;
        double var43 = var33 + (var35 - var33) * var15;
        double var45 = var37 + (var39 - var37) * var17;
        double var47 = var41 + (var43 - var41) * var17;
        return var45 + (var47 - var45) * var19;
    }

    private static double fbm(double var0, double var2, double var4, int var6) {
        double var7 = 0.0;
        double var9 = 0.5;
        double var11 = 0.0;

        for (int var13 = 0; var13 < var6; var13++) {
            var7 += var9 * noise(var0, var2, var4);
            var11 += var9;
            var0 = var0 * 2.03 + 17.3;
            var2 = var2 * 2.03 + 9.1;
            var4 = var4 * 2.03 + 31.7;
            var9 *= 0.5;
        }

        return var7 / var11;
    }

    private static double sstep(double var0, double var2, double var4) {
        double var6 = (var4 - var0) / (var2 - var0);
        var6 = var6 < 0.0 ? 0.0 : (var6 > 1.0 ? 1.0 : var6);
        return var6 * var6 * (3.0 - 2.0 * var6);
    }

    private static void cloudN(double var0, double var2, double var4, double var6, double var8, double var10, double[] var12) {
        double var13 = var0 * var8 * 2.6 + var6 * 0.045;
        double var15 = var2 * var8 * 3.2;
        double var17 = var4 * var8 * 2.6 - var6 * 0.03;
        double var19 = 0.0;
        double var21 = 0.0;
        if (var10 > 0.001) {
            var19 = (fbm(var13 * 0.55 + var6 * 0.02 + 3.1, var15 * 0.55, var17 * 0.55, 2) - 0.5) * var10 * 2.4;
            var21 = (fbm(var13 * 0.55 + 7.7, var15 * 0.55 + var6 * 0.015, var17 * 0.55 + 1.3, 2) - 0.5) * var10 * 2.4;
        }

        var12[0] = fbm(var13 + var19, var15 + (var19 + var21) * 0.5, var17 + var21, 4);
        var12[1] = fbm(var13 + var19 + 0.14, var15 + (var19 + var21) * 0.5 + 0.2, var17 + var21 + 0.06, 2);
    }

    private static float lerp(float var0, float var1, double var2) {
        return (float)(var0 + (var1 - var0) * var2);
    }

    @Override
    public void onRender3D(class_4587 var1) {
        class_310 var2 = class_310.method_1551();
        if (var2 != null && var2.field_1724 != null) {
            long var3 = System.nanoTime();
            double var5 = this.lastNs == 0L ? 0.0 : Math.min(0.1, (var3 - this.lastNs) / 1.0E9);
            this.lastNs = var3;
            this.clock += var5;
            int var7 = this.detail.getValue();
            if (var7 != this.curDetail) {
                this.buildGrid(var7);
            }

            if (this.stx == null) {
                this.buildStars();
            }

            boolean var8 = CustomSkyGpu.ready();
            if (this.clock - this.lastBase > 0.04) {
                if (var8) {
                    this.updateStarsOnly(this.clock);
                } else {
                    this.updateBase(this.clock);
                }

                this.lastBase = this.clock;
            }

            if (this.lightning.getValue()) {
                if (this.clock >= this.nextStrike) {
                    this.strike(var2);
                    this.nextStrike = this.clock + this.lInterval.getValue().floatValue() * (0.5 + this.rnd.nextDouble());
                }
            } else if (!this.bolts.isEmpty()) {
                this.bolts.clear();
            }

            for (int var9 = this.bolts.size() - 1; var9 >= 0; var9--) {
                CustomSky.Bolt var10 = (CustomSky.Bolt)this.bolts.get(var9);
                if (this.clock - var10.born - var10.delay > 1.0) {
                    this.bolts.remove(var9);
                }
            }

            if (!var8) {
                this.composeFinal();
            }

            int var20 = 12;

            try {
                var20 = (Integer)var2.field_1690.method_42503().method_41753();
            } catch (Throwable var19) {
            }

            double var21 = var20 * 64.0;

            try {
                Matrix4f var12 = RenderSystem.getProjectionMatrix();
                double var13 = var12.m32() / (var12.m22() + 1.0F);
                if (!Double.isNaN(var13) && !Double.isInfinite(var13) && var13 > 16.0) {
                    var21 = var13;
                }
            } catch (Throwable var18) {
            }

            double var22 = var21 * 0.9;
            double var14 = var20 * 16.0 * 1.5 + 24.0;
            if (var22 < var14) {
                var22 = Math.min(var14, var21 * 0.97);
            }

            Matrix4f var16 = var1.method_23760().method_23761();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.setShader(class_757::method_34540);
            float var17 = this.opacity.getValue();
            if (var17 >= 0.999F) {
                RenderSystem.disableBlend();
            } else {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
            }

            if (!var8 || !this.drawDomeGpu(var16, var22)) {
                if (var8) {
                    this.updateBase(this.clock);
                    this.composeFinal();
                }

                this.drawDome(var16, var22);
            }

            this.vn = 0;
            if (this.stars.getValue() && !this.style.is(CustomSky.Style.Storm) && !this.style.is(CustomSky.Style.Rainbow)) {
                this.buildStarQuads(var22 * 0.992);
            }

            if (this.lightning.getValue()) {
                this.buildBolts(var22 * 0.985);
            }

            if (this.vn > 0) {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
                this.flush(var16);
            }

            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private boolean drawDomeGpu(Matrix4f var1, double var2) {
        try {
            CustomSky.Style var4 = this.style.getValue();
            float[] var5 = new float[3];
            float[] var6 = new float[3];
            float[] var7 = new float[3];
            float[] var8 = new float[3];
            float[] var9 = new float[3];
            this.palette(var4, var5, var6, var7, var8, var9);
            CustomSkyGpu.uView.set(var1);
            CustomSkyGpu.uRadius.set((float)var2);
            CustomSkyGpu.uTime.set((float)this.clock);
            CustomSkyGpu.uStyle.set(var4.ordinal());
            CustomSkyGpu.uQuality.set(this.detail.getValue().intValue());
            CustomSkyGpu.uCloud.set(this.cloudSpeed.getValue(), this.cloudScale.getValue(), this.cloudDensity.getValue(), this.swirl.getValue());
            CustomSkyGpu.uBright.set(this.brightness.getValue(), this.opacity.getValue(), this.flowSpeed.getValue(), 0.0F);
            double var10 = Math.toRadians(this.sunAngle.getValue().floatValue());
            double var12 = Math.cos(var10);
            double var14 = 0.04;
            double var16 = Math.sin(var10);
            double var18 = Math.sqrt(var12 * var12 + var14 * var14 + var16 * var16);
            CustomSkyGpu.uSun.set((float)(var12 / var18), (float)(var14 / var18), (float)(var16 / var18));
            CustomSkyGpu.uTop.set(var5[0], var5[1], var5[2]);
            CustomSkyGpu.uHor.set(var6[0], var6[1], var6[2]);
            CustomSkyGpu.uBot.set(var7[0], var7[1], var7[2]);
            CustomSkyGpu.uDark.set(var8[0], var8[1], var8[2]);
            CustomSkyGpu.uLight.set(var9[0], var9[1], var9[2]);
            ColorSetting var20 = this.lColor.getValue();
            CustomSkyGpu.uFlashCol.set(0.5F + 0.5F * var20.getRed() / 255.0F, 0.5F + 0.5F * var20.getGreen() / 255.0F, 0.5F + 0.5F * var20.getBlue() / 255.0F);
            double[] var21 = new double[16];
            float var22 = this.lFlash.getValue();
            if (this.lightning.getValue() && var22 > 0.001F) {
                for (CustomSky.Bolt var24 : this.bolts) {
                    double var25 = this.clock - var24.born - var24.delay;
                    if (!(var25 < 0.0)) {
                        double var27 = envelope(var25);
                        if (!(var27 < 0.01)) {
                            double var29 = var27 * var22 * (var24.sheet ? 1.25 : 0.8);
                            int var31 = -1;
                            double var32 = var29;

                            for (int var34 = 0; var34 < 4; var34++) {
                                if (var21[var34 * 4 + 3] < var32) {
                                    var32 = var21[var34 * 4 + 3];
                                    var31 = var34;
                                }
                            }

                            if (var31 >= 0) {
                                var21[var31 * 4] = var24.cx;
                                var21[var31 * 4 + 1] = var24.cy;
                                var21[var31 * 4 + 2] = var24.cz;
                                var21[var31 * 4 + 3] = var29;
                            }
                        }
                    }
                }
            }

            for (int var36 = 0; var36 < 4; var36++) {
                CustomSkyGpu.uFlash[var36].set((float)var21[var36 * 4], (float)var21[var36 * 4 + 1], (float)var21[var36 * 4 + 2], (float)var21[var36 * 4 + 3]);
            }

            RenderSystem.setShader(() -> CustomSkyGpu.SHADER.getProgram());
            class_287 var37 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1592);
            int var38 = this.gN + 1;

            for (int var39 = 0; var39 < this.gM; var39++) {
                for (int var26 = 0; var26 < this.gN; var26++) {
                    int var40 = var39 * var38 + var26;
                    int[] var28 = new int[]{var40, var40 + 1, var40 + var38 + 1, var40 + var38};

                    for (int var41 = 0; var41 < 4; var41++) {
                        var37.method_22912(this.gx[var28[var41]], this.gy[var28[var41]], this.gz[var28[var41]]);
                    }
                }
            }

            class_286.method_43433(var37.method_60800());
            RenderSystem.setShader(class_757::method_34540);
            return true;
        } catch (Throwable var35) {
            RenderSystem.setShader(class_757::method_34540);
            return false;
        }
    }

    private void drawDome(Matrix4f var1, double var2) {
        class_287 var4 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);
        int var5 = this.gN + 1;
        int var6 = (int)(this.opacity.getValue() * 255.0F);

        for (int var7 = 0; var7 < this.gM; var7++) {
            for (int var8 = 0; var8 < this.gN; var8++) {
                int var9 = var7 * var5 + var8;
                int var10 = var9 + 1;
                int var11 = var9 + var5 + 1;
                int var12 = var9 + var5;
                int[] var13 = new int[]{var9, var10, var11, var12};

                for (int var14 = 0; var14 < 4; var14++) {
                    int var15 = var13[var14];
                    int var16 = this.fin[var15] & 16777215 | var6 << 24;
                    var4.method_22918(var1, (float)(this.gx[var15] * var2), (float)(this.gy[var15] * var2), (float)(this.gz[var15] * var2)).method_39415(var16);
                }
            }
        }

        class_286.method_43433(var4.method_60800());
    }

    private void flush(Matrix4f var1) {
        class_287 var2 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);

        for (int var3 = 0; var3 < this.vn; var3++) {
            var2.method_22918(var1, this.vx[var3], this.vy[var3], this.vz[var3]).method_39415(this.vc[var3]);
        }

        class_286.method_43433(var2.method_60800());
    }

    private void buildGrid(int var1) {
        this.curDetail = var1;
        this.gN = var1 == 1 ? 64 : (var1 == 2 ? 96 : 144);
        this.gM = var1 == 1 ? 28 : (var1 == 2 ? 44 : 72);
        int var2 = (this.gN + 1) * (this.gM + 1);
        this.gx = new float[var2];
        this.gy = new float[var2];
        this.gz = new float[var2];
        this.ga = new float[var2];
        this.br = new float[var2];
        this.bg = new float[var2];
        this.bb = new float[var2];
        this.cov = new float[var2];
        this.fin = new int[var2];

        for (int var3 = 0; var3 <= this.gM; var3++) {
            double var4 = -1.0 + 2.0 * var3 / this.gM;
            double var6 = Math.signum(var4) * Math.pow(Math.abs(var4), 1.35) * (Math.PI / 2);
            double var8 = Math.cos(var6);
            double var10 = Math.sin(var6);

            for (int var12 = 0; var12 <= this.gN; var12++) {
                double var13 = var12 * (Math.PI * 2) / this.gN;
                int var15 = var3 * (this.gN + 1) + var12;
                this.gx[var15] = (float)(var8 * Math.cos(var13));
                this.gy[var15] = (float)var10;
                this.gz[var15] = (float)(var8 * Math.sin(var13));
                this.ga[var15] = (float)var13;
            }
        }

        this.lastBase = -10.0;
    }

    private void buildStars() {
        this.stx = new float[2000];
        this.sty = new float[2000];
        this.stz = new float[2000];
        this.ssz = new float[2000];
        this.sph = new float[2000];
        this.scr = new float[2000];
        this.scg = new float[2000];
        this.scb = new float[2000];
        this.sv = new float[2000];
        Random var1 = new Random(1337L);

        for (int var2 = 0; var2 < 2000; var2++) {
            double var3 = var1.nextDouble() * 2.0 - 1.0;
            double var5 = var1.nextDouble() * (Math.PI * 2);
            double var7 = Math.sqrt(1.0 - var3 * var3);
            this.stx[var2] = (float)(var7 * Math.cos(var5));
            this.sty[var2] = (float)var3;
            this.stz[var2] = (float)(var7 * Math.sin(var5));
            this.ssz[var2] = (float)(0.5 + Math.pow(var1.nextDouble(), 3.0) * 1.8);
            this.sph[var2] = (float)(var1.nextDouble() * (Math.PI * 2));
            double var9 = var1.nextDouble();
            this.scr[var2] = (float)(0.78 + 0.22 * var9);
            this.scg[var2] = (float)(0.84 + 0.16 * (1.0 - Math.abs(var9 - 0.5) * 2.0));
            this.scb[var2] = 1.0F;
        }
    }

    private void palette(CustomSky.Style var1, float[] var2, float[] var3, float[] var4, float[] var5, float[] var6) {
        float[][] var7 = switch (var1) {
            case Storm -> new float[][]{{0.05F, 0.08F, 0.11F}, {0.5F, 0.6F, 0.63F}, {0.1F, 0.13F, 0.16F}, {0.03F, 0.05F, 0.07F}, {0.52F, 0.6F, 0.64F}};
            case Aurora -> new float[][]{{0.008F, 0.014F, 0.045F}, {0.035F, 0.085F, 0.14F}, {0.01F, 0.02F, 0.04F}, {0.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 0.0F}};
            case Nebula -> new float[][]{{0.02F, 0.01F, 0.06F}, {0.08F, 0.04F, 0.14F}, {0.02F, 0.01F, 0.05F}, {0.42F, 0.12F, 0.7F}, {0.1F, 0.58F, 0.9F}};
            case Sunset -> new float[][]{{0.1F, 0.1F, 0.32F}, {1.0F, 0.45F, 0.18F}, {0.25F, 0.08F, 0.1F}, {0.45F, 0.18F, 0.32F}, {1.0F, 0.66F, 0.36F}};
            default -> new float[][]{{0.12F, 0.2F, 0.55F}, {0.55F, 0.75F, 1.0F}, {0.1F, 0.15F, 0.3F}, {0.35F, 0.45F, 0.62F}, {0.8F, 0.88F, 0.96F}};
        };
        System.arraycopy(var7[0], 0, var2, 0, 3);
        System.arraycopy(var7[1], 0, var3, 0, 3);
        System.arraycopy(var7[2], 0, var4, 0, 3);
        System.arraycopy(var7[3], 0, var5, 0, 3);
        System.arraycopy(var7[4], 0, var6, 0, 3);
        if (this.customColors.getValue()) {
            ColorSetting var8 = this.topColor.getValue();
            ColorSetting var9 = this.horizonColor.getValue();
            ColorSetting var10 = this.bottomColor.getValue();
            ColorSetting var11 = this.cloudColor.getValue();
            var2[0] = var8.getRed() / 255.0F;
            var2[1] = var8.getGreen() / 255.0F;
            var2[2] = var8.getBlue() / 255.0F;
            var3[0] = var9.getRed() / 255.0F;
            var3[1] = var9.getGreen() / 255.0F;
            var3[2] = var9.getBlue() / 255.0F;
            var4[0] = var10.getRed() / 255.0F;
            var4[1] = var10.getGreen() / 255.0F;
            var4[2] = var10.getBlue() / 255.0F;
            if (this.hasClouds()) {
                var6[0] = var11.getRed() / 255.0F;
                var6[1] = var11.getGreen() / 255.0F;
                var6[2] = var11.getBlue() / 255.0F;
                var5[0] = var6[0] * 0.3F;
                var5[1] = var6[1] * 0.3F;
                var5[2] = var6[2] * 0.3F;
            }
        }
    }

    private void updateBase(double var1) {
        CustomSky.Style var3 = this.style.getValue();
        float[] var4 = new float[3];
        float[] var5 = new float[3];
        float[] var6 = new float[3];
        float[] var7 = new float[3];
        float[] var8 = new float[3];
        this.palette(var3, var4, var5, var6, var7, var8);
        double var9 = this.cloudScale.getValue().floatValue();
        double var11 = this.cloudSpeed.getValue().floatValue();
        double var13 = this.swirl.getValue().floatValue();
        double var15 = this.cloudDensity.getValue().floatValue() * (var3 == CustomSky.Style.Gradient ? 0.6 : 1.0);
        double var17 = var1 * var11;
        double var19 = var1 * this.flowSpeed.getValue().floatValue();
        boolean var21 = this.hasClouds() && var15 > 0.001;
        double[] var22 = new double[2];
        double var23 = Math.toRadians(this.sunAngle.getValue().floatValue());
        double var25 = Math.cos(var23) * 0.9992;
        double var27 = 0.04;
        double var29 = Math.sin(var23) * 0.9992;
        double var31 = Math.sqrt(var25 * var25 + var27 * var27 + var29 * var29);
        var25 /= var31;
        var27 /= var31;
        var29 /= var31;
        double var33 = 0.35;
        double var35 = 0.8;
        double var37 = 0.49;
        double var39 = Math.sqrt(var33 * var33 + var35 * var35 + var37 * var37);
        var33 /= var39;
        var35 /= var39;
        var37 /= var39;
        int var41 = this.br.length;

        for (int var42 = 0; var42 < var41; var42++) {
            double var43 = this.gx[var42];
            double var45 = this.gy[var42];
            double var47 = this.gz[var42];
            double var49 = this.ga[var42];
            double var51 = var45;
            if (var3 == CustomSky.Style.Gradient) {
                var51 = var45 + 0.06 * Math.sin(var49 * 2.0 + var19 * 0.5);
            }

            float var55;
            float var56;
            float var57;
            if (var51 >= 0.0) {
                double var53 = Math.pow(Math.min(1.0, var51), 0.55);
                var55 = lerp(var5[0], var4[0], var53);
                var56 = lerp(var5[1], var4[1], var53);
                var57 = lerp(var5[2], var4[2], var53);
            } else {
                double var90 = Math.pow(Math.min(1.0, -var51), 0.6);
                var55 = lerp(var5[0], var6[0], var90);
                var56 = lerp(var5[1], var6[1], var90);
                var57 = lerp(var5[2], var6[2], var90);
            }

            double var58 = 0.3;
            switch (var3) {
                case Storm:
                case Sunset:
                case Gradient:
                    var58 = 0.0;
                    if (var21) {
                        cloudN(var43, var45, var47, var17, var9, var13, var22);
                        double var97 = var22[0];
                        double var100 = sstep(1.0 - var15 - 0.15, 1.0 - var15 + 0.12, var97);
                        var100 *= 0.3 + 0.7 * sstep(-0.5, 0.05, var45);
                        if (var3 == CustomSky.Style.Storm) {
                            var100 *= 0.6 + 0.4 * sstep(0.0, 0.5, var45);
                        }

                        double var104 = Math.max(0.0, Math.min(1.0, 0.5 + (var97 - var22[1]) * 5.0));
                        double var106 = sstep(0.55, 0.9, var97);
                        double var108 = Math.max(0.0, Math.min(1.0, var104 * 0.85 + 0.15 * (1.0 - var106)));
                        float var111 = lerp(var7[0], var8[0], var108);
                        float var71 = lerp(var7[1], var8[1], var108);
                        float var114 = lerp(var7[2], var8[2], var108);
                        var111 = lerp(var111, var7[0], var106 * 0.45);
                        var71 = lerp(var71, var7[1], var106 * 0.45);
                        var114 = lerp(var114, var7[2], var106 * 0.45);
                        var55 = lerp(var55, var111, var100 * 0.96);
                        var56 = lerp(var56, var71, var100 * 0.96);
                        var57 = lerp(var57, var114, var100 * 0.96);
                        var58 = var100;
                    }

                    if (var3 == CustomSky.Style.Sunset) {
                        double var98 = Math.max(0.0, var43 * var25 + var45 * var27 + var47 * var29);
                        double var102 = Math.pow(var98, 26.0) * 1.1 + Math.pow(var98, 5.0) * 0.28;
                        if (var98 > 0.9986) {
                            var102 += 2.0;
                        }

                        var55 = (float)(var55 + 1.0 * var102);
                        var56 = (float)(var56 + 0.78F * var102);
                        var57 = (float)(var57 + 0.46F * var102);
                    }
                    break;
                case Aurora:
                    var58 = 0.0;
                    if (var45 > 0.0) {
                        double var96 = Math.asin(Math.min(1.0, var45));
                        double var103 = 0.42 + 0.13 * Math.sin(var49 * 3.0 + var19 * 0.25) + 0.08 * Math.sin(var49 * 7.0 - var19 * 0.4);
                        double var105 = 0.24 + 0.06 * Math.sin(var49 * 2.0 + var19 * 0.2);
                        double var107 = (var96 - var103) / var105;
                        double var109 = Math.exp(-var107 * var107);
                        double var72 = fbm(var49 * 2.2 + 5.0, var96 * 4.0 - var19 * 0.1, var19 * 0.15, 3);
                        double var74 = 0.55 + 0.45 * Math.sin(var49 * 46.0 + var72 * 9.0 + var19 * 0.6);
                        var109 *= (0.35 + 0.9 * var72) * (0.5 + 0.5 * var74) * sstep(0.0, 0.08, var45);
                        double var76 = sstep(var103 - 0.12, var103 + 0.45, var96);
                        double var78 = 0.15 + 0.5 * var76;
                        double var80 = 1.0 + -0.72 * var76;
                        double var82 = 0.5 + 0.5 * var76;
                        var55 = (float)(var55 + var78 * var109 * 1.5);
                        var56 = (float)(var56 + var80 * var109 * 1.5);
                        var57 = (float)(var57 + var82 * var109 * 1.5);
                    }
                    break;
                case Nebula:
                    var58 = 0.0;
                    if (var21) {
                        cloudN(var43, var45, var47, var17, var9, var13, var22);
                        double var95 = var22[0];
                        cloudN(var43 + 0.7, var45 - 0.4, var47 + 0.2, var17 + 40.0, var9 * 1.2, var13, var22);
                        double var99 = var22[0];
                        double var64 = sstep(0.38, 0.85, var95) * var15 * 1.6;
                        double var66 = sstep(0.42, 0.9, var99) * var15 * 1.4;
                        var55 = (float)(var55 + (var7[0] * var64 * 0.9 + var8[0] * var66 * 0.75));
                        var56 = (float)(var56 + (var7[1] * var64 * 0.9 + var8[1] * var66 * 0.75));
                        var57 = (float)(var57 + (var7[2] * var64 * 0.9 + var8[2] * var66 * 0.75));
                        double var68 = var43 * var33 + var45 * var35 + var47 * var37;
                        double var70 = Math.exp(-var68 * var68 / 0.03) * (0.35 + 0.65 * var95) * 0.55;
                        var55 = (float)(var55 + 0.7F * var70);
                        var56 = (float)(var56 + 0.75 * var70);
                        var57 = (float)(var57 + 1.0 * var70);
                    }
                    break;
                default:
                    var58 = 0.0;
                    double var60 = (var49 / (Math.PI * 2) + var45 * 0.7 + var19 * 0.03) % 1.0;
                    if (var60 < 0.0) {
                        var60++;
                    }

                    int var62 = Color.HSBtoRGB((float)var60, 0.62F, (float)(0.62 + 0.38 * Math.max(0.0, var45)));
                    var55 = (var62 >> 16 & 0xFF) / 255.0F;
                    var56 = (var62 >> 8 & 0xFF) / 255.0F;
                    var57 = (var62 & 0xFF) / 255.0F;
            }

            this.br[var42] = var55;
            this.bg[var42] = var56;
            this.bb[var42] = var57;
            this.cov[var42] = (float)var58;
        }

        this.updateStars(var3, var21, var15, var9, var13, var17);
    }

    private void updateStarsOnly(double var1) {
        CustomSky.Style var3 = this.style.getValue();
        double var4 = this.cloudDensity.getValue().floatValue() * (var3 == CustomSky.Style.Gradient ? 0.6 : 1.0);
        this.updateStars(
            var3,
            this.hasClouds() && var4 > 0.001,
            var4,
            this.cloudScale.getValue().floatValue(),
            this.swirl.getValue().floatValue(),
            var1 * this.cloudSpeed.getValue().floatValue()
        );
    }

    private void updateStars(CustomSky.Style var1, boolean var2, double var3, double var5, double var7, double var9) {
        double[] var11 = new double[2];
        double var12 = var1 != CustomSky.Style.Aurora && var1 != CustomSky.Style.Nebula
            ? (var1 == CustomSky.Style.Gradient ? 0.55 : (var1 == CustomSky.Style.Sunset ? 0.35 : 0.0))
            : 1.0;
        int var14 = Math.min(2000, this.starCount.getValue());

        for (int var15 = 0; var15 < var14; var15++) {
            double var16 = var12 * sstep(-0.02, 0.14, this.sty[var15]);
            if (var16 > 0.001 && var2 && var1 != CustomSky.Style.Nebula) {
                cloudN(this.stx[var15], this.sty[var15], this.stz[var15], var9, var5, var7, var11);
                double var18 = sstep(1.0 - var3 - 0.15, 1.0 - var3 + 0.12, var11[0]);
                var16 *= 1.0 - var18;
            }

            this.sv[var15] = (float)var16;
        }
    }

    private void composeFinal() {
        int var1 = this.br.length;
        float var2 = this.brightness.getValue();
        int var3 = 0;
        double[] var4 = new double[this.bolts.size() * 4];
        float var5 = this.lFlash.getValue();
        if (this.lightning.getValue() && var5 > 0.001F) {
            for (CustomSky.Bolt var7 : this.bolts) {
                double var8 = this.clock - var7.born - var7.delay;
                if (!(var8 < 0.0)) {
                    double var10 = envelope(var8);
                    if (!(var10 < 0.01)) {
                        var4[var3 * 4] = var7.cx;
                        var4[var3 * 4 + 1] = var7.cy;
                        var4[var3 * 4 + 2] = var7.cz;
                        var4[var3 * 4 + 3] = var10 * var5 * (var7.sheet ? 1.25 : 0.8);
                        var3++;
                    }
                }
            }
        }

        ColorSetting var21 = this.lColor.getValue();
        float var22 = 0.5F + 0.5F * var21.getRed() / 255.0F;
        float var23 = 0.5F + 0.5F * var21.getGreen() / 255.0F;
        float var9 = 0.5F + 0.5F * var21.getBlue() / 255.0F;

        for (int var24 = 0; var24 < var1; var24++) {
            float var11 = this.br[var24] * var2;
            float var12 = this.bg[var24] * var2;
            float var13 = this.bb[var24] * var2;
            if (var3 > 0) {
                double var14 = 0.0;
                double var16 = 0.3 + 0.7 * this.cov[var24];

                for (int var18 = 0; var18 < var3; var18++) {
                    double var19 = this.gx[var24] * var4[var18 * 4] + this.gy[var24] * var4[var18 * 4 + 1] + this.gz[var24] * var4[var18 * 4 + 2];
                    var14 += var4[var18 * 4 + 3] * Math.exp((var19 - 1.0) * 2.2);
                }

                var14 *= var16;
                var11 = (float)(var11 + var22 * var14);
                var12 = (float)(var12 + var23 * var14);
                var13 = (float)(var13 + var9 * var14);
            }

            int var26 = (int)(Math.max(0.0F, Math.min(1.0F, var11)) * 255.0F);
            int var15 = (int)(Math.max(0.0F, Math.min(1.0F, var12)) * 255.0F);
            int var27 = (int)(Math.max(0.0F, Math.min(1.0F, var13)) * 255.0F);
            this.fin[var24] = 0xFF000000 | var26 << 16 | var15 << 8 | var27;
        }
    }

    private void ensure(int var1) {
        if (var1 > this.vx.length) {
            int var2 = Math.max(var1, this.vx.length * 2);
            this.vx = Arrays.copyOf(this.vx, var2);
            this.vy = Arrays.copyOf(this.vy, var2);
            this.vz = Arrays.copyOf(this.vz, var2);
            this.vc = Arrays.copyOf(this.vc, var2);
        }
    }

    private void v(double var1, double var3, double var5, int var7) {
        this.ensure(this.vn + 1);
        this.vx[this.vn] = (float)var1;
        this.vy[this.vn] = (float)var3;
        this.vz[this.vn] = (float)var5;
        this.vc[this.vn] = var7;
        this.vn++;
    }

    private void quad(
        double var1,
        double var3,
        double var5,
        int var7,
        double var8,
        double var10,
        double var12,
        int var14,
        double var15,
        double var17,
        double var19,
        int var21,
        double var22,
        double var24,
        double var26,
        int var28
    ) {
        this.v(var1, var3, var5, var7);
        this.v(var8, var10, var12, var14);
        this.v(var15, var17, var19, var21);
        this.v(var22, var24, var26, var28);
    }

    private static int argb(int var0, double var1) {
        int var3 = (int)Math.max(0.0, Math.min(255.0, var1 * 255.0));
        return var3 << 24 | var0 & 16777215;
    }

    private void buildStarQuads(double var1) {
        int var3 = Math.min(2000, this.starCount.getValue());
        float var4 = this.starSize.getValue();
        float var5 = this.opacity.getValue();

        for (int var6 = 0; var6 < var3; var6++) {
            double var7 = this.sv[var6];
            if (!(var7 < 0.02)) {
                double var9 = 0.65 + 0.35 * Math.sin(this.clock * (1.4 + var6 % 7 * 0.35) + this.sph[var6]);
                double var11 = var7 * var9 * var5;
                if (!(var11 < 0.03)) {
                    double var13 = this.stx[var6];
                    double var15 = this.sty[var6];
                    double var17 = this.stz[var6];
                    double var19 = var17;
                    double var21 = 0.0;
                    double var23 = -var13;
                    double var25 = Math.sqrt(var17 * var17 + var23 * var23);
                    if (var25 < 1.0E-4) {
                        var19 = 1.0;
                        var21 = 0.0;
                        var23 = 0.0;
                        var25 = 1.0;
                    }

                    var19 /= var25;
                    var21 /= var25;
                    var23 /= var25;
                    double var27 = var15 * var23 - var17 * var21;
                    double var29 = var17 * var19 - var13 * var23;
                    double var31 = var13 * var21 - var15 * var19;
                    int var33 = (int)(this.scr[var6] * 255.0F) << 16 | (int)(this.scg[var6] * 255.0F) << 8 | (int)(this.scb[var6] * 255.0F);
                    double var34 = var13 * var1;
                    double var36 = var15 * var1;
                    double var38 = var17 * var1;
                    double var40 = var1 * 0.0013 * var4 * this.ssz[var6];
                    this.starQuad(var34, var36, var38, var19, var21, var23, var27, var29, var31, var40, argb(var33, var11));
                    this.starQuad(var34, var36, var38, var19, var21, var23, var27, var29, var31, var40 * 3.4, argb(var33, var11 * 0.16));
                }
            }
        }
    }

    private void starQuad(
        double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, int var21
    ) {
        this.quad(
            var1 - var7 * var19 - var13 * var19,
            var3 - var9 * var19 - var15 * var19,
            var5 - var11 * var19 - var17 * var19,
            var21,
            var1 + var7 * var19 - var13 * var19,
            var3 + var9 * var19 - var15 * var19,
            var5 + var11 * var19 - var17 * var19,
            var21,
            var1 + var7 * var19 + var13 * var19,
            var3 + var9 * var19 + var15 * var19,
            var5 + var11 * var19 + var17 * var19,
            var21,
            var1 - var7 * var19 + var13 * var19,
            var3 - var9 * var19 + var15 * var19,
            var5 - var11 * var19 + var17 * var19,
            var21
        );
    }

    private void line(double var1, double var3, double var5, double var7, double var9, double var11, double var13, int var15, int var16) {
        double var17 = var7 - var1;
        double var19 = var9 - var3;
        double var21 = var11 - var5;
        double var23 = (var1 + var7) * 0.5;
        double var25 = (var3 + var9) * 0.5;
        double var27 = (var5 + var11) * 0.5;
        double var29 = var19 * var27 - var21 * var25;
        double var31 = var21 * var23 - var17 * var27;
        double var33 = var17 * var25 - var19 * var23;
        double var35 = Math.sqrt(var29 * var29 + var31 * var31 + var33 * var33);
        if (!(var35 < 1.0E-9)) {
            double var37 = var13 / var35;
            var29 *= var37;
            var31 *= var37;
            var33 *= var37;
            double var39 = 0.35;
            int var41 = var15 & 16777215;
            int var42 = var16 & 16777215;
            this.quad(
                var1 - var29,
                var3 - var31,
                var5 - var33,
                var41,
                var1 - var29 * var39,
                var3 - var31 * var39,
                var5 - var33 * var39,
                var15,
                var7 - var29 * var39,
                var9 - var31 * var39,
                var11 - var33 * var39,
                var16,
                var7 - var29,
                var9 - var31,
                var11 - var33,
                var42
            );
            this.quad(
                var1 - var29 * var39,
                var3 - var31 * var39,
                var5 - var33 * var39,
                var15,
                var1 + var29 * var39,
                var3 + var31 * var39,
                var5 + var33 * var39,
                var15,
                var7 + var29 * var39,
                var9 + var31 * var39,
                var11 + var33 * var39,
                var16,
                var7 - var29 * var39,
                var9 - var31 * var39,
                var11 - var33 * var39,
                var16
            );
            this.quad(
                var1 + var29 * var39,
                var3 + var31 * var39,
                var5 + var33 * var39,
                var15,
                var1 + var29,
                var3 + var31,
                var5 + var33,
                var41,
                var7 + var29,
                var9 + var31,
                var11 + var33,
                var42,
                var7 + var29 * var39,
                var9 + var31 * var39,
                var11 + var33 * var39,
                var16
            );
        }
    }

    private static double envelope(double var0) {
        double var2 = var0 < 0.04 ? var0 / 0.04 : Math.exp(-(var0 - 0.04) * 9.0);
        double var4 = var0 - 0.14;
        double var6 = var4 < 0.0 ? 0.0 : (var4 < 0.03 ? var4 / 0.03 : Math.exp(-(var4 - 0.03) * 8.0)) * 0.8;
        double var8 = var0 - 0.27;
        double var10 = var8 < 0.0 ? 0.0 : (var8 < 0.03 ? var8 / 0.03 : Math.exp(-(var8 - 0.03) * 10.0)) * 0.5;
        return Math.max(var2, Math.max(var6, var10));
    }

    private void buildBolts(double var1) {
        ColorSetting var3 = this.lColor.getValue();
        int var4 = var3.getRed() << 16 | var3.getGreen() << 8 | var3.getBlue();
        int var5 = Math.min(255, var3.getRed() + (255 - var3.getRed()) * 6 / 10) << 16
            | Math.min(255, var3.getGreen() + (255 - var3.getGreen()) * 6 / 10) << 8
            | Math.min(255, var3.getBlue() + (255 - var3.getBlue()) * 6 / 10);
        double var6 = this.lThickness.getValue().floatValue();
        double var8 = this.lGlow.getValue().floatValue();

        for (CustomSky.Bolt var11 : this.bolts) {
            double var12 = this.clock - var11.born - var11.delay;
            if (!(var12 < 0.0)) {
                double var14 = envelope(var12);
                if (!(var14 < 0.01)) {
                    if (var11.sheet) {
                        this.sheetDisc(var11, var1, var14 * 0.55 * Math.max(0.2, var8), var4, var12);
                    } else {
                        double var16 = Math.min(1.0, var12 / 0.06);

                        for (int var18 = 0; var18 < var11.n; var18++) {
                            if (!(var11.ord[var18] > var16)) {
                                byte var19 = var11.lv[var18];
                                double var20 = var1 * 0.0034 * var6 * (var19 == 0 ? 1.0 : (var19 == 1 ? 0.6 : 0.38));
                                double var22 = 0.82 + 0.18 * Math.sin(var18 * 12.9898 + this.clock * 55.0);
                                double var24 = Math.min(1.0, var14 * var22 * (var19 == 0 ? 1.0 : 0.85));
                                double var26 = Math.cos(var11.e0[var18]);
                                double var28 = Math.cos(var11.e1[var18]);
                                double var30 = var26 * Math.cos(var11.a0[var18]) * var1;
                                double var32 = Math.sin(var11.e0[var18]) * var1;
                                double var34 = var26 * Math.sin(var11.a0[var18]) * var1;
                                double var36 = var28 * Math.cos(var11.a1[var18]) * var1;
                                double var38 = Math.sin(var11.e1[var18]) * var1;
                                double var40 = var28 * Math.sin(var11.a1[var18]) * var1;
                                if (var8 > 0.01) {
                                    int var42 = argb(var4, var24 * 0.13 * var8);
                                    this.line(var30, var32, var34, var36, var38, var40, var20 * 7.0, var42, var42);
                                    int var43 = argb(var4, var24 * 0.3 * var8);
                                    this.line(var30, var32, var34, var36, var38, var40, var20 * 2.6, var43, var43);
                                }

                                int var44 = argb(var5, var24);
                                this.line(var30, var32, var34, var36, var38, var40, var20, var44, var44);
                            }
                        }
                    }
                }
            }
        }
    }

    private void sheetDisc(CustomSky.Bolt var1, double var2, double var4, int var6, double var7) {
        double var9 = var2 * Math.tan(Math.min(1.2, var1.sheetR * (1.0 + var7 * 0.35)));
        double var11 = var1.cx * var2;
        double var13 = var1.cy * var2;
        double var15 = var1.cz * var2;
        double var17 = var1.cz;
        double var19 = -var1.cx;
        double var21 = Math.sqrt(var17 * var17 + var19 * var19);
        double var23 = 0.0;
        if (var21 < 1.0E-4) {
            var17 = 1.0;
            var19 = 0.0;
            var21 = 1.0;
        }

        var17 /= var21;
        var19 /= var21;
        double var25 = var1.cy * var19 - var1.cz * var23;
        double var27 = var1.cz * var17 - var1.cx * var19;
        double var29 = var1.cx * var23 - var1.cy * var17;
        byte var31 = 28;
        int var32 = argb(var6, var4);
        int var33 = argb(var6, 0.0);

        for (int var34 = 0; var34 < var31; var34++) {
            double var35 = var34 * (Math.PI * 2) / var31;
            double var37 = (var34 + 1) * (Math.PI * 2) / var31;
            double var39 = var11 + (var17 * Math.cos(var35) + var25 * Math.sin(var35)) * var9;
            double var41 = var13 + (var23 * Math.cos(var35) + var27 * Math.sin(var35)) * var9;
            double var43 = var15 + (var19 * Math.cos(var35) + var29 * Math.sin(var35)) * var9;
            double var45 = var11 + (var17 * Math.cos(var37) + var25 * Math.sin(var37)) * var9;
            double var47 = var13 + (var23 * Math.cos(var37) + var27 * Math.sin(var37)) * var9;
            double var49 = var15 + (var19 * Math.cos(var37) + var29 * Math.sin(var37)) * var9;
            this.quad(var11, var13, var15, var32, var39, var41, var43, var33, var45, var47, var49, var33, var45, var47, var49, var33);
        }
    }

    private static double angDiff(double var0, double var2) {
        double var4 = (var0 - var2) % (Math.PI * 2);
        if (var4 > Math.PI) {
            var4 -= Math.PI * 2;
        }

        if (var4 < -Math.PI) {
            var4 += Math.PI * 2;
        }

        return var4;
    }

    private static void addSeg(CustomSky.Bolt var0, double var1, double var3, double var5, double var7, int var9, double var10) {
        if (var0.n >= var0.a0.length) {
            int var12 = var0.n * 2;
            var0.a0 = Arrays.copyOf(var0.a0, var12);
            var0.e0 = Arrays.copyOf(var0.e0, var12);
            var0.a1 = Arrays.copyOf(var0.a1, var12);
            var0.e1 = Arrays.copyOf(var0.e1, var12);
            var0.ord = Arrays.copyOf(var0.ord, var12);
            var0.lv = Arrays.copyOf(var0.lv, var12);
        }

        var0.a0[var0.n] = (float)var1;
        var0.e0[var0.n] = (float)var3;
        var0.a1[var0.n] = (float)var5;
        var0.e1[var0.n] = (float)var7;
        var0.ord[var0.n] = (float)var10;
        var0.lv[var0.n] = (byte)var9;
        var0.n++;
    }

    private void walk(
        CustomSky.Bolt var1,
        double var2,
        double var4,
        double var6,
        int var8,
        double var9,
        double var11,
        double var13,
        double var15,
        int var17,
        double var18,
        double var20,
        double var22,
        int var24,
        double var25
    ) {
        for (int var27 = 0; var27 < var8; var27++) {
            var6 += (this.rnd.nextDouble() - 0.5) * var11 + angDiff(var15, var6) * var13;
            double var28 = var9 * (0.6 + 0.8 * this.rnd.nextDouble());
            double var30 = var2 + Math.cos(var6) * var28 / Math.max(0.35, Math.cos(var4));
            double var32 = var4 + Math.sin(var6) * var28;
            double var34 = var18 + var20 * (var27 + 1) / var8;
            boolean var36 = var32 < Math.max(var25, 0.03);
            if (var32 < 0.03) {
                var32 = 0.03;
            }

            addSeg(var1, var2, var4, var30, var32, var17, var34);
            if (var36) {
                break;
            }

            if (var17 < var24 && this.rnd.nextDouble() < var22 * (var17 == 0 ? 1.0 : 0.5)) {
                double var37 = this.rnd.nextBoolean() ? 1.0 : -1.0;
                double var39 = var6 + var37 * (0.45 + this.rnd.nextDouble() * 0.7);
                int var41 = (int)((4 + this.rnd.nextInt(8)) * (var17 == 0 ? 1.0 : 0.6));
                this.walk(
                    var1, var30, var32, var39, Math.max(2, var41), var9 * 0.85, var11 * 1.1, 0.08, var39, var17 + 1, var34, var20 * 0.4, var22, var24, -9.0
                );
            }

            var2 = var30;
            var4 = var32;
        }
    }

    private void finish(CustomSky.Bolt var1) {
        double var2 = 0.0;
        double var4 = 0.0;
        double var6 = 0.0;

        for (int var8 = 0; var8 < var1.n; var8++) {
            double var9 = Math.cos(var1.e1[var8]);
            var2 += var9 * Math.cos(var1.a1[var8]);
            var4 += Math.sin(var1.e1[var8]);
            var6 += var9 * Math.sin(var1.a1[var8]);
        }

        double var12 = Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
        if (var12 < 1.0E-6) {
            var2 = 0.0;
            var4 = 1.0;
            var6 = 0.0;
            var12 = 1.0;
        }

        var1.cx = var2 / var12;
        var1.cy = var4 / var12;
        var1.cz = var6 / var12;
        float var10 = 1.0E-6F;

        for (int var11 = 0; var11 < var1.n; var11++) {
            var10 = Math.max(var10, var1.ord[var11]);
        }

        for (int var13 = 0; var13 < var1.n; var13++) {
            var1.ord[var13] = var1.ord[var13] / var10;
        }
    }

    private void strike(class_310 var1) {
        CustomSky.Shape var2 = this.lShape.getValue();
        if (var2 == CustomSky.Shape.Random) {
            var2 = CustomSky.Shape.values()[1 + this.rnd.nextInt(6)];
        }

        double var3 = this.lLength.getValue().floatValue();
        int var5 = this.lBranches.getValue();
        double var6 = 0.03 * var5;
        int var8 = var5 > 0 ? 2 : 0;
        double var9 = 0.0;

        try {
            double var11 = Math.toRadians(var1.field_1724.method_36454());
            var9 = Math.atan2(Math.cos(var11), -Math.sin(var11));
        } catch (Throwable var27) {
        }

        double var28 = this.rnd.nextDouble() < 0.78 ? var9 + this.rnd.nextGaussian() * 0.85 : this.rnd.nextDouble() * (Math.PI * 2);
        switch (var2) {
            case Bolt:
                CustomSky.Bolt var33 = this.newBolt(0.0);
                double var38 = 1.0 + this.rnd.nextDouble() * 0.35;
                this.walk(
                    var33,
                    var28,
                    var38,
                    (-Math.PI / 2) + (this.rnd.nextDouble() - 0.5) * 0.5,
                    (int)(60.0 * var3),
                    0.05 * var3,
                    1.1,
                    0.16,
                    -Math.PI / 2,
                    0,
                    0.0,
                    1.0,
                    var6 * 4.5,
                    var8,
                    0.05 + this.rnd.nextDouble() * 0.1
                );
                this.finish(var33);
                this.bolts.add(var33);
                break;
            case Fork:
                CustomSky.Bolt var32 = this.newBolt(0.0);
                double var37 = 1.05 + this.rnd.nextDouble() * 0.3;
                this.walk(var32, var28, var37, -Math.PI / 2, (int)(16.0 * var3), 0.05, 0.9, 0.2, -Math.PI / 2, 0, 0.0, 0.35, var6 * 2.0, var8, 0.5);
                int var41 = var32.n;
                double var42 = var32.a1[var41 - 1];
                double var45 = var32.e1[var41 - 1];
                int var21 = 3 + this.rnd.nextInt(3) + var5 / 3;

                for (int var22 = 0; var22 < var21; var22++) {
                    double var23 = (var22 - (var21 - 1) / 2.0) * (0.7 / Math.max(1, var21 - 1) * 2.0);
                    double var25 = (-Math.PI / 2) + var23 + (this.rnd.nextDouble() - 0.5) * 0.3;
                    this.walk(
                        var32,
                        var42,
                        var45,
                        var25,
                        (int)((22 + this.rnd.nextInt(14)) * var3),
                        0.045 * var3,
                        1.0,
                        0.12,
                        var25,
                        1,
                        0.35,
                        0.65,
                        var6 * 2.5,
                        var8,
                        0.05 + this.rnd.nextDouble() * 0.12
                    );
                }

                this.finish(var32);
                this.bolts.add(var32);
                break;
            case Crawler:
                CustomSky.Bolt var31 = this.newBolt(0.0);
                double var36 = 0.55 + this.rnd.nextDouble() * 0.4;
                double var40 = this.rnd.nextBoolean() ? 0.0 : Math.PI;
                this.walk(
                    var31, var28 - (var40 == 0.0 ? 0.8 : -0.8) * var3, var36, var40, (int)(62.0 * var3), 0.045, 1.0, 0.16, var40, 0, 0.0, 1.0, 0.0, 0, -9.0
                );
                int var43 = var31.n;

                for (int var44 = 3; var44 < var43; var44++) {
                    if (this.rnd.nextDouble() < 0.07 + 0.02 * var5) {
                        double var46 = (-Math.PI / 2) + (this.rnd.nextDouble() - 0.5) * 1.0;
                        this.walk(
                            var31,
                            var31.a1[var44],
                            var31.e1[var44],
                            var46,
                            4 + this.rnd.nextInt(7),
                            0.04,
                            1.0,
                            0.1,
                            var46,
                            1,
                            var31.ord[var44],
                            0.15,
                            0.0,
                            0,
                            0.08
                        );
                    }
                }

                this.finish(var31);
                this.bolts.add(var31);
                break;
            case Spider:
                CustomSky.Bolt var30 = this.newBolt(0.0);
                double var35 = 0.85 + this.rnd.nextDouble() * 0.3;
                int var39 = 6 + var5 + this.rnd.nextInt(3);
                double var17 = this.rnd.nextDouble() * (Math.PI * 2);

                for (int var19 = 0; var19 < var39; var19++) {
                    double var20 = var17 + var19 * (Math.PI * 2) / var39 + (this.rnd.nextDouble() - 0.5) * 0.4;
                    this.walk(
                        var30, var28, var35, var20, (int)((16 + this.rnd.nextInt(14)) * var3), 0.042, 1.0, 0.1, var20, 0, 0.0, 1.0, var6 * 1.1, var8, 0.05
                    );
                }

                this.finish(var30);
                this.bolts.add(var30);
                break;
            case Chain:
                int var29 = 3 + this.rnd.nextInt(3);

                for (int var34 = 0; var34 < var29; var34++) {
                    CustomSky.Bolt var15 = this.newBolt(var34 * (0.07 + this.rnd.nextDouble() * 0.06));
                    double var16 = var28 + (var34 - var29 / 2.0) * (0.3 + this.rnd.nextDouble() * 0.12);
                    double var18 = 0.95 + this.rnd.nextDouble() * 0.35;
                    this.walk(
                        var15,
                        var16,
                        var18,
                        (-Math.PI / 2) + (this.rnd.nextDouble() - 0.5) * 0.4,
                        (int)(45.0 * var3),
                        0.05 * var3,
                        1.1,
                        0.16,
                        -Math.PI / 2,
                        0,
                        0.0,
                        1.0,
                        var6 * 3.5,
                        var8,
                        0.06 + this.rnd.nextDouble() * 0.1
                    );
                    this.finish(var15);
                    this.bolts.add(var15);
                }
                break;
            default:
                CustomSky.Bolt var13 = this.newBolt(0.0);
                var13.sheet = true;
                double var14 = 0.45 + this.rnd.nextDouble() * 0.6;
                var13.cx = Math.cos(var14) * Math.cos(var28);
                var13.cy = Math.sin(var14);
                var13.cz = Math.cos(var14) * Math.sin(var28);
                var13.sheetR = (0.35 + this.rnd.nextDouble() * 0.35) * var3;
                this.bolts.add(var13);
        }

        while (this.bolts.size() > 12) {
            this.bolts.remove(0);
        }
    }

    private CustomSky.Bolt newBolt(double var1) {
        CustomSky.Bolt var3 = new CustomSky.Bolt();
        var3.born = this.clock;
        var3.delay = var1;
        return var3;
    }

    private static final class Bolt {
        float[] a0 = new float[256];
        float[] e0 = new float[256];
        float[] a1 = new float[256];
        float[] e1 = new float[256];
        float[] ord = new float[256];
        byte[] lv = new byte[256];
        int n;
        double born;
        double delay;
        double cx;
        double cy;
        double cz;
        boolean sheet;
        double sheetR;
    }

    public static enum Shape {
        Random,
        Bolt,
        Fork,
        Crawler,
        Spider,
        Chain,
        Sheet;
    }

    public static enum Style {
        Storm,
        Aurora,
        Nebula,
        Sunset,
        Gradient,
        Rainbow;
    }
}
