package neva.ware.features.modules.combat;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.class_1297;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_7833;
import net.minecraft.class_293.class_5596;
import neva.ware.features.modules.client.HudEditor;
import org.joml.Matrix4f;

public final class GhostEsp {
    private static final class_2960 GLOW = class_2960.method_60655("nevaware", "textures/particles/ghost_glow.png");
    private static final long START_TIME = System.currentTimeMillis();
    private static final double RADIUS = 0.8;
    private static final float SPEED = 27.0F;
    private static final float SIZE = 0.42F;
    private static final double DISTANCE = 5.5;
    private static final int LENGTH = 52;

    private GhostEsp() {
    }

    public static void render(class_1297 var0, class_4587 var1, float var2) {
        if (var0 != null && var1 != null) {
            class_4184 var3 = class_310.method_1551().field_1773.method_19418();
            double var4 = var3.method_19326().field_1352;
            double var6 = var3.method_19326().field_1351;
            double var8 = var3.method_19326().field_1350;
            double var10 = var0.field_6014 + (var0.method_23317() - var0.field_6014) * var2 - var4;
            double var12 = var0.field_6036 + (var0.method_23318() - var0.field_6036) * var2 - var6;
            double var14 = var0.field_5969 + (var0.method_23321() - var0.field_5969) * var2 - var8;
            double var18 = var12 + 0.9;
            long var22 = System.currentTimeMillis();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.setShaderTexture(0, GLOW);
            RenderSystem.setShader(class_757::method_34543);
            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
            draw(var1, var3, var22, var10, var18, var14, 0.7F, 1.0F);
            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE);
            draw(var1, var3, var22, var10, var18, var14, 0.35F, 1.0F);
            draw(var1, var3, var22, var10, var18, var14, 0.1F, 2.4F);
            RenderSystem.blendFunc(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void draw(class_4587 var0, class_4184 var1, long var2, double var4, double var6, double var8, float var10, float var11) {
        class_287 var12 = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1575);

        for (int var13 = 0; var13 < 3; var13++) {
            for (int var14 = 0; var14 < 52; var14++) {
                double var15 = 0.15F * (var2 - START_TIME - var14 * 5.5) / 27.0 + var13 * (Math.PI * 2.0 / 3.0);
                double var17 = Math.cos(var15) * 0.8;
                double var19 = Math.sin(var15) * 0.8;
                double var21 = Math.sin(var15 * 1.0 + var13 * 2.1) * 0.55;
                float var23 = var14 / 52.0F;
                float var24 = (float)Math.pow(1.0 - var23, 1.0);
                int var25 = Math.max(0, Math.min(255, (int)(255.0F * var24 * var10)));
                if (var25 > 0) {
                    Color var26 = HudEditor.getColor(var14 * 5);
                    int var27 = var25 << 24 | var26.getRGB() & 16777215;
                    float var28 = 0.42F * (0.22F + 0.78F * (float)Math.pow(1.0 - var23, 0.8)) * var11 / 2.0F;
                    var0.method_22903();
                    var0.method_22904(var4 + var17, var6 + var21, var8 + var19);
                    var0.method_22907(class_7833.field_40716.rotationDegrees(-var1.method_19330()));
                    var0.method_22907(class_7833.field_40714.rotationDegrees(var1.method_19329()));
                    Matrix4f var29 = var0.method_23760().method_23761();
                    var12.method_22918(var29, -var28, -var28, 0.0F).method_22913(0.0F, 1.0F).method_39415(var27);
                    var12.method_22918(var29, var28, -var28, 0.0F).method_22913(1.0F, 1.0F).method_39415(var27);
                    var12.method_22918(var29, var28, var28, 0.0F).method_22913(1.0F, 0.0F).method_39415(var27);
                    var12.method_22918(var29, -var28, var28, 0.0F).method_22913(0.0F, 0.0F).method_39415(var27);
                    var0.method_22909();
                }
            }
        }

        class_286.method_43433(var12.method_60800());
    }
}
