package neva.ware.features.hud.impl;

import net.minecraft.class_332;
import neva.ware.core.Managers;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.setting.Setting;

public class TPSCounter extends HudElement {
    private final Setting<Boolean> extraTps = new Setting<>("ExtraTPS", true);
    private final NwHud.Anim anim = new NwHud.Anim();

    public TPSCounter() {
        super("TPS", 50, 10);
    }

    @Override
    public void onRender2D(class_332 var1) {
        super.onRender2D(var1);
        String var2 = String.format("%.1f", Managers.SERVER.getTPS());
        if (this.extraTps.getValue()) {
            var2 = var2 + " / " + String.format("%.1f", Managers.SERVER.getTPS2());
        }

        NwInfoPill.render(this, var1, this.anim, NwHud.Icon.TPS, var2, "Ticks");
    }
}
