package neva.ware.utility.render;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_4587;

public final class BlockAnimationUtility {
   private static final Map<BlockAnimationUtility.BlockRenderData, Long> blocks = new ConcurrentHashMap<>();

   public static void onRender(class_4587 matrixStack) {
      blocks.forEach((animation, time) -> {
         if ((float)(System.currentTimeMillis() - time) > 300.0F) {
            blocks.remove(animation);
         } else {
            animation.renderWithTime(System.currentTimeMillis() - time, matrixStack);
         }
      });
   }

   public static void renderBlock(
      class_2338 pos,
      Color lineColor,
      int lineWidth,
      Color fillColor,
      BlockAnimationUtility.BlockAnimationMode animationMode,
      BlockAnimationUtility.BlockRenderMode renderMode
   ) {
      if (renderMode != BlockAnimationUtility.BlockRenderMode.None) {
         blocks.put(new BlockAnimationUtility.BlockRenderData(pos, lineColor, lineWidth, fillColor, animationMode, renderMode), System.currentTimeMillis());
      }
   }

   public static boolean isRendering(class_2338 pos) {
      return blocks.keySet().stream().anyMatch(blockRenderData -> blockRenderData.pos().equals(pos));
   }

   public static enum BlockAnimationMode {
      Fade,
      Hover,
      Decrease,
      Static,
      Flash,
      Grow,
      Fill,
      TNT,
      Pull;
   }

