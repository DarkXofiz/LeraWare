package neva.ware.gui.clickui.impl;

import java.awt.Color;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.NevaWare;
import neva.ware.gui.clickui.AbstractElement;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwEl;
import neva.ware.gui.clickui.NwSized;
import neva.ware.gui.font.FontRenderer;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;

public class ColorPickerElement extends AbstractElement implements NwSized {
    private float hue;
    private float saturation;
    private float brightness;
    private int alpha;
    private boolean afocused;
    private boolean hfocused;
    private boolean sbfocused;
    private float spos;
    private float bpos;
    private float hpos;
    private float apos;
    private Color prevColor;
    private boolean firstInit = true;
    private boolean extended;
    private final Setting colorSetting;
    private float openAnim;
    private float hover;
    private final float[] btnHover = new float[3];
    private static final float HEAD = 15.0F;
    private static final float PICK_H = 40.0F;
    private static final float BTN_H = 10.0F;

    public ColorSetting getColorSetting() {
        return (ColorSetting)this.colorSetting.getValue();
    }

    public ColorPickerElement(Setting var1) {
        super(var1);
        this.colorSetting = var1;
        this.prevColor = this.getColorSetting().getColorObject();
        this.updatePos();
    }

    @Override
    public float prefHeight() {
        return 15.0F + 59.0F * this.openAnim;
    }

    private float ind() {
        return NwEl.indent(this.setting);
    }

    private float px() {
        return this.x + 5.0F + 1.0F + this.ind();
    }

    private float py() {
        return this.y + 15.0F + 1.0F;
    }

    private float pw() {
        return this.width - 12.0F - this.ind() - 20.0F;
    }

    private float hueX() {
        return this.px() + this.pw() + 4.0F;
    }

    private float alphaX() {
        return this.px() + this.pw() + 12.0F;
    }

    private float btnY() {
        return this.py() + 40.0F + 4.0F;
    }

    private float btnW() {
        return (this.width - 12.0F - this.ind() - 6.0F) / 3.0F;
    }

    private float btnX(int var1) {
        return this.px() + var1 * (this.btnW() + 3.0F);
    }

