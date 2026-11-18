package neva.ware.features.modules.render;

import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import ru.donyka.chunkanimator.config.AnimationMode;
import ru.donyka.chunkanimator.config.ChunkAnimatorConfig;
import ru.donyka.chunkanimator.config.EasingFunction;

public class ChunkAnim extends Module {
   public final Setting<ChunkAnim.Mode> mode = new Setting<>("Mode", ChunkAnim.Mode.Below);
   public final Setting<ChunkAnim.Easing> easing = new Setting<>("Easing", ChunkAnim.Easing.Quad);
   public final Setting<Integer> duration = new Setting<>("Duration", 1000, 100, 5000);
   public final Setting<Boolean> disableAround = new Setting<>("Disable Around Player", Boolean.FALSE);

   public ChunkAnim() {
      super("ChunkAnimator", Module.Category.RENDER);
      off();
   }

   private static void off() {
      try {
         ChunkAnimatorConfig.get().animationDuration = 0;
      } catch (Throwable var1) {
      }
   }

   private void sync() {
      try {
         ChunkAnimatorConfig var1 = ChunkAnimatorConfig.get();
         AnimationMode[] var2 = AnimationMode.values();
         EasingFunction[] var3 = EasingFunction.values();
         var1.mode = var2[Math.min(var2.length - 1, this.mode.getValue().ordinal())];
         var1.easingFunction = var3[Math.min(var3.length - 1, this.easing.getValue().ordinal())];
         var1.animationDuration = this.duration.getValue();
         var1.disableAroundPlayer = this.disableAround.getValue();
      } catch (Throwable var4) {
      }
   }

   @Override
   public void onEnable() {
      this.sync();
   }

   @Override
   public void onDisable() {
      off();
   }

   @Override
   public void onUpdate() {
      this.sync();
   }

   public static enum Easing {
      Linear,
      Quad,
      Cubic,
      Quart,
      Quint,
      Expo,
      Sine,
      Circ,
      Back,
      Bounce,
      Elastic;
   }

   public static enum Mode {
      Below,
      Above,
      Hybrid,
      HorizontalSlide,
      HorizontalSlideAlt;
   }
}
