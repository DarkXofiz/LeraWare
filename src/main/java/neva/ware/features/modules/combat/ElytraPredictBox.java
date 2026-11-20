package neva.ware.features.modules.combat;

import net.minecraft.class_238;
import net.minecraft.class_243;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.utility.render.Render3DEngine;

public final class ElytraPredictBox {
    private static class_243 lastRef;
    private static double px;
    private static double py;
    private static double pz;
    private static double cx;
    private static double cy;
    private static double cz;
    private static double rx;
    private static double ry;
    private static double rz;
    private static boolean has;
    private static long lastNs;

    private ElytraPredictBox() {
    }

    public static void draw() {
        class_243 var0 = ElytraTargetBox.pos();
        if (var0 == null) {
            has = false;
            lastRef = null;
        } else {
            long var1 = System.nanoTime();
            double var3 = lastNs == 0L ? 0.016 : Math.min(0.1, (var1 - lastNs) / 1.0E9);
            lastNs = var1;
            if (!has) {
                px = cx = rx = var0.field_1352;
                py = cy = ry = var0.field_1351;
                pz = cz = rz = var0.field_1350;
                lastRef = var0;
                has = true;
            } else if (var0 != lastRef) {
                px = cx;
                py = cy;
                pz = cz;
                cx = var0.field_1352;
                cy = var0.field_1351;
                cz = var0.field_1350;
                lastRef = var0;
                double var5 = cx - rx;
                double var7 = cy - ry;
                double var9 = cz - rz;
                if (var5 * var5 + var7 * var7 + var9 * var9 > 64.0) {
                    px = rx = cx;
                    py = ry = cy;
                    pz = rz = cz;
                }
            }

            float var19 = Render3DEngine.getTickDelta();
            if (var19 < 0.0F) {
                var19 = 0.0F;
            }

            if (var19 > 1.0F) {
                var19 = 1.0F;
            }

            double var6 = px + (cx - px) * var19;
            double var8 = py + (cy - py) * var19;
            double var10 = pz + (cz - pz) * var19;
            double var12 = 1.0 - Math.exp(-var3 * 16.0);
            rx = rx + (var6 - rx) * var12;
            ry = ry + (var8 - ry) * var12;
            rz = rz + (var10 - rz) * var12;
            double var14 = 0.3;
            double var16 = 0.6;
            class_238 var18 = new class_238(rx - var14, ry, rz - var14, rx + var14, ry + var16, rz + var14);
            Render3DEngine.drawBoxOutline(var18, HudEditor.getColor(1), 2.0F);
        }
    }
}
