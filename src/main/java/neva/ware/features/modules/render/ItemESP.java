package neva.ware.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Iterator;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.gui.font.FontRenderers;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.Render3DEngine;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4d;

public class ItemESP extends Module {
   public final Setting<SettingGroup> item = new Setting<>("Item", new SettingGroup(false, 0));
   private final Setting<Boolean> showNetherite = new Setting<>("Netherite-Gear", true).addToGroup(this.item);
   private final Setting<Boolean> showDiamond = new Setting<>("Diamond-Gear", true).addToGroup(this.item);
   private final Setting<Boolean> showGold = new Setting<>("Gold-Gear", true).addToGroup(this.item);
   private final Setting<Boolean> showIron = new Setting<>("Iron-Gear", true).addToGroup(this.item);
   private final Setting<Boolean> showLeather = new Setting<>("Leather-Gear", true).addToGroup(this.item);
   private final Setting<Boolean> showOtherGear = new Setting<>("OtherGear", true).addToGroup(this.item);
   private final Setting<Boolean> showOtherItem = new Setting<>("OtherItem", true).addToGroup(this.item);
   public final Setting<SettingGroup> color = new Setting<>("Color", new SettingGroup(false, 0));
   private final Setting<ColorSetting> netheriteColor = new Setting<>("Netherite", new ColorSetting(new Color(16733525).getRGB())).addToGroup(this.color);
   private final Setting<ColorSetting> diamondColor = new Setting<>("Diamond", new ColorSetting(new Color(5636095).getRGB())).addToGroup(this.color);
   private final Setting<ColorSetting> goldColor = new Setting<>("Gold", new ColorSetting(new Color(16777045).getRGB())).addToGroup(this.color);
   private final Setting<ColorSetting> ironColor = new Setting<>("Iron", new ColorSetting(new Color(11184810).getRGB())).addToGroup(this.color);
   private final Setting<ColorSetting> leatherColor = new Setting<>("Leather", new ColorSetting(new Color(10506797).getRGB())).addToGroup(this.color);
   private final Setting<ColorSetting> otherGearColor = new Setting<>("OtherGear", new ColorSetting(new Color(11141375).getRGB())).addToGroup(this.color);
   private final Setting<ColorSetting> otherItemColor = new Setting<>("OtherItem", new ColorSetting(new Color(16777215).getRGB())).addToGroup(this.color);
   private final Setting<ColorSetting> scolor = new Setting<>("ShadowColor", new ColorSetting(new Color(0).getRGB())).addToGroup(this.color);
   private final Setting<ItemESP.ESPMode> espMode = new Setting<>("Mode", ItemESP.ESPMode.None);
   private final Setting<Float> radius = new Setting<>("Radius", 1.0F, 0.1F, 5.0F, v -> this.espMode.getValue() == ItemESP.ESPMode.Circle);
   private final Setting<Boolean> useHudColor = new Setting<>("UseHudColor", true, v -> this.espMode.getValue() == ItemESP.ESPMode.Circle);
   private final Setting<Integer> cOffset = new Setting<>(
      "ColorOffset", 2, 1, 50, v -> this.espMode.getValue() == ItemESP.ESPMode.Circle && this.useHudColor.getValue()
   );
   private final Setting<ColorSetting> circleColor = new Setting<>(
      "CircleColor", new ColorSetting(new Color(-1).getRGB()), v -> this.espMode.getValue() == ItemESP.ESPMode.Circle && !this.useHudColor.getValue()
   );
   private final Setting<Integer> cPoints = new Setting<>("CirclePoints", 12, 3, 32, v -> this.espMode.getValue() == ItemESP.ESPMode.Circle);
   private final Setting<Boolean> shadow = new Setting<>("Shadow", true);

   public ItemESP() {
      super("ItemESP", Module.Category.RENDER);
   }

