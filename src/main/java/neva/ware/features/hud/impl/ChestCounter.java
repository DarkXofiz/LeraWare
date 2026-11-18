package neva.ware.features.hud.impl;

import net.minecraft.class_2281;
import net.minecraft.class_2586;
import net.minecraft.class_2595;
import net.minecraft.class_2745;
import net.minecraft.class_2769;
import net.minecraft.class_332;
import net.minecraft.class_3545;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.features.modules.render.StorageEsp;

public class ChestCounter extends HudElement {
   private final NwHud.Anim anim = new NwHud.Anim();

   public ChestCounter() {
      super("ChestCounter", 50, 10);
   }

   @Override
   public void onRender2D(class_332 var1) {
      super.onRender2D(var1);
      class_3545 var2 = this.getChestCount();
      NwInfoPill.render(this, var1, this.anim, NwHud.Icon.CHEST, var2.method_15442() + " / " + var2.method_15441(), "Chests");
   }

   public class_3545 getChestCount() {
      int var1 = 0;
      int var2 = 0;

      try {
         for (class_2586 var4 : StorageEsp.getBlockEntities()) {
            if (var4 instanceof class_2595) {
               class_2595 var5 = (class_2595)var4;
               class_2745 var6 = (class_2745)mc.field_1687.method_8320(var5.method_11016()).method_11654((class_2769)class_2281.field_10770);
               if (var6 == class_2745.field_12569) {
                  var1++;
               } else {
                  var2++;
               }
            }
         }
      } catch (Throwable var7) {
      }

      return new class_3545(var1, var2 / 2);
   }
}
