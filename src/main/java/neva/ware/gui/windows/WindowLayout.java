package neva.ware.gui.windows;

import java.util.ArrayList;
import java.util.List;
import neva.ware.features.modules.Module;
import neva.ware.gui.windows.impl.ProxyWindow;
import neva.ware.gui.windows.impl.WaypointWindow;
import neva.ware.setting.impl.PositionSetting;

public final class WindowLayout {
   private static final float GAP = 12.0F;
   private static final float MARGIN_X = 14.0F;
   private static final float MARGIN_TOP = 14.0F;
   private static final float MARGIN_BOTTOM = 40.0F;

   private WindowLayout() {
   }

   private static float prefW(WindowBase var0) {
      return !(var0 instanceof WaypointWindow) && !(var0 instanceof ProxyWindow) ? 200.0F : 340.0F;
   }

   private static float prefH(WindowBase var0) {
      return 180.0F;
   }

   public static void arrange(List<WindowBase> var0) {
      try {
         doArrange(var0);
      } catch (Throwable var2) {
      }
   }

   private static void doArrange(List<WindowBase> var0) {
      ArrayList var1 = new ArrayList();

      for (WindowBase var3 : var0) {
         if (var3 != null && var3.isVisible()) {
            var1.add(var3);
         }
      }

      int var29 = var1.size();
      if (var29 != 0) {
         float var30 = (float)Module.mc.method_22683().method_4486();
         float var4 = (float)Module.mc.method_22683().method_4502();
         float var5 = var30 - 28.0F;
         float var6 = var4 - 14.0F - 40.0F;
         int var7 = 1;
         float var8 = -1.0F;

         for (int var9 = 1; var9 <= var29; var9++) {
            float var10 = 0.0F;
            int var11 = 0;
            int var12 = var29 / var9;
            int var13 = var29 % var9;

            for (int var14 = 0; var14 < var9; var14++) {
               int var15 = var12 + (var14 < var13 ? 1 : 0);
               float var16 = 12.0F * (float)(var15 - 1);

               for (int var17 = 0; var17 < var15; var17++) {
                  var16 += prefW((WindowBase)var1.get(var11++));
               }

               var10 = Math.max(var10, var16);
            }

            float var36 = (float)var9 * prefH((WindowBase)var1.get(0)) + 12.0F * (float)(var9 - 1);
            float var38 = Math.min(1.0F, Math.min(var5 / var10, var6 / var36));
            if (var38 > var8 + 0.001F) {
               var8 = var38;
               var7 = var9;
            }
         }

         int var31 = var7;
         int var32 = var29 / var7;
         int var33 = var29 % var7;
         float var34 = var8;
         float[] var35 = new float[var29];
         float[] var37 = new float[var29];

         for (int var39 = 0; var39 < var29; var39++) {
            WindowBase var41 = (WindowBase)var1.get(var39);
            var35[var39] = Math.max((float)var41.getMinWidth(), (float)Math.round(prefW(var41) * var34));
            var37[var39] = Math.max((float)var41.getMinHeight(), (float)Math.round(prefH(var41) * var34));
         }

         float[] var40 = new float[var7];
         float var42 = 12.0F * (float)(var7 - 1);
         int var43 = 0;

         for (int var18 = 0; var18 < var31; var18++) {
            int var19 = var32 + (var18 < var33 ? 1 : 0);

            for (int var20 = 0; var20 < var19; var20++) {
               var40[var18] = Math.max(var40[var18], var37[var43 + var20]);
            }

            var43 += var19;
            var42 += var40[var18];
         }

         float var45 = 14.0F + Math.max(0.0F, (var6 - var42) / 2.0F);
         var43 = 0;

         for (int var46 = 0; var46 < var31; var46++) {
            int var47 = var32 + (var46 < var33 ? 1 : 0);
            float var21 = 12.0F * (float)(var47 - 1);

            for (int var22 = 0; var22 < var47; var22++) {
               var21 += var35[var43 + var22];
            }

            float var48 = 14.0F + Math.max(0.0F, (var5 - var21) / 2.0F);

            for (int var23 = 0; var23 < var47; var23++) {
               WindowBase var24 = (WindowBase)var1.get(var43 + var23);
               float var25 = (float)Math.round(var48);
               float var26 = (float)Math.round(var45);
               var24.setWidth(var35[var43 + var23]);
               var24.setHeight(var40[var46]);
               var24.setX(var25);
               var24.setY(var26);

               try {
                  PositionSetting var27 = var24.position.getValue();
                  var27.setX(var25 / var30);
                  var27.setY(var26 / var4);
               } catch (Throwable var28) {
               }

               var48 += var35[var43 + var23] + 12.0F;
            }

            var45 += var40[var46] + 12.0F;
            var43 += var47;
         }
      }
   }
}
