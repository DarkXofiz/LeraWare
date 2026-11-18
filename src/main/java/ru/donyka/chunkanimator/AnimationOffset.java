package ru.donyka.chunkanimator;

public final class AnimationOffset {
   public static final AnimationOffset ZERO = new AnimationOffset(0.0F, 0.0F, 0.0F);
   private final float x;
   private final float y;
   private final float z;

   public AnimationOffset(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public float z() {
      return this.z;
   }

   public boolean isZero() {
      return this.x == 0.0F && this.y == 0.0F && this.z == 0.0F;
   }
}
