package neva.ware.features.hud.impl;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.gui.clickui.NwDraw;

public class PVPResources extends HudElement {
    public PVPResources() {
        super("PVPResources", 50, 50);
    }

    public int getItemCount(class_1792 var1) {
        if (mc.field_1724 == null) {
            return 0;
        } else {
            int var2 = 0;

            for (int var3 = 0; var3 <= 44; var3++) {
                class_1799 var4 = mc.field_1724.method_31548().method_5438(var3);
                if (var4.method_7909() == var1) {
                    var2 += var4.method_7947();
                }
            }

            return var2;
        }
    }

    @Override
    public void onRender2D(class_332 var1) {
        super.onRender2D(var1);
        class_4587 var2 = var1.method_51448();
        float var3 = this.getPosX();
        float var4 = this.getPosY();
        class_1792[] var5 = new class_1792[]{class_1802.field_8288, class_1802.field_8367, class_1802.field_8287, class_1802.field_8301};
        float var6 = 22.0F;
        float var7 = 3.0F;
        float var8 = 4.0F;
        float var9 = var8 * 2.0F + var6 * 2.0F + var7;
        NwHud.plate(var2, var3, var4, var9, var9);

        for (int var11 = 0; var11 < 4; var11++) {
            float var12 = var3 + var8 + var11 % 2 * (var6 + var7);
            float var13 = var4 + var8 + var11 / 2 * (var6 + var7);
            NwDraw.round(var2, var12, var13, var6, var6, Math.min(3.0F, NwHud.radius()), 570425344);
            int var14 = this.getItemCount(var5[var11]);
            float var15 = NwDraw.alpha;
            var1.method_51427(new class_1799((class_1935)var5[var11], 1), (int)(var12 + 3.0F), (int)(var13 + 3.0F));
            String var16 = String.valueOf(var14);
            NwHud.textSmall(var2, var16, var12 + var6 - 2.0F - NwHud.ws(var16), var13 + var6 - 3.5F, var14 > 0 ? NwHud.textColor() : -38037);
            NwDraw.alpha = var15;
        }

        this.setBounds(var3, var4, var9, var9);
    }
}
