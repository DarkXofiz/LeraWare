package neva.ware.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.utility.render.Render2DEngine;

public class MainMenuButton {
    private final float posX;
    private final float posY;
    private final float width;
    private final float height;
    private final String name;
    private final Runnable action;
    private float hover = 0.0F;
    private static final class_2960 TEX = class_2960.method_60655("nevaware", "textures/gui/menu/btn.png");
    private static final class_2960 TEX_HOVER = class_2960.method_60655("nevaware", "textures/gui/menu/btn_hover.png");

    public MainMenuButton(float var1, float var2, String var3, Runnable var4, boolean var5) {
        this.name = var3;
        this.posX = var1;
        this.posY = var2;
        this.action = var4;
        this.width = 126.0F;
        this.height = 24.0F;
    }

    public MainMenuButton(float var1, float var2, String var3, Runnable var4) {
        this(var1, var2, var3, var4, false);
    }

    private float absX() {
        return MainMenuScreen.columnX() - this.width / 2.0F + this.posX;
    }

    private float absY() {
        return MainMenuScreen.centerY() + this.posY;
    }

    private static Color mix(Color var0, Color var1, float var2) {
        var2 = Math.max(0.0F, Math.min(1.0F, var2));
        return new Color(
            (int)(var0.getRed() + (var1.getRed() - var0.getRed()) * var2),
            (int)(var0.getGreen() + (var1.getGreen() - var0.getGreen()) * var2),
            (int)(var0.getBlue() + (var1.getBlue() - var0.getBlue()) * var2),
            (int)(var0.getAlpha() + (var1.getAlpha() - var0.getAlpha()) * var2)
        );
    }

    public void onRender(class_332 var1, float var2, float var3) {
        class_4587 var4 = var1.method_51448();
        float var5 = this.absX();
        float var6 = this.absY();
        boolean var7 = Render2DEngine.isHovered(var2, var3, var5, var6, this.width, this.height);
        this.hover = this.hover + ((var7 ? 1.0F : 0.0F) - this.hover) * 0.25F;
        int var8 = Math.round(var5) - 6;
        int var9 = Math.round(var6) - 6;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        var1.method_25293(TEX, var8, var9, 138, 36, 0.0F, 0.0F, 414, 108, 414, 108);
        if (this.hover > 0.02F) {
            MainMenuScreen.tint(MainMenuScreen.theme(0), this.hover);
            var1.method_25293(TEX_HOVER, var8, var9, 138, 36, 0.0F, 0.0F, 414, 108, 414, 108);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        Color var10 = mix(new Color(150, 152, 162), new Color(255, 255, 255), this.hover);
        MainMenuScreen.drawSpaced(var4, this.name, var5 + this.width / 2.0F, var6 + this.height / 2.0F - 2.0F, 1.6F, var10.getRGB());
    }

    public void onClick(int var1, int var2) {
        if (Render2DEngine.isHovered(var1, var2, this.absX(), this.absY(), this.width, this.height)) {
            this.action.run();
        }
    }
}
