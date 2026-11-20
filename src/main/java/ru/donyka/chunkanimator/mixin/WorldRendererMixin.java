package ru.donyka.chunkanimator.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_761;
import net.minecraft.class_846.class_851;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import ru.donyka.chunkanimator.AnimationOffset;
import ru.donyka.chunkanimator.ChunkAnimator;

@Mixin({class_761.class})
public abstract class WorldRendererMixin {
    @ModifyArgs(
        method = {"Lnet/minecraft/class_761;method_3251(Lnet/minecraft/class_1921;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_284;method_1249(FFF)V",
            ordinal = 0
        )
    )
    private void chunkanimator$offsetChunk(Args args, @Local class_851 builtChunk) {
        AnimationOffset offset = ChunkAnimator.animationHandler().offsetFor(builtChunk);
        if (!offset.isZero()) {
            args.set(0, (Float)args.get(0) + offset.x());
            args.set(1, (Float)args.get(1) + offset.y());
            args.set(2, (Float)args.get(2) + offset.z());
        }
    }
}
