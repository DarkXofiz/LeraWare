package neva.ware.gui.clickui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.hud.HudElement;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.client.NwCloseAnim;
import neva.ware.features.modules.client.Windows;
import neva.ware.gui.font.FontRenderer;
import neva.ware.gui.hud.HudEditorGui;
import neva.ware.utility.render.NwCursor;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends class_437 {
   public static List<AbstractCategory> windows = Lists.newArrayList();
   public static boolean anyHovered;
   private boolean firstOpen;
   public static boolean close = false;
   public static String currentDescription = "";
   private static ClickGUI INSTANCE = new ClickGUI();
   public static ModuleButton hoveredModule;
   private static String search = "";
   private static boolean searchFocused;
   private float searchFocusAnim;
   private float searchHoverAnim;
   private long caretStart;
   private long openStart;
   private boolean intro = true;
   private boolean closing;
   private int closeMode;
   private float sinceAtClose;
   private int openMode = 2;
   private long closeStart;
   private float panelHeight = -1.0F;
   private float lastTop;
   private ModuleButton tipModule;
   private float tipTime;
   private float tipAnim;
   private float hudBtnAnim;
   private float winBtnAnim;
   private float sbX;
   private float sbY;
   private float sbW;
   private float sbH;
   private float hudX;
   private float winX;
   private float btnY;
   private float btnS;
   private static final float OPEN_TIME = 0.42F;
   private static final float CLOSE_TIME = 0.2F;

   private int timeline() {
      return this.closing && this.closeMode == 2 ? 2 : this.openMode;
   }

   private float globalK(float var1) {
      return switch (this.timeline()) {
         case 0 -> 1.0F;
         case 1 -> NwDraw.easeOutCubic(var1 / 0.22F);
         default -> NwDraw.easeOutCubic(var1 / 0.3F);
      };
   }

   private float panelK(int var1, float var2) {
      return switch (this.timeline()) {
         case 0 -> 1.0F;
         case 1 -> NwDraw.easeOutCubic(var2 / 0.22F);
         default -> NwDraw.easeOutCubic((var2 - (float)var1 * 0.045F) / 0.42F);
      };
   }

   private float panelOffset() {
      return this.timeline() == 1 ? 10.0F : 22.0F;
   }

   private float bottomK(float var1) {
      return switch (this.timeline()) {
         case 0 -> 1.0F;
         case 1 -> NwDraw.easeOutCubic(var1 / 0.22F);
         default -> NwDraw.easeOutCubic((var1 - 0.12F) / 0.42F);
      };
   }

   public ClickGUI() {
      super(class_2561.method_30163("NewClickGUI"));
      windows = Lists.newArrayList();
      this.firstOpen = true;
      this.setInstance();
   }

   public static ClickGUI getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ClickGUI();
      }

      return INSTANCE;
   }

   public static ClickGUI getClickGui() {
      windows.forEach(AbstractCategory::init);
      return getInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static String searchQuery() {
      return search == null ? "" : search.trim().toLowerCase();
   }

   private static boolean ru() {
      try {
         return ClientSettings.isRu();
      } catch (Throwable var1) {
         return false;
      }
   }

   private static int order(Module.Category var0) {
      if (var0 == Module.Category.COMBAT) {
         return 0;
      } else if (var0 == Module.Category.MOVEMENT) {
         return 1;
      } else if (var0 == Module.Category.RENDER) {
         return 2;
      } else if (var0 == Module.Category.PLAYER) {
         return 3;
      } else if (var0 == Module.Category.MISC) {
         return 4;
      } else {
         return var0 == Module.Category.CLIENT ? 5 : 6;
      }
   }

   protected void method_25426() {
      if (this.firstOpen) {
         ArrayList<Module.Category> var1 = new ArrayList<>(Managers.MODULE.getCategories());
         var1.removeIf(var0 -> var0 == Module.Category.HUD);
         var1.sort((var0, var1x) -> Integer.compare(order(var0), order(var1x)));

         for (Module.Category var3 : var1) {
            Category var4 = new Category(var3, Managers.MODULE.getModulesByCategory(var3), 0.0F, 0.0F, 110.0F, 200.0F);
            var4.setOpen(true);
            windows.add(var4);
         }

         this.firstOpen = false;
      }

      if (this.intro) {
         this.openStart = System.nanoTime();
         this.intro = false;
         Object var5 = ModuleManager.clickGui.openAnimation.getValue();
         this.openMode = var5 == NwCloseAnim.Off ? 0 : (var5 == NwCloseAnim.V1 ? 1 : 2);
         this.panelHeight = -1.0F;
      }

      this.closing = false;
      close = false;
      windows.forEach(AbstractCategory::init);
   }

   public void method_25432() {
      this.intro = true;
      search = "";
      searchFocused = false;
      this.closing = false;
      close = false;
   }

   public boolean method_25421() {
      return false;
   }

   public void method_25393() {
      windows.forEach(AbstractCategory::tick);
   }

   private float openProgress() {
      return (float)(System.nanoTime() - this.openStart) / 1.0E9F;
   }

   private float closeProgress() {
      if (this.closing && this.closeMode != 2) {
         float var1 = (float)(System.nanoTime() - this.closeStart) / 1.0E9F / 0.2F;
         return 1.0F - NwDraw.clamp(var1, 0.0F, 1.0F);
      } else {
         return 1.0F;
      }
   }

   private void finishClose() {
      this.closing = false;
      close = false;
      windows.forEach(AbstractCategory::onClose);
      searchFocused = false;
      search = "";
      super.method_25419();
   }

   public void method_25394(class_332 var1, int var2, int var3, float var4) {
      NwDraw.frame();
      NwDraw.prepareScreen(var1);
      class_4587 var5 = var1.method_51448();
      int var6 = Module.mc.method_22683().method_4486();
      int var7 = Module.mc.method_22683().method_4502();
      float var8 = this.openProgress();
      if (this.closing && this.closeMode == 2) {
         var8 = this.sinceAtClose - (float)(System.nanoTime() - this.closeStart) / 1.0E9F;
         if (var8 <= 0.0F) {
            this.finishClose();
            return;
         }
      }

      float var9 = this.globalK(var8) * NwDraw.easeInOutCubic(this.closeProgress());
      if (this.closing && this.closeProgress() <= 0.0F) {
         this.finishClose();
      } else {
         if (Module.fullNullCheck()) {
            this.method_25420(var1, var2, var3, var4);
         } else if (ModuleManager.clickGui.blur.getValue()) {
            this.method_57734(var4);
         }

         NwDraw.alpha = var9;
         NwDraw.round(var5, 0.0F, 0.0F, (float)var6, (float)var7, 0.0F, 1174866194, 2013924374);
         NwDraw.alpha = 1.0F;
         anyHovered = false;
         hoveredModule = null;
         currentDescription = "";
         int var10 = Math.max(1, windows.size());
         float var11 = 7.0F;
         float var12 = 12.0F;
         float var13 = (float)Math.floor((double)Math.min(122.0F, ((float)var6 - var12 * 2.0F - var11 * (float)(var10 - 1)) / (float)var10));
         var13 = Math.max(60.0F, var13);
         float var14 = var13 * (float)var10 + var11 * (float)(var10 - 1);
         float var15 = (float)Math.floor((double)(((float)var6 - var14) / 2.0F));
         boolean var16 = ModuleManager.clickGui.tips.getValue();
         float var17 = 18.0F;
         float var18 = 8.0F + var17 + (var16 ? 13.0F : 4.0F);
         float var19 = (float)var7 - 24.0F - var18;
         float var20 = 150.0F;

         for (AbstractCategory var22 : windows) {
            if (var22 instanceof Category var23) {
               var20 = Math.max(var20, var23.naturalHeight());
            }
         }

         float var33 = Math.max(80.0F, Math.min(var19, var20));
         this.panelHeight = this.panelHeight < 0.0F ? var33 : NwDraw.approach(this.panelHeight, var33, 12.0F);
         float var34 = (float)Math.floor((double)(((float)var7 - (this.panelHeight + var18)) / 2.0F));
         this.lastTop = var34;

         for (int var35 = 0; var35 < windows.size(); var35++) {
            AbstractCategory var24 = windows.get(var35);
            float var25 = this.panelK(var35, var8);
            float var26 = NwDraw.easeInOutCubic(this.closeProgress());
            float var27 = var25 * var26;
            float var28 = var15 + (float)var35 * (var13 + var11);
            float var29 = var34 + (1.0F - var25) * this.panelOffset() + (1.0F - var26) * 10.0F;
            if (var24 instanceof Category var30) {
               var30.setGeometry(var28, var29, var13, this.panelHeight);
            } else {
               var24.setX(var28);
               var24.setY(var29);
               var24.setWidth(var13);
               var24.setHeight(this.panelHeight);
            }

            if (!(var27 <= 0.01F)) {
               NwDraw.alpha = var27;
               boolean var42 = var27 > 0.85F && !this.closing;
               var24.render(var1, var42 ? var2 : -10000, var42 ? var3 : -10000, var4);
            }
         }

         NwDraw.alpha = 1.0F;
         float var36 = this.bottomK(var8) * NwDraw.easeInOutCubic(this.closeProgress());
         NwDraw.alpha = var36;
         this.sbW = Math.min(150.0F, Math.max(110.0F, var13 * 1.25F));
         this.sbH = var17;
         this.sbX = (float)Math.floor((double)((float)var6 / 2.0F - this.sbW / 2.0F));
         this.sbY = var34 + this.panelHeight + 8.0F + (1.0F - var36) * 10.0F;
         this.btnS = var17;
         this.btnY = this.sbY;
         this.hudX = this.sbX - 6.0F - this.btnS;
         this.winX = this.sbX + this.sbW + 6.0F;
         this.renderSearch(var5, var2, var3);
         this.hudBtnAnim = NwDraw.approach(
            this.hudBtnAnim, NwDraw.hovered((double)var2, (double)var3, this.hudX, this.btnY, this.btnS, this.btnS) ? 1.0F : 0.0F, 16.0F
         );
         this.winBtnAnim = NwDraw.approach(
            this.winBtnAnim, NwDraw.hovered((double)var2, (double)var3, this.winX, this.btnY, this.btnS, this.btnS) ? 1.0F : 0.0F, 16.0F
         );
         this.renderIconButton(var5, this.hudX, this.btnY, this.btnS, this.hudBtnAnim, true);
         this.renderIconButton(var5, this.winX, this.btnY, this.btnS, this.winBtnAnim, false);
         if (var16) {
            FontRenderer var37 = NwDraw.smallFont();
            String var39 = ru()
               ? "ЛКМ - вкл/выкл   ПКМ - настройки   СКМ - бинд   CTRL + F - поиск"
               : "LMB - toggle   RMB - settings   MMB - bind   CTRL + F - search";
            NwDraw.textCentered(var37, var5, var39, (float)var6 / 2.0F, this.sbY + this.sbH + 5.0F, NwDraw.withAlpha(-4604725, 170));
         }

         if (this.hudBtnAnim > 0.02F) {
            this.label(var5, ru() ? "HUD редактор" : "HUD Editor", this.hudX + this.btnS / 2.0F, this.btnY - 4.0F, this.hudBtnAnim);
         }

         if (this.winBtnAnim > 0.02F) {
            this.label(var5, ru() ? "Окна" : "Windows", this.winX + this.btnS / 2.0F, this.btnY - 4.0F, this.winBtnAnim);
         }

         NwDraw.alpha = 1.0F;
         this.renderTooltip(var5, var2, var3, var6, var7, var9);

         try {
            if (GLFW.glfwGetPlatform() != 393219) {
               long var38 = Module.mc.method_22683().method_4490();
               boolean var40 = NwDraw.hovered((double)var2, (double)var3, this.sbX, this.sbY, this.sbW, this.sbH);
               boolean var41 = this.hudBtnAnim > 0.5F || this.winBtnAnim > 0.5F || hoveredModule != null || anyHovered;
               if (var40) {
                  GLFW.glfwSetCursor(var38, NwCursor.get(221186));
               } else if (var41) {
                  GLFW.glfwSetCursor(var38, NwCursor.get(221188));
               } else if (!HudElement.anyHovered) {
                  GLFW.glfwSetCursor(var38, NwCursor.get(221185));
               }
            }
         } catch (Throwable var31) {
         }
      }
   }

   private void label(class_4587 var1, String var2, float var3, float var4, float var5) {
      FontRenderer var6 = NwDraw.smallFont();
      float var7 = NwDraw.width(var6, var2) + 10.0F;
      float var8 = 12.0F;
      float var9 = NwDraw.alpha;
      NwDraw.alpha = var9 * var5;
      float var10 = var4 - var8 - (1.0F - var5) * 3.0F;
      NwDraw.shadow(var1, var3 - var7 / 2.0F, var10, var7, var8, 4.0F, 6.0F, 1342177280);
      NwDraw.round(var1, var3 - var7 / 2.0F, var10, var7, var8, 4.0F, -233038292, -233433310);
      NwDraw.border(var1, var3 - var7 / 2.0F, var10, var7, var8, 4.0F, 0.5F, 385875967);
      NwDraw.textCentered(var6, var1, var2, var3, NwDraw.textTop(var10 + var8 / 2.0F, NwDraw.smallSize()), -1);
      NwDraw.alpha = var9;
   }

   private void renderSearch(class_4587 var1, int var2, int var3) {
      boolean var4 = NwDraw.hovered((double)var2, (double)var3, this.sbX, this.sbY, this.sbW, this.sbH);
      this.searchHoverAnim = NwDraw.approach(this.searchHoverAnim, var4 ? 1.0F : 0.0F, 16.0F);
      this.searchFocusAnim = NwDraw.approach(this.searchFocusAnim, searchFocused ? 1.0F : 0.0F, 14.0F);
      int var5 = NwDraw.accent(0);
      NwDraw.shadow(var1, this.sbX, this.sbY, this.sbW, this.sbH, 6.0F, 10.0F, 1610612736);
      if (this.searchFocusAnim > 0.02F) {
         NwDraw.shadow(var1, this.sbX, this.sbY, this.sbW, this.sbH, 6.0F, 7.0F, NwDraw.withAlpha(var5, Math.round(40.0F * this.searchFocusAnim)));
      }

      NwDraw.round(var1, this.sbX, this.sbY, this.sbW, this.sbH, 6.0F, NwDraw.mix(-266855897, -232906704, this.searchHoverAnim), -233828071);
      NwDraw.border(var1, this.sbX, this.sbY, this.sbW, this.sbH, 6.0F, 0.6F, NwDraw.mix(318767103, NwDraw.withAlpha(var5, 150), this.searchFocusAnim));
      float var6 = this.sbX + 9.0F;
      float var7 = this.sbY + this.sbH / 2.0F - 0.8F;
      int var8 = NwDraw.mix(-8552041, -1, Math.max(this.searchFocusAnim, this.searchHoverAnim * 0.5F));
      NwDraw.border(var1, var6 - 3.0F, var7 - 3.0F, 6.0F, 6.0F, 3.0F, 1.0F, var8);
      NwDraw.round(var1, var6 + 2.0F, var7 + 2.0F, 2.6F, 1.1F, 0.5F, var8);
      FontRenderer var9 = NwDraw.moduleFont();
      float var10 = this.sbX + 17.0F;
      float var11 = NwDraw.textTop(this.sbY + this.sbH / 2.0F, NwDraw.moduleSize());
      float var12 = this.sbW - 24.0F;
      if (search.isEmpty()) {
         NwDraw.text(var9, var1, ru() ? "Поиск" : "Search", var10, var11, NwDraw.withAlpha(-6644046, 200));
      } else {
         String var13 = search;

         while (NwDraw.width(var9, var13) > var12 && var13.length() > 1) {
            var13 = var13.substring(1);
         }

         NwDraw.text(var9, var1, var13, var10, var11, -1);
         var10 += NwDraw.width(var9, var13);
      }

      if (searchFocused) {
         long var17 = (System.nanoTime() - this.caretStart) / 1000000L;
         float var15 = var17 / 530L % 2L == 0L ? 1.0F : 0.0F;
         if (var15 > 0.0F) {
            float var16 = search.isEmpty() ? this.sbX + 17.0F : var10 + 0.8F;
            NwDraw.round(var1, var16, this.sbY + 4.5F, 0.8F, this.sbH - 9.0F, 0.4F, NwDraw.withAlpha(-1, 220));
         }
      }

      if (!search.isEmpty()) {
         float var18 = this.sbX + this.sbW - 9.0F;
         float var14 = this.sbY + this.sbH / 2.0F;
         boolean var19 = NwDraw.hovered((double)var2, (double)var3, var18 - 4.0F, var14 - 4.0F, 8.0F, 8.0F);
         NwDraw.circle(var1, var18, var14, 3.6F, var19 ? 1090519039 : 486539263);
         NwDraw.textCentered(NwDraw.smallFont(), var1, "x", var18, NwDraw.textTop(var14, NwDraw.smallSize()), -3946796);
      }
   }

   private void renderIconButton(class_4587 var1, float var2, float var3, float var4, float var5, boolean var6) {
      int var7 = NwDraw.accent(0);
      float var8 = var5 * 1.2F;
      var3 -= var8;
      NwDraw.shadow(var1, var2, var3, var4, var4, 6.0F, 9.0F, 1509949440);
      if (var5 > 0.02F) {
         NwDraw.shadow(var1, var2, var3, var4, var4, 6.0F, 6.0F, NwDraw.withAlpha(var7, Math.round(36.0F * var5)));
      }

      NwDraw.round(var1, var2, var3, var4, var4, 6.0F, NwDraw.mix(-266855897, -232511688, var5), -233828071);
      NwDraw.border(var1, var2, var3, var4, var4, 6.0F, 0.6F, NwDraw.mix(318767103, NwDraw.withAlpha(var7, 140), var5));
      int var9 = NwDraw.mix(-4933430, -1, var5);
      float var10 = var2 + var4 / 2.0F;
      float var11 = var3 + var4 / 2.0F;
      if (var6) {
         NwDraw.border(var1, var10 - 5.0F, var11 - 4.0F, 10.0F, 8.0F, 1.6F, 0.9F, var9);
         NwDraw.round(var1, var10 - 3.4F, var11 - 2.4F, 3.2F, 1.4F, 0.6F, var9);
         NwDraw.round(var1, var10 + 0.6F, var11 + 0.9F, 2.8F, 1.6F, 0.6F, NwDraw.mix(var9, var7, 0.6F));
      } else {
         NwDraw.border(var1, var10 - 4.8F, var11 - 4.2F, 7.0F, 5.6F, 1.2F, 0.9F, NwDraw.withAlpha(var9, 150));
         NwDraw.round(var1, var10 - 2.2F, var11 - 1.4F, 7.0F, 5.6F, 1.2F, NwDraw.withAlpha(-15066071, 255));
         NwDraw.border(var1, var10 - 2.2F, var11 - 1.4F, 7.0F, 5.6F, 1.2F, 0.9F, var9);
         NwDraw.round(var1, var10 - 2.2F, var11 - 1.4F, 7.0F, 1.6F, 0.8F, NwDraw.mix(var9, var7, 0.6F));
      }
   }

   private void renderTooltip(class_4587 var1, int var2, int var3, int var4, int var5, float var6) {
      boolean var7 = ModuleManager.clickGui.descriptions.getValue();
      if (hoveredModule != this.tipModule) {
         this.tipModule = hoveredModule;
         this.tipTime = 0.0F;
      }

      this.tipTime = this.tipTime + NwDraw.dt;
      boolean var8 = var7
         && this.tipModule != null
         && this.tipTime > 0.35F
         && !this.closing
         && currentDescription != null
         && !Objects.equals(currentDescription, "");
      this.tipAnim = NwDraw.approach(this.tipAnim, var8 ? 1.0F : 0.0F, var8 ? 14.0F : 22.0F);
      if (!(this.tipAnim < 0.02F) && this.tipModule != null && currentDescription != null && !currentDescription.isEmpty()) {
         FontRenderer var9 = NwDraw.smallFont();
         List<String> var10 = wrap(var9, currentDescription, 170.0F);
         float var11 = 0.0F;

         for (String var13 : var10) {
            var11 = Math.max(var11, NwDraw.width(var9, var13));
         }

         float var22 = NwDraw.smallSize() + 3.0F;
         float var23 = var11 + 12.0F;
         float var14 = (float)var10.size() * var22 + 8.0F;
         float var15 = (float)var2 + 9.0F;
         float var16 = (float)var3 + 11.0F;
         if (var15 + var23 > (float)var4 - 4.0F) {
            var15 = (float)var2 - var23 - 6.0F;
         }

         if (var16 + var14 > (float)var5 - 4.0F) {
            var16 = (float)var3 - var14 - 6.0F;
         }

         float var17 = NwDraw.alpha;
         NwDraw.alpha = this.tipAnim * var6;
         var16 += (1.0F - this.tipAnim) * 3.0F;
         int var18 = NwDraw.accent(0);
         NwDraw.shadow(var1, var15, var16, var23, var14, 5.0F, 8.0F, 1677721600);
         NwDraw.round(var1, var15, var16, var23, var14, 5.0F, -182640850, -183101662);
         NwDraw.border(var1, var15, var16, var23, var14, 5.0F, 0.6F, NwDraw.withAlpha(var18, 80));
         float var19 = var16 + 4.5F;

         for (String var21 : var10) {
            NwDraw.text(var9, var1, var21, var15 + 6.0F, var19, -3946796);
            var19 += var22;
         }

         NwDraw.alpha = var17;
      }
   }

   private static List<String> wrap(FontRenderer var0, String var1, float var2) {
      ArrayList<String> var3 = new ArrayList<>();

      for (String var7 : var1.split("\n")) {
         StringBuilder var8 = new StringBuilder();

         for (String var12 : var7.split(" ")) {
            String var13 = var8.length() == 0 ? var12 : var8 + " " + var12;
            if (var0.getStringWidth(var13) > var2 && var8.length() > 0) {
               var3.add(var8.toString());
               var8 = new StringBuilder(var12);
            } else {
               var8 = new StringBuilder(var13);
            }
         }

         if (var8.length() > 0) {
            var3.add(var8.toString());
         }

         if (var3.size() > 6) {
            break;
         }
      }

      return var3;
   }

   public boolean method_25401(double var1, double var3, double var5, double var7) {
      for (AbstractCategory var10 : windows) {
         if (var10 instanceof Category var11 && var11.isOverPanel(var1, var3)) {
            var11.scroll(var7);
            return true;
         }
      }

      return super.method_25401(var1, var3, var5, var7);
   }

   public boolean method_25402(double var1, double var3, int var5) {
      if (this.closing) {
         return true;
      } else if (NwDraw.hovered(var1, var3, this.sbX, this.sbY, this.sbW, this.sbH)) {
         if (!search.isEmpty() && NwDraw.hovered(var1, var3, this.sbX + this.sbW - 14.0F, this.sbY, 14.0F, this.sbH)) {
            search = "";
         }

         if (var5 == 1) {
            search = "";
         }

         searchFocused = true;
         this.caretStart = System.nanoTime();
         return true;
      } else {
         searchFocused = false;
         if (var5 == 0 && NwDraw.hovered(var1, var3, this.hudX, this.btnY - 1.5F, this.btnS, this.btnS + 1.5F)) {
            playClick();
            windows.forEach(AbstractCategory::onClose);
            Module.mc.method_1507(HudEditorGui.getHudGui());
            return true;
         } else if (var5 == 0 && NwDraw.hovered(var1, var3, this.winX, this.btnY - 1.5F, this.btnS, this.btnS + 1.5F)) {
            playClick();
            windows.forEach(AbstractCategory::onClose);

            try {
               Windows var6 = ModuleManager.windows;
               if (var6.isEnabled()) {
                  var6.disable();
               }

               var6.enable();
            } catch (Throwable var7) {
               Module.mc.method_1507(null);
            }

            return true;
         } else {
            windows.forEach(var5x -> var5x.mouseClicked((int)var1, (int)var3, var5));
            return super.method_25402(var1, var3, var5);
         }
      }
   }

   private static void playClick() {
      try {
         Managers.SOUND.playSwipeIn();
      } catch (Throwable var1) {
      }
   }

   public boolean method_25406(double var1, double var3, int var5) {
      windows.forEach(var5x -> var5x.mouseReleased((int)var1, (int)var3, var5));
      return super.method_25406(var1, var3, var5);
   }

   public boolean method_25400(char var1, int var2) {
      if (searchFocused) {
         if (var1 >= ' ' && var1 != 127 && search.length() < 32) {
            search = search + var1;
            this.caretStart = System.nanoTime();
         }

         return true;
      } else {
         windows.forEach(var2x -> var2x.charTyped(var1, var2));
         return true;
      }
   }

   private boolean anyBinding() {
      for (AbstractCategory var2 : windows) {
         if (var2 instanceof Category var3 && var3.isBinding()) {
            return true;
         }
      }

      return false;
   }

   public boolean method_25404(int var1, int var2, int var3) {
      boolean var4 = (var3 & 2) != 0;
      if (var4 && var1 == 70) {
         searchFocused = true;
         this.caretStart = System.nanoTime();
         return true;
      } else if (!searchFocused) {
         boolean var5 = this.anyBinding();
         windows.forEach(var1x -> var1x.keyTyped(var1));
         if (var1 == 256) {
            return var5 ? true : this.startClose(var1, var2, var3);
         } else {
            return false;
         }
      } else {
         if (var1 == 259) {
            if (!search.isEmpty()) {
               search = var4 ? "" : search.substring(0, search.length() - 1);
            }

            this.caretStart = System.nanoTime();
         } else if (var1 == 256 || var1 == 257 || var1 == 335) {
            searchFocused = false;
            if (var1 == 256 && search.isEmpty()) {
               return this.startClose(var1, var2, var3);
            }
         } else if (var1 == 261) {
            search = "";
         }

         return true;
      }
   }

   private float introLength() {
      float var1 = 0.42F + (float)Math.max(0, windows.size() - 1) * 0.045F;
      return Math.max(var1, 0.53999996F);
   }

   private boolean startClose(int var1, int var2, int var3) {
      if (this.closing) {
         return true;
      } else {
         Object var4 = ModuleManager.clickGui.closeAnimation.getValue();
         if (Module.mc.field_1724 != null && var4 != NwCloseAnim.Off) {
            this.closeMode = var4 == NwCloseAnim.V1 ? 1 : 2;
            this.sinceAtClose = Math.min(this.openProgress(), this.introLength());
            this.closing = true;
            close = true;
            this.closeStart = System.nanoTime();
            return true;
         } else {
            search = "";
            close = false;
            windows.forEach(AbstractCategory::onClose);
            searchFocused = false;
            super.method_25404(var1, var2, var3);
            return true;
         }
      }
   }
}
