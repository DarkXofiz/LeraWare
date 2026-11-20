package neva.ware.injection.accesors;

import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_746.class})
public interface IClientPlayerEntity {
    @Invoker("method_3136")
    void iSendMovementPackets();

    @Accessor("field_3941")
    float getLastYaw();

    @Accessor("field_3925")
    float getLastPitch();

    @Accessor("field_3941")
    void setLastYaw(float var1);

    @Accessor("field_3925")
    void setLastPitch(float var1);

    @Accessor("field_3922")
    void setMountJumpStrength(float var1);
}
