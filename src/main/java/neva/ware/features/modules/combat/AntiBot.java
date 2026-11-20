package neva.ware.features.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_745;
import net.minecraft.class_1297.class_5529;
import neva.ware.events.impl.EventSync;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.misc.FakePlayer;
import neva.ware.features.modules.render.NameTags;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;

public final class AntiBot extends Module {
    public static ArrayList<class_1657> bots = new ArrayList();
    public Setting<Boolean> remove = new Setting<>("Remove", false);
    public Setting<Boolean> onlyAura = new Setting<>("OnlyAura", true);
    private final Setting<AntiBot.Mode> mode = new Setting<>("Mode", AntiBot.Mode.UUIDCheck);
    public Setting<Integer> checkticks = new Setting<>("checkTicks", 3, 0, 10, v -> this.mode.getValue() == AntiBot.Mode.MotionCheck);
    private final Timer clearTimer = new Timer();
    private int ticks = 0;

    public AntiBot() {
        super("AntiBot", Module.Category.COMBAT);
    }

    @EventHandler
    public void onSync(EventSync e) {
        if (!this.onlyAura.getValue()) {
            mc.field_1687.method_18456().forEach(this::markAsBot);
        } else if (Aura.target instanceof class_1657 ent) {
            this.markAsBot(ent);
        }

        if (this.remove.getValue()) {
            bots.forEach(b -> {
                try {
                    mc.field_1687.method_2945(b.method_5628(), class_5529.field_26998);
                } catch (Exception var2x) {
                }
            });
        }

        if (this.clearTimer.passedMs(10000L)) {
            bots.clear();
            this.ticks = 0;
            this.clearTimer.reset();
        }
    }

    private void markAsBot(class_1657 ent) {
        if (!bots.contains(ent)) {
            switch ((AntiBot.Mode)this.mode.getValue()) {
                case UUIDCheck:
                    if (!ent.method_5667().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + ent.method_5477().getString()).getBytes(StandardCharsets.UTF_8)))
                        && ent instanceof class_745
                        && (FakePlayer.fakePlayer == null || ent.method_5628() != FakePlayer.fakePlayer.method_5628())
                        && !ent.method_5477().getString().contains("-")) {
                        this.addBot(ent);
                    }
                    break;
                case MotionCheck:
                    double diffX = ent.method_23317() - ent.field_6014;
                    double diffZ = ent.method_23321() - ent.field_5969;
                    if (diffX * diffX + diffZ * diffZ > 0.5) {
                        if (this.ticks >= this.checkticks.getValue()) {
                            this.addBot(ent);
                        }

                        this.ticks++;
                    }
                    break;
                case ZeroPing:
                    if (NameTags.getEntityPing(ent) <= 0) {
                        this.addBot(ent);
                    }
            }
        }
    }

    private void addBot(class_1657 entity) {
        this.sendMessage(entity.method_5477().getString() + " is a bot!");
        bots.add(entity);
    }

    @Override
    public String getDisplayInfo() {
        return String.valueOf(bots.size());
    }

    public static enum Mode {
        UUIDCheck,
        MotionCheck,
        ZeroPing;
    }
}
