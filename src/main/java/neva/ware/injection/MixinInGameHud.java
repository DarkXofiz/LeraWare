package neva.ware.injection;

import net.minecraft.class_1297;
import net.minecraft.class_266;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import neva.ware.core.Managers;
import neva.ware.core.manager.IManager;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.hud.impl.Hotbar;
import neva.ware.features.modules.Module;
import neva.ware.gui.windows.WindowsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_329.class})
public abstract class MixinInGameHud {
    @Inject(
        at = {@At("HEAD")},
        method = {"Lnet/minecraft/class_329;method_1753(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"}
    )
    public void renderHook(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        if (!Module.fullNullCheck()) {
            Managers.MODULE.onRender2D(context);
            Managers.NOTIFICATION.onRender2D(context);
        }
    }

    @Inject(
        at = {@At("HEAD")},
        method = {"Lnet/minecraft/class_329;method_1760(Lnet/minecraft/class_332;)V"},
        cancellable = true
    )
    private void renderStatusBarsHook(class_332 context, CallbackInfo ci) {
        if (IManager.mc != null && IManager.mc.field_1755 instanceof WindowsScreen) {
            ci.cancel();
        }
    }

    @Inject(
        at = {@At("HEAD")},
        method = {"Lnet/minecraft/class_329;method_1759(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
        cancellable = true
    )
    public void renderHotbarCustom(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        if (IManager.mc != null && IManager.mc.field_1755 instanceof WindowsScreen) {
            ci.cancel();
        }

        if (ModuleManager.hotbar.isEnabled()) {
            ci.cancel();
            Hotbar.renderHotBarItems(tickCounter.method_60637(true), context);
        }
    }

    @Inject(
        at = {@At("HEAD")},
        method = {"Lnet/minecraft/class_329;method_1749(Lnet/minecraft/class_332;)V"},
        cancellable = true
    )
    public void renderHeldItemTooltipHook(class_332 context, CallbackInfo ci) {
        if (ModuleManager.noRender.isEnabled() && ModuleManager.noRender.hotbarItemName.getValue()) {
            ci.cancel();
        }
    }

    @Inject(
        at = {@At("HEAD")},
        method = {"Lnet/minecraft/class_329;method_1765(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
        cancellable = true
    )
    public void renderStatusEffectOverlayHook(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        if (ModuleManager.potionHud.isEnabled() || ModuleManager.legacyHud.isEnabled() && ModuleManager.legacyHud.potions.getValue()) {
            ci.cancel();
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_329;method_1754(Lnet/minecraft/class_332;I)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    public void renderXpBarCustom(class_332 context, int x, CallbackInfo ci) {
        if (IManager.mc != null && IManager.mc.field_1755 instanceof WindowsScreen) {
            ci.cancel();
        }

        if (ModuleManager.hotbar.isEnabled()) {
            ci.cancel();
            Hotbar.renderXpBar(x, context.method_51448());
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_329;method_1757(Lnet/minecraft/class_332;Lnet/minecraft/class_266;)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void renderScoreboardSidebarHook(class_332 context, class_266 objective, CallbackInfo ci) {
        if (ModuleManager.noRender.noScoreBoard.getValue() && ModuleManager.noRender.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_329;method_1735(Lnet/minecraft/class_332;Lnet/minecraft/class_1297;)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void renderVignetteOverlayHook(class_332 context, class_1297 entity, CallbackInfo ci) {
        if (ModuleManager.noRender.vignette.getValue()) {
            ci.cancel();
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_329;method_1746(Lnet/minecraft/class_332;F)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void renderPortalOverlayHook(class_332 context, float nauseaStrength, CallbackInfo ci) {
        if (ModuleManager.noRender.portal.getValue()) {
            ci.cancel();
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_329;method_1736(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    public void renderCrosshair(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        if (ModuleManager.crosshair.isEnabled()) {
            ci.cancel();
        }
    }
}
