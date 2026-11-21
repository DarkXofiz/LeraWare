package neva.ware.features.modules.render;

import net.minecraft.class_4587;
import net.minecraft.class_5498;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import neva.ware.utility.render.animation.AnimationUtility;

public class NoCameraClip extends Module {
    public Setting<Boolean> antiFront = new Setting<>("AntiFront", false);
    public Setting<Float> distance = new Setting<>("Distance", 3.0F, 1.0F, 20.0F);
    private float animation;

    public NoCameraClip() {
        super("NoCameraClip", Module.Category.RENDER);
    }

    @Override
    public void onRender3D(class_4587 matrix) {
        if (mc.field_1690.method_31044() == class_5498.field_26664) {
            this.animation = AnimationUtility.fast(this.animation, 0.0F, 10.0F);
        } else {
            this.animation = AnimationUtility.fast(this.animation, 1.0F, 10.0F);
        }

        if (mc.field_1690.method_31044() == class_5498.field_26666 && this.antiFront.getValue()) {
            mc.field_1690.method_31043(class_5498.field_26664);
        }
    }

    public float getDistance() {
        return 1.0F + (this.distance.getValue() - 1.0F) * this.animation;
    }
}
