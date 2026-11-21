package neva.ware.features.modules.combat;

import net.minecraft.class_243;
import neva.ware.setting.Setting;

public final class ElytraTargetBox {
    public static class_243 pos() {
        return ElytraTarget.predictBoxStatic instanceof Setting var1 && var1.getValue() instanceof Boolean var3 && !var3
            ? null
            : ElytraTarget.currentPredictedPos;
    }
}
