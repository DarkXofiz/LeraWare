package neva.ware.gui.clickui.impl;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.NevaWare;
import neva.ware.gui.clickui.AbstractElement;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwEl;
import neva.ware.gui.clickui.NwSized;
import neva.ware.gui.font.FontRenderer;
import neva.ware.setting.Setting;

public class StringElement extends AbstractElement implements NwSized {
    public boolean listening;
    private String currentString = "";
    private float hover;
    private float focus;
    private long caret = System.nanoTime();

    public StringElement(Setting var1) {
        super(var1);
    }

    @Override
    public float prefHeight() {
        return 15.0F;
    }

    public boolean isListening() {
        return this.listening;
    }

    private String value() {
        Object var1 = this.setting.getValue();
        return var1 == null ? "" : String.valueOf(var1);
    }

    @Override
    public void render(class_332 var1, int var2, int var3, float var4) {
        super.render(var1, var2, var3, var4);
        class_4587 var5 = var1.method_51448();
        int var6 = NwDraw.accent(0);
        this.hover = NwDraw.approach(this.hover, this.hovered ? 1.0F : 0.0F, 18.0F);
        this.focus = NwDraw.approach(this.focus, this.listening ? 1.0F : 0.0F, 16.0F);
        if (this.hovered) {
            NwEl.hand();
        }

        float var7 = NwEl.indent(this.setting);
        NwEl.guides(var5, this.x, this.y, this.height, this.setting);
        float var8 = this.y + this.height / 2.0F;
        float var9 = (this.width - var7) * 0.55F;
        float var10 = this.x + this.width - 5.0F - 1.0F - var9;
        float var11 = 11.0F;
        NwEl.label(var5, this.setting.getName(), this.x + 5.0F + 1.0F + var7, var8, var10 - (this.x + 5.0F + 1.0F + var7) - 4.0F, this.hover);
        NwDraw.round(var5, var10, var8 - var11 / 2.0F, var9, var11, 3.5F, NwDraw.withAlpha(-16777216, 70), NwDraw.withAlpha(-16777216, 55));
        NwDraw.border(
            var5,
            var10,
            var8 - var11 / 2.0F,
            var9,
            var11,
            3.5F,
            0.6F,
            NwDraw.mix(NwDraw.withAlpha(-1, Math.round(16.0F + 16.0F * this.hover)), NwDraw.withAlpha(var6, 190), this.focus)
        );
        FontRenderer var12 = NwDraw.settingFont();
        String var13 = this.listening ? this.currentString : this.value();
        float var14 = var9 - 8.0F;
        String var15 = var13;

        while (NwDraw.width(var12, var15) > var14 && var15.length() > 1) {
            var15 = this.listening ? var15.substring(1) : var15.substring(0, var15.length() - 1);
        }

        NwDraw.text(var12, var5, var15, var10 + 4.0F, NwEl.textY(var8), this.listening ? -1 : -3946796);
        if (this.listening && (System.nanoTime() - this.caret) / 530000000L % 2L == 0L) {
            float var16 = var10 + 4.0F + NwDraw.width(var12, var15) + 0.6F;
            NwDraw.round(var5, var16, var8 - 3.5F, 0.7F, 7.0F, 0.3F, -570425345);
        }
    }

    @Override
    public void mouseClicked(int var1, int var2, int var3) {
        if (this.hovered && var3 == 0) {
            this.listening = !this.listening;
            if (this.listening) {
                NevaWare.currentKeyListener = NevaWare.KeyListening.Strings;
                this.currentString = this.value();
                this.caret = System.nanoTime();
            }
        } else if (this.listening && !this.hovered) {
            this.listening = false;
        }

        super.mouseClicked(var1, var2, var3);
    }

    @Override
    public void charTyped(char var1, int var2) {
        if (this.listening) {
            if (var1 >= ' ' && var1 != 127 && var1 != 167 && this.currentString.length() < 64) {
                this.currentString = this.currentString + var1;
                this.caret = System.nanoTime();
            }
        }
    }

    @Override
    public void keyTyped(int var1) {
        if (NevaWare.currentKeyListener == NevaWare.KeyListening.Strings) {
            if (this.listening) {
                switch (var1) {
                    case 256:
                        this.listening = false;
                        break;
                    case 257:
                    case 335:
                        if (this.currentString == null || this.currentString.isEmpty()) {
                            this.setting.setValue(this.setting.getDefaultValue());
                        } else if (this.setting.getValue() instanceof Character) {
                            this.setting.setValue(this.currentString.charAt(0));
                        } else {
                            this.setting.setValue(this.currentString);
                        }

                        this.currentString = "";
                        this.listening = false;
                        break;
                    case 259:
                        this.currentString = SliderElement.removeLastChar(this.currentString);
                        this.caret = System.nanoTime();
                }
            }
        }
    }
}
