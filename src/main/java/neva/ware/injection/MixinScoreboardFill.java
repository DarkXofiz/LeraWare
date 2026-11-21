package neva.ware.injection;

import net.minecraft.class_332;
import neva.ware.features.modules.render.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_332.class})
public class MixinScoreboardFill {
    @Inject(
        method = {"method_25294(IIIII)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void nevaware$scoreboardFill(int var1, int var2, int var3, int var4, int var5, CallbackInfo var6) {
        if (Scoreboard.renderingSidebar && Scoreboard.INSTANCE != null && Scoreboard.INSTANCE.transparent.getValue()) {
            var6.cancel();
        }
    }
}
