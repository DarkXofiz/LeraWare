package neva.ware.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import neva.ware.utility.render.Render2DEngine;

/** Static MiranWare background (cover-fit, no panning). Used by the splash screen and every menu screen. */
public class PanoramaBackground {
    private static final class_2960 FALLBACK = class_2960.method_60655("nevaware", "textures/gui/menu/bg.png");
    private static final int FALLBACK_W = 2048;
    private static final int FALLBACK_H = 1280;
    public static float fadeAlpha = 1.0F;

    public static void drawMainMenuShader(class_4587 matrices, float x, float y, float width, float height) {
        MiranAssets.Tex tex = MiranAssets.background();
        class_2960 id = tex != null ? tex.id : FALLBACK;
        int texW = tex != null ? tex.w : FALLBACK_W;
        int texH = tex != null ? tex.h : FALLBACK_H;

        // cover: scale so the image fills the whole screen, crop the overflow evenly
        float scale = Math.max(width / (float)texW, height / (float)texH);
        float regionW = width / scale;
        float regionH = height / scale;
        float u = ((float)texW - regionW) / 2.0F;
        float v = ((float)texH - regionH) / 2.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fadeAlpha);
        fadeAlpha = 1.0F;
        RenderSystem.setShaderTexture(0, id);
        Render2DEngine.renderTexture(matrices, x, y, width, height, u, v, regionW, regionH, texW, texH);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
