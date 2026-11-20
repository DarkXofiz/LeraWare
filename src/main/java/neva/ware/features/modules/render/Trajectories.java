package neva.ware.features.modules.render;

import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1667;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1771;
import net.minecraft.class_1776;
import net.minecraft.class_1779;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1803;
import net.minecraft.class_1823;
import net.minecraft.class_1828;
import net.minecraft.class_1835;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4587;
import net.minecraft.class_6880;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.Render3DEngine;

public class Trajectories extends Module {
    private final Setting<Trajectories.Mode> mode = new Setting<>("ColorMode", Trajectories.Mode.Sync);
    private final Setting<ColorSetting> color = new Setting<>("Color", new ColorSetting(575714484), v -> this.mode.getValue() == Trajectories.Mode.Custom);
    private final Setting<Trajectories.Mode> lmode = new Setting<>("LandedColorMode", Trajectories.Mode.Sync);
    private final Setting<ColorSetting> lcolor = new Setting<>(
        "LandedColor", new ColorSetting(575714484), v -> this.lmode.getValue() == Trajectories.Mode.Custom
    );

    public Trajectories() {
        super("Trajectories", Module.Category.RENDER);
    }

    private boolean isThrowable(class_1792 item) {
        return item instanceof class_1776
            || item instanceof class_1835
            || item instanceof class_1779
            || item instanceof class_1823
            || item instanceof class_1771
            || item instanceof class_1828
            || item instanceof class_1803;
    }

    private float getDistance(class_1792 item) {
        return item instanceof class_1753 ? 1.0F : 0.4F;
    }

    private float getThrowVelocity(class_1792 item) {
        if (item instanceof class_1828 || item instanceof class_1803) {
            return 0.5F;
        } else if (item instanceof class_1779) {
            return 0.59F;
        } else {
            return item instanceof class_1835 ? 2.0F : 1.5F;
        }
    }

    private int getThrowPitch(class_1792 item) {
        return !(item instanceof class_1828) && !(item instanceof class_1803) && !(item instanceof class_1779) ? 0 : 20;
    }

