package neva.ware.features.modules.render;

import net.minecraft.class_1041;
import net.minecraft.class_332;
import net.minecraft.class_408;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import org.lwjgl.glfw.GLFW;

public class Scoreboard extends Module {
    public static final int BOX_WIDTH = 150;
    public static final int BOX_HEIGHT = 120;
    public static Scoreboard INSTANCE;
    public static boolean renderingSidebar;
    public static boolean pushedMatrix;
    public final Setting<Float> scale = new Setting<>("Scale", 1.0F, 0.1F, 2.0F);
    public final Setting<Boolean> transparent = new Setting<>("Transparent", false);
    public final Setting<Boolean> showHitbox = new Setting<>("ShowHitbox", true);
    public final Setting<Float> posX = new Setting<>("PosX", 0.0F, -2000.0F, 2000.0F);
    public final Setting<Float> posY = new Setting<>("PosY", 0.0F, -2000.0F, 2000.0F);
    private boolean dragging;
    private float dragOffsetX;
    private float dragOffsetY;

    public Scoreboard() {
        super("Scoreboard", Module.Category.RENDER);
        INSTANCE = this;
    }

    public static boolean active() {
        return INSTANCE != null && INSTANCE.isEnabled();
    }

    @Override
    public void onDisable() {
        this.dragging = false;
    }

    @Override
    public void onRender2D(class_332 var1) {
        this.updateDragLogic();
    }

    public void updateDragLogic() {
        if (this.isEnabled() && mc.field_1755 != null && mc.field_1755 instanceof class_408) {
            class_1041 var1 = mc.method_22683();
            if (var1.method_4480() > 0 && var1.method_4507() > 0) {
                double var2 = mc.field_1729.method_1603() * var1.method_4486() / var1.method_4480();
                double var4 = mc.field_1729.method_1604() * var1.method_4502() / var1.method_4507();
                float var6 = this.scale.getValue();
                float var7 = 150.0F * var6;
                float var8 = 120.0F * var6;
                float var9 = var1.method_4486();
                float var10 = var1.method_4502() / 2.0F;
                float var11 = var9 + this.posX.getValue() - var7;
                float var12 = var10 + this.posY.getValue() - var8 / 2.0F;
                boolean var13 = var2 >= var11 && var2 <= var11 + var7 && var4 >= var12 && var4 <= var12 + var8;
                boolean var14 = GLFW.glfwGetMouseButton(var1.method_4490(), 0) == 1;
                if (var14) {
                    if (!this.dragging && var13) {
                        this.dragging = true;
                        this.dragOffsetX = (float)(this.posX.getValue().floatValue() - (var2 - var9));
                        this.dragOffsetY = (float)(this.posY.getValue().floatValue() - (var4 - var10));
                    }

                    if (this.dragging) {
                        this.posX.setValue((float)(var2 - var9) + this.dragOffsetX);
                        this.posY.setValue((float)(var4 - var10) + this.dragOffsetY);
                    }
                } else {
                    this.dragging = false;
                }
            }
        } else {
            this.dragging = false;
        }
    }
}
