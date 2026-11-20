package neva.ware.features.modules.render;

import net.minecraft.class_1657;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class NoBob extends Module {
    public static Setting<NoBob.Mode> mode = new Setting<>("Mode", NoBob.Mode.Sexy);

    public NoBob() {
        super("NoBob", Module.Category.RENDER);
    }

    public void bobView(class_4587 matrices, float tickDelta) {
        if (mc.method_1560() instanceof class_1657) {
            float g = -(mc.field_1724.field_5973 + (mc.field_1724.field_5973 - mc.field_1724.field_6039) * tickDelta);
            float h = class_3532.method_16439(tickDelta, mc.field_1724.field_7505, mc.field_1724.field_7483);
            matrices.method_22904(0.0, -Math.abs(g * h * (mode.is(NoBob.Mode.Sexy) ? 3.5E-4 : 0.0)), 0.0);
        }
    }

    public static enum Mode {
        Sexy,
        Off;
    }
}
