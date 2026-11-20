package neva.ware.features.modules.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2940;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_745;
import net.minecraft.class_746;
import neva.ware.events.impl.EventAttack;
import neva.ware.setting.Setting;

public final class FakePlayerShield {
    public static final Setting<Boolean> shield = new Setting<>("Shield", Boolean.FALSE);
    public static final Setting<FakePlayerShield.Mode> mode = new Setting<>("Shield Mode", FakePlayerShield.Mode.Always, var0 -> shield.getValue());
    public static final Setting<FakePlayerShield.Hand> hand = new Setting<>("Shield Hand", FakePlayerShield.Hand.Offhand, var0 -> shield.getValue());
    public static final Setting<Integer> holdMin = new Setting<>("Hold Min", 20, 5, 200, var0 -> shield.getValue() && mode.is(FakePlayerShield.Mode.Random));
    public static final Setting<Integer> holdMax = new Setting<>("Hold Max", 60, 5, 200, var0 -> shield.getValue() && mode.is(FakePlayerShield.Mode.Random));
    public static final Setting<Integer> pauseMin = new Setting<>("Pause Min", 10, 1, 200, var0 -> shield.getValue() && mode.is(FakePlayerShield.Mode.Random));
    public static final Setting<Integer> pauseMax = new Setting<>("Pause Max", 40, 1, 200, var0 -> shield.getValue() && mode.is(FakePlayerShield.Mode.Random));
    public static final Setting<Boolean> axeBreak = new Setting<>("Axe Break", Boolean.TRUE, var0 -> shield.getValue());
    public static final Setting<Integer> cooldown = new Setting<>("Break Cooldown", 100, 20, 200, var0 -> shield.getValue() && axeBreak.getValue());
    public static final Setting<Boolean> sounds = new Setting<>("Shield Sounds", Boolean.TRUE, var0 -> shield.getValue());
    private static boolean raised;
    private static int raisedTicks;
    private static int stateTicks;
    private static int disabledTicks;
    private static class_1799 savedStack;
    private static boolean equipped;
    private static class_1268 equippedHand;
    private static class_2940<Byte> FLAGS;
    private static boolean flagsLooked;
    private static Field autoTotemF;

    private FakePlayerShield() {
    }

    private static class_2940<Byte> flags() {
        if (flagsLooked) {
            return FLAGS;
        } else {
            flagsLooked = true;

            try {
                Field var0 = null;

                try {
                    var0 = class_1309.class.getDeclaredField("field_6257");
                } catch (NoSuchFieldException var5) {
                }

                if (var0 == null) {
                    for (Field var4 : class_1309.class.getDeclaredFields()) {
                        if (Modifier.isStatic(var4.getModifiers()) && class_2940.class.isAssignableFrom(var4.getType())) {
                            var0 = var4;
                            break;
                        }
                    }
                }

                if (var0 != null) {
                    var0.setAccessible(true);
                    FLAGS = (class_2940<Byte>)var0.get(null);
                }
            } catch (Throwable var6) {
            }

            return FLAGS;
        }
    }

    private static boolean autoTotem(FakePlayer var0) {
        try {
            if (autoTotemF == null) {
                autoTotemF = FakePlayer.class.getDeclaredField("autoTotem");
                autoTotemF.setAccessible(true);
            }

            Object var1 = autoTotemF.get(var0);
            return var1 instanceof Setting && Boolean.TRUE.equals(((Setting)var1).getValue());
        } catch (Throwable var2) {
            return false;
        }
    }

    private static class_1268 mcHand(FakePlayer var0) {
        if (hand.is(FakePlayerShield.Hand.Mainhand)) {
            return class_1268.field_5808;
        } else {
            return autoTotem(var0) ? class_1268.field_5808 : class_1268.field_5810;
        }
    }

    private static class_1799 inHand(class_745 var0, class_1268 var1) {
        return var1 == class_1268.field_5808 ? var0.method_6047() : var0.method_6079();
    }

    private static void setUsing(class_745 var0, boolean var1) {
        class_2940 var2 = flags();
        if (var2 != null) {
            byte var3 = 0;
            if (var1) {
                var3 = (byte)(1 | (equippedHand == class_1268.field_5810 ? 2 : 0));
            }

            try {
                var0.method_5841().method_12778(var2, var3);
            } catch (Throwable var5) {
            }
        }
    }

    private static void sound(class_745 var0, String var1) {
        if (sounds.getValue()) {
            try {
                class_310 var2 = class_310.method_1551();
                class_3414 var3 = class_3414.method_47908(class_2960.method_60655("minecraft", var1));
                var2.field_1687
                    .method_43128(
                        var2.field_1724,
                        var0.method_23317(),
                        var0.method_23318(),
                        var0.method_23321(),
                        var3,
                        class_3419.field_15248,
                        1.0F,
                        0.8F + ThreadLocalRandom.current().nextFloat() * 0.4F
                    );
            } catch (Throwable var4) {
            }
        }
    }

