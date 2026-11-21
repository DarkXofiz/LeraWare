package neva.ware.features.modules.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_1060;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_8685;

public final class FakePlayerSkin {
    private static final int SIZE = 64;
    private static class_2960 blackId;
    private static boolean failed;

    private FakePlayerSkin() {
    }

    public static class_8685 apply(Object var0, class_8685 var1) {
        if (var0 != null && var1 != null && var0 == FakePlayer.fakePlayer) {
            class_2960 var2 = black();
            if (var2 == null) {
                return null;
            } else {
                try {
                    return new class_8685(var2, var1.comp_1911(), null, null, var1.comp_1629(), var1.comp_1630());
                } catch (Throwable var4) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private static class_2960 black() {
        if (blackId == null && !failed) {
            try {
                Constructor var0 = class_1011.class.getConstructor(int.class, int.class, boolean.class);
                class_1011 var1 = (class_1011)var0.newInstance(64, 64, false);
                Method var2 = null;

                for (String var6 : new String[]{"method_4305", "method_61941"}) {
                    try {
                        var2 = class_1011.class.getMethod(var6, int.class, int.class, int.class);
                        break;
                    } catch (NoSuchMethodException var13) {
                    }
                }

                if (var2 == null) {
                    throw new IllegalStateException("setColor");
                }

                for (int var15 = 0; var15 < 64; var15++) {
                    for (int var17 = 0; var17 < 64; var17++) {
                        var2.invoke(var1, var15, var17, -16777216);
                    }
                }

                Object var16 = null;

                for (Constructor var7 : class_1043.class.getConstructors()) {
                    Class[] var8 = var7.getParameterTypes();
                    if (var8.length == 1 && var8[0] == class_1011.class) {
                        var16 = var7.newInstance(var1);
                        break;
                    }
                }

                if (var16 == null) {
                    for (Constructor var28 : class_1043.class.getConstructors()) {
                        Class[] var31 = var28.getParameterTypes();
                        if (var31.length == 2 && var31[1] == class_1011.class && var31[0] == Supplier.class) {
                            Supplier var9 = () -> "nevaware_fakeplayer_black";
                            var16 = var28.newInstance(var9, var1);
                            break;
                        }
                    }
                }

                if (var16 == null) {
                    throw new IllegalStateException("texture");
                }

                class_310 var20 = class_310.method_1551();
                Object var23 = null;

                for (Method var34 : class_310.class.getMethods()) {
                    if (var34.getParameterCount() == 0 && var34.getReturnType() == class_1060.class && !Modifier.isStatic(var34.getModifiers())) {
                        var23 = var34.invoke(var20);
                        break;
                    }
                }

                if (var23 == null) {
                    throw new IllegalStateException("textureManager");
                }

                class_2960 var27 = class_2960.method_60655("nevaware", "fakeplayer_black");
                Method var30 = null;

                for (Method var11 : class_1060.class.getMethods()) {
                    Class[] var12 = var11.getParameterTypes();
                    if (var12.length == 2 && var12[0] == class_2960.class && var12[1] == class_1044.class && var11.getReturnType() == void.class) {
                        var30 = var11;
                        break;
                    }
                }

                if (var30 == null) {
                    throw new IllegalStateException("register");
                }

                var30.invoke(var23, var27, var16);
                blackId = var27;
            } catch (Throwable var14) {
                failed = true;
            }

            return blackId;
        } else {
            return blackId;
        }
    }
}
