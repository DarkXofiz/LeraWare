package neva.ware.utility;

import net.minecraft.class_1713;
import net.minecraft.class_310;
import net.minecraft.class_490;
import net.minecraft.class_746;
import neva.ware.features.modules.combat.AutoTotem;

public final class AutoInv {
    private AutoInv() {
    }

    public static void swapInv(AutoTotem var0, int var1) {
        class_310 var2 = class_310.method_1551();
        if (var2 != null && var2.field_1724 != null && var2.field_1761 != null) {
            class_746 var3 = var2.field_1724;
            boolean var4 = false;

            try {
                if (var2.field_1755 == null && var2.method_18854()) {
                    var2.method_1507(new class_490(var3));
                    var4 = true;
                }
            } catch (Throwable var13) {
                var4 = false;
            }

            try {
                var2.field_1761.method_2906(var3.field_7512.field_7763, var1, 40, class_1713.field_7791, var3);
            } finally {
                if (var4) {
                    try {
                        var2.method_1507(null);
                    } catch (Throwable var12) {
                    }
                }
            }
        }
    }
}
