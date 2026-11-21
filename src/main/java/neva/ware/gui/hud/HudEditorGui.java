package neva.ware.gui.hud;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import neva.ware.NevaWare;
import neva.ware.core.Managers;
import neva.ware.features.hud.HudElement;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.gui.clickui.AbstractCategory;
import neva.ware.gui.clickui.Category;
import neva.ware.gui.clickui.ClickGUI;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.clickui.NwNav;
import neva.ware.gui.font.FontRenderer;

public class HudEditorGui extends class_437 {
   public static HudElement currentlyDragging;
   private final List<AbstractCategory> windows;
   private static HudEditorGui instance = new HudEditorGui();
   private boolean firstOpen;
   private double dWheel;
   private final NwNav nav = new NwNav();
   private long openStart;
   private boolean intro = true;
   private float gridAnim;
   private float guideX;
   private float guideY;
   private final Map<HudElement, Float> hoverAnim = new HashMap<>();
   private boolean placed;

   public HudEditorGui() {
      super(class_2561.method_30163("HudEditorGui"));
      this.windows = Lists.newArrayList();
      this.firstOpen = true;
      this.setInstance();
   }

   private static boolean ru() {
      try {
         return ClientSettings.isRu();
      } catch (Throwable var1) {
         return false;
      }
   }

   protected void method_25426() {
      if (this.firstOpen) {
         Category var1 = new Category(
            Module.Category.HUD,
            Managers.MODULE.getModulesByCategory(Module.Category.HUD),
            (float)Module.mc.method_22683().method_4486() / 2.0F - 61.0F,
            14.0F,
            122.0F,
            200.0F
         );
         var1.setOpen(true);
         var1.movable = true;
         var1.searchable = false;
         this.windows.add(var1);
         this.firstOpen = false;
      }

      if (this.intro) {
         this.openStart = System.nanoTime();
         this.intro = false;
      }

      this.windows.forEach(AbstractCategory::init);
   }

   public boolean method_25421() {
      return false;
   }

   private List<HudElement> elements() {
      ArrayList<HudElement> var1 = new ArrayList<>();

      try {
         for (Module var3 : Managers.MODULE.getEnabledModules()) {
            if (var3 instanceof HudElement var4) {
               var1.add(var4);
            }
         }
      } catch (Throwable var5) {
      }

      return var1;
   }

