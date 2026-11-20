package neva.ware.features.modules.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1764;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2879;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_742;
import net.minecraft.class_7833;
import net.minecraft.class_811;
import neva.ware.NevaWare;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventHeldItemRenderer;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.Aura;
import neva.ware.injection.accesors.IHeldItemRenderer;
import neva.ware.setting.Setting;
import org.jetbrains.annotations.NotNull;

public class Animations extends Module {
    private final Setting<Boolean> onlyaura = new Setting<>("OnlyAura", false);
    public Setting<Boolean> oldAnimationsM = new Setting<>("DisableSwapMain", true);
    public Setting<Boolean> oldAnimationsOff = new Setting<>("DisableSwapOff", true);
    private final Setting<Animations.Mode> mode = new Setting<>("Mode", Animations.Mode.Default);
    public static Setting<Boolean> slowAnimation = new Setting<>("SlowAnimation", true);
    public static Setting<Integer> slowAnimationVal = new Setting<>("SlowValue", 12, 1, 50);
    public boolean flip;

    public Animations() {
        super("Animations", Module.Category.RENDER);
    }

    public boolean shouldAnimate() {
        return this.isEnabled()
            && (!this.onlyaura.getValue() || ModuleManager.aura.isEnabled() && Aura.target != null)
            && this.mode.getValue() != Animations.Mode.Normal;
    }

    public boolean shouldChangeAnimationDuration() {
        return this.isEnabled();
    }

