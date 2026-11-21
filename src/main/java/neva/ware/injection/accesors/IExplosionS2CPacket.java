package neva.ware.injection.accesors;

import net.minecraft.class_2664;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2664.class})
public interface IExplosionS2CPacket {
   @Mutable
   @Accessor("field_12176")
   void setMotionX(float var1);

   @Mutable
   @Accessor("field_12183")
   void setMotionY(float var1);

   @Mutable
   @Accessor("field_12182")
   void setMotionZ(float var1);

   @Accessor("field_12176")
   float getMotionX();

   @Accessor("field_12183")
   float getMotionY();

   @Accessor("field_12182")
   float getMotionZ();
}
