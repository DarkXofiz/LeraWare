package neva.ware.utility;

import net.minecraft.class_310;

public class TickTimer {
    private int time;

    public TickTimer() {
        this.reset();
    }

    public boolean passedTicks(long t) {
        if (this.getPassedTicks() < 0) {
            this.reset();
        }

        return this.getPassedTicks() >= t;
    }

    public boolean every(long ms) {
        if (this.getPassedTicks() < 0) {
            this.reset();
        }

        boolean passed = this.getPassedTicks() >= ms;
        if (passed) {
            this.reset();
        }

        return passed;
    }

    public void set(int t) {
        this.time = t;
    }

    public void reset() {
        class_310 mc = class_310.method_1551();
        this.time = mc != null && mc.field_1724 != null ? mc.field_1724.field_6012 : 0;
    }

    private int getPassedTicks() {
        class_310 mc = class_310.method_1551();
        return mc != null && mc.field_1724 != null ? mc.field_1724.field_6012 - this.time : 0;
    }
}