    @Override
    public void onRender3D(class_4587 stack) {
        if (!mc.field_1690.field_1842) {
            if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1690.method_31044().method_31034()) {
                class_1799 mainHand = mc.field_1724.method_6047();
                class_1799 offHand = mc.field_1724.method_6079();
                class_1268 hand;
                if (!(mainHand.method_7909() instanceof class_1753)
                    && !(mainHand.method_7909() instanceof class_1764)
                    && !this.isThrowable(mainHand.method_7909())) {
                    if (!(offHand.method_7909() instanceof class_1753)
                        && !(offHand.method_7909() instanceof class_1764)
                        && !this.isThrowable(offHand.method_7909())) {
                        return;
                    }

                    hand = class_1268.field_5810;
                } else {
                    hand = class_1268.field_5808;
                }

                boolean prev_bob = (Boolean)mc.field_1690.method_42448().method_41753();
                mc.field_1690.method_42448().method_41748(false);
                if (offHand.method_7909() instanceof class_1764
                        && class_1890.method_8225(
                                (class_6880)mc.field_1687
                                    .method_30349()
                                    .method_30530(class_1893.field_9108.method_58273())
                                    .method_40264(class_1893.field_9108)
                                    .get(),
                                offHand
                            )
                            != 0
                    || mainHand.method_7909() instanceof class_1764
                        && class_1890.method_8225(
                                (class_6880)mc.field_1687
                                    .method_30349()
                                    .method_30530(class_1893.field_9108.method_58273())
                                    .method_40264(class_1893.field_9108)
                                    .get(),
                                mainHand
                            )
                            != 0) {
                    this.calcTrajectory(hand == class_1268.field_5810 ? offHand.method_7909() : mainHand.method_7909(), mc.field_1724.method_36454() - 10.0F);
                    this.calcTrajectory(hand == class_1268.field_5810 ? offHand.method_7909() : mainHand.method_7909(), mc.field_1724.method_36454());
                    this.calcTrajectory(hand == class_1268.field_5810 ? offHand.method_7909() : mainHand.method_7909(), mc.field_1724.method_36454() + 10.0F);
                } else {
                    this.calcTrajectory(hand == class_1268.field_5810 ? offHand.method_7909() : mainHand.method_7909(), mc.field_1724.method_36454());
                }

                mc.field_1690.method_42448().method_41748(prev_bob);
            }
        }
    }

    private void calcTrajectory(class_1792 item, float yaw) {
        double x = Render2DEngine.interpolate(mc.field_1724.field_6014, mc.field_1724.method_23317(), Render3DEngine.getTickDelta());
        double y = Render2DEngine.interpolate(mc.field_1724.field_6036, mc.field_1724.method_23318(), Render3DEngine.getTickDelta());
        double z = Render2DEngine.interpolate(mc.field_1724.field_5969, mc.field_1724.method_23321(), Render3DEngine.getTickDelta());
        y = y + mc.field_1724.method_18381(mc.field_1724.method_18376()) - 0.1000000014901161;
        if (item == mc.field_1724.method_6047().method_7909()) {
            x -= class_3532.method_15362(yaw / 180.0F * (float) Math.PI) * 0.16F;
            z -= class_3532.method_15374(yaw / 180.0F * (float) Math.PI) * 0.16F;
        } else {
            x += class_3532.method_15362(yaw / 180.0F * (float) Math.PI) * 0.16F;
            z += class_3532.method_15374(yaw / 180.0F * (float) Math.PI) * 0.16F;
        }

        float maxDist = this.getDistance(item);
        double motionX = -class_3532.method_15374(yaw / 180.0F * (float) Math.PI)
            * class_3532.method_15362(mc.field_1724.method_36455() / 180.0F * (float) Math.PI)
            * maxDist;
        double motionY = -class_3532.method_15374((mc.field_1724.method_36455() - this.getThrowPitch(item)) / 180.0F * 3.141593F) * maxDist;
        double motionZ = class_3532.method_15362(yaw / 180.0F * (float) Math.PI)
            * class_3532.method_15362(mc.field_1724.method_36455() / 180.0F * (float) Math.PI)
            * maxDist;
        float power = mc.field_1724.method_6048() / 20.0F;
        power = (power * power + power * 2.0F) / 3.0F;
        if (power > 1.0F || power == 0.0F) {
            power = 1.0F;
        }

        float distance = class_3532.method_15355((float)(motionX * motionX + motionY * motionY + motionZ * motionZ));
        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;
        float pow = (item instanceof class_1753 ? power * 2.0F : (item instanceof class_1764 ? 2.2F : 1.0F)) * this.getThrowVelocity(item);
        motionX *= pow;
        motionY *= pow;
        motionZ *= pow;
        if (!mc.field_1724.method_24828()) {
            motionY += mc.field_1724.method_18798().method_10214();
        }

        for (int i = 0; i < 300; i++) {
            class_243 lastPos = new class_243(x, y, z);
            x += motionX;
            y += motionY;
            z += motionZ;
            if (mc.field_1687.method_8320(new class_2338((int)x, (int)y, (int)z)).method_26204() == class_2246.field_10382) {
                motionX *= 0.8;
                motionY *= 0.8;
                motionZ *= 0.8;
            } else {
                motionX *= 0.99;
                motionY *= 0.99;
                motionZ *= 0.99;
            }

            if (item instanceof class_1753) {
                motionY -= 0.05F;
            } else if (mc.field_1724.method_6047().method_7909() instanceof class_1764) {
                motionY -= 0.05F;
            } else {
                motionY -= 0.03F;
            }

            class_243 pos = new class_243(x, y, z);

            for (class_1297 ent : mc.field_1687.method_18112()) {
                if (!(ent instanceof class_1667)
                    && !ent.equals(mc.field_1724)
                    && ent.method_5829().method_994(new class_238(x - 0.3, y - 0.3, z - 0.3, x + 0.3, y + 0.3, z + 0.3))) {
                    Render3DEngine.OUTLINE_QUEUE
                        .add(
                            new Render3DEngine.OutlineAction(
                                ent.method_5829(),
                                this.lmode.getValue() == Trajectories.Mode.Sync ? HudEditor.getColor(i * 10) : this.lcolor.getValue().getColorObject(),
                                2.0F
                            )
                        );
                    Render3DEngine.FILLED_QUEUE
                        .add(
                            new Render3DEngine.FillAction(
                                ent.method_5829(),
                                this.lmode.getValue() == Trajectories.Mode.Sync
                                    ? Render2DEngine.injectAlpha(HudEditor.getColor(i * 10), 100)
                                    : this.lcolor.getValue().getColorObject()
                            )
                        );
                    break;
                }
            }

            class_3965 bhr = mc.field_1687.method_17742(new class_3959(lastPos, pos, class_3960.field_17559, class_242.field_1348, mc.field_1724));
            if (bhr != null && bhr.method_17783() == class_240.field_1332) {
                Render3DEngine.OUTLINE_SIDE_QUEUE
                    .add(
                        new Render3DEngine.OutlineSideAction(
                            new class_238(bhr.method_17777()),
                            this.lmode.getValue() == Trajectories.Mode.Sync ? HudEditor.getColor(i * 10) : this.lcolor.getValue().getColorObject(),
                            2.0F,
                            bhr.method_17780()
                        )
                    );
                Render3DEngine.FILLED_SIDE_QUEUE
                    .add(
                        new Render3DEngine.FillSideAction(
                            new class_238(bhr.method_17777()),
                            this.lmode.getValue() == Trajectories.Mode.Sync
                                ? Render2DEngine.injectAlpha(HudEditor.getColor(i * 10), 100)
                                : this.lcolor.getValue().getColorObject(),
                            bhr.method_17780()
                        )
                    );
                break;
            }

            if (y <= -65.0) {
                break;
            }

            if (motionX != 0.0 || motionY != 0.0 || motionZ != 0.0) {
                Render3DEngine.drawLine(
                    lastPos, pos, this.mode.getValue() == Trajectories.Mode.Sync ? HudEditor.getColor(i) : this.color.getValue().getColorObject()
                );
            }
        }
    }

    private static enum Mode {
        Custom,
        Sync;
    }
}
