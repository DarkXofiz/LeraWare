package neva.ware.features.modules.render;

import java.awt.Color;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ShaderManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.injection.accesors.IGameRenderer;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render3DEngine;
import neva.ware.utility.render.shaders.satin.api.managed.ManagedShaderEffect;
import neva.ware.utility.render.shaders.satin.api.managed.ShaderEffectManager;
import org.joml.Matrix4f;

public class HandOutline extends Module {
    public final Setting<HandOutline.ColorMode> colorMode = new Setting<>("Color Mode", HandOutline.ColorMode.Sync);
    public final Setting<ColorSetting> color = new Setting<>(
        "Color", new ColorSetting(new Color(-6595329, true)), var1 -> this.colorMode.is(HandOutline.ColorMode.Custom)
    );
    public final Setting<Integer> thickness = new Setting<>("Thickness", 2, 1, 10);
    public final Setting<Integer> brightness = new Setting<>("Brightness", 60, 0, 100);
    public final Setting<Integer> opacity = new Setting<>("Opacity", 100, 5, 100);
    private static ManagedShaderEffect effect;
    private static ManagedShaderEffect original;

    public HandOutline() {
        super("HandOutline", Module.Category.RENDER);
    }

    private static ManagedShaderEffect effect() {
        if (effect == null) {
            effect = ShaderEffectManager.getInstance().manage(class_2960.method_60655("nevaware", "shaders/post/handoutline.json"));
        }

        return effect;
    }

    @Override
    public void onRender3D(class_4587 var1) {
        class_310 var2 = class_310.method_1551();
        if (var2.field_1724 != null && var2.field_1773 != null) {
            ManagedShaderEffect var3 = ShaderManager.DEFAULT;
            ManagedShaderEffect var4 = effect();
            if (var3 != null && var3 != var4) {
                original = var3;
                ShaderManager.DEFAULT = var4;
            }

            float var5 = 1.0F;
            if (this.colorMode.is(HandOutline.ColorMode.Sync)) {
                Color var6 = HudEditor.getColor(0);
                Color var7 = HudEditor.getColor(180);
                var4.setUniformValue("hoColor", var6.getRed() / 255.0F, var6.getGreen() / 255.0F, var6.getBlue() / 255.0F, 1.0F);
                var4.setUniformValue("hoColor2", var7.getRed() / 255.0F, var7.getGreen() / 255.0F, var7.getBlue() / 255.0F, 1.0F);
            } else {
                ColorSetting var8 = this.color.getValue();
                var4.setUniformValue("hoColor", var8.getGlRed(), var8.getGlGreen(), var8.getGlBlue(), 1.0F);
                var4.setUniformValue("hoColor2", var8.getGlRed(), var8.getGlGreen(), var8.getGlBlue(), 1.0F);
                var5 = var8.getGlAlpha();
            }

            var4.setUniformValue("hoThickness", (float)this.thickness.getValue().intValue());
            var4.setUniformValue("hoBright", this.brightness.getValue().intValue() / 100.0F);
            var4.setUniformValue("hoOpacity", this.opacity.getValue().intValue() / 100.0F * var5);
            Matrix4f var9 = var1.method_23760().method_23761();
            Managers.SHADER
                .renderShader(
                    () -> ((IGameRenderer)(Object)var2.field_1773).irenderHand(var2.field_1773.method_19418(), Render3DEngine.getTickDelta(), var9),
                    ShaderManager.Shader.Default
                );
        }
    }

    @Override
    public void onDisable() {
        if (original != null && ShaderManager.DEFAULT == effect) {
            ShaderManager.DEFAULT = original;
        }

        original = null;
    }

    public static enum ColorMode {
        Sync,
        Custom;
    }
}
