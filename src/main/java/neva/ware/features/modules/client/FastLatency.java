package neva.ware.features.modules.client;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2639;
import net.minecraft.class_2805;
import net.minecraft.class_4587;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;
import neva.ware.utility.math.MathUtility;
import org.jetbrains.annotations.NotNull;

public final class FastLatency extends Module {
    private final Setting<Integer> delay = new Setting<>("Delay", 80, 0, 1000);
    private final Timer timer = new Timer();
    private final Timer limitTimer = new Timer();
    private long ping;
    public int resolvedPing;

    public FastLatency() {
        super("FastLatency", Module.Category.CLIENT);
    }

    @Override
    public void onRender3D(class_4587 stack) {
        if (this.timer.passedMs(5000L) && this.limitTimer.every(this.delay.getValue().intValue())) {
            this.sendPacket(new class_2805(1337, "w "));
            this.ping = System.currentTimeMillis();
            this.timer.reset();
        }
    }

    @EventHandler
    public void onPacketReceive(@NotNull PacketEvent.Receive e) {
        if (e.getPacket() instanceof class_2639 c && c.comp_2262() == 1337) {
            this.resolvedPing = (int)MathUtility.clamp((float)(System.currentTimeMillis() - this.ping), 0.0F, 1000.0F);
            this.timer.setMs(5000L);
        }
    }
}
