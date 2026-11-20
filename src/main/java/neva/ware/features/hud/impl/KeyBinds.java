package neva.ware.features.hud.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.features.modules.Module;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.setting.Setting;

public class KeyBinds extends HudElement {
    public final Setting<Boolean> onlyEnabled = new Setting<>("OnlyEnabled", false);
    private final NwPanel panel = new NwPanel();
    private final Map<Module, NwHud.Anim> state = new HashMap();

    public KeyBinds() {
        super("KeyBinds", 100, 100);
    }

    public static String getShortKeyName(Module var0) {
        String var1 = var0.getBind().getBind();
        if (var1 == null) {
            return "None";
        } else {
            return switch (var1) {
                case "LEFT_CONTROL" -> "LCtrl";
                case "RIGHT_CONTROL" -> "RCtrl";
                case "LEFT_SHIFT" -> "LShift";
                case "RIGHT_SHIFT" -> "RShift";
                case "LEFT_ALT" -> "LAlt";
                case "RIGHT_ALT" -> "RAlt";
                default -> var1.toUpperCase();
            };
        }
    }

    @Override
    public void onRender2D(class_332 var1) {
        super.onRender2D(var1);
        class_4587 var2 = var1.method_51448();
        ArrayList<Module> var3 = new ArrayList<>();

        for (Module var5 : Managers.MODULE.modules) {
            if (var5 != ModuleManager.clickGui) {
                String var6 = var5.getBind().getBind();
                if (var6 != null && !var6.equalsIgnoreCase("none") && (!this.onlyEnabled.getValue() || var5.isOn())) {
                    var3.add(var5);
                }
            }
        }

        float var18 = 0.0F;

        for (Module var21 : var3) {
            var18 = Math.max(var18, 14.0F + NwHud.w(var21.getName()) + 12.0F + NwHud.ws(getShortKeyName(var21)) + 7.0F + 6.0F);
        }

        boolean var20 = NwHud.editing();
        float var22 = var3.isEmpty() ? (var20 ? 12.0F : 0.0F) : var3.size() * 12.0F;
        if (this.panel.begin(this, var2, NwHud.Icon.KEYBOARD, "Hotkeys", null, var18, var22, !var3.isEmpty() || var20)) {
            float var7 = this.panel.rowsY;
            if (var3.isEmpty()) {
                NwHud.textSmall(var2, "No binds", this.panel.x + 6.0F, var7 + 6.0F, NwHud.textColor2());
            }

            HashSet var8 = new HashSet();
            int var9 = 0;

            for (Module var11 : var3) {
                var8.add(var11.getName());
                float var12 = this.panel.row(var11.getName());
                float var13 = ((NwHud.Anim)this.state.computeIfAbsent(var11, var1x -> new NwHud.Anim(var11.isOn() ? 1.0F : 0.0F)))
                    .to(var11.isOn() ? 1.0F : 0.0F, 14.0F);
                float var14 = var7 + 6.0F;
                float var15 = NwDraw.alpha;
                NwDraw.alpha = var15 * var12;
                float var16 = this.panel.x + 6.0F - (1.0F - var12) * 6.0F;
                int var17 = NwHud.accent(var9 * 45);
                NwDraw.circle(var2, var16 + 2.0F, var14, 1.9F, NwDraw.mix(1442840575, var17, var13));
                if (var13 > 0.05F) {
                    NwDraw.circle(var2, var16 + 2.0F, var14, 3.4F, NwDraw.withAlpha(var17, Math.round(50.0F * var13)));
                }

                NwHud.text(var2, var11.getName(), var16 + 8.0F, var14, NwDraw.mix(NwHud.textColor2(), NwHud.textColor(), var13));
                NwHud.chip(var2, getShortKeyName(var11), this.panel.x + this.panel.w - 6.0F, var14, NwHud.textColor());
                NwDraw.alpha = var15;
                var7 += 12.0F;
                var9++;
            }

            this.state.keySet().removeIf(var1x -> !var3.contains(var1x));
            this.panel.forget(var8);
            this.panel.end();
        }
    }
}
