package neva.ware.features.hud.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.features.modules.Module;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.setting.Setting;

public class ModuleList extends HudElement {
   private final Setting<Boolean> hideRender = new Setting<>("HideRender", true);
   private final Setting<Boolean> hideHud = new Setting<>("HideHud", true);
   private final Setting<Boolean> info = new Setting<>("Info", true);
   private final Map<Module, ModuleList.Entry> entries = new HashMap<>();

   public ModuleList() {
      super("ArrayList", 50, 30);
   }

   private boolean shouldRender(Module var1) {
      if (!var1.isDrawn()) {
         return false;
      } else {
         return this.hideRender.getValue() && var1.getCategory() == Module.Category.RENDER
            ? false
            : !this.hideHud.getValue() || var1.getCategory() != Module.Category.HUD;
      }
   }

   private String text(Module var1) {
      String var2 = var1.getDisplayName();
      if (this.info.getValue()) {
         String var3 = var1.getDisplayInfo();
         if (var3 != null && !var3.isEmpty()) {
            var2 = var2 + "  " + var3;
         }
      }

      return var2;
   }

   @Override
   public void onRender2D(class_332 var1) {
      super.onRender2D(var1);
      class_4587 var2 = var1.method_51448();
      ArrayList var3 = new ArrayList();

      for (Module var5 : Managers.MODULE.getEnabledModules()) {
         if (this.shouldRender(var5)) {
            var3.add(var5);
         }
      }

      var3.sort(Comparator.comparing(var1x -> -NwHud.w(this.text(var1x))));

      for (ModuleList.Entry var32 : this.entries.values()) {
         var32.alive = false;
      }

      for (Module var33 : var3) {
         this.entries.computeIfAbsent(var33, var0 -> new ModuleList.Entry()).alive = true;
      }

      float var31 = this.getPosX();
      float var34 = this.getPosY();
      boolean var6 = var31 > (float)mc.method_22683().method_4486() / 2.0F;
      float var7 = 12.0F;
      float var8 = 1.5F;
      float var9 = 60.0F;

      for (Module var11 : var3) {
         var9 = Math.max(var9, NwHud.w(this.text(var11)) + 12.0F);
      }

      float var35 = var6 ? var31 + var9 : var31;
      float var36 = var34;
      ArrayList var12 = new ArrayList(var3);

      for (Map.Entry var14 : this.entries.entrySet()) {
         if (!((ModuleList.Entry)var14.getValue()).alive) {
            var12.add((Module)var14.getKey());
         }
      }

      int var37 = 0;
      float var38 = var34;

      for (Module var16 : var12) {
         ModuleList.Entry var17 = this.entries.get(var16);
         float var18 = var17.show.to(var17.alive ? 1.0F : 0.0F, 12.0F);
         float var19 = var17.alive ? var36 : var17.y.get();
         float var20 = var17.y.to(var19, 14.0F);
         if (var17.alive) {
            var36 += var7 + var8;
         }

         if (!(var18 < 0.01F)) {
            String var21 = this.text(var16);
            String var22 = var16.getDisplayName();
            float var23 = NwHud.w(var21) + 12.0F;
            float var24 = (1.0F - var18) * (var23 + 6.0F);
            float var25 = var6 ? var35 - var23 + var24 : var35 - var24;
            float var26 = NwDraw.alpha;
            NwDraw.alpha = var26 * var18;
            NwHud.plate(var2, var25, var20, var23, var7, Math.min(NwHud.radius(), 3.0F), 1.0F);
            int var27 = NwHud.accent(var37 * 18);
            float var28 = var6 ? var25 + var23 - 1.6F : var25;
            NwDraw.round(var2, var28, var20 + 2.5F, 1.6F, var7 - 5.0F, 0.8F, var27);
            NwHud.text(var2, var22, var25 + 6.0F, var20 + var7 / 2.0F, NwDraw.mix(NwHud.textColor(), var27, 0.15F));
            if (var21.length() > var22.length()) {
               NwHud.text(var2, var21.substring(var22.length()), var25 + 6.0F + NwHud.w(var22), var20 + var7 / 2.0F, NwHud.textColor2());
            }

            NwDraw.alpha = var26;
            var38 = Math.max(var38, var20 + var7);
            var37++;
         }
      }

      this.entries.entrySet().removeIf(var0 -> !var0.getValue().alive && var0.getValue().show.get() < 0.01F);
      this.setBounds(var6 ? var35 - var9 : var31, var34, var9, Math.max(var7, var38 - var34));
   }

   private static final class Entry {
      final NwHud.Anim show = new NwHud.Anim(0.0F);
      final NwHud.Anim y = new NwHud.Anim();
      boolean alive;
   }
}
