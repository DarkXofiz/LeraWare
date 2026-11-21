package neva.ware.gui.clickui;

import net.minecraft.class_4587;
import neva.ware.gui.font.FontRenderer;
import neva.ware.setting.Setting;

public final class NwEl {
    public static final float PAD = 5.0F;

    private NwEl() {
    }

    public static int depth(Setting<?> var0) {
        int var1 = 0;

        for (Setting var2 = var0 == null ? null : var0.group; var2 != null && var1 < 6; var2 = var2.group) {
            var1++;
        }

        return var1;
    }

    public static float indent(Setting<?> var0) {
        return depth(var0) * 6.0F;
    }

    public static void guides(class_4587 var0, float var1, float var2, float var3, Setting<?> var4) {
        int var5 = depth(var4);

        for (int var6 = 0; var6 < var5; var6++) {
            NwDraw.rect(var0, var1 + 5.0F + 1.5F + var6 * 6.0F, var2, 0.7F, var3, 654311423);
        }
    }

    public static void hoverBg(class_4587 var0, float var1, float var2, float var3, float var4, float var5) {
        if (var5 > 0.01F) {
            NwDraw.round(var0, var1, var2, var3, var4, 3.5F, NwDraw.withAlpha(-1, Math.round(11.0F * var5)));
        }
    }

    public static float textY(float var0) {
        return NwDraw.textTop(var0, NwDraw.settingSize());
    }

    public static void label(class_4587 var0, String var1, float var2, float var3, float var4, float var5) {
        FontRenderer var6 = NwDraw.settingFont();
        NwDraw.text(var6, var0, NwDraw.trim(var6, var1, var4), var2, textY(var3), NwDraw.mix(-3946796, -1, var5));
    }

    public static void toggle(class_4587 var0, float var1, float var2, float var3, float var4) {
        int var5 = NwDraw.accent(0);
        int var6 = NwDraw.accent(180);
        int var7 = NwDraw.mix(788529151, 1023410175, var4);
        int var8 = NwDraw.mix(var7, NwDraw.withAlpha(var5, 255), var3);
        int var9 = NwDraw.mix(var7, NwDraw.withAlpha(var6, 255), var3);
        if (var3 > 0.05F) {
            NwDraw.shadow(var0, var1, var2, 15.0F, 8.0F, 4.0F, 4.0F, NwDraw.withAlpha(var5, Math.round(45.0F * var3)));
        }

        NwDraw.round(var0, var1, var2, 15.0F, 8.0F, 4.0F, var8, var9);
        float var10 = var1 + 4.0F + 7.0F * var3;
        NwDraw.circle(var0, var10, var2 + 4.0F, 2.9F, NwDraw.mix(-2565404, -1, var3));
    }

    public static float chip(class_4587 var0, String var1, float var2, float var3, int var4, int var5) {
        FontRenderer var6 = NwDraw.settingFont();
        float var7 = NwDraw.width(var6, var1) + 8.0F;
        float var8 = 10.0F;
        NwDraw.round(var0, var2 - var7, var3 - var8 / 2.0F, var7, var8, 3.5F, var4);
        NwDraw.text(var6, var0, var1, var2 - var7 + 4.0F, textY(var3), var5);
        return var7;
    }

    public static void hand() {
        ClickGUI.anyHovered = true;
    }
}
