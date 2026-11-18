package neva.ware.features.hud;

import neva.ware.utility.render.animation.EaseOutBack;

public class NwEase extends EaseOutBack {
   private static final float DURATION = 0.2F;
   private boolean dir;
   private float progress;
   private long last = System.nanoTime();

   public NwEase() {
      super(10);
   }

   @Override
   public void update(boolean var1) {
      this.step();
      this.dir = var1;
   }

   private void step() {
      long var1 = System.nanoTime();
      float var3 = Math.min(0.1F, (float)(var1 - this.last) / 1.0E9F);
      this.last = var1;
      float var4 = var3 / 0.2F;
      this.progress = this.dir ? Math.min(1.0F, this.progress + var4) : Math.max(0.0F, this.progress - var4);
   }

   @Override
   public double getAnimationd() {
      this.step();
      if (this.progress <= 0.0F) {
         return 0.0;
      } else if (this.progress >= 1.0F) {
         return 1.0;
      } else {
         return this.dir ? dropAnimation((double)this.progress) : (double)(this.progress * this.progress * (3.0F - 2.0F * this.progress));
      }
   }

   @Override
   public void reset() {
      this.progress = 0.0F;
      this.last = System.nanoTime();
   }
}
