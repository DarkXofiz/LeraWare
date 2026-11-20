package neva.ware.features.hud.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.gui.clickui.NwDraw;

public class PotionHud extends HudElement {
    private final NwPanel panel = new NwPanel();

    public PotionHud() {
        super("Potions", 100, 100);
    }

    public static String getDuration(class_1293 var0) {
        if (var0.method_48559()) {
            return "*:*";
        } else {
            int var1 = var0.method_5584() / 20;
            return var1 / 60 + ":" + String.format("%02d", var1 % 60);
        }
    }

    @Override
    public void onRender2D(class_332 var1) {
        super.onRender2D(var1);
        class_4587 var2 = var1.method_51448();
        class_746 var3 = mc.field_1724;
        ArrayList<class_1293> var4 = new ArrayList<>();
        if (var3 != null) {
            Collection<class_1293> var5 = var3.method_6026();
            if (var5 != null) {
                var4.addAll(var5);
            }
        }

        float var22 = 0.0F;
        ArrayList<String> var6 = new ArrayList<>();

        for (class_1293 var8 : var4) {
            String var9;
            try {
                var9 = ((class_1291)var8.method_5579().comp_349()).method_5560().getString();
            } catch (Throwable var21) {
                var9 = "Effect";
            }

            if (var8.method_5578() > 0) {
                var9 = var9 + " " + (var8.method_5578() + 1);
            }

            var6.add(var9);
            var22 = Math.max(var22, 17.0F + NwHud.w(var9) + 10.0F + NwHud.ws(getDuration(var8)) + 7.0F + 6.0F);
        }

        boolean var23 = NwHud.editing();
        float var24 = var4.isEmpty() ? (var23 ? 12.0F : 0.0F) : var4.size() * 12.0F;
        if (this.panel
            .begin(this, var2, NwHud.Icon.POTION, "Active potions", var4.isEmpty() ? null : String.valueOf(var4.size()), var22, var24, !var4.isEmpty() || var23)
            )
         {
            float var25 = this.panel.rowsY;
            HashSet var10 = new HashSet();
            if (var4.isEmpty()) {
                NwHud.textSmall(var2, "No effects", this.panel.x + 6.0F, var25 + 6.0F, NwHud.textColor2());
            }

            for (int var11 = 0; var11 < var4.size(); var11++) {
                class_1293 var12 = (class_1293)var4.get(var11);
                String var13 = (String)var6.get(var11);
                var10.add(var13);
                float var14 = this.panel.row(var13);
                float var15 = var25 + 6.0F;
                float var16 = NwDraw.alpha;
                NwDraw.alpha = var16 * var14;
                float var17 = this.panel.x + 6.0F - (1.0F - var14) * 6.0F;

                try {
                    var1.method_25298((int)var17, (int)(var15 - 4.5F), 0, 9, 9, mc.method_18505().method_18663(var12.method_5579()));
                } catch (Throwable var20) {
                    NwHud.icon(var2, NwHud.Icon.POTION, var17 + 4.5F, var15, 7.0F, NwHud.accent(var11 * 40));
                }

                NwHud.text(var2, var13, var17 + 12.0F, var15, NwHud.textColor());
                int var18 = var12.method_48559() ? Integer.MAX_VALUE : var12.method_5584();
                int var19 = var18 < 200
                    ? NwDraw.mix(-38037, NwHud.textColor(), (float)(Math.sin(System.currentTimeMillis() / 160.0) * 0.5 + 0.5))
                    : NwHud.textColor();
                NwHud.chip(var2, getDuration(var12), this.panel.x + this.panel.w - 6.0F, var15, var19);
                NwDraw.alpha = var16;
                var25 += 12.0F;
            }

            this.panel.forget(var10);
            this.panel.end();
        }
    }
}