   private ItemESP.ItemGroup getItemGroup(class_1542 itemEntity) {
      String name = itemEntity.method_6983().method_7909().method_7876().toLowerCase();
      if (name.contains("netherite_helmet")
         || name.contains("netherite_chestplate")
         || name.contains("netherite_leggings")
         || name.contains("netherite_boots")
         || name.contains("netherite_sword")
         || name.contains("netherite_pickaxe")
         || name.contains("netherite_axe")) {
         return ItemESP.ItemGroup.Netherite;
      } else if (name.contains("diamond_helmet")
         || name.contains("diamond_chestplate")
         || name.contains("diamond_leggings")
         || name.contains("diamond_boots")
         || name.contains("diamond_sword")
         || name.contains("diamond_pickaxe")
         || name.contains("diamond_axe")) {
         return ItemESP.ItemGroup.Diamond;
      } else if (name.contains("golden_helmet")
         || name.contains("golden_chestplate")
         || name.contains("golden_leggings")
         || name.contains("golden_boots")
         || name.contains("golden_sword")
         || name.contains("golden_pickaxe")
         || name.contains("golden_axe")) {
         return ItemESP.ItemGroup.Gold;
      } else if (name.contains("iron_helmet")
         || name.contains("iron_chestplate")
         || name.contains("iron_leggings")
         || name.contains("iron_boots")
         || name.contains("iron_sword")
         || name.contains("iron_pickaxe")
         || name.contains("iron_axe")) {
         return ItemESP.ItemGroup.Iron;
      } else if (name.contains("leather_helmet")
         || name.contains("leather_chestplate")
         || name.contains("leather_leggings")
         || name.contains("leather_boots")
         || name.contains("leather_sword")
         || name.contains("leather_pickaxe")
         || name.contains("leather_axe")) {
         return ItemESP.ItemGroup.Leather;
      } else {
         return !name.contains("mace") && !name.contains("trident") && !name.contains("enchanted_golden_apple")
            ? ItemESP.ItemGroup.OtherItem
            : ItemESP.ItemGroup.OtherGear;
      }
   }

   private int getGroupColor(ItemESP.ItemGroup group) {
      switch (group) {
         case Netherite:
            return this.showNetherite.getValue() ? this.netheriteColor.getValue().getColor() : this.otherItemColor.getValue().getColor();
         case Diamond:
            return this.showDiamond.getValue() ? this.diamondColor.getValue().getColor() : this.otherItemColor.getValue().getColor();
         case Gold:
            return this.showGold.getValue() ? this.goldColor.getValue().getColor() : this.otherItemColor.getValue().getColor();
         case Iron:
            return this.showIron.getValue() ? this.ironColor.getValue().getColor() : this.otherItemColor.getValue().getColor();
         case Leather:
            return this.showLeather.getValue() ? this.leatherColor.getValue().getColor() : this.otherItemColor.getValue().getColor();
         case OtherGear:
            return this.showOtherGear.getValue() ? this.otherGearColor.getValue().getColor() : this.otherItemColor.getValue().getColor();
         default:
            return this.otherItemColor.getValue().getColor();
      }
   }

