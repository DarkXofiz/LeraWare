package ru.donyka.chunkanimator.mixin;

import net.minecraft.class_2338;
import net.minecraft.class_846.class_851;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.donyka.chunkanimator.ChunkAnimator;

@Mixin({class_851.class})
public abstract class BuiltChunkMixin {
    @Inject(
        method = {"Lnet/minecraft/class_846$class_851;method_3653(III)V"},
        at = {@At("TAIL")}
    )
    private void chunkanimator$setOrigin(int x, int y, int z, CallbackInfo ci) {
        ChunkAnimator.animationHandler().setOrigin((class_851)(Object)this, new class_2338(x, y, z));
    }
}
