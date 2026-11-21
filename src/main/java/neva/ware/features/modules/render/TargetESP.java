package neva.ware.features.modules.render;

import java.awt.Color;
import net.minecraft.class_1297;
import net.minecraft.class_4587;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.GhostEsp;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render3DEngine;
import neva.ware.utility.render.animation.CaptureMark;

public class TargetESP extends Module {
    public static TargetESP instance;
    public final Setting<TargetESP.Mode> mode = new Setting<>("Mode", TargetESP.Mode.NevaWare);
    public final Setting<Integer> length = new Setting<>("ESP Length", 25, 1, 50, var1x -> this.mode.is(TargetESP.Mode.NevaWare));
    public final Setting<Float> scale = new Setting<>("ESP Scale", 0.27F, 0.1F, 0.5F, var1x -> this.mode.is(TargetESP.Mode.NevaWare));
    public final Setting<Float> size = new Setting<>("ESPSize", 1.0F, 0.3F, 3.0F, var1x -> this.mode.is(TargetESP.Mode.NevaWare));
    public final Setting<Float> speed = new Setting<>("ESPSpeed", 1.0F, 0.1F, 3.0F, var1x -> this.mode.is(TargetESP.Mode.NevaWare));
    public final Setting<Float> glow = new Setting<>("ESPGlow", 0.5F, 0.0F, 2.0F, var1x -> this.mode.is(TargetESP.Mode.NevaWare));
    public final Setting<TargetESP.Sprite> sprite = new Setting<>("Sprite", TargetESP.Sprite.Glow, var1x -> this.spr());
    public final Setting<TargetESP.ColorMode> colorMode = new Setting<>("Color Mode", TargetESP.ColorMode.Client, var1x -> this.fx());
    public final Setting<ColorSetting> color = new Setting<>(
        "Color",
        new ColorSetting(new Color(-8626945, true)),
        var1x -> this.fx() && (this.colorMode.is(TargetESP.ColorMode.Custom) || this.colorMode.is(TargetESP.ColorMode.Gradient))
    );
    public final Setting<ColorSetting> color2 = new Setting<>(
        "Color 2", new ColorSetting(new Color(-13047553, true)), var1x -> this.fx() && this.colorMode.is(TargetESP.ColorMode.Gradient)
    );
    public final Setting<Float> particleSize = new Setting<>("Particle Size", 0.42F, 0.1F, 1.5F, var1x -> this.spr());
    public final Setting<Float> fxSpeed = new Setting<>("FX Speed", 1.0F, 0.1F, 4.0F, var1x -> this.fx());
    public final Setting<Float> radius = new Setting<>("Radius", 0.9F, 0.3F, 2.5F, var1x -> this.fx());
    public final Setting<Integer> count = new Setting<>("Count", 3, 1, 12, var1x -> this.fx());
    public final Setting<Integer> trail = new Setting<>("Trail Length", 36, 2, 90, var1x -> this.spr());
    public final Setting<Float> fade = new Setting<>("Trail Fade", 1.3F, 0.5F, 3.0F, var1x -> this.spr());
    public final Setting<Float> height = new Setting<>("Height", 0.0F, -1.0F, 1.0F, var1x -> this.fx());
    public final Setting<Float> tilt = new Setting<>(
        "Tilt",
        20.0F,
        0.0F,
        90.0F,
        var1x -> this.mode.is(TargetESP.Mode.Orbit)
            || this.mode.is(TargetESP.Mode.Halo)
            || this.mode.is(TargetESP.Mode.Diamond)
            || this.mode.is(TargetESP.Mode.Sphere)
            || this.mode.is(TargetESP.Mode.Gyro)
            || this.mode.is(TargetESP.Mode.Dome)
    );
    public final Setting<Float> alpha = new Setting<>("Alpha", 1.0F, 0.1F, 1.0F, var1x -> this.fx());
    public final Setting<Float> bloom = new Setting<>("Bloom", 1.0F, 0.0F, 2.0F, var1x -> this.fx());
    public final Setting<Boolean> pulse = new Setting<>("Pulse", Boolean.TRUE, var1x -> this.fx());
    public final Setting<Boolean> depthTest = new Setting<>("Depth Test", Boolean.TRUE, var1x -> this.fx());
    public final Setting<Float> thickness = new Setting<>("Thickness", 0.022F, 0.005F, 0.08F, var1x -> this.geo());
    public final Setting<Float> fill = new Setting<>("Fill", 0.6F, 0.0F, 1.0F, var1x -> this.geo());
    public final Setting<Integer> segments = new Setting<>("Segments", 64, 16, 128, var1x -> this.geo());

    public TargetESP() {
        super("TargetESP", Module.Category.RENDER);
        instance = this;

        try {
            Setting var1 = this.getSettingByName("Enabled");
            if (var1 != null) {
                var1.setValueSilent(Boolean.TRUE);
            }
        } catch (Throwable var3) {
        }
    }

    private boolean spr() {
        TargetESP.Mode var1 = this.mode.getValue();
        return var1 == TargetESP.Mode.Orbit
            || var1 == TargetESP.Mode.Helix
            || var1 == TargetESP.Mode.Halo
            || var1 == TargetESP.Mode.Diamond
            || var1 == TargetESP.Mode.Sphere
            || var1 == TargetESP.Mode.Tornado;
    }

    private boolean geo() {
        TargetESP.Mode var1 = this.mode.getValue();
        return var1 == TargetESP.Mode.Reticle
            || var1 == TargetESP.Mode.Magic
            || var1 == TargetESP.Mode.Scanner
            || var1 == TargetESP.Mode.Gyro
            || var1 == TargetESP.Mode.Dome
            || var1 == TargetESP.Mode.Pillar
            || var1 == TargetESP.Mode.Marker
            || var1 == TargetESP.Mode.Cube;
    }

    private boolean fx() {
        return this.spr() || this.geo();
    }

    public static void render(class_1297 var0, class_4587 var1) {
        TargetESP var2 = instance;
        if (var2 != null && var0 != null && var2.isEnabled()) {
            TargetESP.Mode var3 = var2.mode.getValue();
            if (var3 == TargetESP.Mode.NevaWare) {
                Render3DEngine.renderGhostsNevaFx(
                    var2.length.getValue(), var2.scale.getValue(), var0, var2.size.getValue(), var2.speed.getValue(), var2.glow.getValue()
                );
            } else if (var3 == TargetESP.Mode.NurikZapen) {
                CaptureMark.render(var0);
            } else if (var3 == TargetESP.Mode.CelkaPasta) {
                Render3DEngine.drawOldTargetEsp(var1, var0);
            } else if (var3 == TargetESP.Mode.Ghost) {
                GhostEsp.render(var0, var1, Render3DEngine.getTickDelta());
            } else if (var2.geo()) {
                TargetEspGeo.render(var2, var0, var1, Render3DEngine.getTickDelta());
            } else {
                TargetEspFx.render(var2, var0, var1, Render3DEngine.getTickDelta());
            }
        }
    }

    public static enum ColorMode {
        Client,
        Rainbow,
        Custom,
        Gradient;
    }

    public static enum Mode {
        NevaWare,
        NurikZapen,
        CelkaPasta,
        Ghost,
        Orbit,
        Helix,
        Halo,
        Diamond,
        Sphere,
        Tornado,
        Reticle,
        Magic,
        Scanner,
        Gyro,
        Dome,
        Pillar,
        Marker,
        Cube;
    }

    public static enum Sprite {
        Glow,
        Star,
        Circle,
        Snowflake,
        Firefly,
        Heart,
        Crystal;
    }
}
