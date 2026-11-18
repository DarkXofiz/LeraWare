package neva.ware.features.modules.client;

import java.awt.Color;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render2DEngine;

public final class NwTheme {
   private NwTheme() {
   }

   public static boolean isCustom() {
      try {
         return HudEditor.colorMode.getValue() == HudEditor.ColorMode.Custom;
      } catch (Throwable var1) {
         return false;
      }
   }

   public static Setting<ColorSetting> createColor1() {
      return new Setting<>("Custom Color 1", new ColorSetting(-8614657), var0 -> isCustom());
   }

   public static Setting<ColorSetting> createColor2() {
      return new Setting<>("Custom Color 2", new ColorSetting(-2982657), var0 -> isCustom());
   }

   private static Color color(Setting<?> var0, int var1) {
      try {
         if (var0.getValue() instanceof ColorSetting var3) {
            return var3.getColorObject();
         }
      } catch (Throwable var4) {
      }

      return new Color(var1, true);
   }

   public static Color color1() {
      return color(HudEditor.customColor1, -8614657);
   }

   public static Color color2() {
      return color(HudEditor.customColor2, -2982657);
   }

   public static Color customColor(int var0) {
      int var1 = 10;

      try {
         var1 = HudEditor.colorSpeed.getValue().intValue();
      } catch (Throwable var3) {
      }

      return Render2DEngine.TwoColoreffect(color1(), color2(), (double)var1, (double)var0);
   }

   public static HudEditor.Theme currentThemeOrNull() {
      return isCustom() ? new HudEditor.Theme("Custom", color1(), color2()) : null;
   }
}
