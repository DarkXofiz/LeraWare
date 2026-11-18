package neva.ware.injection.accesors;

import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_636.class})
public interface IInteractionManager {
   @Accessor("field_3715")
   float getCurBlockDamageMP();

   @Accessor("field_3715")
   void setCurBlockDamageMP(float var1);

   @Invoker("method_2911()V")
   void syncSlot();
}