   @Override
   public void onRender2D(class_332 context) {
      boolean allGroupsOff = !this.showNetherite.getValue()
         && !this.showDiamond.getValue()
         && !this.showGold.getValue()
         && !this.showIron.getValue()
         && !this.showLeather.getValue()
         && !this.showOtherGear.getValue()
         && !this.showOtherItem.getValue();
      boolean onlyOtherItem = !this.showNetherite.getValue()
         && !this.showDiamond.getValue()
         && !this.showGold.getValue()
         && !this.showIron.getValue()
         && !this.showLeather.getValue()
         && !this.showOtherGear.getValue()
         && this.showOtherItem.getValue();
      Iterator any = mc.field_1687.method_18112().iterator();

      while (true) {
         class_1297 ent;
         ItemESP.ItemGroup group;
         while (true) {
            if (!any.hasNext()) {
               if (this.espMode.getValue() == ItemESP.ESPMode.Rect) {
                  boolean anyx = false;

                  for (class_1297 entx : mc.field_1687.method_18112()) {
                     if (entx instanceof class_1542) {
                        class_243[] vectors = getPoints(entx);
                        Vector4d position = null;

                        for (class_243 vector : vectors) {
                           vector = Render3DEngine.worldSpaceToScreenSpace(new class_243(vector.field_1352, vector.field_1351, vector.field_1350));
                           if (vector.field_1350 > 0.0 && vector.field_1350 < 1.0) {
                              if (position == null) {
                                 position = new Vector4d(vector.field_1352, vector.field_1351, vector.field_1350, 0.0);
                              }

                              position.x = Math.min(vector.field_1352, position.x);
                              position.y = Math.min(vector.field_1351, position.y);
                              position.z = Math.max(vector.field_1352, position.z);
                              position.w = Math.max(vector.field_1351, position.w);
                           }
                        }

                        if (position != null) {
                           anyx = true;
                        }
                     }
                  }

                  if (!anyx) {
                     return;
                  }

                  Matrix4f matrix = context.method_51448().method_23760().method_23761();
                  Render2DEngine.setupRender();
                  RenderSystem.setShader(class_757::method_34540);
                  class_287 bufferBuilder = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);

                  for (class_1297 entxx : mc.field_1687.method_18112()) {
                     if (entxx instanceof class_1542) {
                        class_243[] vectors = getPoints(entxx);
                        Vector4d position = null;

                        for (class_243 vectorx : vectors) {
                           vectorx = Render3DEngine.worldSpaceToScreenSpace(new class_243(vectorx.field_1352, vectorx.field_1351, vectorx.field_1350));
                           if (vectorx.field_1350 > 0.0 && vectorx.field_1350 < 1.0) {
                              if (position == null) {
                                 position = new Vector4d(vectorx.field_1352, vectorx.field_1351, vectorx.field_1350, 0.0);
                              }

                              position.x = Math.min(vectorx.field_1352, position.x);
                              position.y = Math.min(vectorx.field_1351, position.y);
                              position.z = Math.max(vectorx.field_1352, position.z);
                              position.w = Math.max(vectorx.field_1351, position.w);
                           }
                        }

                        if (position != null) {
                           float posX = (float)position.x;
                           float posY = (float)position.y;
                           float endPosX = (float)position.z;
                           float endPosY = (float)position.w;
                           this.drawRect(bufferBuilder, matrix, posX, posY, endPosX, endPosY);
                        }
                     }
                  }

                  Render2DEngine.endBuilding(bufferBuilder);
                  Render2DEngine.endRender();
               }

               return;
            }

            ent = (class_1297)any.next();
            if (ent instanceof class_1542 itemEntity) {
               group = this.getItemGroup(itemEntity);
               if (!allGroupsOff) {
                  if (this.showOtherItem.getValue()) {
                     break;
                  }

                  boolean shouldRender = false;
                  switch (group) {
                     case Netherite:
                        shouldRender = this.showNetherite.getValue();
                        break;
                     case Diamond:
                        shouldRender = this.showDiamond.getValue();
                        break;
                     case Gold:
                        shouldRender = this.showGold.getValue();
                        break;
                     case Iron:
                        shouldRender = this.showIron.getValue();
                        break;
                     case Leather:
                        shouldRender = this.showLeather.getValue();
                        break;
                     case OtherGear:
                        shouldRender = this.showOtherGear.getValue();
                        break;
                     case OtherItem:
                        shouldRender = false;
                  }

                  if (shouldRender) {
                     break;
                  }
               }
            }
         }

         int groupColor = this.getGroupColor(group);
         if (onlyOtherItem && group != ItemESP.ItemGroup.OtherItem) {
            groupColor = this.otherItemColor.getValue().getColor();
         }

         class_243[] vectors = getPoints(ent);
         Vector4d position = null;

         for (class_243 vectorxx : vectors) {
            vectorxx = Render3DEngine.worldSpaceToScreenSpace(new class_243(vectorxx.field_1352, vectorxx.field_1351, vectorxx.field_1350));
            if (vectorxx.field_1350 > 0.0 && vectorxx.field_1350 < 1.0) {
               if (position == null) {
                  position = new Vector4d(vectorxx.field_1352, vectorxx.field_1351, vectorxx.field_1350, 0.0);
               }

               position.x = Math.min(vectorxx.field_1352, position.x);
               position.y = Math.min(vectorxx.field_1351, position.y);
               position.z = Math.max(vectorxx.field_1352, position.z);
               position.w = Math.max(vectorxx.field_1351, position.w);
            }
         }

         if (position != null) {
            float posX = (float)position.x;
            float posY = (float)position.y;
            float endPosX = (float)position.z;
            float diff = (endPosX - posX) / 2.0F;
            float textWidth = FontRenderers.sf_bold_mini.getStringWidth(ent.method_5476().getString()) * 1.0F;
            float tagX = (posX + diff - textWidth / 2.0F) * 1.0F;
            if (this.shadow.getValue()) {
               Render2DEngine.drawBlurredShadow(
                  context.method_51448(),
                  tagX - 2.0F,
                  posY - 13.0F,
                  FontRenderers.sf_bold_mini.getStringWidth(ent.method_5476().getString()) + 4.0F,
                  10.0F,
                  14,
                  this.scolor.getValue().getColorObject()
               );
            }

            FontRenderers.sf_bold_mini.drawString(context.method_51448(), ent.method_5476().getString(), (double)tagX, (double)(posY - 10.0F), groupColor);
         }
      }
   }

   @Override
   public void onRender3D(class_4587 stack) {
      if (this.espMode.getValue() == ItemESP.ESPMode.Circle) {
         for (class_1297 ent : mc.field_1687.method_18112()) {
            if (ent instanceof class_1542) {
               Render3DEngine.drawCircle3D(
                  stack,
                  ent,
                  this.radius.getValue(),
                  this.circleColor.getValue().getColor(),
                  this.cPoints.getValue(),
                  this.useHudColor.getValue(),
                  this.cOffset.getValue()
               );
            }
         }
      }
   }

   private void drawRect(class_287 bufferBuilder, Matrix4f stack, float posX, float posY, float endPosX, float endPosY) {
      Color black = Color.BLACK;
      Render2DEngine.setRectPoints(bufferBuilder, stack, posX - 1.0F, posY, posX + 0.5F, endPosY + 0.5F, black, black, black, black);
      Render2DEngine.setRectPoints(bufferBuilder, stack, posX - 1.0F, posY - 0.5F, endPosX + 0.5F, posY + 1.0F, black, black, black, black);
      Render2DEngine.setRectPoints(bufferBuilder, stack, endPosX - 1.0F, posY, endPosX + 0.5F, endPosY + 0.5F, black, black, black, black);
      Render2DEngine.setRectPoints(bufferBuilder, stack, posX - 1.0F, endPosY - 1.0F, endPosX + 0.5F, endPosY + 0.5F, black, black, black, black);
      Render2DEngine.setRectPoints(
         bufferBuilder, stack, posX - 0.5F, posY, posX, endPosY, HudEditor.getColor(270), HudEditor.getColor(0), HudEditor.getColor(0), HudEditor.getColor(270)
      );
      Render2DEngine.setRectPoints(
         bufferBuilder,
         stack,
         posX,
         endPosY - 0.5F,
         endPosX,
         endPosY,
         HudEditor.getColor(0),
         HudEditor.getColor(180),
         HudEditor.getColor(180),
         HudEditor.getColor(0)
      );
      Render2DEngine.setRectPoints(
         bufferBuilder,
         stack,
         posX - 0.5F,
         posY,
         endPosX,
         posY + 0.5F,
         HudEditor.getColor(180),
         HudEditor.getColor(90),
         HudEditor.getColor(90),
         HudEditor.getColor(180)
      );
      Render2DEngine.setRectPoints(
         bufferBuilder,
         stack,
         endPosX - 0.5F,
         posY,
         endPosX,
         endPosY,
         HudEditor.getColor(90),
         HudEditor.getColor(270),
         HudEditor.getColor(270),
         HudEditor.getColor(90)
      );
   }

   @NotNull
   private static class_243[] getPoints(class_1297 ent) {
      class_238 axisAlignedBB = getBox(ent);
      return new class_243[]{
         new class_243(axisAlignedBB.field_1323, axisAlignedBB.field_1322, axisAlignedBB.field_1321),
         new class_243(axisAlignedBB.field_1323, axisAlignedBB.field_1325, axisAlignedBB.field_1321),
         new class_243(axisAlignedBB.field_1320, axisAlignedBB.field_1322, axisAlignedBB.field_1321),
         new class_243(axisAlignedBB.field_1320, axisAlignedBB.field_1325, axisAlignedBB.field_1321),
         new class_243(axisAlignedBB.field_1323, axisAlignedBB.field_1322, axisAlignedBB.field_1324),
         new class_243(axisAlignedBB.field_1323, axisAlignedBB.field_1325, axisAlignedBB.field_1324),
         new class_243(axisAlignedBB.field_1320, axisAlignedBB.field_1322, axisAlignedBB.field_1324),
         new class_243(axisAlignedBB.field_1320, axisAlignedBB.field_1325, axisAlignedBB.field_1324)
      };
   }

   @NotNull
   private static class_238 getBox(class_1297 ent) {
      double x = ent.field_6014 + (ent.method_23317() - ent.field_6014) * (double)Render3DEngine.getTickDelta();
      double y = ent.field_6036 + (ent.method_23318() - ent.field_6036) * (double)Render3DEngine.getTickDelta();
      double z = ent.field_5969 + (ent.method_23321() - ent.field_5969) * (double)Render3DEngine.getTickDelta();
      class_238 axisAlignedBB2 = ent.method_5829();
      return new class_238(
         axisAlignedBB2.field_1323 - ent.method_23317() + x - 0.05,
         axisAlignedBB2.field_1322 - ent.method_23318() + y,
         axisAlignedBB2.field_1321 - ent.method_23321() + z - 0.05,
         axisAlignedBB2.field_1320 - ent.method_23317() + x + 0.05,
         axisAlignedBB2.field_1325 - ent.method_23318() + y + 0.15,
         axisAlignedBB2.field_1324 - ent.method_23321() + z + 0.05
      );
   }

   private static enum ESPMode {
      Rect,
      Circle,
      None;
   }

   private static enum ItemGroup {
      Netherite,
      Diamond,
      Gold,
      Iron,
      Leather,
      OtherGear,
      OtherItem;
   }
}
