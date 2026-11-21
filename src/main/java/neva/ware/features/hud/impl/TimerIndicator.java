package neva.ware.features.hud.impl;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.features.modules.movement.Timer;

public class TimerIndicator extends HudElement {
    private final NwHud.Anim energy = new NwHud.Anim();

    public TimerIndicator() {
        super("TimerIndicator", 60, 10);
    }

    @Override
    public void onRender2D(class_332 var1) {
        super.onRender2D(var1);
        class_4587 var2 = var1.method_51448();
        float var3 = this.getPosX();
        float var4 = this.getPosY();
        float var5 = this.energy.to(Math.max(0.0F, Math.min(1.0F, Timer.energy)), 12.0F);
        String var6 = Timer.energy >= 0.99F ? "100%" : Math.round(Timer.energy * 100.0F) + "%";
        float var7 = 19.0F + NwHud.w("Timer") + 8.0F + 50.0F + 6.0F + NwHud.ws("100%") + 6.0F;
        NwHud.plate(var2, var3, var4, var7, 15.0F);
        float var8 = var4 + 7.5F;
        NwHud.icon(var2, NwHud.Icon.TIMER, var3 + 10.5F, var8, 8.0F, NwHud.accent(0));
        NwHud.text(var2, "Timer", var3 + 19.0F, var8, NwHud.textColor());
        float var9 = var3 + 19.0F + NwHud.w("Timer") + 8.0F;
        NwHud.bar(var2, var9, var8 - 1.5F, 50.0F, 3.0F, var5);
        NwHud.textSmall(var2, var6, var9 + 56.0F, var8, NwHud.textColor2());
        this.setBounds(var3, var4, var7, 15.0F);
    }

    @Override
    public void onUpdate() {
    }
}
