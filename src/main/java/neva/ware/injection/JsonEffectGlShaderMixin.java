package neva.ware.injection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_280;
import net.minecraft.class_2960;
import net.minecraft.class_5912;
import net.minecraft.class_281.class_282;
import neva.ware.utility.render.shaders.satin.impl.SamplerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_280.class})
public abstract class JsonEffectGlShaderMixin implements SamplerAccess {
   @WrapOperation(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_2960;method_60656(Ljava/lang/String;)Lnet/minecraft/class_2960;",
         ordinal = 0
      )},
      method = {"Lnet/minecraft/class_280;<init>(Lnet/minecraft/class_5912;Ljava/lang/String;)V"}
   )
   class_2960 constructProgramIdentifier(String arg, Operation<class_2960> original, class_5912 unused, String id) {
      if (!id.contains(":")) {
         return (class_2960)original.call(new Object[]{arg});
      } else {
         class_2960 split = class_2960.method_60654(id);
         return class_2960.method_60655(split.method_12836(), "shaders/program/" + split.method_12832() + ".json");
      }
   }

   @WrapOperation(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_2960;method_60656(Ljava/lang/String;)Lnet/minecraft/class_2960;",
         ordinal = 0
      )},
      method = {"Lnet/minecraft/class_280;method_16036(Lnet/minecraft/class_5912;Lnet/minecraft/class_281$class_282;Ljava/lang/String;)Lnet/minecraft/class_5937;"}
   )
   private static class_2960 constructProgramIdentifier(String arg, Operation<class_2960> original, class_5912 unused, class_282 shaderType, String id) {
      if (!arg.contains(":")) {
         return (class_2960)original.call(new Object[]{arg});
      } else {
         class_2960 split = class_2960.method_60654(id);
         return class_2960.method_60655(split.method_12836(), "shaders/program/" + split.method_12832() + shaderType.method_1284());
      }
   }
}
