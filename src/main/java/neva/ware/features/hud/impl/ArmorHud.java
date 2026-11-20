package neva.ware.features.hud.impl;

import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.gui.clickui.NwDraw;

public class ArmorHud extends HudElement {
    private final NwHud.Anim[] dur = new NwHud.Anim[]{new NwHud.Anim(), new NwHud.Anim(), new NwHud.Anim(), new NwHud.Anim()};

    public ArmorHud() {
        super("ArmorHud", 60, 25);
    }

    @Override
    public void onRender2D(class_332 var1) {
        super.onRender2D(var1);
        class_746 var2 = mc.field_1724;
        if (var2 != null) {
            class_4587 var3 = var1.method_51448();
            float var4 = this.getPosX();
            float var5 = this.getPosY();
            float var6 = 20.0F;
            float var7 = 4.0F;
            float var8 = var7 * 2.0F + var6 * 4.0F + 9.0F;
            float var9 = 30.0F;
            NwHud.plate(var3, var4, var5, var8, var9);
            List<class_1799> var10 = var2.method_31548().field_7548.reversed();

            for (int var11 = 0; var11 < 4 && var11 < var10.size(); var11++) {
                class_1799 var12 = (class_1799)var10.get(var11);
                float var13 = var4 + var7 + var11 * (var6 + 3.0F);
                NwDraw.round(var3, var13, var5 + 3.0F, var6, var6, Math.min(3.0F, NwHud.radius()), 570425344);
                if (var12 != null && !var12.method_7960()) {
                    var1.method_51427(var12, (int)(var13 + 2.0F), (int)(var5 + 5.0F));
                    float var14 = 1.0F;
                    int var15 = var12.method_7936();
                    if (var15 > 0) {
                        var14 = 1.0F - (float)var12.method_7919() / var15;
                    }

                    float var16 = this.dur[var11].to(var14, 10.0F);
                    int var17 = NwDraw.mix(-42406, -10682486, var16);
                    float var18 = var6 - 4.0F;
                    NwDraw.round(var3, var13 + 2.0F, var5 + var9 - 5.0F, var18, 2.0F, 1.0F, 822083583);
                    NwDraw.round(var3, var13 + 2.0F, var5 + var9 - 5.0F, Math.max(2.0F, var18 * var16), 2.0F, 1.0F, var17);
                } else {
                    NwHud.icon(var3, NwHud.Icon.SHIELD, var13 + var6 / 2.0F, var5 + 3.0F + var6 / 2.0F, 8.0F, 822083583);
                    this.dur[var11].to(0.0F, 10.0F);
                }
            }

            this.setBounds(var4, var5, var8, var9);
        }
    }
}
