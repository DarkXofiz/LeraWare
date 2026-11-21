package neva.ware.gui.windows;

import java.awt.Color;
import net.minecraft.class_4587;
import neva.ware.gui.clickui.NwDraw;

public final class NwWinStyle {
    private static float px;
    private static float py;
    private static float pw;
    private static float ph;
    private static final class_4587 IDENTITY = new class_4587();

    private NwWinStyle() {
    }

    private static boolean grey(Color var0) {
        return Math.abs(var0.getRed() - var0.getGreen()) < 8 && Math.abs(var0.getGreen() - var0.getBlue()) < 8;
    }

    private static int lum(Color var0) {
        return (var0.getRed() + var0.getGreen() + var0.getBlue()) / 3;
    }

    private static int fill(Color var0) {
        if (var0 == null) {
            return 0;
        } else if (!grey(var0)) {
            return var0.getRGB();
        } else {
            int var1 = lum(var0);
            if (var1 < 24) {
                return NwDraw.withAlpha(-16447989, Math.round(var0.getAlpha() * 0.55F));
            } else {
                int var2 = Math.round(8.0F + (var1 - 45) * 0.42F);
                var2 = Math.max(6, Math.min(64, var2));
                var2 = Math.round(var2 * Math.max(0.35F, var0.getAlpha() / 197.0F));
                return NwDraw.withAlpha(-1, var2);
            }
        }
    }

    private static int outline(Color var0) {
        if (var0 == null) {
            return 0;
        } else {
            return !grey(var0) ? NwDraw.withAlpha(var0.getRGB(), Math.min(200, var0.getAlpha())) : NwDraw.withAlpha(-1, lum(var0) > 100 ? 30 : 18);
        }
    }

    public static void drawHudBase(class_4587 var0, float var1, float var2, float var3, float var4, float var5, boolean var6) {
        px = var1;
        py = var2;
        pw = var3;
        ph = var4;
        float var7 = 8.0F;
        NwDraw.shadow(var0, var1, var2, var3, var4, var7, 14.0F, 2013265920);
        NwDraw.round(var0, var1, var2, var3, var4, var7, -266855897, -233828071);
        NwDraw.border(var0, var1, var2, var3, var4, var7, 0.6F, 318767103);
        NwDraw.round(var0, var1 + 0.6F, var2 + 0.6F, var3 - 1.2F, 18.0F, var7 - 0.6F, 268435455, 16777215);
    }

    public static void drawRect(class_4587 var0, float var1, float var2, float var3, float var4, Color var5) {
        if (var5 != null && !(var3 <= 0.0F) && !(var4 <= 0.0F)) {
            if (!(Math.abs(var2 - py) < 0.6F) || !(Math.abs(var4 - 16.0F) < 0.6F) || !(var3 > pw - 14.0F) || !(Math.abs(var1 - px) < 2.0F)) {
                if (var3 <= 6.5F && var4 > var3 * 1.5F && grey(var5)) {
                    NwDraw.round(var0, var1 + 1.5F, var2 + 1.0F, Math.max(1.5F, var3 - 3.0F), Math.max(2.0F, var4 - 2.0F), 1.5F, 1442840575);
                } else {
                    float var6 = Math.min(3.5F, Math.min(var3, var4) / 2.0F);
                    NwDraw.round(var0, var1, var2, var3, var4, var6, fill(var5));
                }
            }
        }
    }

    public static void drawRectWithOutline(class_4587 var0, float var1, float var2, float var3, float var4, Color var5, Color var6) {
        if (!(var3 <= 0.0F) && !(var4 <= 0.0F)) {
            if (var3 <= 6.5F && var4 > 20.0F) {
                NwDraw.round(var0, var1 + 1.5F, var2, Math.max(1.5F, var3 - 3.0F), var4, 1.5F, 318767103);
            } else {
                float var7 = Math.min(3.5F, Math.min(var3, var4) / 2.0F);
                NwDraw.round(var0, var1, var2, var3, var4, var7, fill(var5));
                NwDraw.border(var0, var1, var2, var3, var4, var7, 0.5F, outline(var6));
            }
        }
    }

    public static void horizontalGradient(class_4587 var0, float var1, float var2, float var3, float var4, Color var5, Color var6) {
        if (var5 != null && var6 != null) {
            float var7 = Math.min(var1, var3);
            float var8 = Math.min(var2, var4);
            float var9 = Math.abs(var3 - var1);
            float var10 = Math.abs(var4 - var2);
            if (!(var9 <= 0.0F)) {
                boolean var11 = var3 < var1;
                int var12;
                int var13;
                if (var10 <= 1.01F) {
                    int var14 = NwDraw.accent(0);
                    var12 = NwDraw.withAlpha(var14, Math.round(var5.getAlpha() * 0.6F));
                    var13 = NwDraw.withAlpha(var14, Math.round(var6.getAlpha() * 0.6F));
                    var10 = Math.max(0.6F, var10);
                } else {
                    var12 = var5.getRGB();
                    var13 = var6.getRGB();
                }

                NwDraw.hGradient(var0, var7, var8, var9, var10, var11 ? var13 : var12, var11 ? var12 : var13);
            }
        }
    }

    public static void drawLine(float var0, float var1, float var2, float var3, int var4) {
        NwDraw.line(IDENTITY, var0, var1, var2, var3, 0.9F, NwDraw.withAlpha(var4, Math.min(225, NwDraw.alphaOf(var4) == 0 ? 225 : NwDraw.alphaOf(var4))));
    }
}
