package neva.ware.features.modules.movement;

import net.minecraft.class_243;
import net.minecraft.class_746;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class ElytraBooster extends Module {
    public final Setting<Float> boostMultiplier = new Setting<>("BoostMultiplier", 1.05F, 1.0F, 1.5F);

    public ElytraBooster() {
        super("ElytraBooster", Module.Category.MOVEMENT);
    }

    public static ElytraBooster getInstance() {
        return ModuleManager.elytraBooster;
    }

    public void onFireworkBoost(class_746 player) {
        if (this.isEnabled()) {
            if (player != null && player.method_6128()) {
                class_243 vel = player.method_18798();
                float mult = this.boostMultiplier.getValue();
                player.method_18799(vel.method_1021(mult));
            }
        }
    }
}
