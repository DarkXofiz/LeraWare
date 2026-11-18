package neva.ware.gui.clickui.impl;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.core.manager.IManager;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.gui.clickui.AbstractElement;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwEl;
import neva.ware.gui.clickui.NwSized;
import neva.ware.gui.windows.WindowsScreen;
import neva.ware.gui.windows.impl.ItemSelectWindow;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ItemSelectSetting;

public class ItemSelectElement extends AbstractElement implements NwSized {
   private final Setting<ItemSelectSetting> setting;
   private float hover;

   public ItemSelectElement(Setting var1) {
      super(var1);
      this.setting = var1;
   }

   @Override
   public float prefHeight() {
      return 14.0F;
   }

   @Override
   public void render(class_332 var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      class_4587 var5 = var1.method_51448();
      this.hover = NwDraw.approach(this.hover, this.hovered ? 1.0F : 0.0F, 18.0F);
      if (this.hovered) {
         NwEl.hand();
      }

      float var6 = NwEl.indent(this.setting);
      NwEl.guides(var5, this.x, this.y, this.height, this.setting);
      NwEl.hoverBg(var5, this.x + 2.0F + var6, this.y + 0.5F, this.width - 4.0F - var6, this.height - 1.0F, this.hover);
      float var7 = this.y + this.height / 2.0F;
      int var8 = 0;

      try {
         var8 = this.getItemSetting().getValue().getItemsById().size();
      } catch (Throwable var14) {
      }

      boolean var9 = false;

      try {
         var9 = ClientSettings.isRu();
      } catch (Throwable var13) {
      }

      String var10 = var8 + (var9 ? " шт." : " items");
      float var11 = this.x + this.width - 5.0F - 1.0F;
      NwDraw.chevron(var5, var11 - 2.5F, var7 + 0.2F, 2.2F, -90.0F, NwDraw.mix(-8552041, -1, this.hover));
      float var12 = 8.0F
         + NwEl.chip(var5, var10, var11 - 8.0F, var7, NwDraw.withAlpha(-1, Math.round(18.0F + 14.0F * this.hover)), NwDraw.mix(-3946796, -1, this.hover));
      NwEl.label(var5, this.setting.getName(), this.x + 5.0F + 1.0F + var6, var7, this.width - var12 - 16.0F - var6, this.hover);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) {
      if (this.hovered) {
         IManager.mc.method_1507(new WindowsScreen(new ItemSelectWindow(this.getItemSetting())));

         try {
            Managers.SOUND.playSwipeIn();
         } catch (Throwable var5) {
         }
      }

      super.mouseClicked(var1, var2, var3);
   }

   public Setting<ItemSelectSetting> getItemSetting() {
      return this.setting;
   }
}
