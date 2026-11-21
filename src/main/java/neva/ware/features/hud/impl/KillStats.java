package neva.ware.features.hud.impl;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2663;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.features.modules.combat.AttackAura;
import neva.ware.features.modules.combat.AutoCrystal;

public class KillStats extends HudElement {
    int death = 0;
    int killstreak = 0;
    int kills = 0;
    private final NwPanel panel = new NwPanel();

    public KillStats() {
        super("KillStats", 100, 35);
    }

    @Override
    public void onDisable() {
        this.death = 0;
        this.kills = 0;
        this.killstreak = 0;
    }

    @EventHandler
    private void death(PacketEvent.Receive var1) {
        if (var1.getPacket() instanceof class_2663 var3 && var3.method_11470() == 3) {
            class_1297 var4 = var3.method_11469((class_1937)mc.field_1687);
            if (!(var4 instanceof class_1657)) {
                return;
            }

            if (var4 == mc.field_1724) {
                this.death++;
                this.killstreak = 0;
            } else if (AttackAura.target == var4 || AutoCrystal.target == var4) {
                this.killstreak++;
                this.kills++;
            }
        }
    }

    @Override
    public void onRender2D(class_332 var1) {
        super.onRender2D(var1);
        class_4587 var2 = var1.method_51448();
        String var3 = String.format("%.2f", this.death == 0 ? this.kills : (float)this.kills / this.death);
        String[][] var4 = new String[][]{
            {"Kills", String.valueOf(this.kills)}, {"Deaths", String.valueOf(this.death)}, {"Streak", String.valueOf(this.killstreak)}, {"K/D", var3}
        };
        if (this.panel.begin(this, var2, NwHud.Icon.SWORD, "Statistics", null, 100.0F, var4.length * 12.0F, true)) {
            float var5 = this.panel.rowsY;

            for (String[] var9 : var4) {
                float var10 = var5 + 6.0F;
                NwHud.text(var2, var9[0], this.panel.x + 6.0F, var10, NwHud.textColor2());
                NwHud.text(var2, var9[1], this.panel.x + this.panel.w - 6.0F - NwHud.w(var9[1]), var10, NwHud.textColor());
                var5 += 12.0F;
            }

            this.panel.end();
        }
    }
}