    @Override
    public void onUpdate() {
        if (!fullNullCheck()) {
            if (this.oldAnimationsM.getValue() && ((IHeldItemRenderer)(Object)mc.method_1561().method_43336()).getEquippedProgressMainHand() <= 1.0F) {
                ((IHeldItemRenderer)(Object)mc.method_1561().method_43336()).setEquippedProgressMainHand(1.0F);
                ((IHeldItemRenderer)(Object)mc.method_1561().method_43336()).setItemStackMainHand(mc.field_1724.method_6047());
            }

            if (this.oldAnimationsOff.getValue() && ((IHeldItemRenderer)(Object)mc.method_1561().method_43336()).getEquippedProgressOffHand() <= 1.0F) {
                ((IHeldItemRenderer)(Object)mc.method_1561().method_43336()).setEquippedProgressOffHand(1.0F);
                ((IHeldItemRenderer)(Object)mc.method_1561().method_43336()).setItemStackOffHand(mc.field_1724.method_6079());
            }
        }
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof class_2879) {
            this.flip = !this.flip;
        }
    }

    private void renderSwordAnimation(class_4587 matrices, float f, float swingProgress, float equipProgress, class_1306 arm) {
        if (arm != class_1306.field_6182
            || this.mode.getValue() != Animations.Mode.Eleven
                && this.mode.getValue() != Animations.Mode.Ten
                && this.mode.getValue() != Animations.Mode.Nine
                && this.mode.getValue() != Animations.Mode.Three
                && this.mode.getValue() != Animations.Mode.Thirteen
                && this.mode.getValue() != Animations.Mode.Fourteen) {
            switch ((Animations.Mode)this.mode.getValue()) {
                case Default:
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    this.translateToViewModelOff(matrices);
                    this.applySwingOffset(matrices, arm, swingProgress);
                    this.translateBacklOff(matrices);
                    break;
                case One: {
                    float n = -0.4F * class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    this.applyEquipOffset(matrices, arm, n);
                    int i = arm == class_1306.field_6183 ? 1 : -1;
                    this.translateToViewModel(matrices);
                    float f1 = class_3532.method_15374(swingProgress * swingProgress * (float) Math.PI);
                    matrices.method_22907(class_7833.field_40716.rotationDegrees(i * (45.0F + f1 * -20.0F)));
                    float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    matrices.method_22907(class_7833.field_40718.rotationDegrees(i * g * -20.0F));
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(g * 0.0F));
                    matrices.method_22907(class_7833.field_40716.rotationDegrees(i * -45.0F));
                    this.translateBack(matrices);
                    break;
                }
                case Two:
                    this.applyEquipOffset(matrices, arm, 0.2F * class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) (Math.PI * 2)));
                    break;
                case Three: {
                    float n = -0.4F * class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    this.applyEquipOffset(matrices, arm, n);
                    int i = arm == class_1306.field_6183 ? 1 : -1;
                    this.translateToViewModel(matrices);
                    matrices.method_22907(class_7833.field_40716.rotationDegrees(i * (45.0F + f * -20.0F)));
                    matrices.method_22907(class_7833.field_40718.rotationDegrees(i * g * -70.0F));
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(-70.0F));
                    matrices.method_22907(class_7833.field_40716.rotationDegrees(i * -45.0F));
                    this.translateBack(matrices);
                    break;
                }
                case Four:
                    this.applyEquipOffset(matrices, arm, 0.0F);
                    this.translateToViewModel(matrices);
                    matrices.method_22907(
                        class_7833.field_40714.rotationDegrees(swingProgress > 0.0F ? -class_3532.method_15374(swingProgress * 13.0F) * 37.0F : 0.0F)
                    );
                    this.translateBack(matrices);
                    break;
                case Five: {
                    this.applyEquipOffset(matrices, arm, 0.0F);
                    int i = arm == class_1306.field_6183 ? 1 : -1;
                    float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    this.translateToViewModel(matrices);
                    matrices.method_22907(class_7833.field_40718.rotationDegrees(i * g * -20.0F));
                    this.translateBack(matrices);
                    break;
                }
                case Six:
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    this.translateToViewModel(matrices);
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(swingProgress * (this.flip ? 360.0F : -360.0F)));
                    this.translateBack(matrices);
                    break;
                case Seven:
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    float a = -class_3532.method_15374(swingProgress * 3.0F) / 2.0F + 1.0F;
                    matrices.method_22905(a, a, a);
                    break;
                case Eight:
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    this.translateToViewModel(matrices);
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(swingProgress * -360.0F));
                    this.translateBack(matrices);
                    break;
                case Nine: {
                    float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    this.applyEquipOffset(matrices, arm, 0.0F);
                    this.translateToViewModel(matrices);
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(50.0F));
                    matrices.method_22907(class_7833.field_40716.rotationDegrees(-30.0F * (1.0F - g) - 30.0F));
                    matrices.method_22907(class_7833.field_40718.rotationDegrees(110.0F));
                    this.translateBack(matrices);
                    break;
                }
                case Ten: {
                    float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    matrices.method_46416(0.0F, 0.0F, 0.0F);
                    this.applyEquipOffset(matrices, arm, 0.0F);
                    this.translateToViewModel(matrices);
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(50.0F));
                    matrices.method_22907(class_7833.field_40716.rotationDegrees(-60.0F * g - 50.0F));
                    matrices.method_22907(class_7833.field_40718.rotationDegrees(110.0F));
                    this.translateBack(matrices);
                    break;
                }
                case Eleven: {
                    float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    this.applyEquipOffset(matrices, arm, 0.0F);
                    this.translateToViewModel(matrices);
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(50.0F));
                    matrices.method_22907(class_7833.field_40716.rotationDegrees(-60.0F));
                    matrices.method_22907(class_7833.field_40718.rotationDegrees(110.0F + 20.0F * g));
                    this.translateBack(matrices);
                    break;
                }
                case Twelve: {
                    float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    this.applyEquipOffset(matrices, arm, 0.0F);
                    matrices.method_46416(0.0F, 0.0F, -g / 4.0F);
                    this.translateToViewModel(matrices);
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(-120.0F));
                    this.translateBack(matrices);
                    break;
                }
                case Thirteen: {
                    float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    this.applyEquipOffset(matrices, arm, 0.0F);
                    this.translateToViewModel(matrices);
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(-class_3532.method_15374(swingProgress * 3.0F) * 60.0F));
                    matrices.method_22907(class_7833.field_40718.rotationDegrees(-60.0F * g));
                    this.translateBack(matrices);
                    break;
                }
                case Fourteen:
                    if (swingProgress > 0.0F) {
                        float gx = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                        matrices.method_46416(0.56F, equipProgress * -0.2F - 0.5F, -0.7F);
                        this.translateToViewModel(matrices);
                        matrices.method_22907(class_7833.field_40716.rotationDegrees(45.0F));
                        matrices.method_22907(class_7833.field_40714.rotationDegrees(gx * -85.0F));
                        if (ModuleManager.viewModel.isEnabled()) {
                            matrices.method_46416(
                                -0.1F * ModuleManager.viewModel.scaleMain.getValue(),
                                0.28F * ModuleManager.viewModel.scaleMain.getValue(),
                                0.2F * ModuleManager.viewModel.scaleMain.getValue()
                            );
                        } else {
                            matrices.method_46416(-0.1F, 0.28F, 0.2F);
                        }

                        matrices.method_22907(class_7833.field_40714.rotationDegrees(-85.0F));
                        this.translateBack(matrices);
                    } else {
                        float n = -0.4F * class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                        float m = 0.2F * class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) (Math.PI * 2));
                        float f1 = -0.2F * class_3532.method_15374(swingProgress * (float) Math.PI);
                        matrices.method_46416(n, m, f1);
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        this.applySwingOffset(matrices, arm, swingProgress);
                    }
            }
        } else {
            this.applyEquipOffset(matrices, arm, equipProgress);
            matrices.method_46416(
                -ModuleManager.viewModel.positionMainX.getValue(),
                ModuleManager.viewModel.positionMainY.getValue(),
                ModuleManager.viewModel.positionMainZ.getValue()
            );
            this.applySwingOffset(matrices, arm, swingProgress);
            matrices.method_46416(
                ModuleManager.viewModel.positionMainX.getValue(),
                -ModuleManager.viewModel.positionMainY.getValue(),
                -ModuleManager.viewModel.positionMainZ.getValue()
            );
        }
    }

    public void renderFirstPersonItemCustom(
        class_742 player,
        float tickDelta,
        float pitch,
        class_1268 hand,
        float swingProgress,
        class_1799 item,
        float equipProgress,
        class_4587 matrices,
        class_4597 vertexConsumers,
        int light
    ) {
        if (!player.method_31550()) {
            boolean bl = hand == class_1268.field_5808;
            class_1306 arm = bl ? player.method_6068() : player.method_6068().method_5928();
            matrices.method_22903();
            float f = 0.0F;
            if (item.method_31574(class_1802.field_8399)) {
                boolean bl2 = class_1764.method_7781(item);
                boolean bl3 = arm == class_1306.field_6183;
                int i = bl3 ? 1 : -1;
                if (player.method_6115() && player.method_6014() > 0 && player.method_6058() == hand) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    matrices.method_46416(i * -0.4785682F, -0.094387F, 0.05731531F);
                    matrices.method_22907(class_7833.field_40714.rotationDegrees(-11.935F));
                    matrices.method_22907(class_7833.field_40716.rotationDegrees(i * 65.3F));
                    matrices.method_22907(class_7833.field_40718.rotationDegrees(i * -9.785F));
                    f = item.method_7935(mc.field_1724) - (mc.field_1724.method_6014() - tickDelta + 1.0F);
                    float g = f / class_1764.method_7775(item, mc.field_1724);
                    if (g > 1.0F) {
                        g = 1.0F;
                    }

                    if (g > 0.1F) {
                        float h = class_3532.method_15374((f - 0.1F) * 1.3F);
                        float j = g - 0.1F;
                        float k = h * j;
                        matrices.method_46416(k * 0.0F, k * 0.004F, k * 0.0F);
                    }

                    matrices.method_46416(g * 0.0F, g * 0.0F, g * 0.04F);
                    matrices.method_22905(1.0F, 1.0F, 1.0F + g * 0.2F);
                    matrices.method_22907(class_7833.field_40715.rotationDegrees(i * 45.0F));
                } else {
                    f = -0.4F * class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
                    float gx = 0.2F * class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) (Math.PI * 2));
                    float h = -0.2F * class_3532.method_15374(swingProgress * (float) Math.PI);
                    matrices.method_46416(i * f, gx, h);
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    this.applySwingOffset(matrices, arm, swingProgress);
                    if (bl2 && swingProgress < 0.001F && bl) {
                        matrices.method_46416(i * -0.641864F, 0.0F, 0.0F);
                        matrices.method_22907(class_7833.field_40716.rotationDegrees(i * 10.0F));
                    }
                }

                EventHeldItemRenderer event = new EventHeldItemRenderer(hand, item, equipProgress, matrices);
                NevaWare.EVENT_BUS.post(event);
                this.renderItem(player, item, bl3 ? class_811.field_4322 : class_811.field_4321, !bl3, matrices, vertexConsumers, light);
            } else {
                boolean bl2 = arm == class_1306.field_6183;
                float m = 0.0F;
                if (player.method_6115() && player.method_6014() > 0 && player.method_6058() == hand) {
                    int l = bl2 ? 1 : -1;
                    switch (item.method_7976()) {
                        case field_8952:
                        case field_8949:
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            break;
                        case field_8950:
                        case field_8946:
                            this.applyEatOrDrinkTransformationCustom(matrices, tickDelta, arm, item);
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            break;
                        case field_8953:
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            matrices.method_46416(l * -0.2785682F, 0.18344387F, 0.15731531F);
                            matrices.method_22907(class_7833.field_40714.rotationDegrees(-13.935F));
                            matrices.method_22907(class_7833.field_40716.rotationDegrees(l * 35.3F));
                            matrices.method_22907(class_7833.field_40718.rotationDegrees(l * -9.785F));
                            m = item.method_7935(mc.field_1724) - (mc.field_1724.method_6014() - tickDelta + 1.0F);
                            f = m / 20.0F;
                            f = (f * f + f * 2.0F) / 3.0F;
                            if (f > 1.0F) {
                                f = 1.0F;
                            }

                            if (f > 0.1F) {
                                float gx = class_3532.method_15374((m - 0.1F) * 1.3F);
                                float h = f - 0.1F;
                                float j = gx * h;
                                matrices.method_46416(j * 0.0F, j * 0.004F, j * 0.0F);
                            }

                            matrices.method_46416(f * 0.0F, f * 0.0F, f * 0.04F);
                            matrices.method_22905(1.0F, 1.0F, 1.0F + f * 0.2F);
                            matrices.method_22907(class_7833.field_40715.rotationDegrees(l * 45.0F));
                            break;
                        case field_8951:
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            matrices.method_46416(l * -0.5F, 0.7F, 0.1F);
                            matrices.method_22907(class_7833.field_40714.rotationDegrees(-55.0F));
                            matrices.method_22907(class_7833.field_40716.rotationDegrees(l * 35.3F));
                            matrices.method_22907(class_7833.field_40718.rotationDegrees(l * -9.785F));
                            m = item.method_7935(mc.field_1724) - (mc.field_1724.method_6014() - tickDelta + 1.0F);
                            f = m / 10.0F;
                            if (f > 1.0F) {
                                f = 1.0F;
                            }

                            if (f > 0.1F) {
                                float gx = class_3532.method_15374((m - 0.1F) * 1.3F);
                                float h = f - 0.1F;
                                float j = gx * h;
                                matrices.method_46416(j * 0.0F, j * 0.004F, j * 0.0F);
                            }

                            matrices.method_46416(0.0F, 0.0F, f * 0.2F);
                            matrices.method_22905(1.0F, 1.0F, 1.0F + f * 0.2F);
                            matrices.method_22907(class_7833.field_40715.rotationDegrees(l * 45.0F));
                            break;
                        case field_42717:
                            this.applyBrushTransformation(matrices, tickDelta, arm, item, equipProgress);
                    }
                } else if (player.method_6123()) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    int l = bl2 ? 1 : -1;
                    matrices.method_46416(l * -0.4F, 0.8F, 0.3F);
                    matrices.method_22907(class_7833.field_40716.rotationDegrees(l * 65.0F));
                    matrices.method_22907(class_7833.field_40718.rotationDegrees(l * -85.0F));
                } else {
                    this.renderSwordAnimation(matrices, f, swingProgress, equipProgress, arm);
                }

                EventHeldItemRenderer event = new EventHeldItemRenderer(hand, item, equipProgress, matrices);
                NevaWare.EVENT_BUS.post(event);
                this.renderItem(player, item, bl2 ? class_811.field_4322 : class_811.field_4321, !bl2, matrices, vertexConsumers, light);
            }

            matrices.method_22909();
        }
    }

    private void applyBrushTransformation(class_4587 matrices, float tickDelta, class_1306 arm, @NotNull class_1799 stack, float equipProgress) {
        this.applyEquipOffset(matrices, arm, equipProgress);
        float f = mc.field_1724.method_6014() - tickDelta + 1.0F;
        float g = 1.0F - f / stack.method_7935(mc.field_1724);
        float m = -15.0F + 75.0F * class_3532.method_15362(g * 45.0F * (float) Math.PI);
        if (arm != class_1306.field_6183) {
            matrices.method_22904(0.1, 0.83, 0.35);
            matrices.method_22907(class_7833.field_40714.rotationDegrees(-80.0F));
            matrices.method_22907(class_7833.field_40716.rotationDegrees(-90.0F));
            matrices.method_22907(class_7833.field_40714.rotationDegrees(m));
            matrices.method_22904(-0.3, 0.22, 0.35);
        } else {
            matrices.method_22904(-0.25, 0.22, 0.35);
            matrices.method_22907(class_7833.field_40714.rotationDegrees(-80.0F));
            matrices.method_22907(class_7833.field_40716.rotationDegrees(90.0F));
            matrices.method_22907(class_7833.field_40718.rotationDegrees(0.0F));
            matrices.method_22907(class_7833.field_40714.rotationDegrees(m));
        }
    }

    private void applyEquipOffset(@NotNull class_4587 matrices, class_1306 arm, float equipProgress) {
        int i = arm == class_1306.field_6183 ? 1 : -1;
        matrices.method_46416(i * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
    }

    private void applySwingOffset(@NotNull class_4587 matrices, class_1306 arm, float swingProgress) {
        int i = arm == class_1306.field_6183 ? 1 : -1;
        float f = class_3532.method_15374(swingProgress * swingProgress * (float) Math.PI);
        matrices.method_22907(class_7833.field_40716.rotationDegrees(i * (45.0F + f * -20.0F)));
        float g = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
        matrices.method_22907(class_7833.field_40718.rotationDegrees(i * g * -20.0F));
        matrices.method_22907(class_7833.field_40714.rotationDegrees(g * -80.0F));
        matrices.method_22907(class_7833.field_40716.rotationDegrees(i * -45.0F));
    }

    public void renderItem(
        class_1309 entity, class_1799 stack, class_811 renderMode, boolean leftHanded, class_4587 matrices, class_4597 vertexConsumers, int light
    ) {
        if (!stack.method_7960()) {
            mc.method_1480()
                .method_23177(
                    entity,
                    stack,
                    renderMode,
                    leftHanded,
                    matrices,
                    vertexConsumers,
                    entity.method_37908(),
                    light,
                    class_4608.field_21444,
                    entity.method_5628() + renderMode.ordinal()
                );
        }
    }

    private void applyEatOrDrinkTransformationCustom(class_4587 matrices, float tickDelta, class_1306 arm, @NotNull class_1799 stack) {
        float f = mc.field_1724.method_6014() - tickDelta + 1.0F;
        float g = f / stack.method_7935(mc.field_1724);
        if (g < 0.8F) {
            float h = class_3532.method_15379(class_3532.method_15362(f / 4.0F * (float) Math.PI) * 0.005F);
            matrices.method_46416(0.0F, h, 0.0F);
        }

        float h = 1.0F - (float)Math.pow(g, 27.0);
        int i = arm == class_1306.field_6183 ? 1 : -1;
        matrices.method_46416(h * 0.6F * i * ModuleManager.viewModel.eatX.getValue(), h * -0.5F * ModuleManager.viewModel.eatY.getValue(), h * 0.0F);
        matrices.method_22907(class_7833.field_40716.rotationDegrees(i * h * 90.0F));
        matrices.method_22907(class_7833.field_40714.rotationDegrees(h * 10.0F));
        matrices.method_22907(class_7833.field_40718.rotationDegrees(i * h * 30.0F));
    }

    private void translateToViewModel(class_4587 matrices) {
        if (ModuleManager.viewModel.isEnabled()) {
            matrices.method_46416(
                ModuleManager.viewModel.positionMainX.getValue(),
                ModuleManager.viewModel.positionMainY.getValue(),
                ModuleManager.viewModel.positionMainZ.getValue()
            );
        }
    }

    private void translateToViewModelOff(class_4587 matrices) {
        if (ModuleManager.viewModel.isEnabled()) {
            matrices.method_46416(
                -ModuleManager.viewModel.positionMainX.getValue(),
                ModuleManager.viewModel.positionMainY.getValue(),
                ModuleManager.viewModel.positionMainZ.getValue()
            );
        }
    }

    private void translateBack(class_4587 matrices) {
        if (ModuleManager.viewModel.isEnabled()) {
            matrices.method_46416(
                -ModuleManager.viewModel.positionMainX.getValue(),
                -ModuleManager.viewModel.positionMainY.getValue(),
                -ModuleManager.viewModel.positionMainZ.getValue()
            );
        }
    }

    private void translateBacklOff(class_4587 matrices) {
        if (ModuleManager.viewModel.isEnabled()) {
            matrices.method_46416(
                ModuleManager.viewModel.positionMainX.getValue(),
                -ModuleManager.viewModel.positionMainY.getValue(),
                -ModuleManager.viewModel.positionMainZ.getValue()
            );
        }
    }

    private static enum Mode {
        Normal,
        Default,
        One,
        Two,
        Three,
        Four,
        Five,
        Six,
        Seven,
        Eight,
        Nine,
        Ten,
        Eleven,
        Twelve,
        Thirteen,
        Fourteen;
    }
}
