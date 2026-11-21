package neva.ware.features.modules.combat;

import java.lang.reflect.Field;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2664;
import net.minecraft.class_2680;
import net.minecraft.class_2708;
import net.minecraft.class_2709;
import net.minecraft.class_2824;
import net.minecraft.class_2879;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2828.class_2831;
import neva.ware.events.impl.EventPostSync;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;
import neva.ware.utility.math.MathUtility;
import neva.ware.utility.player.InteractionUtility;
import neva.ware.utility.player.InventoryUtility;
import neva.ware.utility.player.SearchInvResult;

public final class Burrow extends Module {
    private final Setting<Burrow.Mode> mode = new Setting<>("Mode", Burrow.Mode.Default);
    private final Setting<Burrow.OffsetMode> offsetMode = new Setting<>("Mode", Burrow.OffsetMode.Smart, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Float> vClip = new Setting<>(
        "VClip", -9.0F, -256.0F, 256.0F, v -> this.offsetMode.getValue() == Burrow.OffsetMode.Constant && this.mode.getValue() == Burrow.Mode.Default
    );
    private final Setting<Boolean> scaleDown = new Setting<>("ScaleDown", false, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Boolean> scaleVelocity = new Setting<>("ScaleVelocity", false, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Boolean> scaleExplosion = new Setting<>("Scale-xplosion", false, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Float> scaleFactor = new Setting<>("ScaleFactor", 1.0F, 0.1F, 10.0F, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Integer> scaleDelay = new Setting<>("ScaleDelay", 250, 0, 1000, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Boolean> attack = new Setting<>("Attack", true, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Boolean> placeDisable = new Setting<>("PlaceDisable", false, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Boolean> wait = new Setting<>("Wait", true);
    private final Setting<Boolean> evade = new Setting<>(
        "Evade", false, v -> this.offsetMode.getValue() == Burrow.OffsetMode.Constant && this.mode.getValue() == Burrow.Mode.Default
    );
    private final Setting<Boolean> noVoid = new Setting<>(
        "NoVoid", false, v -> this.offsetMode.getValue() == Burrow.OffsetMode.Smart && this.mode.getValue() == Burrow.Mode.Default
    );
    private final Setting<Boolean> onGround = new Setting<>("OnGround", true, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Boolean> allowUp = new Setting<>("IgnoreHeadBlock", false, v -> this.mode.getValue() == Burrow.Mode.Default);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", true);
    private final Setting<Boolean> discrete = new Setting<>(
        "Discrete", true, v -> this.offsetMode.getValue() == Burrow.OffsetMode.Smart && this.mode.getValue() == Burrow.Mode.Default
    );
    private final Setting<Boolean> air = new Setting<>(
        "Air", false, v -> this.offsetMode.getValue() == Burrow.OffsetMode.Smart && this.mode.getValue() == Burrow.Mode.Default
    );
    private final Setting<Boolean> fallback = new Setting<>(
        "Fallback", true, v -> this.offsetMode.getValue() == Burrow.OffsetMode.Smart && this.mode.getValue() == Burrow.Mode.Default
    );
    private final Setting<Boolean> skipZero = new Setting<>(
        "SkipZero", true, v -> this.offsetMode.getValue() == Burrow.OffsetMode.Smart && this.mode.getValue() == Burrow.Mode.Default
    );
    private double motionY;
    private class_2338 startPos;
    private volatile double last_x;
    private volatile double last_y;
    private volatile double last_z;
    private final Timer scaleTimer = new Timer();
    private final Timer timer = new Timer();

    public Burrow() {
        super("Burrow", Module.Category.COMBAT);
    }

    @Override
    public void onEnable() {
        this.startPos = getPlayerPos();
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (this.mode.getValue() == Burrow.Mode.Default) {
            if (event.getPacket() instanceof class_2664) {
                if (this.scaleExplosion.getValue()) {
                    this.motionY = ((class_2664)event.getPacket()).method_11473();
                    this.scaleTimer.reset();
                }

                if (this.scaleVelocity.getValue()) {
                    return;
                }

                if (mc.field_1724 != null) {
                    this.motionY = ((class_2664)event.getPacket()).method_11473() / 8000.0;
                    this.scaleTimer.reset();
                }
            }

            if (event.getPacket() instanceof class_2708) {
                class_2708 packet = event.getPacket();
                double x = packet.method_11734();
                double y = packet.method_11735();
                double z = packet.method_11738();
                if (packet.method_11733().contains(class_2709.field_12400)) {
                    x += mc.field_1724.method_23317();
                }

                if (packet.method_11733().contains(class_2709.field_12398)) {
                    y += mc.field_1724.method_23318();
                }

                if (packet.method_11733().contains(class_2709.field_12403)) {
                    z += mc.field_1724.method_23321();
                }

                this.last_x = MathUtility.clamp(x, -3.0E7, 3.0E7);
                this.last_y = y;
                this.last_z = MathUtility.clamp(z, -3.0E7, 3.0E7);
            }
        }
    }

    @EventHandler
    public void onPostSync(EventPostSync e) {
        if (this.wait.getValue()) {
            class_2338 currentPos = getPlayerPos();
            if (!currentPos.equals(this.startPos)) {
                this.disable(ClientSettings.isRu() ? "Отключен из-за движения!" : "Disabled due to movement!");
                return;
            }
        }

        class_2338 pos = getPosition(mc.field_1724);
        if (!mc.field_1687.method_8320(pos).method_45474()) {
            if (!this.wait.getValue()) {
                this.disable(ClientSettings.isRu() ? "Невозможно поставить блок! Отключаю.." : "Can't place the block! Disabling..");
            }
        } else {
            for (class_1297 entity : mc.field_1687.method_18467(class_1297.class, new class_238(pos))) {
                if (entity != null && !mc.field_1724.equals(entity)) {
                    if (!(entity instanceof class_1511) || !this.attack.getValue()) {
                        if (!this.wait.getValue()) {
                            this.disable(ClientSettings.isRu() ? "Невозможно поставить блок! Отключаю.." : "Can't place the block on! Disabling..");
                        }

                        return;
                    }

                    class_2824 attackPacket = class_2824.method_34206(mc.field_1724, mc.field_1724.method_5715());
                    changeId(attackPacket, entity.method_5628());
                    this.sendPacket(attackPacket);
                    this.sendPacket(new class_2879(class_1268.field_5808));
                }
            }

            switch ((Burrow.Mode)this.mode.getValue()) {
                case Default:
                    this.handleDefault(pos);
                    break;
                case Skull:
                    this.handleSkull(pos);
                    break;
                case Web:
                    this.handleWeb(pos);
            }
        }
    }

    private void handleWeb(class_2338 pos) {
        SearchInvResult webResult = InventoryUtility.findBlockInHotBar(class_2246.field_10343);
        if (!webResult.found()) {
            this.disable(ClientSettings.isRu() ? "Нет паутины!" : "No webs found!");
        } else {
            if (this.timer.passedMs(250L)) {
                if (this.rotate.getValue()) {
                    this.sendPacket(new class_2831(mc.field_1724.method_36454(), 90.0F, this.onGround.getValue()));
                }

                InventoryUtility.saveSlot();
                InteractionUtility.placeBlock(
                    pos,
                    InteractionUtility.Rotate.None,
                    InteractionUtility.Interact.Vanilla,
                    InteractionUtility.PlaceMode.Packet,
                    webResult.slot(),
                    false,
                    true
                );
                mc.field_1724.method_6104(class_1268.field_5808);
                this.timer.reset();
                InventoryUtility.returnSlot();
                if (!this.wait.getValue() || this.placeDisable.getValue()) {
                    this.disable(ClientSettings.isRu() ? "Успешно забурровился! Отключаю.." : "Successfully burrowed! Disabling..");
                }
            }
        }
    }

    private void handleSkull(class_2338 pos) {
        SearchInvResult skullResult = InventoryUtility.getSkull();
        if (!skullResult.found()) {
            this.disable(ClientSettings.isRu() ? "Нет голов!" : "No heads found!");
        } else {
            if (this.timer.passedMs(250L)) {
                if (this.rotate.getValue()) {
                    this.sendPacket(new class_2831(mc.field_1724.method_36454(), 90.0F, this.onGround.getValue()));
                }

                InventoryUtility.saveSlot();
                InteractionUtility.placeBlock(
                    pos,
                    InteractionUtility.Rotate.None,
                    InteractionUtility.Interact.Vanilla,
                    InteractionUtility.PlaceMode.Normal,
                    skullResult.slot(),
                    false,
                    true
                );
                mc.field_1724.method_6104(class_1268.field_5808);
                this.timer.reset();
                InventoryUtility.returnSlot();
                if (!this.wait.getValue() || this.placeDisable.getValue()) {
                    this.disable(ClientSettings.isRu() ? "Успешно забурровился! Отключаю.." : "Successfully burrowed! Disabling..");
                }
            }
        }
    }

    public void handleDefault(class_2338 pos) {
        if (!mc.field_1687.method_8320(class_2338.method_49638(mc.field_1724.method_19538().method_43206(class_2350.field_11036, 0.2))).method_51366()
            && mc.field_1724.field_5992) {
            class_1657 rEntity = mc.field_1724;
            class_2338 posHead = getPosition(rEntity).method_10084().method_10084();
            if (mc.field_1687.method_8320(posHead).method_45474() || !this.wait.getValue()) {
                if (!this.allowUp.getValue()) {
                    class_2338 upUp = pos.method_10086(2);
                    class_2680 upState = mc.field_1687.method_8320(upUp);
                    if (upState.method_51366()) {
                        if (!this.wait.getValue()) {
                            this.disable(
                                ClientSettings.isRu()
                                    ? "Над головой блок, невозможно забурровиться! Отключаю.."
                                    : "Above the head block, impossible to burrow! Disabling.."
                            );
                        }

                        return;
                    }
                }

                SearchInvResult obbyResult = InventoryUtility.findBlockInHotBar(class_2246.field_10540);
                SearchInvResult echestResult = InventoryUtility.findBlockInHotBar(class_2246.field_10443);
                int slot = obbyResult.found() && mc.field_1687.method_8320(pos.method_10074()).method_26204() != class_2246.field_10443
                    ? obbyResult.slot()
                    : echestResult.slot();
                if (slot == -1) {
                    this.disable(ClientSettings.isRu() ? "Нет блоков!" : "No Block found!");
                } else {
                    double y = this.applyScale(this.getY(rEntity, this.offsetMode.getValue()));
                    if (!Double.isNaN(y)) {
                        float[] r = InteractionUtility.getPlaceAngle(pos, InteractionUtility.Interact.Strict, true);
                        if (mc.method_1542()) {
                            this.disable(ClientSettings.isRu() ? "Дебил! Ты в одиночке.." : "Retard! You're in singleplayer..");
                        } else {
                            if (this.timer.passedMs(1000L)) {
                                if (this.rotate.getValue() && r != null) {
                                    if (rEntity.method_19538().equals(new class_243(this.last_x, this.last_y, this.last_z))) {
                                        this.sendPacket(new class_2831(r[0], r[1], this.onGround.getValue()));
                                    } else {
                                        this.sendPacket(
                                            new class_2830(
                                                rEntity.method_23317(), rEntity.method_23318(), rEntity.method_23321(), r[0], r[1], this.onGround.getValue()
                                            )
                                        );
                                    }
                                }

                                this.sendPacket(
                                    new class_2829(rEntity.method_23317(), rEntity.method_23318() + 0.42, rEntity.method_23321(), this.onGround.getValue())
                                );
                                this.sendPacket(
                                    new class_2829(rEntity.method_23317(), rEntity.method_23318() + 0.75, rEntity.method_23321(), this.onGround.getValue())
                                );
                                this.sendPacket(
                                    new class_2829(rEntity.method_23317(), rEntity.method_23318() + 1.01, rEntity.method_23321(), this.onGround.getValue())
                                );
                                this.sendPacket(
                                    new class_2829(rEntity.method_23317(), rEntity.method_23318() + 1.16, rEntity.method_23321(), this.onGround.getValue())
                                );
                                InventoryUtility.saveSlot();
                                InteractionUtility.placeBlock(
                                    pos,
                                    InteractionUtility.Rotate.None,
                                    InteractionUtility.Interact.Vanilla,
                                    InteractionUtility.PlaceMode.Packet,
                                    slot,
                                    false,
                                    true
                                );
                                mc.field_1724.method_6104(class_1268.field_5808);
                                this.sendPacket(new class_2829(rEntity.method_23317(), y, rEntity.method_23321(), false));
                                this.timer.reset();
                                InventoryUtility.returnSlot();
                                if (!this.wait.getValue() || this.placeDisable.getValue()) {
                                    this.disable(ClientSettings.isRu() ? "Успешно забурровился! Отключаю.." : "Successfully burrowed! Disabling..");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void changeId(class_2824 packet, int id) {
        try {
            Field field = class_2824.class.getDeclaredField("field_12870");
            field.setAccessible(true);
            field.setInt(packet, id);
        } catch (Exception var3) {
        }
    }

    public double getY(class_1297 entity, Burrow.OffsetMode mode) {
        if (mode == Burrow.OffsetMode.Constant) {
            double y = entity.method_23318() + this.vClip.getValue().floatValue();
            if (this.evade.getValue() && Math.abs(y) < 1.0) {
                y = -1.0;
            }

            return y;
        } else {
            double d = this.getY(entity, 3.0, 10.0, true);
            if (Double.isNaN(d)) {
                d = this.getY(entity, -3.0, -10.0, false);
                if (Double.isNaN(d) && this.fallback.getValue()) {
                    return this.getY(entity, Burrow.OffsetMode.Constant);
                }
            }

            return d;
        }
    }

    public static class_2338 getPosition(class_1297 entity) {
        double y = entity.method_23318();
        if (entity.method_23318() - Math.floor(entity.method_23318()) > 0.5) {
            y = Math.ceil(entity.method_23318());
        }

        return class_2338.method_49637(entity.method_23317(), y, entity.method_23321());
    }

    public double getY(class_1297 entity, double min, double max, boolean add) {
        if ((!(min > max) || !add) && (!(max > min) || add)) {
            double x = entity.method_23317();
            double y = entity.method_23318();
            double z = entity.method_23321();
            boolean air = false;
            double lastOff = 0.0;
            class_2338 last = null;

            for (double off = min; add ? off < max : off > max; off = add ? ++off : --off) {
                class_2338 pos = class_2338.method_49637(x, y - off, z);
                if (!this.noVoid.getValue() || pos.method_10264() >= 0) {
                    if (this.skipZero.getValue() && Math.abs(y) < 1.0) {
                        air = false;
                        last = pos;
                        lastOff = y - off;
                    } else {
                        class_2680 state = mc.field_1687.method_8320(pos);
                        if ((this.air.getValue() || state.method_51366()) && state.method_26204() != class_2246.field_10124) {
                            air = false;
                        } else {
                            if (air) {
                                if (add) {
                                    return this.discrete.getValue() ? pos.method_10264() : y - off;
                                }

                                return this.discrete.getValue() ? last.method_10264() : lastOff;
                            }

                            air = true;
                        }

                        last = pos;
                        lastOff = y - off;
                    }
                }
            }

            return Double.NaN;
        } else {
            return Double.NaN;
        }
    }

    protected double applyScale(double value) {
        if ((!(value < mc.field_1724.method_23318()) || this.scaleDown.getValue())
            && (this.scaleExplosion.getValue() || this.scaleVelocity.getValue())
            && !this.scaleTimer.passedMs(this.scaleDelay.getValue().intValue())
            && this.motionY != 0.0) {
            if (value < mc.field_1724.method_23318()) {
                value -= this.motionY * this.scaleFactor.getValue().floatValue();
            } else {
                value += this.motionY * this.scaleFactor.getValue().floatValue();
            }

            return this.discrete.getValue() ? Math.floor(value) : value;
        } else {
            return value;
        }
    }

    public static class_2338 getPlayerPos() {
        return Math.abs(mc.field_1724.method_18798().method_10214()) > 0.1 ? class_2338.method_49638(mc.field_1724.method_19538()) : getPosition(mc.field_1724);
    }

    private static enum Mode {
        Default,
        Skull,
        Web;
    }

    public static enum OffsetMode {
        Constant,
        Smart;
    }
}
