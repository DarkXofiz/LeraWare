package neva.ware.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import neva.ware.core.manager.client.ModuleManager;

public final class InvAnimBg {
   private static Field fStack;
   private static Field fCurE;
   private static Field fGhost;
   private static Field fOpenStart;
   private static Field fCloseStart;
   private static Method mTransform;
   private static boolean ready;
   private static boolean pending;
   private static int depth;
   private static float pAlpha;
   private static float pE;
   private static float pW;
   private static float pH;
   private static boolean pClosing;
   private static class_4587 pMs;

   private InvAnimBg() {
   }

   private static Field acc(Field var0) {
      var0.setAccessible(true);
      return var0;
   }

   private static float clamp01(float var0) {
      return var0 < 0.0F ? 0.0F : (var0 > 1.0F ? 1.0F : var0);
   }

   public static boolean animating() {
      if (!ready) {
         return false;
      } else {
         try {
            return Boolean.TRUE.equals(((ArrayDeque)fStack.get(null)).peek());
         } catch (Throwable var1) {
            return false;
         }
      }
   }

   private static boolean isGhost(class_437 var0) {
      try {
         return var0 == fGhost.get(null);
      } catch (Throwable var2) {
         return false;
      }
   }

   private static float curE() {
      try {
         return fCurE.getFloat(null);
      } catch (Throwable var1) {
         return 1.0F;
      }
   }

   private static float linear(class_437 var0) {
      InventoryAnimation var1 = InventoryAnimation.instance;
      float var2 = (float)Math.max(1, var1.duration.getValue());
      long var3 = System.currentTimeMillis();

      try {
         return isGhost(var0) ? 1.0F - clamp01((float)(var3 - fCloseStart.getLong(null)) / var2) : clamp01((float)(var3 - fOpenStart.getLong(null)) / var2);
      } catch (Throwable var6) {
         return clamp01(curE());
      }
   }

   private static float sine(float var0) {
      return (float)((1.0 - Math.cos(Math.PI * (double)clamp01(var0))) * 0.5);
   }

   private static void flush(class_332 var0) {
      try {
         var0.method_51452();
      } catch (Throwable var2) {
      }
   }

   private static boolean bgDisabled() {
      try {
         NoRender var0 = ModuleManager.noRender;
         return var0 != null && var0.isEnabled() && Boolean.TRUE.equals(var0.disableGuiBackGround.getValue());
      } catch (Throwable var1) {
         return false;
      }
   }

   private static void applyTransform(class_4587 var0, float var1, float var2, float var3) {
      try {
         mTransform.invoke(null, var0, InventoryAnimation.instance.mode.getValue(), var1, var2, var3);
      } catch (Throwable var5) {
      }
   }

   public static void head(class_332 var0, class_437 var1, int var2, int var3, float var4) {
      pending = false;
      depth = 0;
      InventoryAnimation.begin(var0, var1);
      if (animating()) {
         class_4587 var5 = var0.method_51448();
         float var6 = (float)var1.field_22789;
         float var7 = (float)var1.field_22790;
         boolean var8 = isGhost(var1);
         float var9 = linear(var1);
         float var10 = var8 ? 1.0F - sine(1.0F - var9) : curE();
         var5.method_22909();
         if (bgDisabled()) {
            var5.method_22903();
            applyTransform(var5, var10, var6, var7);
         } else {
            pending = true;
            pMs = var5;
            pE = var10;
            pW = var6;
            pH = var7;
            pClosing = var8;
            pAlpha = var8 ? sine(var9) : var9 * var9 * (3.0F - 2.0F * var9);
         }
      }
   }

   public static void bgHead(class_332 var0) {
      if (pending) {
         if (depth++ == 0) {
            flush(var0);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, pAlpha);
         }
      }
   }

   public static void bgReturn(class_332 var0) {
      if (pending && depth > 0) {
         if (--depth == 0) {
            flush(var0);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            pending = false;
            pMs.method_22903();
            applyTransform(pMs, pE, pW, pH);
         }
      }
   }

   public static void tail(class_332 var0) {
      if (pending) {
         pending = false;
         depth = 0;
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         var0.method_51448().method_22903();
      }

      InventoryAnimation.end(var0);
   }

   public static boolean dim(int var0, int var1, int var2, int var3) {
      return false;
   }

   public static boolean cancelBg() {
      return false;
   }

   public static boolean cancelTooltip() {
      return animating();
   }

   static {
      try {
         Class<InventoryAnimation> var0 = InventoryAnimation.class;
         fStack = acc(var0.getDeclaredField("STACK"));
         fCurE = acc(var0.getDeclaredField("curE"));
         fGhost = acc(var0.getDeclaredField("ghost"));
         fOpenStart = acc(var0.getDeclaredField("openStart"));
         fCloseStart = acc(var0.getDeclaredField("closeStart"));
         mTransform = var0.getDeclaredMethod("transform", class_4587.class, InventoryAnimation.Mode.class, float.class, float.class, float.class);
         mTransform.setAccessible(true);
         ready = true;
      } catch (Throwable var1) {
         ready = false;
      }
   }
}
