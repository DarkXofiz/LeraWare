package neva.ware.gui.clickui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1074;
import net.minecraft.class_332;
import net.minecraft.class_3675;
import net.minecraft.class_4587;
import neva.ware.core.Managers;
import neva.ware.features.cmd.Command;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.gui.clickui.impl.BindElement;
import neva.ware.gui.clickui.impl.BooleanElement;
import neva.ware.gui.clickui.impl.BooleanParentElement;
import neva.ware.gui.clickui.impl.ColorPickerElement;
import neva.ware.gui.clickui.impl.ItemSelectElement;
import neva.ware.gui.clickui.impl.ModeElement;
import neva.ware.gui.clickui.impl.ParentElement;
import neva.ware.gui.clickui.impl.SliderElement;
import neva.ware.gui.clickui.impl.StringElement;
import neva.ware.gui.font.FontRenderer;
import neva.ware.gui.misc.DialogScreen;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.Bind;
import neva.ware.setting.impl.BooleanSettingGroup;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.setting.impl.ItemSelectSetting;
import neva.ware.setting.impl.PositionSetting;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.TextureStorage;

public class ModuleButton extends AbstractButton {
   public static final float ROW_H = 18.0F;
   private final List<AbstractElement> elements;
   public final Module module;
   public final String display;
   private boolean open;
   private boolean hovered;
   private boolean prevHovered;
   private float animation;
   private float animation2;
   float category_animation;
   int ticksOpened;
   private boolean binding;
   private boolean holdbind;
   private float bindAnim;
   private float openAnim;
   private float chipToggleX;
   private float chipHoldX;
   private float chipY;
   private float chipToggleW;
   private float chipHoldW;
   private float chipH;

   public ModuleButton(Module var1) {
      this.module = var1;
      this.display = NwDraw.pretty(var1.getName());
      this.elements = new ArrayList<>();

      for (Setting var3 : var1.getSettings()) {
         Object var4 = var3.getValue();
         if (var4 instanceof Boolean && !var3.getName().equals("Enabled") && !var3.getName().equals("Drawn")) {
            this.elements.add(new BooleanElement(var3));
         } else if (var4 instanceof ColorSetting) {
            this.elements.add(new ColorPickerElement(var3));
         } else if (var4 instanceof BooleanSettingGroup) {
            this.elements.add(new BooleanParentElement(var3));
         } else if (var3.isNumberSetting() && var3.hasRestriction()) {
            this.elements.add(new SliderElement(var3));
         } else if (var4 instanceof ItemSelectSetting) {
            this.elements.add(new ItemSelectElement(var3));
         } else if (var4 instanceof SettingGroup) {
            this.elements.add(new ParentElement(var3));
         } else if (var3.isEnumSetting() && !(var4 instanceof PositionSetting)) {
            this.elements.add(new ModeElement(var3));
         } else if (var4 instanceof Bind && !var3.getName().equals("Keybind")) {
            this.elements.add(new BindElement(var3));
         } else if ((var4 instanceof String || var4 instanceof Character) && !var3.getName().equalsIgnoreCase("displayName")) {
            this.elements.add(new StringElement(var3));
         }
      }
   }

   @Override
   public void init() {
      this.elements.forEach(AbstractElement::init);
   }

   public boolean hasSettings() {
      return !this.elements.isEmpty();
   }

   public boolean isBinding() {
      return this.binding;
   }

   public boolean isHovered() {
      return this.hovered;
   }

   public float getTotalHeight() {
      return this.height + (this.category_animation > 0.3F ? this.category_animation + 2.0F : 0.0F);
   }

   private static boolean ru() {
      try {
         return ClientSettings.isRu();
      } catch (Throwable var1) {
         return false;
      }
   }

   private static boolean keyDown(int var0) {
      try {
         return class_3675.method_15987(Module.mc.method_22683().method_4490(), var0);
      } catch (Throwable var2) {
         return false;
      }
   }

   private float layoutElements(float var1) {
      float var2 = 3.0F;

      for (AbstractElement var4 : this.elements) {
         if (var4.isVisible()) {
            var4.setOffsetY(var2);
            var4.setX(this.x + 1.0F);
            var4.setY(var1);
            var4.setWidth(this.width - 2.0F);
            if (var4 instanceof NwSized var5) {
               var4.setHeight(var5.prefHeight());
            } else {
               var4.setHeight(13.0F);
            }

            var2 += var4.getHeight();
         }
      }

      return var2 + 3.0F;
   }

