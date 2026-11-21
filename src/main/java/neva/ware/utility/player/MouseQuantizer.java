package neva.ware.utility.player;

import net.minecraft.class_310;
import net.minecraft.class_3532;

public final class MouseQuantizer {
    private double cachedSensitivity = Double.NaN;
    private double cachedGcd;
    private double yawResidual;
    private double pitchResidual;

    public double getGcd() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1690 == null) {
            return this.cachedGcd;
        } else {
            double sensitivity = (Double)mc.field_1690.method_42495().method_41753();
            if (sensitivity != this.cachedSensitivity) {
                this.cachedSensitivity = sensitivity;
                double v = sensitivity * 0.6 + 0.2;
                this.cachedGcd = v * v * v * 1.2;
            }

            return this.cachedGcd;
        }
    }

    public void reset() {
        this.yawResidual = 0.0;
        this.pitchResidual = 0.0;
    }

    public float yaw(float current, float desired) {
        double gcd = this.getGcd();
        if (!(gcd > 1.0E-9)) {
            return desired;
        } else {
            double delta = class_3532.method_15338((double)desired - current) + this.yawResidual;
            long steps = Math.round(delta / gcd);
            double applied = steps * gcd;
            this.yawResidual = class_3532.method_15350(delta - applied, -gcd, gcd);
            return (float)(current + applied);
        }
    }

    public float pitch(float current, float desired) {
        double gcd = this.getGcd();
        if (!(gcd > 1.0E-9)) {
            return class_3532.method_15363(desired, -90.0F, 90.0F);
        } else {
            double delta = class_3532.method_15350(desired, -90.0, 90.0) - current + this.pitchResidual;
            long steps = Math.round(delta / gcd);
            double applied = steps * gcd;
            this.pitchResidual = class_3532.method_15350(delta - applied, -gcd, gcd);
            return (float)class_3532.method_15350(current + applied, -90.0, 90.0);
        }
    }
}
