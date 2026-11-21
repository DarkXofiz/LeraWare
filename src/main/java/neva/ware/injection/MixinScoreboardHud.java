package neva.ware.injection;

import net.minecraft.class_266;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_408;
import net.minecraft.class_4587;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.render.NoRender;
import neva.ware.features.modules.render.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_329.class})
public class MixinScoreboardHud {
    @Inject(
        method = {"method_1757(Lnet/minecraft/class_332;Lnet/minecraft/class_266;)V"},
        at = {@At("HEAD")}
    )
    private void nevaware$scoreboardHead(class_332 var1, class_266 var2, CallbackInfo var3) {
        Scoreboard.renderingSidebar = false;
        Scoreboard.pushedMatrix = false;
        if (Scoreboard.active()) {
            NoRender var4 = ModuleManager.noRender;
            if (var4 == null || !var4.isEnabled() || !var4.noScoreBoard.getValue()) {
                Scoreboard var5 = Scoreboard.INSTANCE;
                class_4587 var6 = var1.method_51448();
                var6.method_22903();
                Scoreboard.pushedMatrix = true;
                float var7 = var5.scale.getValue();
                int var8 = var1.method_51421();
                int var9 = var1.method_51443() / 2;
                var6.method_46416(var5.posX.getValue(), var5.posY.getValue(), 0.0F);
                var6.method_46416(var8, var9, 0.0F);
                var6.method_22905(var7, var7, 1.0F);
                var6.method_46416(-var8, -var9, 0.0F);
                if (Module.mc.field_1755 instanceof class_408 && var5.showHitbox.getValue()) {
                    short var10 = 150;
                    byte var11 = 120;
                    int var12 = var8 - var10;
                    int var13 = var9 - var11 / 2;
                    int var14 = -65536;
                    var1.method_25294(var12, var13, var12 + var10, var13 + 1, var14);
                    var1.method_25294(var12, var13 + var11 - 1, var12 + var10, var13 + var11, var14);
                    var1.method_25294(var12, var13 + 1, var12 + 1, var13 + var11 - 1, var14);
                    var1.method_25294(var12 + var10 - 1, var13 + 1, var12 + var10, var13 + var11 - 1, var14);
                    var1.method_25294(var12, var13, var12 + var10, var13 + var11, 872349696);
                }

                Scoreboard.renderingSidebar = true;
            }
        }
    }

    @Inject(
        method = {"method_1757(Lnet/minecraft/class_332;Lnet/minecraft/class_266;)V"},
        at = {@At("RETURN")}
    )
    private void nevaware$scoreboardReturn(class_332 var1, class_266 var2, CallbackInfo var3) {
        Scoreboard.renderingSidebar = false;
        if (Scoreboard.pushedMatrix) {
            Scoreboard.pushedMatrix = false;
            var1.method_51448().method_22909();
        }
    }
}
