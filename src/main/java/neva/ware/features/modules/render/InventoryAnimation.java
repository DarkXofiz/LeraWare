package neva.ware.features.modules.render;

import java.util.ArrayDeque;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class InventoryAnimation extends Module {
   public static InventoryAnimation instance;
   public final Setting<InventoryAnimation.Mode> mode = new Setting<>("Mode", InventoryAnimation.Mode.Smooth);
   public final Setting<InventoryAnimation.Easing> easing = new Setting<>("Easing", InventoryAnimation.Easing.Smooth);
   public final Setting<Integer> duration = new Setting<>("Duration", 260, 100, 1500);
   public final Setting<Boolean> close = new Setting<>("Close Animation", Boolean.TRUE);
   public final Setting<Boolean> containers = new Setting<>("All Containers", Boolean.TRUE);
   private static class_437 tracked;
   private static long openStart;
   private static class_437 ghost;
   private static long closeStart;
   private static final ArrayDeque<Boolean> STACK = new ArrayDeque<>();
   private static float curE = 1.0F;
   private static boolean inDim;

   public InventoryAnimation() {
      super("InventoryAnimation", Module.Category.RENDER);
      instance = this;
   }

   private static boolean hasSuper(Class<?> var0, String var1) {
      while (var0 != null) {
         if (var0.getName().equals(var1)) {
            return true;
         }

         var0 = var0.getSuperclass();
      }

      return false;
   }

   private boolean accepts(class_437 var1) {
      if (var1 == null || !hasSuper(var1.getClass(), "net.minecraft.class_465")) {
         return false;
      } else {
         return this.containers.getValue()
            ? true
            : hasSuper(var1.getClass(), "net.minecraft.class_490") || hasSuper(var1.getClass(), "net.minecraft.class_481");
      }
   }

   private static float clamp01(float var0) {
      return var0 < 0.0F ? 0.0F : (var0 > 1.0F ? 1.0F : var0);
   }

   private static float ease(InventoryAnimation.Easing var0, float var1) {
      switch (var0) {
         case Linear:
            return var1;
         case Back:
            float var9 = 1.70158F;
            float var10 = var9 + 1.0F;
            float var4 = var1 - 1.0F;
            return 1.0F + var10 * var4 * var4 * var4 + var9 * var4 * var4;
         case Elastic:
            if (var1 <= 0.0F) {
               return 0.0F;
            } else {
               if (var1 >= 1.0F) {
                  return 1.0F;
               }

               return (float)(Math.pow(2.0, -10.0 * (double)var1) * Math.sin(((double)var1 * 10.0 - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0);
            }
         case Bounce:
            float var8 = 7.5625F;
            float var3 = 2.75F;
            if (var1 < 1.0F / var3) {
               return var8 * var1 * var1;
            } else if (var1 < 2.0F / var3) {
               var1 -= 1.5F / var3;
               return var8 * var1 * var1 + 0.75F;
            } else {
               if (var1 < 2.5F / var3) {
                  var1 -= 2.25F / var3;
                  return var8 * var1 * var1 + 0.9375F;
               }

               var1 -= 2.625F / var3;
               return var8 * var1 * var1 + 0.984375F;
            }
         case Expo:
            return var1 >= 1.0F ? 1.0F : (float)(1.0 - Math.pow(2.0, -10.0 * (double)var1));
         default:
            float var2 = 1.0F - var1;
            return 1.0F - var2 * var2 * var2 * var2 * var2;
      }
   }

   private static void scaleAt(class_4587 var0, float var1, float var2, float var3, float var4) {
      var0.method_46416(var1, var2, 0.0F);
      var0.method_22905(var3, var4, 1.0F);
      var0.method_46416(-var1, -var2, 0.0F);
   }

   private static void rotAt(class_4587 var0, float var1, float var2, float var3) {
      var0.method_46416(var1, var2, 0.0F);
      var0.method_22907(class_7833.field_40718.rotationDegrees(var3));
      var0.method_46416(-var1, -var2, 0.0F);
   }

   private static void transform(class_4587 var0, InventoryAnimation.Mode var1, float var2, float var3, float var4) {
      float var5 = 1.0F - var2;
      float var6 = var3 * 0.5F;
      float var7 = var4 * 0.5F;
      float var8 = Math.max(var2, 0.0F);
      switch (var1) {
         case Smooth:
            float var14 = 0.84F + 0.16F * var2;
            var0.method_46416(0.0F, var5 * var4 * 0.07F, 0.0F);
            scaleAt(var0, var6, var7, var14, var14);
            break;
         case Zoom:
            scaleAt(var0, var6, var7, var8, var8);
            break;
         case ZoomOut:
            float var13 = 1.0F + var5 * 0.7F;
            scaleAt(var0, var6, var7, var13, var13);
            break;
         case Pop:
            float var12 = 0.8F + 0.2F * var2;
            scaleAt(var0, var6, var7, var12, var12);
            break;
         case SlideUp:
            var0.method_46416(0.0F, var5 * var4, 0.0F);
            break;
         case SlideDown:
            var0.method_46416(0.0F, -var5 * var4, 0.0F);
            break;
         case SlideLeft:
            var0.method_46416(-var5 * var3, 0.0F, 0.0F);
            break;
         case SlideRight:
            var0.method_46416(var5 * var3, 0.0F, 0.0F);
            break;
         case Diagonal:
            var0.method_46416(-var5 * var3, -var5 * var4, 0.0F);
            break;
         case Rise:
            var0.method_46416(0.0F, var5 * var4 * 0.3F, 0.0F);
            float var11 = 0.75F + 0.25F * var2;
            scaleAt(var0, var6, var7, var11, var11);
            break;
         case Expand:
            scaleAt(var0, var6, var7, var8, 1.0F);
            break;
         case Squash:
            scaleAt(var0, var6, var7, 1.0F, var8);
            break;
         case FlipX:
            scaleAt(var0, var6, var7, (float)Math.cos((double)var5 * Math.PI), 1.0F);
            break;
         case FlipY:
            scaleAt(var0, var6, var7, 1.0F, (float)Math.cos((double)var5 * Math.PI));
            break;
         case Spin:
            rotAt(var0, var6, var7, var5 * 360.0F);
            scaleAt(var0, var6, var7, var8, var8);
            break;
         case Swing:
            var0.method_46416(0.0F, -var5 * var4 * 0.15F, 0.0F);
            rotAt(var0, var6, 0.0F, -var5 * 35.0F);
            break;
         case Tilt:
            var0.method_46416(var5 * var3 * 0.35F, 0.0F, 0.0F);
            rotAt(var0, var6, var7, -var5 * 25.0F);
            float var10 = 0.7F + 0.3F * var2;
            scaleAt(var0, var6, var7, var10, var10);
            break;
         case Wobble:
            rotAt(var0, var6, var7, (float)Math.sin((double)var2 * Math.PI * 5.0) * var5 * 14.0F);
            float var9 = 0.5F + 0.5F * var2;
            scaleAt(var0, var6, var7, var9, var9);
            break;
         case Unroll:
            scaleAt(var0, var6, 0.0F, 1.0F, var8);
            break;
         case Curtain:
            scaleAt(var0, 0.0F, var7, var8, 1.0F);
            break;
         default:
            scaleAt(var0, 0.0F, 0.0F, var8, var8);
      }
   }

   public static void begin(class_332 var0, class_437 var1) {
      boolean var2 = false;

      try {
         InventoryAnimation var3 = instance;
         if (var3 != null && var3.isEnabled() && var3.accepts(var1)) {
            long var4 = System.currentTimeMillis();
            float var6 = (float)Math.max(1, var3.duration.getValue());
            boolean var8 = var1 == ghost;
            float var7;
            if (var8) {
               var7 = 1.0F - clamp01((float)(var4 - closeStart) / var6);
            } else {
               if (var1 != tracked) {
                  tracked = var1;
                  openStart = var4;
               }

               var7 = clamp01((float)(var4 - openStart) / var6);
            }

            if (var8 || var7 < 1.0F) {
               float var9 = ease(var3.easing.getValue(), var7);
               curE = var9;
               class_4587 var10 = var0.method_51448();
               var10.method_22903();
               var2 = true;
               transform(var10, var3.mode.getValue(), var9, (float)var1.field_22789, (float)var1.field_22790);
            }
         }
      } catch (Throwable var12) {
         if (var2) {
            try {
               var0.method_51448().method_22909();
            } catch (Throwable var11) {
            }

            var2 = false;
         }
      }

      STACK.push(var2);
   }

   public static void end(class_332 var0) {
      Boolean var1 = STACK.poll();
      if (var1 != null && var1) {
         var0.method_51448().method_22909();
      }
   }

   public static boolean dimIntercept(class_332 var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      if (inDim) {
         return false;
      } else {
         try {
            if (var1 == 0 && var2 == 0 && var5 == -1072689136 && var6 == -804253680) {
               NoRender var7 = ModuleManager.noRender;
               if (var7 != null && var7.isEnabled() && Boolean.TRUE.equals(var7.disableGuiBackGround.getValue())) {
                  return true;
               } else {
                  Boolean var8 = STACK.peek();
                  if (var8 != null && var8) {
                     float var9 = clamp01(curE);
                     int var10 = (int)(192.0F * var9) << 24 | 1052688;
                     int var11 = (int)(208.0F * var9) << 24 | 1052688;
                     class_4587 var12 = var0.method_51448();
                     inDim = true;
                     var12.method_22903();

                     try {
                        var12.method_34426();
                        var0.method_25296(0, 0, var3, var4, var10, var11);
                     } finally {
                        var12.method_22909();
                        inDim = false;
                     }

                     return true;
                  } else {
                     return false;
                  }
               }
            } else {
               return false;
            }
         } catch (Throwable var17) {
            inDim = false;
            return false;
         }
      }
   }

   public static void onSetScreen(class_437 var0) {
      try {
         InventoryAnimation var1 = instance;
         if (var0 != null) {
            ghost = null;
            return;
         }

         class_437 var2 = class_310.method_1551().field_1755;
         if (var1 != null && var1.isEnabled() && var1.close.getValue() && var2 != null && var2 != ghost && var1.accepts(var2)) {
            ghost = var2;
            closeStart = System.currentTimeMillis();
         }
      } catch (Throwable var3) {
      }
   }

   @Override
   public void onRender2D(class_332 var1) {
      class_437 var2 = ghost;
      if (var2 != null) {
         class_310 var3 = class_310.method_1551();
         if (var3.field_1724 != null
            && var3.field_1755 == null
            && this.close.getValue()
            && System.currentTimeMillis() - closeStart < (long)this.duration.getValue().intValue()) {
            try {
               var2.method_25394(var1, var2.field_22789 / 2, var2.field_22790 / 2, 0.0F);
            } catch (Throwable var5) {
               ghost = null;
            }
         } else {
            ghost = null;
         }
      }
   }

   public static enum Easing {
      Smooth,
      Linear,
      Back,
      Elastic,
      Bounce,
      Expo;
   }

   public static enum Mode {
      Smooth,
      Zoom,
      ZoomOut,
      Pop,
      SlideUp,
      SlideDown,
      SlideLeft,
      SlideRight,
      Diagonal,
      Rise,
      Expand,
      Squash,
      FlipX,
      FlipY,
      Spin,
      Swing,
      Tilt,
      Wobble,
      Unroll,
      Curtain,
      Corner;
   }
}
