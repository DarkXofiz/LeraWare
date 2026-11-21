package neva.ware.gui.clickui.impl;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.gui.clickui.AbstractElement;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwEl;
import neva.ware.gui.clickui.NwSized;
import neva.ware.setting.Setting;

public class BooleanElement extends AbstractElement implements NwSized {
    float animation = this.value() ? 1.0F : 0.0F;
    float animation2;

    public BooleanElement(Setting var1) {
        super(var1);
    }

    private boolean value() {
        return this.setting.getValue() instanceof Boolean var2 && var2;
    }

    @Override
    public float prefHeight() {
        return 14.0F;
    }

    @Override
    public void render(class_332 var1, int var2, int var3, float var4) {
        super.render(var1, var2, var3, var4);
        class_4587 var5 = var1.method_51448();
        this.animation = NwDraw.approach(this.animation, this.value() ? 1.0F : 0.0F, 16.0F);
        this.animation2 = NwDraw.approach(this.animation2, this.hovered ? 1.0F : 0.0F, 18.0F);
        if (this.hovered) {
            NwEl.hand();
        }

        float var6 = NwEl.indent(this.setting);
        NwEl.guides(var5, this.x, this.y, this.height, this.setting);
        NwEl.hoverBg(var5, this.x + 2.0F + var6, this.y + 0.5F, this.width - 4.0F - var6, this.height - 1.0F, this.animation2);
        float var7 = this.y + this.height / 2.0F;
        NwEl.label(var5, this.setting.getName(), this.x + 5.0F + 1.0F + var6, var7, this.width - 34.0F - var6, Math.max(this.animation2, this.animation * 0.5F));
        NwEl.toggle(var5, this.x + this.width - 5.0F - 16.0F, var7 - 4.0F, this.animation, this.animation2);
    }

    @Override
    public void mouseClicked(int var1, int var2, int var3) {
        if (this.hovered && var3 == 0) {
            this.setting.setValue(!this.value());

            try {
                Managers.SOUND.playBoolean();
            } catch (Throwable var5) {
            }
        }

        super.mouseClicked(var1, var2, var3);
    }
}
