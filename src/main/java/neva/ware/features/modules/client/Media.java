package neva.ware.features.modules.client;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2561;
import net.minecraft.class_640;
import net.minecraft.class_7439;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.injection.accesors.IGameMessageS2CPacket;
import neva.ware.setting.Setting;
import org.jetbrains.annotations.NotNull;

public final class Media extends Module {
    public static final Setting<Boolean> skinProtect = new Setting<>("Skin Protect", true);
    public static final Setting<Boolean> nickProtect = new Setting<>("Nick Protect", true);

    public Media() {
        super("Media", Module.Category.CLIENT);
    }

    @EventHandler
    public void onPacketReceive(@NotNull PacketEvent.Receive e) {
        if (e.getPacket() instanceof class_7439 pac && nickProtect.getValue()) {
            for (class_640 ple : mc.field_1724.field_3944.method_2880()) {
                if (pac.comp_763().getString().contains(ple.method_2966().getName())) {
                    IGameMessageS2CPacket packet = e.getPacket();
                    packet.setContent(class_2561.method_30163(pac.comp_763().getString().replace(ple.method_2966().getName(), "Protected")));
                }
            }
        }
    }
}
