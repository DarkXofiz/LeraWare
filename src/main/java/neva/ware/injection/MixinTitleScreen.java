package neva.ware.injection;

import java.net.URI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_3675;
import net.minecraft.class_410;
import net.minecraft.class_437;
import net.minecraft.class_442;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.gui.mainmenu.MainMenuScreen;
import neva.ware.gui.misc.DialogScreen;
import neva.ware.utility.render.TextureStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_442.class})
public class MixinTitleScreen extends class_437 {
   protected MixinTitleScreen(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"Lnet/minecraft/class_442;method_25426()V"},
      at = {@At("RETURN")}
   )
   public void postInitHook(CallbackInfo ci) {
      if (ClientSettings.customMainMenu.getValue() && !MainMenuScreen.getInstance().confirm && ModuleManager.clickGui.getBind().getKey() != -1) {
         Module.mc.method_1507(MainMenuScreen.getInstance());
      }

      if (ModuleManager.clickGui.getBind().getKey() == -1) {
         DialogScreen dialogScreen2 = new DialogScreen(
            TextureStorage.cutie,
            ClientSettings.isRu() ? "Спасибо что скачали NevaWare!" : "Thank you for downloading NevaWare!",
            ClientSettings.isRu() ? "Меню с функциями клиента открывается на клавишу - P" : "Menu with client modules is opened with the key - P",
            ClientSettings.isRu() ? "Зайти в майн" : "Join on minecraft",
            ClientSettings.isRu() ? "Закрыть майн" : "Close minecraft",
            () -> {
               ModuleManager.clickGui.setBind(class_3675.method_15981("key.keyboard.p").method_1444(), false, false);
               Module.mc.method_1507(MainMenuScreen.getInstance());
            },
            () -> {
               ModuleManager.clickGui.setBind(class_3675.method_15981("key.keyboard.p").method_1444(), false, false);
               Module.mc.method_1490();
            }
         );
         DialogScreen dialogScreen1 = new DialogScreen(TextureStorage.questionPic, "Hello!", "What's your language?", "Русский", "English", () -> {
            ClientSettings.language.setValue(ClientSettings.Language.RU);
            Module.mc.method_1507(dialogScreen2);
         }, () -> {
            ClientSettings.language.setValue(ClientSettings.Language.ENG);
            Module.mc.method_1507(dialogScreen2);
         });
         Module.mc.method_1507(dialogScreen1);
      }

      if (false && !FabricLoader.getInstance().isDevelopmentEnvironment()) {
         Module.mc
            .method_1507(
               new class_410(
                  confirm -> {
                     if (confirm) {
                        class_156.method_668()
                           .method_673(URI.create("https://github.com/Pan4ur/ThunderHack-Recode/releases/download/latest/thunderhack-1.7.jar/"));
                     } else {
                        Module.mc.method_1490();
                     }
                  },
                  class_2561.method_30163(class_124.field_1061 + "You are using an outdated version of NevaWare Recode"),
                  class_2561.method_30163("Please update to the latest release"),
                  class_2561.method_30163("Download"),
                  class_2561.method_30163("Quit Game")
               )
            );
      }
   }
}
