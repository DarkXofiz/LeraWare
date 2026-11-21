package neva.ware.gui.nevagui.components;

import java.awt.Color;
import net.minecraft.class_4587;
import neva.ware.gui.font.FontRenderers;
import neva.ware.gui.nevagui.NevaGui;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.render.Render2DEngine;

public class ParentComponent extends SettingElement {
    public ParentComponent(Setting setting) {
        super(setting);
        SettingGroup settingGroup = (SettingGroup)setting.getValue();
        settingGroup.setExtended(true);
    }

    @Override
    public void render(class_4587 stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (!(this.getY() > NevaGui.getInstance().main_posY + NevaGui.getInstance().field_22790) && !(this.getY() < NevaGui.getInstance().main_posY)) {
            FontRenderers.modules
                .drawCenteredString(
                    stack, this.getSetting().getName(), this.getX() + this.width / 2.0F, this.getY() + 2.0F, new Color(-1325400065, true).getRGB()
                );
            Render2DEngine.draw2DGradientRect(
                stack,
                this.getX() + 10.0F,
                this.getY() + 6.0F,
                this.getX() + this.width / 2.0F - 20.0F,
                this.getY() + 7.0F,
                new Color(16777215, true),
                new Color(16777215, true),
                new Color(-1325400065, true),
                new Color(-1325400065, true)
            );
            Render2DEngine.draw2DGradientRect(
                stack,
                this.getX() + this.width / 2.0F + 20.0F,
                this.getY() + 6.0F,
                this.getX() + this.width - 10.0F,
                this.getY() + 7.0F,
                new Color(-1325400065, true),
                new Color(-1325400065, true),
                new Color(16777215, true),
                new Color(16777215, true)
            );
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!(this.getY() > NevaGui.getInstance().main_posY + NevaGui.getInstance().field_22790) && !(this.getY() < NevaGui.getInstance().main_posY)) {
            if (this.hovered) {
                SettingGroup settingGroup = (SettingGroup)this.setting.getValue();
                settingGroup.setExtended(!settingGroup.isExtended());
            }
        }
    }
}
