package neva.ware.gui.clickui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.NevaWare;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.BaritoneSettings;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.gui.font.FontRenderer;
import neva.ware.utility.render.Render2DEngine;

public class Category extends AbstractCategory {
    public static final float HEADER = 26.0F;
    public static final float PAD = 5.0F;
    public static final float GAP = 3.0F;
    private final List<AbstractButton> buttons;
    private final Module.Category category;
    private final String title;
    public float catHeight;
    private float scroll;
    private float scrollTarget;
    private float contentHeight;
    private float barAnim;
    private boolean viewportHovered;
    private float dragOffX;
    private float dragOffY;
    public boolean movable;
    public boolean searchable = true;

    public Category(Module.Category var1, ArrayList<Module> var2, float var3, float var4, float var5, float var6) {
        super(var1.getName(), var3, var4, var5, var6);
        this.category = var1;
        this.title = displayName(var1);
        this.buttons = new ArrayList();
        var2.forEach(var1x -> {
            if (!(var1x instanceof BaritoneSettings) || NevaWare.baritone) {
                this.buttons.add(new ModuleButton(var1x));
            }
        });
    }

    public static String displayName(Module.Category var0) {
        if (var0 == Module.Category.RENDER) {
            return "Visuals";
        } else if (var0 == Module.Category.MISC) {
            return "Misc";
        } else {
            return var0 == Module.Category.HUD ? "HUD" : NwDraw.pretty(var0.getName());
        }
    }

    public Module.Category getCategory() {
        return this.category;
    }

    @Override
    public void init() {
        this.buttons.forEach(AbstractButton::init);
    }

    private static boolean ru() {
        try {
            return ClientSettings.isRu();
        } catch (Throwable var1) {
            return false;
        }
    }

    private List<ModuleButton> visibleButtons() {
        ArrayList<ModuleButton> var1 = new ArrayList<>();
        String var2 = this.searchable ? ClickGUI.searchQuery() : "";

        for (AbstractButton var4 : this.buttons) {
            if (var4 instanceof ModuleButton var5) {
                if (!var2.isEmpty()) {
                    String var6 = var5.module.getName().toLowerCase();
                    String var7 = var5.display.toLowerCase();
                    if (!var6.contains(var2) && !var7.contains(var2) && !var6.contains(var2.replace(" ", ""))) {
                        continue;
                    }
                }

                var1.add(var5);
            }
        }

        return var1;
    }

    public float naturalHeight() {
        float var1 = 0.0F;

        for (ModuleButton var3 : this.visibleButtons()) {
            var1 += Math.max(18.0F, var3.getTotalHeight()) + 3.0F;
        }

        if (var1 > 0.0F) {
            var1 -= 3.0F;
        }

        return 26.0F + var1 + 5.0F + 2.0F;
    }

    public void scroll(double var1) {
        this.scrollTarget -= (float)var1 * 26.0F;
    }

    public boolean isBinding() {
        for (AbstractButton var2 : this.buttons) {
            if (var2 instanceof ModuleButton var3 && var3.isCapturingKeys()) {
                return true;
            }
        }

        return false;
    }

    public boolean isOverPanel(double var1, double var3) {
        return NwDraw.hovered(var1, var3, this.x, this.y, this.width, this.height);
    }

    public boolean isOverHeader(double var1, double var3) {
        return NwDraw.hovered(var1, var3, this.x, this.y, this.width, 26.0F);
    }

