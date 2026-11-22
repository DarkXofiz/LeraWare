package neva.ware.gui.mainmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.InputStream;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_2960;
import neva.ware.features.modules.Module;

/**
 * Loads the MiranWare background and logo straight from the mod jar and registers them
 * as dynamic textures. Loading from the classpath (instead of by resource id) means the
 * images are available while the Mojang splash screen is still reloading resources.
 * Every accessor returns null on failure, callers must have a fallback.
 */
public final class MiranAssets {
   public static final class Tex {
      public final class_2960 id;
      public final int w;
      public final int h;

      Tex(class_2960 id, int w, int h) {
         this.id = id;
         this.w = w;
         this.h = h;
      }
   }

   private static final String BASE = "/assets/nevaware/textures/gui/menu/";
   private static Tex background;
   private static Tex logo;
   private static boolean backgroundTried;
   private static boolean logoTried;

   private MiranAssets() {
   }

   public static Tex background() {
      if (!backgroundTried) {
         backgroundTried = true;
         background = load("miranware_bg.png", "dynamic/miranware_bg");
      }

      return background;
   }

   public static Tex logo() {
      if (!logoTried) {
         logoTried = true;
         logo = load("miranware_logo.png", "dynamic/miranware_logo");
      }

      return logo;
   }

   private static Tex load(String file, String idPath) {
      try (InputStream in = MiranAssets.class.getResourceAsStream(BASE + file)) {
         if (in == null) {
            return null;
         }

         class_1011 image = class_1011.method_4309(in);
         int w = image.method_4307();
         int h = image.method_4323();
         class_1043 texture = new class_1043(image);
         class_2960 id = class_2960.method_60655("nevaware", idPath);
         Module.mc.method_1531().method_4616(id, texture);
         // smooth scaling: default dynamic textures are nearest-neighbour
         GlStateManager._bindTexture(texture.method_4624());
         GlStateManager._texParameter(3553, 10241, 9729);
         GlStateManager._texParameter(3553, 10240, 9729);
         return new Tex(id, w, h);
      } catch (Throwable t) {
         return null;
      }
   }
}
