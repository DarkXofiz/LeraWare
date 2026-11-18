package neva.ware.core.manager.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import neva.ware.features.modules.Module;

public final class CombatModuleFilter {
   private static final Set<String> COMBAT_ALLOWED = new HashSet<>(
      Arrays.asList(
         "AntiBot",
         "Attack Aura",
         "AutoPotion",
         "AutoGApple",
         "AutoTotem",
         "Criticals",
         "ElytraTarget",
         "HitBoxes",
         "Reach",
         "TargetStrafe",
         "LegitAura",
         "LegitTarget"
      )
   );
   private static final Set<String> MISC_ALLOWED = new HashSet<>(
      Arrays.asList("ExtraTab", "FakePlayer", "ItemScroller", "MiddleClick", "NameProtect", "Nuker", "TotemPopCounter", "VisualRange")
   );
   private static final Set<String> RENDER_REMOVED = new HashSet<>(
      Arrays.asList("BlockESP", "Chams", "HoleESP", "KillEffect", "LogoutSpots", "Models", "NoBob", "PenisESP", "PopChams", "VoidESP")
   );
   private static final Set<String> MOVEMENT_ALLOWED = new HashSet<>(
      Arrays.asList("AntiWeb", "AutoSprint", "BoatFly", "ElytraBooster", "Flight", "GuiMove", "NoFall", "NoPush", "NoSlow", "Speed", "Strafe", "Velocity")
   );
   private static final Set<String> PLAYER_ALLOWED = new HashSet<>(
      Arrays.asList("AutoArmor", "AutoRespawn", "ElytraHelper", "ElytraHelper", "NoInteract", "FastUse", "SpeedMine", "LegitHelper")
   );
   private static final Set<String> CLIENT_REMOVED = new HashSet<>(Arrays.asList("ChatTranslator", "FastLatency", "Media", "OptifineCapes"));

   public static void filter(ArrayList<Module> var0) {
      if (var0 != null) {
         var0.removeIf(CombatModuleFilter::remove);
      }
   }

   private static boolean remove(Module var0) {
      if (var0 == null) {
         return true;
      } else {
         Module.Category var1 = var0.getCategory();
         if (var1 == Module.Category.COMBAT) {
            return !COMBAT_ALLOWED.contains(var0.getName());
         } else if (var1 == Module.Category.MISC) {
            return !MISC_ALLOWED.contains(var0.getName());
         } else if (var1 == Module.Category.RENDER) {
            return RENDER_REMOVED.contains(var0.getName());
         } else if (var1 == Module.Category.MOVEMENT) {
            return !MOVEMENT_ALLOWED.contains(var0.getName());
         } else if (var1 == Module.Category.PLAYER) {
            return !PLAYER_ALLOWED.contains(var0.getName());
         } else {
            return var1 == Module.Category.CLIENT ? CLIENT_REMOVED.contains(var0.getName()) : false;
         }
      }
   }
}
