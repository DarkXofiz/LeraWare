package neva.ware.injection.accesors;

import net.minecraft.class_4668;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_4668.class})
public interface IRenderPhaseMixin {
    @Accessor("field_21363")
    String getName();
}