   private static record BlockRenderData(
      class_2338 pos,
      Color lineColor,
      int lineWidth,
      Color fillColor,
      BlockAnimationUtility.BlockAnimationMode animationMode,
      BlockAnimationUtility.BlockRenderMode renderMode
   ) {
      void renderWithTime(Long time, class_4587 stack) {
         switch (this.animationMode) {
            case Fade: {
               class_238 box = new class_238(this.pos);
               renderBox(time, stack, box, this.renderMode, this.lineColor, this.lineWidth, this.fillColor);
               break;
            }
            case Hover: {
               float scale = 1.0F + (float)time.longValue() / 1500.0F;
               class_238 box = new class_238(
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260(),
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260()
               );
               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Line) {
                  Render3DEngine.drawBoxOutline(
                     box.method_1002((double)scale, (double)scale, (double)scale)
                        .method_989(0.5 + (double)(scale * 0.5F), 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5),
                     Render2DEngine.injectAlpha(this.lineColor, (int)((float)this.lineColor.getAlpha() * (1.0F - (float)time.longValue() / 300.0F))),
                     (float)this.lineWidth
                  );
               }

               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Fill) {
                  Render3DEngine.drawFilledBox(
                     stack,
                     box.method_1002((double)scale, (double)scale, (double)scale)
                        .method_989(0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5, 0.5 + (double)scale * 0.5),
                     Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * (1.0F - (float)time.longValue() / 300.0F)))
                  );
               }
               break;
            }
            case Decrease: {
               float scalexxxxxx = 1.0F - (float)time.longValue() / 300.0F;
               class_238 box = new class_238(
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260(),
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260()
               );
               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Line) {
                  Render3DEngine.drawBoxOutline(
                     box.method_1002((double)scalexxxxxx, (double)scalexxxxxx, (double)scalexxxxxx)
                        .method_989(0.5 + (double)scalexxxxxx * 0.5, 0.5 + (double)scalexxxxxx * 0.5, 0.5 + (double)scalexxxxxx * 0.5),
                     this.lineColor,
                     (float)this.lineWidth
                  );
               }

               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Fill) {
                  Render3DEngine.drawFilledBox(
                     stack,
                     box.method_1002((double)scalexxxxxx, (double)scalexxxxxx, (double)scalexxxxxx)
                        .method_989(0.5 + (double)scalexxxxxx * 0.5, 0.5 + (double)scalexxxxxx * 0.5, 0.5 + (double)scalexxxxxx * 0.5),
                     Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * (1.0F - (float)time.longValue() / 300.0F)))
                  );
               }
               break;
            }
            case Static:
               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Line) {
                  Render3DEngine.drawBoxOutline(new class_238(this.pos), this.lineColor, (float)this.lineWidth);
               }

               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Fill) {
                  Render3DEngine.drawFilledBox(stack, new class_238(this.pos), this.fillColor);
               }
               break;
            case Flash: {
               float scalexxxx;
               if (time > 100L) {
                  scalexxxx = 1.0F - (float)(time - 100L) / 400.0F;
               } else {
                  scalexxxx = (float)time.longValue() / 100.0F;
               }

               class_238 box = new class_238(
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260(),
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260()
               );
               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Line) {
                  Render3DEngine.drawBoxOutline(
                     box.method_1002((double)scalexxxx, (double)scalexxxx, (double)scalexxxx)
                        .method_989(0.5 + (double)scalexxxx * 0.5, 0.5 + (double)scalexxxx * 0.5, 0.5 + (double)scalexxxx * 0.5),
                     this.lineColor,
                     (float)this.lineWidth
                  );
               }

               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Fill) {
                  Render3DEngine.drawFilledBox(
                     stack,
                     box.method_1002((double)scalexxxx, (double)scalexxxx, (double)scalexxxx)
                        .method_989(0.5 + (double)scalexxxx * 0.5, 0.5 + (double)scalexxxx * 0.5, 0.5 + (double)scalexxxx * 0.5),
                     Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * scalexxxx))
                  );
               }
               break;
            }
            case Grow: {
               float scalexxx = (float)time.longValue() / 300.0F;
               class_238 box = new class_238(
                  (double)this.pos.method_10263(),
                  (double)((float)this.pos.method_10264() + scalexxx),
                  (double)this.pos.method_10260(),
                  (double)(this.pos.method_10263() + 1),
                  (double)this.pos.method_10264(),
                  (double)(this.pos.method_10260() + 1)
               );
               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Line) {
                  Render3DEngine.drawBoxOutline(box, this.lineColor, (float)this.lineWidth);
               }

               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Fill) {
                  Render3DEngine.drawFilledBox(
                     stack, box, Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * ((float)time.longValue() / 300.0F)))
                  );
               }
               break;
            }
            case Fill: {
               float scalexxxxx = (float)time.longValue() / 300.0F;
               class_238 box = new class_238(
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260(),
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260()
               );
               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Line) {
                  Render3DEngine.drawBoxOutline(
                     box.method_1002((double)scalexxxxx, (double)scalexxxxx, (double)scalexxxxx)
                        .method_989(0.5 + (double)scalexxxxx * 0.5, 0.5 + (double)scalexxxxx * 0.5, 0.5 + (double)scalexxxxx * 0.5),
                     this.lineColor,
                     (float)this.lineWidth
                  );
               }

               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Fill) {
                  Render3DEngine.drawFilledBox(
                     stack,
                     box.method_1002((double)scalexxxxx, (double)scalexxxxx, (double)scalexxxxx)
                        .method_989(0.5 + (double)scalexxxxx * 0.5, 0.5 + (double)scalexxxxx * 0.5, 0.5 + (double)scalexxxxx * 0.5),
                     Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * ((float)time.longValue() / 300.0F)))
                  );
               }
               break;
            }
            case TNT: {
               float scalexx;
               if (time < 200L) {
                  scalexx = 1.0F;
               } else {
                  scalexx = 1.0F + ((float)time.longValue() - 200.0F) / 400.0F;
               }

               class_238 box = new class_238(
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260(),
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260()
               );
               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Line) {
                  Render3DEngine.drawBoxOutline(
                     box.method_1002((double)scalexx, (double)scalexx, (double)scalexx)
                        .method_989(0.5 + (double)(scalexx * 0.5F), 0.5 + (double)scalexx * 0.5, 0.5 + (double)scalexx * 0.5),
                     this.lineColor,
                     (float)this.lineWidth
                  );
               }

               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Fill) {
                  Render3DEngine.drawFilledBox(
                     stack,
                     box.method_1002((double)scalexx, (double)scalexx, (double)scalexx)
                        .method_989(0.5 + (double)scalexx * 0.5, 0.5 + (double)scalexx * 0.5, 0.5 + (double)scalexx * 0.5),
                     Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * scalexx))
                  );
               }
               break;
            }
            case Pull: {
               float scalex;
               if (time < 200L) {
                  scalex = 1.5F - (float)time.longValue() / 200.0F * 0.5F;
               } else {
                  scalex = 1.0F;
               }

               class_238 box = new class_238(
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260(),
                  (double)this.pos.method_10263(),
                  (double)this.pos.method_10264(),
                  (double)this.pos.method_10260()
               );
               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Line) {
                  Render3DEngine.drawBoxOutline(
                     box.method_1002((double)scalex, (double)scalex, (double)scalex)
                        .method_989(0.5 + (double)(scalex * 0.5F), 0.5 + (double)scalex * 0.5, 0.5 + (double)scalex * 0.5),
                     this.lineColor,
                     (float)this.lineWidth
                  );
               }

               if (this.renderMode == BlockAnimationUtility.BlockRenderMode.All || this.renderMode == BlockAnimationUtility.BlockRenderMode.Fill) {
                  Render3DEngine.drawFilledBox(
                     stack,
                     box.method_1002((double)scalex, (double)scalex, (double)scalex)
                        .method_989(0.5 + (double)scalex * 0.5, 0.5 + (double)scalex * 0.5, 0.5 + (double)scalex * 0.5),
                     Render2DEngine.injectAlpha(this.fillColor, (int)((float)this.fillColor.getAlpha() * scalex))
                  );
               }
            }
         }
      }

      private static void renderBox(
         Long time, class_4587 stack, class_238 box, BlockAnimationUtility.BlockRenderMode renderMode, Color lineColor, int lineWidth, Color fillColor
      ) {
         if (renderMode == BlockAnimationUtility.BlockRenderMode.All || renderMode == BlockAnimationUtility.BlockRenderMode.Line) {
            Render3DEngine.drawBoxOutline(
               box, Render2DEngine.injectAlpha(lineColor, (int)((float)fillColor.getAlpha() * (1.0F - (float)time.longValue() / 300.0F))), (float)lineWidth
            );
         }

         if (renderMode == BlockAnimationUtility.BlockRenderMode.All || renderMode == BlockAnimationUtility.BlockRenderMode.Fill) {
            Render3DEngine.drawFilledBox(
               stack, box, Render2DEngine.injectAlpha(fillColor, (int)((float)fillColor.getAlpha() * (1.0F - (float)time.longValue() / 300.0F)))
            );
         }
      }
   }

   public static enum BlockRenderMode {
      Fill,
      Line,
      All,
      None;
   }
}
