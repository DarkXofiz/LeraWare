package neva.ware.features.modules.combat;

import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_746;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.movement.ElytraBooster;

public final class ElytraCatch {
   private ElytraCatch() {
   }

   public static void onFireworkTick() {
      class_746 var0 = Module.mc.field_1724;
      if (var0 != null && var0.method_6128()) {
         ElytraBooster var1 = ModuleManager.elytraBooster;
         if (var1 != null && var1.isEnabled()) {
            float var28 = var1.boostMultiplier.getValue();
            if (var28 != 1.0F) {
               var0.method_18799(var0.method_18798().method_1021((double)var28));
            }
         } else {
            ElytraTarget var2 = ModuleManager.elytratarget;
            if (var2 != null) {
               class_1297 var3 = null;
               if (var2.isEnabled()) {
                  var3 = ElytraTarget.target;
               }

               if (var3 == null) {
                  Aura var4 = ModuleManager.aura;
                  if (var4 != null && var4.isEnabled() && var4.elytraTarget.getValue()) {
                     var3 = Aura.target;
                  }
               }

               if (var3 != null) {
                  if (var2.catchBoostEnabled.getValue()) {
                     float var29 = var2.catchBoost.getValue();
                     if (!(var29 <= 1.0001F)) {
                        class_243 var5 = var0.method_18798();
                        double var6 = Math.sqrt(var5.field_1352 * var5.field_1352 + var5.field_1351 * var5.field_1351 + var5.field_1350 * var5.field_1350);
                        double var8 = var3.method_23317() - var3.field_6014;
                        double var10 = var3.method_23318() - var3.field_6036;
                        double var12 = var3.method_23321() - var3.field_5969;
                        double var14 = Math.sqrt(var8 * var8 + var10 * var10 + var12 * var12);
                        double var16 = var3.method_23317() - var0.method_23317();
                        double var18 = var3.method_23318() - var0.method_23318();
                        double var20 = var3.method_23321() - var0.method_23321();
                        double var22 = Math.sqrt(var16 * var16 + var18 * var18 + var20 * var20);
                        double var24;
                        if (var22 > 3.5) {
                           var24 = (double)var29;
                        } else {
                           double var26 = var14 * 1.03 / Math.max(var6, 0.01);
                           var24 = Math.max(1.0, Math.min((double)var29, var26));
                        }

                        if (var24 > 1.0001) {
                           var0.method_18799(var5.method_1021(var24));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static final class Visible implements Predicate {
      private final ElytraTarget t;

      public Visible(ElytraTarget var1) {
         this.t = var1;
      }

      @Override
      public boolean test(Object var1) {
         return this.t.catchBoostEnabled != null ? this.t.catchBoostEnabled.getValue() : true;
      }
   }
}
