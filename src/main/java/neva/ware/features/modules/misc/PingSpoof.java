package neva.ware.features.modules.misc;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2827;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class PingSpoof extends Module {
    public final Setting<Float> ping = new Setting<>("Ping", 500.0F, 1.0F, 1500.0F);
    private final ConcurrentHashMap<class_2827, Long> packets = new ConcurrentHashMap();

    public PingSpoof() {
        super("PingSpoof", Module.Category.MISC);
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send var1) {
        if (var1.getPacket() instanceof class_2827) {
            class_2827 var2 = var1.getPacket();
            if (this.packets.containsKey(var2)) {
                this.packets.remove(var2);
            } else {
                this.packets.put(var2, System.currentTimeMillis());
                var1.cancel();
            }
        }
    }

    @Override
    public void onUpdate() {
        this.flush(false);
    }

    @Override
    public void onDisable() {
        this.flush(true);
        this.packets.clear();
        super.onDisable();
    }

    private void flush(boolean var1) {
        if (!this.packets.isEmpty()) {
            long var2 = System.currentTimeMillis();
            float var4 = this.ping.getValue();

            for (Entry var6 : new ArrayList<>(this.packets.entrySet())) {
                if (var1 || (float)(var2 - (Long)var6.getValue()) >= var4) {
                    this.sendPacketSilent((class_2596<?>)var6.getKey());
                    this.packets.remove(var6.getKey());
                }
            }
        }
    }
}
