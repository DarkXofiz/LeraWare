package neva.ware.features.modules.render;

import net.minecraft.class_1309;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class LegitSpin extends Module {
    private static LegitSpin instance;
    public Setting<Float> speed = new Setting<>("Speed", 20.0F, 1.0F, 100.0F);
    private float angle;
    private long lastNanos;
    private boolean applied;
    private float savedBody;
    private float savedPrevBody;
    private float savedHead;
    private float savedPrevHead;

    public LegitSpin() {
        super("LegitSpin", Module.Category.RENDER);
        instance = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lastNanos = 0L;
        this.applied = false;
    }

    public static void apply(class_1309 var0) {
        LegitSpin var1 = instance;
        if (var1 != null && var0 != null && var1.isEnabled()) {
            if (mc != null && mc.field_1724 != null && var0 == mc.field_1724) {
                long var2 = System.nanoTime();
                float var4 = var1.lastNanos == 0L ? 0.0F : (float)(var2 - var1.lastNanos) / 1.0E9F;
                var1.lastNanos = var2;
                if (var4 > 0.25F) {
                    var4 = 0.0F;
                }

                var1.angle = (var1.angle + var1.speed.getValue() * 20.0F * var4) % 360.0F;
                if (!var1.applied) {
                    var1.savedBody = var0.field_6283;
                    var1.savedPrevBody = var0.field_6220;
                    var1.savedHead = var0.field_6241;
                    var1.savedPrevHead = var0.field_6259;
                    var1.applied = true;
                }

                var0.field_6283 = var1.angle;
                var0.field_6220 = var1.angle;
                var0.field_6241 = var1.angle;
                var0.field_6259 = var1.angle;
            }
        }
    }

    public static void restore(class_1309 var0) {
        LegitSpin var1 = instance;
        if (var1 != null && var1.applied && var0 != null) {
            if (mc != null && mc.field_1724 != null && var0 == mc.field_1724) {
                var0.field_6283 = var1.savedBody;
                var0.field_6220 = var1.savedPrevBody;
                var0.field_6241 = var1.savedHead;
                var0.field_6259 = var1.savedPrevHead;
                var1.applied = false;
            }
        }
    }
}
