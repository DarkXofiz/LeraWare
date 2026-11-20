package neva.ware.features.modules.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import neva.ware.core.Managers;
import neva.ware.events.impl.TotemPopEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.combat.AntiBot;
import neva.ware.gui.notification.Notification;
import neva.ware.setting.Setting;
import org.jetbrains.annotations.NotNull;

public class TotemPopCounter extends Module {
    public Setting<Boolean> notification = new Setting<>("Notification", true);

    public TotemPopCounter() {
        super("TotemPopCounter", Module.Category.MISC);
    }

    @EventHandler
    public void onTotemPop(@NotNull TotemPopEvent event) {
        if (event.getEntity() != mc.field_1724) {
            String s;
            if (ClientSettings.isRu()) {
                s = class_124.field_1060
                    + event.getEntity().method_5477().getString()
                    + class_124.field_1068
                    + " попнул "
                    + class_124.field_1075
                    + (event.getPops() > 1 ? "" + event.getPops() + class_124.field_1068 + " тотемов!" : class_124.field_1068 + "тотем!");
            } else {
                s = class_124.field_1060
                    + event.getEntity().method_5477().getString()
                    + class_124.field_1068
                    + " popped "
                    + class_124.field_1075
                    + (event.getPops() > 1 ? "" + event.getPops() + class_124.field_1068 + " totems!" : class_124.field_1068 + " a totem!");
            }

            this.sendMessage(s);
            if (this.notification.getValue()) {
                Managers.NOTIFICATION.publicity("TotemPopCounter", s, 2, Notification.Type.INFO);
            }
        }
    }

    @Override
    public void onUpdate() {
        for (class_1657 player : mc.field_1687.method_18456()) {
            if (player != mc.field_1724
                && !AntiBot.bots.contains(player)
                && !(player.method_6032() > 0.0F)
                && Managers.COMBAT.popList.containsKey(player.method_5477().getString())) {
                String s;
                if (ClientSettings.isRu()) {
                    s = class_124.field_1060
                        + player.method_5477().getString()
                        + class_124.field_1068
                        + " попнул "
                        + (
                            Managers.COMBAT.popList.get(player.method_5477().getString()) > 1
                                ? "" + Managers.COMBAT.popList.get(player.method_5477().getString()) + class_124.field_1068 + " тотемов и сдох!"
                                : class_124.field_1068 + "тотем и сдох!"
                        );
                } else {
                    s = class_124.field_1060
                        + player.method_5477().getString()
                        + class_124.field_1068
                        + " popped "
                        + (
                            Managers.COMBAT.popList.get(player.method_5477().getString()) > 1
                                ? "" + Managers.COMBAT.popList.get(player.method_5477().getString()) + class_124.field_1068 + " totems and died EZ LMAO!"
                                : class_124.field_1068 + "totem and died EZ LMAO!"
                        );
                }

                this.sendMessage(s);
                if (this.notification.getValue()) {
                    Managers.NOTIFICATION.publicity("TotemPopCounter", s, 2, Notification.Type.INFO);
                }
            }
        }
    }
}
