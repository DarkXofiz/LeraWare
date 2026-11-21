package neva.ware.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2404;
import neva.ware.events.impl.EventCollision;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class Jesus extends Module {
    public final Setting<Jesus.Mode> mode = new Setting<>("Mode", Jesus.Mode.SOLID);

    public Jesus() {
        super("Jesus", Module.Category.MOVEMENT);
    }

    @EventHandler
    public void onCollide(EventCollision e) {
        if (e.getState().method_26204() instanceof class_2404) {
            e.setState(this.mode.is(Jesus.Mode.SOLID) ? class_2246.field_10443.method_9564() : class_2246.field_10540.method_9564());
        }
    }

    public static enum Mode {
        SOLID,
        SOLID2;
    }
}
