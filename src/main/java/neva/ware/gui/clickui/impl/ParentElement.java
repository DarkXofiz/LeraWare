package neva.ware.gui.clickui.impl;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.gui.clickui.AbstractElement;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwEl;
import neva.ware.gui.clickui.NwSized;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.SettingGroup;

public class ParentElement extends AbstractElement implements NwSized {
   private final Setting<SettingGroup> parentSetting;
   private float animation;
   private float hover;

   public ParentElement(Setting var1) {
      super(var1);
      this.parentSetting = var1;
      this.animation = this.extended() ? 1.0F : 0.0F;
   }

   private boolean extended() {
      return this.getParentSetting().getValue().isExtended();
   }

   @Override
   public float prefHeight() {
      return 14.0F;
   }

   @Override
   public void render(class_332 var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      class_4587 var5 = var1.method_51448();
      this.animation = NwDraw.approach(this.animation, this.extended() ? 1.0F : 0.0F, 16.0F);
      this.hover = NwDraw.approach(this.hover, this.hovered ? 1.0F : 0.0F, 18.0F);
      if (this.hovered) {
         NwEl.hand();
      }

      float var6 = NwEl.indent(this.setting);
      NwEl.guides(var5, this.x, this.y, this.height, this.setting);
      NwDraw.round(
         var5,
         this.x + 2.0F + var6,
         this.y + 0.5F,
         this.width - 4.0F - var6,
         this.height - 1.0F,
         3.5F,
         NwDraw.withAlpha(-1, Math.round(9.0F + 9.0F * this.hover))
      );
      float var7 = this.y + this.height / 2.0F;
      NwDraw.chevron(
         var5,
         this.x + this.width - 5.0F - 3.5F,
         var7 + 0.2F,
         2.2F,
         -90.0F + 90.0F * this.animation,
         NwDraw.mix(-8552041, -1, Math.max(this.hover, this.animation))
      );
      NwEl.label(var5, this.setting.getName(), this.x + 5.0F + 1.0F + var6, var7, this.width - 20.0F - var6, Math.max(this.hover, this.animation * 0.7F));
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) {
      if (this.hovered) {
         SettingGroup var4 = this.getParentSetting().getValue();
         var4.setExtended(!var4.isExtended());

         try {
            if (var4.isExtended()) {
               Managers.SOUND.playSwipeIn();
            } else {
               Managers.SOUND.playSwipeOut();
            }
         } catch (Throwable var6) {
         }
      }

      super.mouseClicked(var1, var2, var3);
   }

   public Setting<SettingGroup> getParentSetting() {
      return this.parentSetting;
   }
}
