package neva.ware.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_1799;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import net.minecraft.class_811;
import net.minecraft.class_4597.class_4598;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class TotemAnimation extends Module {
    private final Setting<TotemAnimation.Mode> mode = new Setting<>("Mode", TotemAnimation.Mode.FadeOut);
    private final Setting<Integer> speed = new Setting<>("Speed", 40, 1, 100);
    private class_1799 floatingItem = null;
    private int floatingItemTimeLeft;

    public TotemAnimation() {
        super("TotemAnimation", Module.Category.RENDER);
    }

    public void showFloatingItem(class_1799 floatingItem) {
        this.floatingItem = floatingItem;
        this.floatingItemTimeLeft = this.getTime();
    }

    @Override
    public void onUpdate() {
        if (this.floatingItemTimeLeft > 0) {
            this.floatingItemTimeLeft--;
            if (this.floatingItemTimeLeft == 0) {
                this.floatingItem = null;
            }
        }
    }

    public void renderFloatingItem(float tickDelta) {
        if (this.floatingItem != null && this.floatingItemTimeLeft > 0 && !this.mode.is(TotemAnimation.Mode.Off)) {
            int scaledWidth = mc.method_22683().method_4486();
            int scaledHeight = mc.method_22683().method_4502();
            int elapsedTime = this.getTime() - this.floatingItemTimeLeft;
            float animationProgress = (elapsedTime + tickDelta) / this.getTime();
            float progressSquared = animationProgress * animationProgress;
            float progressCubed = animationProgress * progressSquared;
            float oscillationFactor = 10.25F * progressCubed * progressSquared
                - 24.95F * progressSquared * progressSquared
                + 25.5F * progressCubed
                - 13.8F * progressSquared
                + 4.0F * animationProgress;
            float oscillationRadians = oscillationFactor * (float) Math.PI;
            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            class_4587 matrixStack = new class_4587();
            matrixStack.method_22903();
            float adjustedProgress = elapsedTime + tickDelta;
            float scale = 50.0F + 175.0F * class_3532.method_15374(oscillationRadians);
            switch ((TotemAnimation.Mode)this.mode.getValue()) {
                case FadeOut:
                    float x2 = (float)(Math.sin(adjustedProgress * 112.0F / 180.0F) * 100.0);
                    float y2 = (float)(Math.cos(adjustedProgress * 112.0F / 180.0F) * 50.0);
                    matrixStack.method_46416(scaledWidth / 2 + x2, scaledHeight / 2 + y2, -50.0F);
                    matrixStack.method_22905(scale, -scale, scale);
                    break;
                case Size:
                    matrixStack.method_46416(scaledWidth / 2, scaledHeight / 2, -50.0F);
                    matrixStack.method_22905(scale, -scale, scale);
                    break;
                case Otkisuli:
                    matrixStack.method_46416(scaledWidth / 2, scaledHeight / 2, -50.0F);
                    matrixStack.method_22907(class_7833.field_40714.rotationDegrees(adjustedProgress * 2.0F));
                    matrixStack.method_22907(class_7833.field_40718.rotationDegrees(adjustedProgress * 2.0F));
                    matrixStack.method_22905(200.0F - adjustedProgress * 1.5F, -200.0F + adjustedProgress * 1.5F, 200.0F - adjustedProgress * 1.5F);
                    break;
                case Insert:
                    matrixStack.method_46416(scaledWidth / 2, scaledHeight / 2, -50.0F);
                    matrixStack.method_22907(class_7833.field_40714.rotationDegrees(adjustedProgress * 3.0F));
                    matrixStack.method_22905(200.0F - adjustedProgress * 1.5F, -200.0F + adjustedProgress * 1.5F, 200.0F - adjustedProgress * 1.5F);
                    break;
                case Fall: {
                    float downFactor = (float)(Math.pow(adjustedProgress, 3.0) * 0.2F);
                    matrixStack.method_46416(scaledWidth / 2, scaledHeight / 2 + downFactor, -50.0F);
                    matrixStack.method_22907(class_7833.field_40718.rotationDegrees(adjustedProgress * 5.0F));
                    matrixStack.method_22905(200.0F - adjustedProgress * 1.5F, -200.0F + adjustedProgress * 1.5F, 200.0F - adjustedProgress * 1.5F);
                    break;
                }
                case Rocket: {
                    float downFactor = (float)(Math.pow(adjustedProgress, 3.0) * 0.2F) - 20.0F;
                    matrixStack.method_46416(scaledWidth / 2, scaledHeight / 2 - downFactor, -50.0F);
                    matrixStack.method_22907(class_7833.field_40716.rotationDegrees(adjustedProgress * this.floatingItemTimeLeft * 2.0F));
                    matrixStack.method_22905(200.0F - adjustedProgress * 1.5F, -200.0F + adjustedProgress * 1.5F, 200.0F - adjustedProgress * 1.5F);
                    break;
                }
                case Roll:
                    float rightFactor = (float)(Math.pow(adjustedProgress, 2.0) * 4.5);
                    matrixStack.method_46416(scaledWidth / 2 + rightFactor, scaledHeight / 2, -50.0F);
                    matrixStack.method_22907(class_7833.field_40718.rotationDegrees(adjustedProgress * 40.0F));
                    matrixStack.method_22905(200.0F - adjustedProgress * 1.5F, -200.0F + adjustedProgress * 1.5F, 200.0F - adjustedProgress * 1.5F);
            }

            class_4598 immediate = mc.method_22940().method_23000();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F - animationProgress);
            mc.method_1480().method_23178(this.floatingItem, class_811.field_4319, 15728880, class_4608.field_21444, matrixStack, immediate, mc.field_1687, 0);
            matrixStack.method_22909();
            immediate.method_22993();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();
        }
    }

    private int getTime() {
        int invertedSpeed = 101 - this.speed.getValue();
        if (this.mode.is(TotemAnimation.Mode.FadeOut)) {
            return invertedSpeed / 4;
        } else {
            return this.mode.is(TotemAnimation.Mode.Insert) ? invertedSpeed / 2 : invertedSpeed;
        }
    }

    private static enum Mode {
        FadeOut,
        Size,
        Otkisuli,
        Insert,
        Fall,
        Rocket,
        Roll,
        Off;
    }
}
