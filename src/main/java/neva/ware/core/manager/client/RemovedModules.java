package neva.ware.core.manager.client;

import java.util.ArrayList;
import neva.ware.features.hud.impl.AutoCrystalInfo;
import neva.ware.features.hud.impl.CandleHud;
import neva.ware.features.hud.impl.Companion;
import neva.ware.features.hud.impl.Crosshair;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.render.Shaders;
import neva.ware.features.modules.render.SoundESP;
import neva.ware.features.modules.render.StorageEsp;

public final class RemovedModules {
   private RemovedModules() {
   }

   public static void purge(ArrayList<Module> var0) {
      var0.removeIf(
         var0x -> var0x instanceof Shaders
               || var0x instanceof SoundESP
               || var0x instanceof StorageEsp
               || var0x instanceof Companion
               || var0x instanceof AutoCrystalInfo
               || var0x instanceof CandleHud
               || var0x instanceof Crosshair
      );
   }
}
