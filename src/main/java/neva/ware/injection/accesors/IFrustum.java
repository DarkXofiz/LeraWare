package neva.ware.injection.accesors;

import net.minecraft.class_4604;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_4604.class})
public interface IFrustum {
   @Accessor("field_40823")
   FrustumIntersection getFrustumIntersection();

   @Accessor("field_20995")
   double getX();

   @Accessor("field_20995")
   void setX(double var1);

   @Accessor("field_20996")
   double getY();

   @Accessor("field_20996")
   void setY(double var1);

   @Accessor("field_20997")
   double getZ();

   @Accessor("field_20997")
   void setZ(double var1);
}