    @Override
    public void render(class_332 var1, int var2, int var3, float var4) {
        class_4587 var5 = var1.method_51448();
        if (this.dragging) {
            this.x = var2 - this.dragOffX;
            this.y = var3 - this.dragOffY;
        }

        float var6 = 8.0F;
        int var7 = NwDraw.accent(0);
        NwDraw.shadow(var5, this.x, this.y, this.width, this.height, var6, 14.0F, 2013265920);
        NwDraw.round(var5, this.x, this.y, this.width, this.height, var6, -266855897, -233828071);
        NwDraw.border(var5, this.x, this.y, this.width, this.height, var6, 0.6F, 318767103);
        NwDraw.hGradient(var5, this.x + var6, this.y + 0.6F, (this.width - var6 * 2.0F) / 2.0F, 0.6F, 16777215, 587202559);
        NwDraw.hGradient(var5, this.x + this.width / 2.0F, this.y + 0.6F, (this.width - var6 * 2.0F) / 2.0F, 0.6F, 587202559, 16777215);
        FontRenderer var8 = NwDraw.titleFont();
        String var9 = NwDraw.trim(var8, this.title, this.width - 14.0F);
        NwDraw.textCentered(var8, var5, var9, this.x + this.width / 2.0F, NwDraw.textTop(this.y + 13.0F, NwDraw.titleSize()), -1);
        if (this.movable) {
            float var10 = NwDraw.width(var8, var9) / 2.0F;
            NwDraw.circle(var5, this.x + this.width / 2.0F - var10 - 6.0F, this.y + 13.0F + 0.5F, 1.4F, NwDraw.withAlpha(var7, 220));
        }

        float var27 = this.x + 5.0F + 1.0F;
        float var11 = this.y + 26.0F;
        float var12 = this.width - 12.0F;
        float var13 = this.height - 26.0F - 5.0F;
        if (!(var13 <= 2.0F)) {
            List<ModuleButton> var14 = this.visibleButtons();
            float var15 = 0.0F;

            for (ModuleButton var17 : var14) {
                var15 += Math.max(18.0F, var17.getTotalHeight()) + 3.0F;
            }

            if (var15 > 0.0F) {
                var15 -= 3.0F;
            }

            this.contentHeight = var15;
            this.catHeight = var15;
            float var28 = Math.max(0.0F, var15 - var13);
            this.scrollTarget = NwDraw.clamp(this.scrollTarget, 0.0F, var28);
            this.scroll = NwDraw.approach(this.scroll, this.scrollTarget, 16.0F);
            this.scroll = NwDraw.clamp(this.scroll, 0.0F, var28);
            this.viewportHovered = NwDraw.hovered(var2, var3, var27 - 1.0F, var11, var12 + 2.0F, var13);
            int var29 = this.viewportHovered ? var2 : -10000;
            int var18 = this.viewportHovered ? var3 : -10000;
            Render2DEngine.addWindow(var5, var27 - 2.0F, var11, var27 + var12 + 2.0F, var11 + var13, 1.0);
            float var19 = var11 - this.scroll;

            for (ModuleButton var21 : var14) {
                var21.setX(var27);
                var21.setY(var19);
                var21.setWidth(var12);
                var21.setHeight(18.0F);
                float var22 = Math.max(18.0F, var21.getTotalHeight());
                if (var19 + var22 + 8.0F >= var11 && var19 - 8.0F <= var11 + var13) {
                    var21.render(var1, var29, var18, var4);
                } else {
                    var21.skipFrame();
                }

                var19 += Math.max(18.0F, var21.getTotalHeight()) + 3.0F;
            }

            Render2DEngine.popWindow();
            int var30 = NwDraw.mix(-266855897, -233828071, (var11 - this.y) / this.height);
            int var31 = NwDraw.mix(-266855897, -233828071, (var11 + var13 - this.y) / this.height);
            float var32 = NwDraw.clamp(this.scroll / 10.0F, 0.0F, 1.0F);
            float var23 = NwDraw.clamp((var28 - this.scroll) / 10.0F, 0.0F, 1.0F);
            if (var32 > 0.01F) {
                NwDraw.round(var5, this.x + 1.0F, var11, this.width - 2.0F, 7.0F, 0.0F, NwDraw.mulAlpha(var30, var32), var30 & 16777215);
            }

            if (var23 > 0.01F) {
                NwDraw.round(var5, this.x + 1.0F, var11 + var13 - 7.0F, this.width - 2.0F, 7.0F, 0.0F, var31 & 16777215, NwDraw.mulAlpha(var31, var23));
            }

            boolean var24 = this.isOverPanel(var2, var3);
            this.barAnim = NwDraw.approach(this.barAnim, var28 > 0.0F ? (var24 ? 1.0F : 0.55F) : 0.0F, 10.0F);
            if (this.barAnim > 0.02F && var28 > 0.0F) {
                float var25 = Math.max(14.0F, var13 * var13 / Math.max(var13, var15));
                float var26 = var11 + (var13 - var25) * (this.scroll / var28);
                NwDraw.round(var5, this.x + this.width - 3.4F, var26, 1.6F, var25, 0.8F, NwDraw.withAlpha(-1, Math.round(70.0F * this.barAnim)));
            }

            if (var14.isEmpty()) {
                FontRenderer var33 = NwDraw.smallFont();
                String var34 = ru() ? "Ничего не найдено" : "Nothing found";
                NwDraw.textCentered(var33, var5, var34, this.x + this.width / 2.0F, var11 + 8.0F, -8552041);
            }
        }
    }

    @Override
    public void mouseClicked(int var1, int var2, int var3) {
        if (this.movable && var3 == 0 && this.isOverHeader(var1, var2)) {
            this.dragging = true;
            this.dragOffX = var1 - this.x;
            this.dragOffY = var2 - this.y;
        } else {
            boolean var4 = this.isBinding();
            if (this.viewportHovered || var4 || NwDraw.hovered(var1, var2, this.x + 5.0F, this.y + 26.0F, this.width - 10.0F, this.height - 26.0F - 5.0F)) {
                for (ModuleButton var6 : this.visibleButtons()) {
                    var6.mouseClicked(var1, var2, var3);
                }
            }
        }
    }

    @Override
    public void mouseReleased(int var1, int var2, int var3) {
        if (var3 == 0) {
            this.dragging = false;
        }

        this.buttons.forEach(var3x -> var3x.mouseReleased(var1, var2, var3));
    }

    @Override
    public boolean keyTyped(int var1) {
        for (AbstractButton var3 : this.buttons) {
            var3.keyTyped(var1);
        }

        return false;
    }

    @Override
    public void charTyped(char var1, int var2) {
        for (AbstractButton var4 : this.buttons) {
            var4.charTyped(var1, var2);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        this.dragging = false;
        this.buttons.forEach(AbstractButton::onGuiClosed);
    }

    @Override
    public void tick() {
        this.buttons.forEach(AbstractButton::tick);
    }

    @Override
    public void hudClicked(Module var1) {
        float var2 = 0.0F;

        for (ModuleButton var4 : this.visibleButtons()) {
            if (var4.module == var1) {
                var4.setOpen(true);
                this.scrollTarget = var2;
                return;
            }

            var2 += Math.max(18.0F, var4.getTotalHeight()) + 3.0F;
        }
    }

    public double getButtonsHeight() {
        return this.contentHeight;
    }

    public void setGeometry(float var1, float var2, float var3, float var4) {
        if (!this.dragging) {
            this.x = var1;
            this.y = var2;
        }

        this.width = var3;
        this.height = var4;
    }
}
