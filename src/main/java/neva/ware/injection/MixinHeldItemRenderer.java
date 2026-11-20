package neva.ware.injection;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1806;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_759;
import net.minecraft.class_7833;
import neva.ware.NevaWare;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventHeldItemRenderer;
import neva.ware.features.modules.Module;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_759.class})
public abstract class MixinHeldItemRenderer {
    @Inject(
        method = {"Lnet/minecraft/class_759;method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"},
        at = {@At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_759;method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;ZLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"
        )}
    )
    private void onRenderItem(
        class_742 player,
        float tickDelta,
        float pitch,
        class_1268 hand,
        float swingProgress,
        class_1799 item,
        float equipProgress,
        class_4587 matrices,
        class_4597 vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        if (!Module.fullNullCheck()) {
            EventHeldItemRenderer event = new EventHeldItemRenderer(hand, item, equipProgress, matrices);
            NevaWare.EVENT_BUS.post(event);
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_759;method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"},
        at = {@At("RETURN")}
    )
    private void onRenderItemPost(
        class_742 player,
        float tickDelta,
        float pitch,
        class_1268 hand,
        float swingProgress,
        class_1799 item,
        float equipProgress,
        class_4587 matrices,
        class_4597 vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        if (ModuleManager.chams.isEnabled() && ModuleManager.chams.handItems.getValue()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Inject(
        method = {"Lnet/minecraft/class_759;method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void onRenderItemHook(
        class_742 player,
        float tickDelta,
        float pitch,
        class_1268 hand,
        float swingProgress,
        class_1799 item,
        float equipProgress,
        class_4587 matrices,
        class_4597 vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        if (Managers.MODULE != null && ModuleManager.animations.shouldAnimate() && !item.method_7960() && !(item.method_7909() instanceof class_1806)) {
            ci.cancel();
            ModuleManager.animations
                .renderFirstPersonItemCustom(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
        }
    }

    private void applyEatOrDrinkTransformationCustom(class_4587 matrices, float tickDelta, class_1306 arm, @NotNull class_1799 stack) {
        float f = Module.mc.field_1724.method_6014() - tickDelta + 1.0F;
        float g = f / stack.method_7935(Module.mc.field_1724);
        if (g < 0.8F) {
            float h = class_3532.method_15379(class_3532.method_15362(f / 4.0F * (float) Math.PI) * 0.005F);
            matrices.method_46416(0.0F, h, 0.0F);
        }

        float h = 1.0F - (float)Math.pow(g, 27.0);
        int i = arm == class_1306.field_6183 ? 1 : -1;
        matrices.method_46416(h * 0.6F * i * ModuleManager.viewModel.eatX.getValue(), h * -0.5F * ModuleManager.viewModel.eatY.getValue(), h * 0.0F);
        matrices.method_22907(class_7833.field_40716.rotationDegrees(i * h * 90.0F));
        matrices.method_22907(class_7833.field_40714.rotationDegrees(h * 10.0F));
        matrices.method_22907(class_7833.field_40718.rotationDegrees(i * h * 30.0F));
    }

    @Inject(
        method = {"Lnet/minecraft/class_759;method_3218(Lnet/minecraft/class_4587;FLnet/minecraft/class_1306;Lnet/minecraft/class_1799;Lnet/minecraft/class_1657;)V"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void applyEatOrDrinkTransformationHook(class_4587 matrices, float tickDelta, class_1306 arm, class_1799 stack, class_1657 player, CallbackInfo ci) {
        if (ModuleManager.animations.isEnabled()) {
            this.applyEatOrDrinkTransformationCustom(matrices, tickDelta, arm, stack);
            ci.cancel();
        }
    }

    @ModifyArgs(
        method = {"Lnet/minecraft/class_759;method_22976(FLnet/minecraft/class_4587;Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_746;I)V"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_759;method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"
        )
    )
    private void renderItem(Args args) {
        if (ModuleManager.noRender.noSwing.getValue()) {
            args.set(6, 0.0F);
        }
    }
}
