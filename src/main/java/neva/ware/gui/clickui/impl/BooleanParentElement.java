package neva.ware.gui.clickui.impl;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.gui.clickui.AbstractElement;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwEl;
import neva.ware.gui.clickui.NwSized;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.BooleanSettingGroup;

public class BooleanParentElement extends AbstractElement implements NwSized {
    private final Setting<BooleanSettingGroup> parentSetting;
    float animation;
    float arrowAnimation;
    private float hover;

    public BooleanParentElement(Setting var1) {
        super(var1);
        this.parentSetting = var1;
        this.animation = this.getParentSetting().getValue().isEnabled() ? 1.0F : 0.0F;
        this.arrowAnimation = this.getParentSetting().getValue().isExtended() ? 1.0F : 0.0F;
    }

    @Override
    public float prefHeight() {
        return 14.0F;
    }

    @Override
    public void render(class_332 var1, int var2, int var3, float var4) {
        super.render(var1, var2, var3, var4);
        class_4587 var5 = var1.method_51448();
        BooleanSettingGroup var6 = this.getParentSetting().getValue();
        this.animation = NwDraw.approach(this.animation, var6.isEnabled() ? 1.0F : 0.0F, 16.0F);
        this.arrowAnimation = NwDraw.approach(this.arrowAnimation, var6.isExtended() ? 1.0F : 0.0F, 16.0F);
        this.hover = NwDraw.approach(this.hover, this.hovered ? 1.0F : 0.0F, 18.0F);
        if (this.hovered) {
            NwEl.hand();
        }

        float var7 = NwEl.indent(this.setting);
        NwEl.guides(var5, this.x, this.y, this.height, this.setting);
        NwDraw.round(
            var5,
            this.x + 2.0F + var7,
            this.y + 0.5F,
            this.width - 4.0F - var7,
            this.height - 1.0F,
            3.5F,
            NwDraw.withAlpha(-1, Math.round(9.0F + 9.0F * this.hover))
        );
        float var8 = this.y + this.height / 2.0F;
        float var9 = this.x + this.width - 5.0F - 1.0F;
        NwEl.toggle(var5, var9 - 15.0F, var8 - 4.0F, this.animation, this.hover);
        NwDraw.chevron(
            var5, var9 - 21.0F, var8 + 0.2F, 2.2F, -90.0F + 90.0F * this.arrowAnimation, NwDraw.mix(-8552041, -1, Math.max(this.hover, this.arrowAnimation))
        );
        NwEl.label(var5, this.setting.getName(), this.x + 5.0F + 1.0F + var7, var8, this.width - 40.0F - var7, Math.max(this.hover, this.animation * 0.6F));
    }

    @Override
    public void mouseClicked(int var1, int var2, int var3) {
        if (this.hovered) {
            BooleanSettingGroup var4 = this.getParentSetting().getValue();

            try {
                if (var3 == 0) {
                    var4.setEnabled(!var4.isEnabled());
                    Managers.SOUND.playBoolean();
                } else {
                    var4.setExtended(!var4.isExtended());
                    if (var4.isExtended()) {
                        Managers.SOUND.playSwipeIn();
                    } else {
                        Managers.SOUND.playSwipeOut();
                    }
                }
            } catch (Throwable var6) {
            }
        }

        super.mouseClicked(var1, var2, var3);
    }

    public Setting<BooleanSettingGroup> getParentSetting() {
        return this.parentSetting;
    }
}
