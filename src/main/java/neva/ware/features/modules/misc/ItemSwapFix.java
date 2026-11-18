package neva.ware.features.modules.misc;

import net.minecraft.class_2868;
import neva.ware.features.modules.Module;

public class ItemSwapFix extends Module {
   public ItemSwapFix() {
      super("ItemSwapFix", Module.Category.MISC);
   }

   public void forceSync() {
      if (this.isEnabled() && mc.field_1724 != null && mc.method_1562() != null) {
         mc.method_1562().method_52787(new class_2868(mc.field_1724.method_31548().field_7545));
      }
   }
}