   public void method_25394(class_332 var1, int var2, int var3, float var4) {
      NwDraw.frame();
      NwDraw.prepareScreen(var1);
      ClickGUI.anyHovered = false;
      class_4587 var5 = var1.method_51448();
      int var6 = Module.mc.method_22683().method_4486();
      int var7 = Module.mc.method_22683().method_4502();
      float var8 = (float)(System.nanoTime() - this.openStart) / 1.0E9F;
      float var9 = NwDraw.easeOutCubic(var8 / 0.35F);
      int var10 = NwDraw.accent(0);
      NwDraw.alpha = var9;
      NwDraw.round(var5, 0.0F, 0.0F, (float)var6, (float)var7, 0.0F, 570886418, 973539602);
      HudElement var11 = currentlyDragging;
      this.gridAnim = NwDraw.approach(this.gridAnim, var11 != null ? 1.0F : 0.0F, 10.0F);
      if (this.gridAnim > 0.02F) {
         NwDraw.alpha = var9 * this.gridAnim;
         float var12 = 16.0F;

         for (float var13 = (float)var6 / 2.0F % var12; var13 < (float)var6; var13 += var12) {
            NwDraw.rect(var5, var13, 0.0F, 0.5F, (float)var7, 251658239);
         }

         for (float var35 = (float)var7 / 2.0F % var12; var35 < (float)var7; var35 += var12) {
            NwDraw.rect(var5, 0.0F, var35, (float)var6, 0.5F, 251658239);
         }
      }

      boolean var34 = false;
      boolean var36 = false;
      if (var11 != null) {
         float var14 = var11.getHitX() + var11.getWidth() / 2.0F;
         float var15 = var11.getHitY() + var11.getHeight() / 2.0F;
         var34 = Math.abs(var14 - (float)var6 / 2.0F) < 2.5F;
         var36 = Math.abs(var15 - (float)var7 / 2.0F) < 2.5F;
      }

      this.guideX = NwDraw.approach(this.guideX, var34 ? 1.0F : (var11 != null ? 0.25F : 0.0F), 14.0F);
      this.guideY = NwDraw.approach(this.guideY, var36 ? 1.0F : (var11 != null ? 0.25F : 0.0F), 14.0F);
      NwDraw.alpha = var9;
      if (this.guideX > 0.02F) {
         NwDraw.round(
            var5,
            (float)var6 / 2.0F - 0.4F,
            0.0F,
            0.8F,
            (float)var7,
            0.0F,
            NwDraw.withAlpha(NwDraw.mix(-1, var10, this.guideX), Math.round(160.0F * this.guideX))
         );
      }

      if (this.guideY > 0.02F) {
         NwDraw.round(
            var5,
            0.0F,
            (float)var7 / 2.0F - 0.4F,
            (float)var6,
            0.8F,
            0.0F,
            NwDraw.withAlpha(NwDraw.mix(-1, var10, this.guideY), Math.round(160.0F * this.guideY))
         );
      }

      FontRenderer var37 = NwDraw.smallFont();
      boolean var38 = false;

      for (AbstractCategory var17 : this.windows) {
         if (var17 instanceof Category) {
            Category var18 = (Category)var17;
            if (var18.isOverPanel((double)var2, (double)var3)) {
               var38 = true;
            }
         }
      }

      boolean var39 = this.nav.isOver((double)var2, (double)var3);
      Object var40 = null;

      for (HudElement var19 : this.elements()) {
         float var20 = var19.getHitX();
         float var21 = var19.getHitY();
         float var22 = var19.getWidth();
         float var23 = var19.getHeight();
         if (!(var22 <= 0.0F) && !(var23 <= 0.0F)) {
            boolean var24 = !var38 && !var39 && NwDraw.hovered((double)var2, (double)var3, var20, var21, var22, var23);
            if (var24) {
               ;
            }

            float var25 = this.hoverAnim.getOrDefault(var19, 0.0F);
            var25 = NwDraw.approach(var25, !var24 && var19 != var11 ? 0.0F : 1.0F, 16.0F);
            this.hoverAnim.put(var19, var25);
            NwDraw.alpha = var9;
            float var26 = 3.0F;
            NwDraw.round(var5, var20 - 1.0F, var21 - 1.0F, var22 + 2.0F, var23 + 2.0F, var26, NwDraw.withAlpha(var10, Math.round(18.0F * var25)));
            NwDraw.border(
               var5,
               var20 - 1.0F,
               var21 - 1.0F,
               var22 + 2.0F,
               var23 + 2.0F,
               var26,
               0.6F,
               NwDraw.withAlpha(NwDraw.mix(-1, var10, var25), Math.round(38.0F + 150.0F * var25))
            );
            if (var25 > 0.02F) {
               float var27 = 2.2F;
               int var28 = NwDraw.withAlpha(var10, Math.round(230.0F * var25));
               NwDraw.round(var5, var20 - 1.0F - var27 / 2.0F, var21 - 1.0F - var27 / 2.0F, var27, var27, 0.6F, var28);
               NwDraw.round(var5, var20 + var22 + 1.0F - var27 / 2.0F, var21 - 1.0F - var27 / 2.0F, var27, var27, 0.6F, var28);
               NwDraw.round(var5, var20 - 1.0F - var27 / 2.0F, var21 + var23 + 1.0F - var27 / 2.0F, var27, var27, 0.6F, var28);
               NwDraw.round(var5, var20 + var22 + 1.0F - var27 / 2.0F, var21 + var23 + 1.0F - var27 / 2.0F, var27, var27, 0.6F, var28);
               String var29 = NwDraw.pretty(var19.getName()) + "   " + Math.round(var20) + ", " + Math.round(var21);
               float var30 = NwDraw.width(var37, var29) + 8.0F;
               float var31 = 10.0F;
               float var32 = NwDraw.clamp(var20 + var22 / 2.0F - var30 / 2.0F, 2.0F, (float)var6 - var30 - 2.0F);
               float var33 = var21 - var31 - 4.0F;
               if (var33 < 2.0F) {
                  var33 = var21 + var23 + 4.0F;
               }

               NwDraw.alpha = var9 * var25;
               NwDraw.shadow(var5, var32, var33, var30, var31, 3.5F, 6.0F, 1426063360);
               NwDraw.round(var5, var32, var33, var30, var31, 3.5F, -233038292, -233499103);
               NwDraw.border(var5, var32, var33, var30, var31, 3.5F, 0.5F, NwDraw.withAlpha(var10, 90));
               NwDraw.text(var37, var5, var29, var32 + 4.0F, NwDraw.textTop(var33 + var31 / 2.0F, NwDraw.smallSize()), -1);
            }
         }
      }

      NwDraw.alpha = 1.0F;

      for (AbstractCategory var44 : this.windows) {
         if (var44 instanceof Category) {
            Category var46 = (Category)var44;
            float var48 = 122.0F;
            float var50 = Math.max(80.0F, Math.min(var46.naturalHeight(), (float)var7 * 0.62F));
            float var51 = var46.getX();
            float var53 = var46.getY();
            if (!this.placed) {
               var51 = (float)var6 / 2.0F - var48 / 2.0F;
               var53 = 14.0F;
               this.placed = true;
            }

            var51 = NwDraw.clamp(var51, 2.0F, Math.max(2.0F, (float)var6 - var48 - 2.0F));
            var53 = NwDraw.clamp(var53, 2.0F, Math.max(2.0F, (float)var7 - 40.0F));
            var46.setGeometry(var51, var53, var48, var50);
            NwDraw.alpha = var9;
            var46.render(var1, var9 > 0.85F ? var2 : -10000, var9 > 0.85F ? var3 : -10000, var4);
         }
      }

      NwDraw.alpha = 1.0F;
      float var43 = 18.0F;
      float var45 = (float)var7 - var43 - 20.0F + (1.0F - var9) * 10.0F;
      float var47 = this.nav.width();
      NwDraw.alpha = var9;
      this.nav.render(var5, (float)Math.floor((double)((float)var6 / 2.0F - var47 / 2.0F)), var45, var43, 1, var2, var3, true);
      String var49 = ru() ? "ЛКМ - перетащить   ПКМ - настройки элемента   ESC - выход" : "LMB - drag   RMB - element settings   ESC - exit";
      NwDraw.textCentered(var37, var5, var49, (float)var6 / 2.0F, var45 + var43 + 5.0F, NwDraw.withAlpha(-4604725, 170));
      NwDraw.alpha = 1.0F;
   }