    private static int rnd(int var0, int var1) {
        int var2 = Math.min(var0, var1);
        int var3 = Math.max(var0, var1);
        return var2 + ThreadLocalRandom.current().nextInt(var3 - var2 + 1);
    }

    private static void equip(FakePlayer var0, class_745 var1) {
        class_1268 var2 = mcHand(var0);
        if (equipped && equippedHand != var2) {
            unequip(var1);
        }

        boolean var3 = false;

        try {
            var3 = inHand(var1, var2).method_7909() == class_1802.field_8255;
        } catch (Throwable var5) {
        }

        if (!var3 || !equipped) {
            if (!equipped) {
                savedStack = inHand(var1, var2);
            }

            var1.method_6122(var2, new class_1799(class_1802.field_8255));
            equipped = true;
            equippedHand = var2;
            if (raised) {
                setUsing(var1, false);
                setUsing(var1, true);
            }
        }
    }

    private static void unequip(class_745 var0) {
        if (equipped) {
            setUsing(var0, false);

            try {
                boolean var1 = inHand(var0, equippedHand).method_7909() == class_1802.field_8255;
                if (var1 && savedStack != null && savedStack.method_7909() != class_1802.field_8255) {
                    var0.method_6122(equippedHand, savedStack);
                }
            } catch (Throwable var2) {
            }

            equipped = false;
            raised = false;
            savedStack = null;
        }
    }

    private static void lower(class_745 var0) {
        if (raised) {
            setUsing(var0, false);
        }

        raised = false;
        raisedTicks = 0;
    }

    private static void raise(class_745 var0) {
        if (!raised) {
            setUsing(var0, true);
        }

        raised = true;
    }

    public static void reset() {
        raised = false;
        raisedTicks = 0;
        stateTicks = 0;
        disabledTicks = 0;
        equipped = false;
        savedStack = null;
    }

    public static void tick(FakePlayer var0) {
        class_745 var1 = FakePlayer.fakePlayer;
        if (var1 != null) {
            if (!shield.getValue()) {
                unequip(var1);
            } else {
                equip(var0, var1);
                if (disabledTicks > 0) {
                    disabledTicks--;
                    lower(var1);
                    if (disabledTicks == 0 && mode.is(FakePlayerShield.Mode.Random)) {
                        stateTicks = rnd(pauseMin.getValue(), pauseMax.getValue());
                    }
                } else {
                    if (mode.is(FakePlayerShield.Mode.Always)) {
                        raise(var1);
                    } else {
                        if (stateTicks <= 0) {
                            if (raised) {
                                lower(var1);
                                stateTicks = rnd(pauseMin.getValue(), pauseMax.getValue());
                            } else {
                                raise(var1);
                                stateTicks = rnd(holdMin.getValue(), holdMax.getValue());
                            }
                        }

                        stateTicks--;
                    }

                    if (raised) {
                        raisedTicks++;
                        setUsing(var1, true);
                    }
                }
            }
        }
    }

    public static boolean isBlocking() {
        return shield.getValue() && equipped && raised && raisedTicks >= 5 && disabledTicks <= 0;
    }

    public static boolean onAttack(EventAttack var0) {
        class_745 var1 = FakePlayer.fakePlayer;
        if (var1 != null && var0.getEntity() == var1 && isBlocking()) {
            class_746 var2 = class_310.method_1551().field_1724;
            if (var2 == null) {
                return false;
            } else {
                double var3 = Math.toRadians(var1.method_36454());
                double var5 = -Math.sin(var3);
                double var7 = Math.cos(var3);
                double var9 = var2.method_23317() - var1.method_23317();
                double var11 = var2.method_23321() - var1.method_23321();
                if (var9 * var5 + var11 * var7 <= 0.0) {
                    return false;
                } else if (var0.isPre()) {
                    return true;
                } else {
                    boolean var13 = false;

                    try {
                        var13 = var2.method_6047().method_7909() instanceof class_1743;
                    } catch (Throwable var15) {
                    }

                    if (var13 && axeBreak.getValue()) {
                        disabledTicks = cooldown.getValue();
                        lower(var1);
                        sound(var1, "item.shield.break");
                    } else {
                        sound(var1, "item.shield.block");
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public static void disable() {
        class_745 var0 = FakePlayer.fakePlayer;
        if (var0 != null) {
            unequip(var0);
        }

        reset();
    }

    public static enum Hand {
        Offhand,
        Mainhand;
    }

    public static enum Mode {
        Always,
        Random;
    }
}
