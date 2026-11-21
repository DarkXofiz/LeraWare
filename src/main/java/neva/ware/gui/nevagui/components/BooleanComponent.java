package neva.ware.gui.nevagui.components;

import java.awt.Color;
import net.minecraft.class_4587;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.gui.font.FontRenderers;
import neva.ware.gui.nevagui.NevaGui;
import neva.ware.setting.Setting;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.animation.AnimationUtility;

public class BooleanComponent extends SettingElement {
   float animation = 0.0F;

   public BooleanComponent(Setting setting) {
      super(setting);
   }

   @Override
   public void render(class_4587 stack, int mouseX, int mouseY, float partialTicks) {
      super.render(stack, mouseX, mouseY, partialTicks);
      if (!(this.getY() > (float)(NevaGui.getInstance().main_posY + NevaGui.getInstance().field_22790))
         && !(this.getY() < (float)NevaGui.getInstance().main_posY)) {
         FontRenderers.modules
            .drawString(
               stack,
               this.getSetting().getName(),
               (double)this.getX(),
               (double)(this.getY() + 5.0F),
               this.isHovered() ? -1 : new Color(-1325400065, true).getRGB()
            );
         this.animation = AnimationUtility.fast(this.animation, ((Boolean)this.setting.getValue()) ? 1.0F : 0.0F, 15.0F);
         double paddingX = (double)(7.0F * this.animation);
         Color color = HudEditor.getColor(1);
         Render2DEngine.drawRound(
            stack, this.x + this.width - 18.0F, this.y + this.height / 2.0F - 4.0F, 15.0F, 8.0F, 4.0F, paddingX > 4.0 ? color : new Color(-5066319)
         );
         Render2DEngine.drawRound(
            stack, (float)((double)(this.x + this.width - 17.0F) + paddingX), this.y + this.height / 2.0F - 3.0F, 6.0F, 6.0F, 3.0F, new Color(-1)
         );
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int button) {
      if (!(this.getY() > (float)(NevaGui.getInstance().main_posY + NevaGui.getInstance().field_22790))
         && !(this.getY() < (float)NevaGui.getInstance().main_posY)) {
         if (this.isHovered()) {
            this.setting.setValue(!(Boolean)this.setting.getValue());
         }
      }
   }
}
