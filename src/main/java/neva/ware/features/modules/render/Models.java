package neva.ware.features.modules.render;

import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;

public class Models extends Module {
   public Setting<Boolean> onlySelf = new Setting<>("OnlySelf", false);
   public Setting<Boolean> friends = new Setting<>("Friends", false);
   public Setting<Boolean> friendHighlight = new Setting<>("FriendHighlight", false);
   public Setting<Boolean> changeZ = new Setting<>("Reversed", true);
   public Setting<Models.Mode> mode = new Setting<>("Mode", Models.Mode.Rabbit);
   public Setting<ColorSetting> eyeColor = new Setting<>("EyeColor", new ColorSetting(-2009289807));
   public Setting<ColorSetting> bodyColor = new Setting<>("BodyColor", new ColorSetting(-2009289807));
   public Setting<ColorSetting> legsColor = new Setting<>("LegsColor", new ColorSetting(-2009289807));

   public Models() {
      super("Models", Module.Category.RENDER);
   }

   public static enum Mode {
      Amogus,
      Rabbit;
   }
}
