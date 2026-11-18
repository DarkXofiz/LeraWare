package neva.ware.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventTick;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.Timer;
import neva.ware.utility.math.PredictUtility;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.render.BlockAnimationUtility;
import neva.ware.utility.world.HoleUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class HoleFill extends Module {
   private final Setting<InteractionUtility.Rotate> rotate = new Setting<>("Rotate", InteractionUtility.Rotate.None);
   private final Setting<InteractionUtility.Interact> interactMode = new Setting<>("Interact Mode", InteractionUtility.Interact.Vanilla);
   private final Setting<Float> placeRange = new Setting<>("Range", 5.0F, 1.0F, 6.0F);
   private final Setting<Float> placeWallRange = new Setting<>("WallRange", 5.0F, 1.0F, 6.0F);
   private final Setting<Integer> actionShift = new Setting<>("BLock Per Tick", 1, 1, 4);
   private final Setting<Integer> actionInterval = new Setting<>("Delay", 0, 0, 5);
   private final Setting<Boolean> jumpDisable = new Setting<>("Jump Disable", false);
   private final Setting<HoleFill.FillBlocks> blocks = new Setting<>("Blocks", HoleFill.FillBlocks.All);
   private final Setting<SettingGroup> fill = new Setting<>("Fill Holes", new SettingGroup(true, 0));
   private final Setting<Boolean> selfFill = new Setting<>("Self Fill", false).addToGroup(this.fill);
   private final Setting<HoleFill.SelfFillMode> selfFillMode = new Setting<>("Self Fill Mode", HoleFill.SelfFillMode.Burrow).addToGroup(this.fill);
   private final Setting<Boolean> fillSingle = new Setting<>("Single", true).addToGroup(this.fill);
   private final Setting<Boolean> fillDouble = new Setting<>("Double", false).addToGroup(this.fill);
   private final Setting<Boolean> fillQuad = new Setting<>("Quad", false).addToGroup(this.fill);
   private final Setting<HoleFill.Mode> mode = new Setting<>("Mode", HoleFill.Mode.Always);
   private final Setting<Float> rangeToTarget = new Setting<>("Range To Target", 2.0F, 1.0F, 5.0F, v -> this.mode.getValue() == HoleFill.Mode.Target);
   private final Setting<Boolean> autoDisable = new Setting<>("Auto Disable", false);
   private final Setting<InteractionUtility.PlaceMode> placeMode = new Setting<>("Place Mode", InteractionUtility.PlaceMode.Packet);
   private final Setting<SettingGroup> renderCategory = new Setting<>("Render", new SettingGroup(false, 0));
   private final Setting<BlockAnimationUtility.BlockRenderMode> renderMode = new Setting<>("Render Mode", BlockAnimationUtility.BlockRenderMode.All)
      .addToGroup(this.renderCategory);
   private final Setting<BlockAnimationUtility.BlockAnimationMode> animationMode = new Setting<>(
         "Animation Mode", BlockAnimationUtility.BlockAnimationMode.Fade
      )
      .addToGroup(this.renderCategory);
   private final Setting<ColorSetting> renderFillColor = new Setting<>("Render Fill Color", new ColorSetting(HudEditor.getColor(0)))
      .addToGroup(this.renderCategory);
   private final Setting<ColorSetting> renderLineColor = new Setting<>("Render Line Color", new ColorSetting(HudEditor.getColor(0)))
      .addToGroup(this.renderCategory);
   private final Setting<Integer> renderLineWidth = new Setting<>("Render Line Width", 2, 1, 5).addToGroup(this.renderCategory);
   private static final class_2382[] HOLE_VECTORS = new class_2382[]{
      new class_2382(-1, 0, -1), new class_2382(1, 0, -1), new class_2382(-1, 0, 1), new class_2382(1, 0, 1)
   };
   private boolean burrowWasEnabled = false;
   public static final Timer inactivityTimer = new Timer();
   private int tickCounter = 0;
   private boolean selfFillNeed = false;

   public HoleFill() {
      super("HoleFill", Module.Category.COMBAT);
   }

   @Override
   public void onEnable() {
      this.burrowWasEnabled = false;
      this.selfFillNeed = false;
   }

   @EventHandler
   public void onTick(EventTick event) {
      if (!fullNullCheck()) {
         if (this.jumpDisable.getValue() && mc.field_1724.field_6036 < mc.field_1724.method_23318()) {
            this.disable(ClientSettings.isRu() ? "Вы прыгнули! Выключаю..." : "You jumped! Disabling...");
         }

         if (this.tickCounter < this.actionInterval.getValue()) {
            this.tickCounter++;
         } else {
            if (HoleUtility.isHole(mc.field_1724.method_24515()) && mc.field_1687.method_8320(mc.field_1724.method_24515()).method_26215()) {
               this.burrowWasEnabled = false;
            }

            int slot = this.getBlockSlot();
            if (slot != -1) {
               List<class_2338> holes = this.findHoles();
               class_1657 target = Managers.COMBAT
                  .getTargets(this.placeRange.getValue())
                  .stream()
                  .min(Comparator.comparing(e -> mc.field_1724.method_5858(e)))
                  .orElse(null);
               if (this.mode.getValue() != HoleFill.Mode.Target || target != null) {
                  class_1657 predicted = PredictUtility.predictPlayer(target, 3);
                  int blocksPlaced = 0;

                  while (blocksPlaced < this.actionShift.getValue()) {
                     class_2338 pos;
                     if (this.mode.getValue() == HoleFill.Mode.Target) {
                        pos = holes.stream()
                           .filter(this::isHole)
                           .filter(p -> mc.field_1724.method_19538().method_1022(p.method_46558()) <= (double)this.placeRange.getValue().floatValue())
                           .filter(p -> predicted.method_19538().method_1022(p.method_46558()) <= (double)this.rangeToTarget.getValue().floatValue())
                           .filter(p -> {
                              if (p.equals(mc.field_1724.method_24515()) && this.selfFill.getValue()) {
                                 this.selfFillNeed = true;
                                 return true;
                              } else {
                                 return InteractionUtility.canPlaceBlock(p, this.interactMode.getValue(), false);
                              }
                           })
                           .min(Comparator.comparing(p -> mc.field_1724.method_19538().method_1022(p.method_46558())))
                           .orElse(null);
                     } else {
                        pos = holes.stream()
                           .filter(this::isHole)
                           .filter(p -> mc.field_1724.method_19538().method_1022(p.method_46558()) <= (double)this.placeRange.getValue().floatValue())
                           .filter(p -> {
                              if (p.equals(mc.field_1724.method_24515()) && this.selfFill.getValue()) {
                                 this.selfFillNeed = true;
                                 return true;
                              } else {
                                 return InteractionUtility.canPlaceBlock(p, this.interactMode.getValue(), false);
                              }
                           })
                           .min(Comparator.comparing(p -> mc.field_1724.method_19538().method_1022(p.method_46558())))
                           .orElse(null);
                     }

                     if (pos == null) {
                        if (this.autoDisable.getValue()) {
                           this.disable(ClientSettings.isRu() ? "Все холки заполнены!" : "All holes are filled!");
                        }
                        break;
                     }

                     List<class_2338> poses = this.getHolePoses(pos)
                        .stream()
                        .filter(
                           blockPosx -> mc.field_1724.method_19538().method_1022(blockPosx.method_46558()) <= (double)this.placeRange.getValue().floatValue()
                        )
                        .toList();
                     boolean broke = false;
                     if (this.selfFillNeed && HoleUtility.isHole(mc.field_1724.method_24515())) {
                        switch ((HoleFill.SelfFillMode)this.selfFillMode.getValue()) {
                           case Burrow:
                              if (!ModuleManager.burrow.isEnabled() && !this.burrowWasEnabled) {
                                 ModuleManager.burrow.enable();
                                 this.selfFillNeed = false;
                                 return;
                              }

                              return;
                           case Trap:
                              class_2338 headPos = class_2338.method_49638(mc.field_1724.method_19538()).method_10086(2);
                              if (mc.field_1687.method_8320(headPos).method_45474()
                                 && InteractionUtility.canPlaceBlock(headPos, this.interactMode.getValue(), false)) {
                                 this.selfFillNeed = false;
                                 InteractionUtility.placeBlock(
                                    headPos, this.rotate.getValue(), this.interactMode.getValue(), this.placeMode.getValue(), slot, true, false
                                 );
                                 BlockAnimationUtility.renderBlock(
                                    headPos,
                                    this.renderLineColor.getValue().getColorObject(),
                                    this.renderLineWidth.getValue(),
                                    this.renderFillColor.getValue().getColorObject(),
                                    this.animationMode.getValue(),
                                    this.renderMode.getValue()
                                 );
                                 this.tickCounter = 0;
                                 inactivityTimer.reset();
                                 return;
                              }

                              boolean placed = false;

                              for (int i = 0; i < 3; i++) {
                                 for (class_2382 vecAdd : HoleUtility.VECTOR_PATTERN) {
                                    class_2338 checkPos = headPos.method_10081(vecAdd).method_10087(i);
                                    if (mc.field_1687.method_8320(checkPos).method_45474()
                                       && InteractionUtility.canPlaceBlock(checkPos, this.interactMode.getValue(), false)) {
                                       InteractionUtility.placeBlock(
                                          checkPos, this.rotate.getValue(), this.interactMode.getValue(), this.placeMode.getValue(), slot, true, false
                                       );
                                       blocksPlaced++;
                                       this.tickCounter = 0;
                                       inactivityTimer.reset();
                                       placed = true;
                                       break;
                                    }
                                 }

                                 if (placed) {
                                    break;
                                 }
                              }

                              if (placed) {
                                 continue;
                              }
                        }
                     }

                     for (class_2338 blockPos : poses) {
                        if (!InteractionUtility.placeBlock(
                           blockPos, this.rotate.getValue(), this.interactMode.getValue(), this.placeMode.getValue(), slot, true, false
                        )) {
                           broke = true;
                           break;
                        }

                        blocksPlaced++;
                        this.tickCounter = 0;
                        BlockAnimationUtility.renderBlock(
                           blockPos,
                           this.renderLineColor.getValue().getColorObject(),
                           this.renderLineWidth.getValue(),
                           this.renderFillColor.getValue().getColorObject(),
                           this.animationMode.getValue(),
                           this.renderMode.getValue()
                        );
                        if (!mc.field_1724.method_24828()) {
                           return;
                        }

                        inactivityTimer.reset();
                     }

                     if (broke) {
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   @NotNull
   @Unmodifiable
   private List<class_2338> getHolePoses(class_2338 fromPos) {
      if (HoleUtility.validQuadBedrock(fromPos) || HoleUtility.validQuadIndestructible(fromPos)) {
         for (class_2382 vec : HOLE_VECTORS) {
            if (mc.field_1687.method_8320(fromPos.method_10081(vec)).method_45474()
               && mc.field_1687.method_8320(fromPos.method_10069(vec.method_10263(), 0, 0)).method_45474()
               && mc.field_1687.method_8320(fromPos.method_10069(0, 0, vec.method_10260())).method_45474()) {
               return List.of(
                  fromPos, fromPos.method_10081(vec), fromPos.method_10069(vec.method_10263(), 0, 0), fromPos.method_10069(0, 0, vec.method_10260())
               );
            }
         }
      }

      if (HoleUtility.validTwoBlockBedrock(fromPos) || HoleUtility.validTwoBlockIndestructible(fromPos)) {
         for (class_2382 vecx : HoleUtility.VECTOR_PATTERN) {
            if (mc.field_1687.method_8320(fromPos).method_45474() && mc.field_1687.method_8320(fromPos.method_10081(vecx)).method_45474()) {
               return List.of(fromPos, fromPos.method_10081(vecx));
            }
         }
      }

      return List.of(fromPos);
   }

   @NotNull
   private List<class_2338> findHoles() {
      List<class_2338> positions = new ArrayList<>();
      class_2338 centerPos = mc.field_1724.method_24515();
      int r = (int)Math.ceil((double)this.placeRange.getValue().floatValue()) + 1;
      int h = this.placeRange.getValue().intValue();

      for (int i = centerPos.method_10263() - r; i < centerPos.method_10263() + r; i++) {
         for (int j = centerPos.method_10264() - h; j < centerPos.method_10264() + h; j++) {
            for (int k = centerPos.method_10260() - r; k < centerPos.method_10260() + r; k++) {
               class_2338 pos = new class_2338(i, j, k);
               boolean foundEntity = false;
               if (this.isHole(pos) && !this.isFillingNow(pos)) {
                  for (class_1657 pe : Managers.ASYNC.getAsyncPlayers()) {
                     if (new class_238(pos).method_994(pe.method_5829())) {
                        foundEntity = true;
                        break;
                     }
                  }

                  if (!foundEntity) {
                     class_3965 wallCheck = mc.field_1687
                        .method_17742(
                           new class_3959(
                              InteractionUtility.getEyesPos(mc.field_1724),
                              pos.method_46558().method_43206(class_2350.field_11036, 0.5),
                              class_3960.field_17558,
                              class_242.field_1348,
                              mc.field_1724
                           )
                        );
                     if (wallCheck == null
                        || wallCheck.method_17783() != class_240.field_1332
                        || wallCheck.method_17777() == pos
                        || !(InteractionUtility.squaredDistanceFromEyes(pos.method_46558()) > this.placeWallRange.getPow2Value())) {
                        positions.add(pos);
                     }
                  }
               }
            }
         }
      }

      return positions;
   }

   private int getBlockSlot() {
      class_1799 stack = mc.field_1724.method_6047();
      if (!stack.method_7960() && this.isValidItem(stack.method_7909())) {
         return mc.field_1724.method_31548().field_7545;
      } else {
         for (int i = 0; i < 9; i++) {
            stack = mc.field_1724.method_31548().method_5438(i);
            if (!stack.method_7960() && this.isValidItem(stack.method_7909())) {
               return i;
            }
         }

         return -1;
      }
   }

   private boolean isFillingNow(class_2338 pos) {
      return BlockAnimationUtility.isRendering(pos);
   }

   private boolean isValidItem(class_1792 item) {
      if (!(item instanceof class_1747)) {
         return false;
      } else {
         class_2248 block = ((class_1747)item).method_7711();

         return switch ((HoleFill.FillBlocks)this.blocks.getValue()) {
            case Webs -> block == class_2246.field_10343;
            case Obsidian -> block == class_2246.field_10540;
            case Indestructible -> block == class_2246.field_10540
            || block == class_2246.field_22423
            || block == class_2246.field_22108
            || block == class_2246.field_23152;
            default -> true;
         };
      }
   }

   private boolean isHole(class_2338 pos) {
      return (HoleUtility.validTwoBlockIndestructible(pos) || HoleUtility.validTwoBlockBedrock(pos)) && this.fillDouble.getValue()
         || (HoleUtility.validQuadBedrock(pos) || HoleUtility.validQuadIndestructible(pos)) && this.fillQuad.getValue()
         || (HoleUtility.validBedrock(pos) || HoleUtility.validIndestructible(pos)) && this.fillSingle.getValue();
   }

   private static enum FillBlocks {
      All,
      Webs,
      Obsidian,
      Indestructible;
   }

   private static enum Mode {
      Always,
      Target;
   }

   private static enum SelfFillMode {
      Burrow,
      Trap;
   }
}