    @Override
    public void render(class_332 var1, int var2, int var3, float var4) {
        super.render(var1, var2, var3, var4);
        class_4587 var5 = var1.method_51448();
        ColorSetting var6 = this.getColorSetting();
        Color var7 = var6.getColorObject();
        if (this.firstInit) {
            this.updatePos();
            this.firstInit = false;
        }

        if (!this.sbfocused && !this.hfocused && !this.afocused && (this.prevColor == null || this.prevColor.getRGB() != var7.getRGB())) {
            this.updatePos();
            this.prevColor = var7;
        }

        this.openAnim = NwDraw.approach(this.openAnim, this.extended ? 1.0F : 0.0F, 15.0F);
        boolean var8 = NwDraw.hovered(var2, var3, this.x, this.y, this.width, 15.0F);
        this.hover = NwDraw.approach(this.hover, var8 ? 1.0F : 0.0F, 18.0F);
        if (var8) {
            NwEl.hand();
        }

        float var9 = this.ind();
        NwEl.guides(var5, this.x, this.y, this.height, this.setting);
        NwEl.hoverBg(var5, this.x + 2.0F + var9, this.y + 0.5F, this.width - 4.0F - var9, 14.0F, this.hover);
        float var10 = this.y + 7.5F;
        float var11 = this.x + this.width - 5.0F - 1.0F;
        float var12 = 16.0F;
        float var13 = 9.0F;
        NwDraw.round(var5, var11 - var12, var10 - var13 / 2.0F, var12, var13, 3.0F, -14013384);
        NwDraw.round(var5, var11 - var12, var10 - var13 / 2.0F, var12 / 2.0F, var13 / 2.0F, 1.0F, -12960436);
        NwDraw.round(var5, var11 - var12 / 2.0F, var10, var12 / 2.0F, var13 / 2.0F, 1.0F, -12960436);
        NwDraw.shadow(var5, var11 - var12, var10 - var13 / 2.0F, var12, var13, 3.0F, 4.0F, NwDraw.withAlpha(var7.getRGB(), 70));
        NwDraw.round(var5, var11 - var12, var10 - var13 / 2.0F, var12, var13, 3.0F, var7.getRGB());
        NwDraw.border(var5, var11 - var12, var10 - var13 / 2.0F, var12, var13, 3.0F, 0.6F, 1090519039);
        NwEl.label(var5, this.setting.getName(), this.x + 5.0F + 1.0F + var9, var10, this.width - 30.0F - var9, this.hover);
        if (!(this.openAnim < 0.02F)) {
            float var14 = this.px();
            float var15 = this.py();
            float var16 = Math.max(10.0F, this.pw());
            float var17 = 40.0F;
            if (this.sbfocused) {
                this.saturation = NwDraw.clamp((var2 - var14) / var16, 0.0F, 1.0F);
                this.brightness = 1.0F - NwDraw.clamp((var3 - var15) / var17, 0.0F, 1.0F);
                this.apply();
            } else if (this.hfocused) {
                this.hue = NwDraw.clamp((var3 - var15) / var17, 0.0F, 0.999F);
                this.apply();
            } else if (this.afocused) {
                this.alpha = Math.round(255.0F * (1.0F - NwDraw.clamp((var3 - var15) / var17, 0.0F, 1.0F)));
                this.apply();
            }

            this.spos = NwDraw.approach(this.spos, this.saturation, 25.0F);
            this.bpos = NwDraw.approach(this.bpos, this.brightness, 25.0F);
            this.hpos = NwDraw.approach(this.hpos, this.hue, 25.0F);
            this.apos = NwDraw.approach(this.apos, this.alpha / 255.0F, 25.0F);
            float var18 = NwDraw.alpha;
            NwDraw.alpha = var18 * this.openAnim;
            int var19 = 0xFF000000 | Color.HSBtoRGB(this.hue, 1.0F, 1.0F) & 16777215;
            NwDraw.roundQuad(var5, var14, var15, var16, var17, 3.5F, -1, var19, -16777216, -16777216);
            NwDraw.border(var5, var14, var15, var16, var17, 3.5F, 0.5F, 822083583);
            float var20 = var14 + this.spos * var16;
            float var21 = var15 + (1.0F - this.bpos) * var17;
            NwDraw.circle(var5, var20, var21, 3.2F, -1);
            NwDraw.circle(var5, var20, var21, 2.2F, 0xFF000000 | var7.getRGB() & 16777215);
            float var22 = this.hueX();
            float var23 = 5.0F;

            for (int var24 = 0; var24 < 6; var24++) {
                float var25 = var15 + var17 * var24 / 6.0F;
                float var26 = var15 + var17 * (var24 + 1) / 6.0F;
                int var27 = 0xFF000000 | Color.HSBtoRGB(var24 / 6.0F, 1.0F, 1.0F) & 16777215;
                int var28 = 0xFF000000 | Color.HSBtoRGB((var24 + 1) / 6.0F, 1.0F, 1.0F) & 16777215;
                NwDraw.roundQuad(var5, var22, var25, var23, var26 - var25 + 0.3F, var24 != 0 && var24 != 5 ? 0.0F : 1.5F, var27, var27, var28, var28);
            }

            float var37 = var15 + this.hpos * var17;
            NwDraw.round(var5, var22 - 1.0F, var37 - 1.0F, var23 + 2.0F, 2.0F, 1.0F, -1);
            float var38 = this.alphaX();
            NwDraw.round(var5, var38, var15, var23, var17, 1.5F, -14013384);
            int var39 = 0xFF000000 | var7.getRGB() & 16777215;
            NwDraw.round(var5, var38, var15, var23, var17, 1.5F, var39, var39 & 16777215);
            NwDraw.border(var5, var38, var15, var23, var17, 1.5F, 0.5F, 822083583);
            float var40 = var15 + (1.0F - this.apos) * var17;
            NwDraw.round(var5, var38 - 1.0F, var40 - 1.0F, var23 + 2.0F, 2.0F, 1.0F, -1);
            FontRenderer var41 = NwDraw.settingFont();
            String[] var29 = new String[]{"Rainbow", "Copy", "Paste"};
            int var30 = NwDraw.accent(0);
            float var31 = this.btnY();

            for (int var32 = 0; var32 < 3; var32++) {
                float var33 = this.btnX(var32);
                boolean var34 = this.openAnim > 0.9F && NwDraw.hovered(var2, var3, var33, var31, this.btnW(), 10.0F);
                if (var34) {
                    NwEl.hand();
                }

                this.btnHover[var32] = NwDraw.approach(this.btnHover[var32], var34 ? 1.0F : 0.0F, 18.0F);
                boolean var35 = var32 == 0 && var6.isRainbow();
                int var36 = var35
                    ? NwDraw.withAlpha(NwDraw.mix(-13946792, var30, 0.55F), 230)
                    : NwDraw.withAlpha(-1, Math.round(14.0F + 16.0F * this.btnHover[var32]));
                NwDraw.round(var5, var33, var31, this.btnW(), 10.0F, 3.5F, var36);
                NwDraw.textCentered(
                    var41,
                    var5,
                    var29[var32],
                    var33 + this.btnW() / 2.0F,
                    NwEl.textY(var31 + 5.0F),
                    var35 ? -1 : NwDraw.mix(-8552041, -1, this.btnHover[var32])
                );
            }

            NwDraw.alpha = var18;
        }
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    private void apply() {
        Color var1 = new Color(Color.HSBtoRGB(this.hue, this.saturation, this.brightness));
        this.setColor(new Color(var1.getRed(), var1.getGreen(), var1.getBlue(), Math.max(0, Math.min(255, this.alpha))));
    }

    private void updatePos() {
        Color var1 = this.getColorSetting().getColorObject();
        float[] var2 = Color.RGBtoHSB(var1.getRed(), var1.getGreen(), var1.getBlue(), null);
        this.hue = var2[0];
        this.saturation = var2[1];
        this.brightness = var2[2];
        this.alpha = this.getColorSetting().getAlpha();
    }

    private void setColor(Color var1) {
        this.getColorSetting().setColor(var1.getRGB());
        this.prevColor = var1;
    }

    @Override
    public void mouseClicked(int var1, int var2, int var3) {
        if (NwDraw.hovered(var1, var2, this.x, this.y, this.width, 15.0F)) {
            this.extended = !this.extended;
            super.mouseClicked(var1, var2, var3);
        } else if (this.extended && !(this.openAnim < 0.9F) && var3 == 0) {
            float var4 = this.px();
            float var5 = this.py();
            float var6 = Math.max(10.0F, this.pw());
            float var7 = 40.0F;
            if (NwDraw.hovered(var1, var2, this.alphaX() - 1.0F, var5, 7.0F, var7)) {
                this.afocused = true;
            } else if (NwDraw.hovered(var1, var2, this.hueX() - 1.0F, var5, 7.0F, var7)) {
                this.hfocused = true;
            } else if (NwDraw.hovered(var1, var2, var4, var5, var6, var7)) {
                this.sbfocused = true;
            } else {
                float var8 = this.btnY();

                for (int var9 = 0; var9 < 3; var9++) {
                    if (NwDraw.hovered(var1, var2, this.btnX(var9), var8, this.btnW(), 10.0F)) {
                        ColorSetting var10 = this.getColorSetting();
                        if (var9 == 0) {
                            var10.setRainbow(!var10.isRainbow());
                        } else if (var9 == 1) {
                            NevaWare.copy_color = var10.getColorObject();
                        } else if (NevaWare.copy_color != null) {
                            var10.setColor(NevaWare.copy_color.getRGB());
                            this.updatePos();
                        }
                    }
                }
            }

            super.mouseClicked(var1, var2, var3);
        } else {
            super.mouseClicked(var1, var2, var3);
        }
    }

    @Override
    public void mouseReleased(int var1, int var2, int var3) {
        this.hfocused = false;
        this.afocused = false;
        this.sbfocused = false;
    }

    @Override
    public void onClose() {
        this.hfocused = false;
        this.afocused = false;
        this.sbfocused = false;
    }
}
