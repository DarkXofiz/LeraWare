package neva.ware.injection.accesors;

import net.minecraft.class_1268;
import net.minecraft.class_2885;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2885.class})
public interface IPlayerInteractBlockC2SPacket {
    @Mutable
    @Accessor("field_13134")
    void setHand(class_1268 var1);
}
