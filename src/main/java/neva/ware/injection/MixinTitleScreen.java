package neva.ware.injection;

import net.minecraft.class_2561;
import net.minecraft.class_3675;
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
    }
}
