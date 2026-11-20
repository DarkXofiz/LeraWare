package neva.ware.injection.accesors;

import net.minecraft.class_2743;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2743.class})
public interface ISPacketEntityVelocity {
    @Mutable
    @Accessor("field_12563")
    void setMotionX(int var1);

    @Mutable
    @Accessor("field_12562")
    void setMotionY(int var1);

    @Mutable
    @Accessor("field_12561")
    void setMotionZ(int var1);
}
