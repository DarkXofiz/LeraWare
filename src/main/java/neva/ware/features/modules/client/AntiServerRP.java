package neva.ware.features.modules.client;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2720;
import net.minecraft.class_2856;
import net.minecraft.class_2856.class_2857;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.utility.math.MathUtility;
import org.jetbrains.annotations.NotNull;

public final class AntiServerRP extends Module {
    private boolean confirm;
    private boolean accepted;
    private int delay;

    public AntiServerRP() {
        super("AntiServerRP", Module.Category.CLIENT);
    }

    @EventHandler
    public void onPacketReceive(@NotNull PacketEvent.Receive e) {
        if (e.getPacket() instanceof class_2720) {
            this.confirm = true;
            this.accepted = false;
            this.delay = 0;
            e.cancel();
        }
    }

    @Override
    public void onUpdate() {
        if (this.confirm) {
            this.delay++;
            if (this.delay > MathUtility.random(15.0F, 30.0F) && !this.accepted) {
                this.sendPacket(new class_2856(mc.field_1724.method_5667(), class_2857.field_13016));
                this.accepted = true;
            }

            if (this.delay > MathUtility.random(40.0F, 60.0F) && this.accepted) {
                this.sendPacket(new class_2856(mc.field_1724.method_5667(), class_2857.field_13017));
                this.confirm = false;
            }
        }
    }
}
