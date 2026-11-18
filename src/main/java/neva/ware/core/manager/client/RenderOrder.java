package neva.ware.core.manager.client;

import java.util.ArrayList;
import net.minecraft.class_4587;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.render.CustomSky;

public final class RenderOrder {
   private RenderOrder() {
   }

   private static boolean background(Module var0) {
      return var0 instanceof CustomSky;
   }

   public static void render3D(ArrayList<Module> var0, class_4587 var1) {
      int var2 = var0.size();

      for (int var3 = 0; var3 < var2; var3++) {
         Module var4 = (Module)var0.get(var3);
         if (var4.isEnabled() && background(var4)) {
            var4.onRender3D(var1);
         }
      }

      for (int var5 = 0; var5 < var2; var5++) {
         Module var6 = (Module)var0.get(var5);
         if (var6.isEnabled() && !background(var6)) {
            var6.onRender3D(var1);
         }
      }
   }
}
