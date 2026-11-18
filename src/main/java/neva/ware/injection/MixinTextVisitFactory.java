package neva.ware.injection;

import net.minecraft.class_5223;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.core.manager.player.FriendManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.misc.NameProtect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({class_5223.class})
public class MixinTextVisitFactory {
   @ModifyArg(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
         ordinal = 0
      ),
      method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"},
      index = 0
   )
   private static String adjustText(String text) {
      return protect(text);
   }

   private static String protect(String string) {
      if (ModuleManager.nameProtect.isEnabled() && Module.mc.field_1724 != null) {
         String me = Module.mc.method_1548().method_1676();
         return !string.contains(me) && (!FriendManager.friends.stream().anyMatch(i -> i.contains(string)) || !NameProtect.hideFriends.getValue())
            ? string
            : string.replace(me, NameProtect.getCustomName());
      } else {
         return string;
      }
   }
}
