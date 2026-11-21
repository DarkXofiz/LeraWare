package neva.ware.features.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventMove;
import neva.ware.events.impl.PlayerUpdateEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.AttackAura;
import neva.ware.setting.Setting;

public class ElytraMotion extends Module {
    public final Setting<Float> attackDistance = new Setting<>("Distance", 3.0F, 0.1F, 5.0F);
    public boolean freeze;

    public ElytraMotion() {
        super("ElytraMotion", Module.Category.MOVEMENT);
    }

    @EventHandler
    public void onUpdate(PlayerUpdateEvent event) {
        if (!mc.field_1724.method_6128()) {
            this.freeze = false;
        } else if (this.check()) {
            mc.field_1690.field_1903.method_23481(false);
            this.freeze = true;
        } else {
            mc.field_1690.field_1903.method_23481(true);
            this.freeze = false;
        }
    }

    @EventHandler
    public void onMotion(EventMove eventMotion) {
        if (this.freeze) {
            eventMotion.setX(0.0);
            eventMotion.setY(0.0);
            eventMotion.setZ(0.0);
            eventMotion.cancel();
        }
    }

    public boolean check() {
        if (!ModuleManager.aura.isEnabled()) {
            return false;
        } else {
            class_1297 target = AttackAura.target;
            return target == null
                ? false
                : mc.field_1724.method_5707(target.method_19538()) < this.attackDistance.getValue() * this.attackDistance.getValue()
                    && mc.field_1724.method_6128();
        }
    }

    @Override
    public void onDisable() {
        this.freeze = false;
        super.onDisable();
    }
}