   public boolean method_25401(double var1, double var3, double var5, double var7) {
      this.dWheel = var7;

      for (AbstractCategory var10 : this.windows) {
         if (var10 instanceof Category var11 && var11.isOverPanel(var1, var3)) {
            var11.scroll(var7);
            return true;
         }
      }

      return super.method_25401(var1, var3, var5, var7);
   }

   public boolean method_25402(double var1, double var3, int var5) {
      if (this.nav.click(var1, var3, var5, 1)) {
         return true;
      } else {
         this.windows.forEach(var6 -> {
            var6.mouseClicked((int)var1, (int)var3, var5);
            this.windows.forEach(var1xx -> {
               if (var6.dragging && var6 != var1xx) {
                  var1xx.dragging = false;
               }
            });
         });
         return super.method_25402(var1, var3, var5);
      }
   }

   public boolean method_25406(double var1, double var3, int var5) {
      this.windows.forEach(var5x -> var5x.mouseReleased((int)var1, (int)var3, var5));
      return super.method_25406(var1, var3, var5);
   }

   public boolean method_25400(char var1, int var2) {
      this.windows.forEach(var2x -> var2x.charTyped(var1, var2));
      return true;
   }

   public boolean method_25404(int var1, int var2, int var3) {
      boolean var4 = false;

      for (AbstractCategory var6 : this.windows) {
         if (var6 instanceof Category) {
            Category var7 = (Category)var6;
            if (var7.isBinding()) {
               var4 = true;
            }
         }
      }

      this.windows.forEach(var1x -> var1x.keyTyped(var1));
      if (var1 != 256) {
         return false;
      } else if (var4) {
         return true;
      } else {
         this.windows.forEach(AbstractCategory::onClose);
         super.method_25404(var1, var2, var3);
         return true;
      }
   }

   public void method_25432() {
      this.intro = true;
      currentlyDragging = null;

      try {
         NevaWare.EVENT_BUS.unsubscribe(this);
      } catch (Throwable var2) {
      }
   }

   public void hudClicked(Module var1) {
      for (AbstractCategory var3 : this.windows) {
         var3.hudClicked(var1);
      }
   }

   public static HudEditorGui getInstance() {
      if (instance == null) {
         instance = new HudEditorGui();
      }

      return instance;
   }

   public static HudEditorGui getHudGui() {
      return getInstance();
   }

   private void setInstance() {
      instance = this;
   }
}