   public boolean isCapturingKeys() {
      if (this.binding) {
         return true;
      } else if (!this.open) {
         return false;
      } else {
         for (AbstractElement var2 : this.elements) {
            if (var2 instanceof BindElement var3 && var3.isListening) {
               return true;
            }

            if (var2 instanceof StringElement var4 && var4.isListening()) {
               return true;
            }

            if (var2 instanceof SliderElement var5 && var5.isListening()) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void render(class_332 var1, int var2, int var3, float var4) {
      class_4587 var5 = var1.method_51448();
      this.hovered = NwDraw.hovered((double)var2, (double)var3, this.x, this.y, this.width, this.height);
      this.animation = NwDraw.approach(this.animation, this.module.isEnabled() ? 1.0F : 0.0F, 14.0F);
      this.animation2 = NwDraw.approach(this.animation2, this.hovered ? 1.0F : 0.0F, 18.0F);
      this.bindAnim = NwDraw.approach(this.bindAnim, this.binding ? 1.0F : 0.0F, 16.0F);
      this.openAnim = NwDraw.approach(this.openAnim, this.open ? 1.0F : 0.0F, 14.0F);
      if (this.hovered) {
         if (!this.prevHovered) {
            try {
               Managers.SOUND.playScroll();
            } catch (Throwable var30) {
            }
         }

         try {
            ClickGUI.currentDescription = class_1074.method_4662(this.module.getDescription(), new Object[0]);
         } catch (Throwable var29) {
            ClickGUI.currentDescription = this.module.getDescription();
         }

         ClickGUI.hoveredModule = this;
      }

      this.prevHovered = this.hovered;
      int var6 = NwDraw.accent(0);
      int var7 = NwDraw.accent(180);
      int var8 = NwDraw.mix(218103807, 402653183, this.animation2);
      int var9 = NwDraw.withAlpha(NwDraw.mix(-14275766, var6, 0.3F), 150);
      int var10 = NwDraw.withAlpha(NwDraw.mix(-14473406, var7, 0.22F), 135);
      int var11 = NwDraw.mix(var8, var9, this.animation);
      int var12 = NwDraw.mix(var8, var10, this.animation);
      if (this.animation > 0.02F) {
         NwDraw.shadow(var5, this.x, this.y, this.width, this.height, 4.0F, 5.0F, NwDraw.withAlpha(var6, Math.round(26.0F * this.animation)));
      }

      NwDraw.round(var5, this.x, this.y, this.width, this.height, 4.0F, var11, var12);
      if (this.animation > 0.02F) {
         NwDraw.border(
            var5, this.x, this.y, this.width, this.height, 4.0F, 0.5F, NwDraw.withAlpha(-1, Math.round(14.0F * this.animation + 6.0F * this.animation2))
         );
      }

      FontRenderer var13 = NwDraw.moduleFont();
      FontRenderer var14 = NwDraw.smallFont();
      float var15 = NwDraw.textTop(this.y + this.height / 2.0F, NwDraw.moduleSize());
      int var16 = NwDraw.mix(-3946796, -1, Math.max(this.animation, this.animation2 * 0.6F));
      float var17 = this.x + this.width - 5.0F;
      if (this.bindAnim > 0.02F) {
         String var18 = "Toggle";
         String var19 = "Hold";
         this.chipH = 9.0F;
         this.chipY = this.y + (this.height - this.chipH) / 2.0F;
         this.chipHoldW = NwDraw.width(var14, var19) + 6.0F;
         this.chipToggleW = NwDraw.width(var14, var18) + 6.0F;
         this.chipHoldX = var17 - this.chipHoldW;
         this.chipToggleX = this.chipHoldX - 2.0F - this.chipToggleW;
         float var20 = NwDraw.alpha;
         NwDraw.alpha = var20 * this.bindAnim;
         NwDraw.round(var5, this.chipToggleX, this.chipY, this.chipToggleW, this.chipH, 3.0F, this.holdbind ? 352321535 : NwDraw.withAlpha(var6, 150));
         NwDraw.round(var5, this.chipHoldX, this.chipY, this.chipHoldW, this.chipH, 3.0F, this.holdbind ? NwDraw.withAlpha(var6, 150) : 352321535);
         float var21 = NwDraw.textTop(this.chipY + this.chipH / 2.0F, NwDraw.smallSize());
         NwDraw.text(var14, var5, var18, this.chipToggleX + 3.0F, var21, this.holdbind ? -8552041 : -1);
         NwDraw.text(var14, var5, var19, this.chipHoldX + 3.0F, var21, this.holdbind ? -1 : -8552041);
         NwDraw.alpha = var20;
         var17 = this.chipToggleX - 3.0F;
      } else {
         if (this.hasSettings()) {
            int var31 = NwDraw.mix(-1261251102, -1, Math.max(this.animation2, this.openAnim));
            NwDraw.dots(var5, var17 - 3.5F, this.y + this.height / 2.0F, var31);
            var17 -= 11.0F;
         }

         String var32 = null;
         if (this.hovered && keyDown(340)) {
            var32 = this.module.isDrawn() ? (ru() ? "Виден" : "Visible") : (ru() ? "Скрыт" : "Hidden");
         } else {
            try {
               String var34 = this.module.getBind().getBind();
               if (var34 != null && !var34.equalsIgnoreCase("none")) {
                  var32 = shortBind(var34);
               }
            } catch (Throwable var28) {
            }
         }

         if (var32 != null) {
            float var35 = NwDraw.width(var14, var32) + 6.0F;
            float var37 = 9.0F;
            float var39 = this.y + (this.height - var37) / 2.0F;
            NwDraw.round(var5, var17 - var35, var39, var35, var37, 3.0F, NwDraw.mix(318767103, 587202559, this.animation2));
            NwDraw.text(
               var14,
               var5,
               var32,
               var17 - var35 + 3.0F,
               NwDraw.textTop(var39 + var37 / 2.0F, NwDraw.smallSize()),
               NwDraw.mix(-8552041, -3946796, this.animation2)
            );
            var17 -= var35 + 3.0F;
         }
      }

      String var33 = this.binding ? (ru() ? "Нажмите клавишу..." : "Press a key...") : this.display;
      float var36 = var17 - (this.x + 6.0F) - 2.0F;
      NwDraw.text(var13, var5, NwDraw.trim(var13, var33, var36), this.x + 6.0F, var15, this.binding ? NwDraw.mix(var16, var6, 0.5F) : var16);
      float var38 = this.y + this.height + 2.0F;
      float var40 = this.open ? this.layoutElements(var38) : 0.0F;
      if (!this.open && this.category_animation > 0.3F) {
         this.layoutElements(var38);
      }

      this.category_animation = NwDraw.approach(this.category_animation, var40, 15.0F);
      if (this.category_animation > 0.3F) {
         float var22 = this.category_animation;
         NwDraw.round(var5, this.x, var38, this.width, var22, 4.0F, 184549375, 117440511);
         NwDraw.border(var5, this.x, var38, this.width, var22, 4.0F, 0.5F, 234881023);
         Render2DEngine.addWindow(var5, this.x, var38, this.x + this.width, var38 + var22, 1.0);
         boolean var23 = NwDraw.hovered((double)var2, (double)var3, this.x, var38, this.width, var22);
         int var24 = var23 ? var2 : -10000;
         int var25 = var23 ? var3 : -10000;

         for (AbstractElement var27 : this.elements) {
            if (var27.isVisible()) {
               var27.render(var1, var24, var25, var4);
            }
         }

         Render2DEngine.popWindow();
      }
   }

   public void skipFrame() {
      this.hovered = false;
      this.prevHovered = false;
      this.animation = this.module.isEnabled() ? 1.0F : 0.0F;
      this.animation2 = 0.0F;
      this.bindAnim = this.binding ? 1.0F : 0.0F;
      this.openAnim = this.open ? 1.0F : 0.0F;
      this.category_animation = this.open ? this.layoutElements(this.y + this.height + 2.0F) : 0.0F;
   }

   private static String shortBind(String var0) {
      return switch (var0) {
         case "LEFT_CONTROL" -> "LCtrl";
         case "RIGHT_CONTROL" -> "RCtrl";
         case "LEFT_SHIFT" -> "LShift";
         case "RIGHT_SHIFT" -> "RShift";
         case "LEFT_ALT" -> "LAlt";
         case "RIGHT_ALT" -> "RAlt";
         default -> var0.length() > 8 ? var0.substring(0, 8) : var0;
      };
   }

   private String getSbind() {
      return shortBind(this.module.getBind().getBind());
   }

   private boolean settingsReady() {
      return this.open && this.category_animation > 0.3F;
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) {
      if (this.binding) {
         if (NwDraw.hovered((double)var1, (double)var2, this.chipToggleX, this.chipY, this.chipToggleW, this.chipH)) {
            this.holdbind = false;
            this.module.getBind().setHold(false);
         } else if (NwDraw.hovered((double)var1, (double)var2, this.chipHoldX, this.chipY, this.chipHoldW, this.chipH)) {
            this.holdbind = true;
            this.module.getBind().setHold(true);
         } else {
            this.module.setBind(var3, true, this.holdbind);
            this.binding = false;
         }
      } else if (this.hovered) {
         if (keyDown(340) && var3 == 0) {
            this.module.setDrawn(!this.module.isDrawn());
         } else if (keyDown(261) && var3 == 0) {
            boolean var8 = ru();
            DialogScreen var9 = new DialogScreen(
               TextureStorage.questionPic,
               var8 ? "Сброс модуля" : "Reset module",
               var8 ? "Вы уверены, что хотите сбросить " + this.module.getName() + "?" : "Are you sure you want to reset " + this.module.getName() + "?",
               var8 ? "Да" : "Yes",
               var8 ? "Нет" : "No",
               this::resetModule,
               () -> Module.mc.method_1507(ClickGUI.getClickGui())
            );
            Module.mc.method_1507(var9);
         } else {
            if (var3 == 0) {
               if (this.module.isToggleable()) {
                  this.module.toggle();
               }
            } else if (var3 == 1) {
               if (this.hasSettings()) {
                  this.setOpen(!this.isOpen());

                  try {
                     if (this.open) {
                        Managers.SOUND.playSwipeIn();
                     } else {
                        Managers.SOUND.playSwipeOut();
                     }
                  } catch (Throwable var7) {
                  }
               }
            } else if (var3 == 2) {
               this.binding = !this.binding;
               this.holdbind = this.module.getBind().isHold();
            }
         }
      } else {
         if (this.settingsReady()) {
            float var4 = this.y + this.height + 2.0F;
            if (NwDraw.hovered((double)var1, (double)var2, this.x, var4, this.width, this.category_animation)) {
               for (AbstractElement var6 : this.elements) {
                  if (var6.isVisible()) {
                     var6.mouseClicked(var1, var2, var3);
                  }
               }
            }
         }
      }
   }

   private void resetModule() {
      if (this.module.isEnabled()) {
         this.module.disable("reseting");
      }

      for (Setting var2 : this.module.getSettings()) {
         if (var2.getValue() instanceof ColorSetting var3) {
            var3.setDefault();
         } else {
            resetSetting(var2);
         }
      }

      Module.mc.method_1507(ClickGUI.getClickGui());
   }

   private static void resetSetting(Setting var0) {
      var0.setValue(var0.getDefaultValue());
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      if (this.isOpen()) {
         this.elements.forEach(var3x -> var3x.mouseReleased(var1, var2, var3));
      }
   }

   @Override
   public void charTyped(char var1, int var2) {
      if (this.isOpen()) {
         for (AbstractElement var4 : this.elements) {
            var4.charTyped(var1, var2);
         }
      }
   }

   @Override
   public void keyTyped(int var1) {
      if (this.isOpen()) {
         for (AbstractElement var3 : this.elements) {
            var3.keyTyped(var1);
         }
      }

      if (this.binding) {
         boolean var4 = ru();
         if (var1 != 256 && var1 != 261) {
            this.module.setBind(var1, false, this.holdbind);
            Command.sendMessage(this.module.getName() + (var4 ? " бинд изменён на " : " bind changed to ") + this.module.getBind().getBind());
         } else {
            this.module.setBind(-1, false, this.holdbind);
            Command.sendMessage((var4 ? "Удалён бинд с " : "Removed bind from ") + this.module.getName());
         }

         this.binding = false;
      }
   }

   @Override
   public void onGuiClosed() {
      this.elements.forEach(AbstractElement::onClose);
      this.binding = false;
   }

   public List<AbstractElement> getElements() {
      return this.elements;
   }

   public double getElementsHeight() {
      return (double)this.category_animation;
   }

   public double interp(double var1, double var3, float var5) {
      return var3 + (var1 - var3) * (double)var5;
   }

   public boolean isOpen() {
      return this.open;
   }

   public void setOpen(boolean var1) {
      this.open = var1;
   }

   @Override
   public void tick() {
      if (this.isOpen()) {
         this.ticksOpened++;
      } else {
         this.ticksOpened = 0;
      }
   }
}
