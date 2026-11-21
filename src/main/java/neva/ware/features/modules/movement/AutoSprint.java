package neva.ware.features.modules.movement;

import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.combat.AttackAura;
import neva.ware.setting.Setting;

public class AutoSprint extends Module {
    public static final Setting<Boolean> sprint = new Setting<>("KeepSprint", true);
    public static final Setting<Float> motion = new Setting<>("Motion", 1.0F, 0.0F, 1.0F, v -> sprint.getValue());
    private final Setting<Boolean> stopWhileUsing = new Setting<>("StopWhileUsing", false);
    private final Setting<Boolean> pauseWhileAura = new Setting<>("PauseWhileAura", false);

    public AutoSprint() {
        super("AutoSprint", Module.Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        mc.field_1724
            .method_5728(
                mc.field_1724.method_7344().method_7586() > 6
                    && !mc.field_1724.field_5976
                    && mc.field_1724.field_3913.field_3905 > 0.0F
                    && (!mc.field_1724.method_5715() || ModuleManager.noSlow.isEnabled() && ModuleManager.noSlow.sneak.getValue())
                    && (!mc.field_1724.method_6115() || !this.stopWhileUsing.getValue())
                    && (!ModuleManager.aura.isEnabled() || AttackAura.target == null || !this.pauseWhileAura.getValue())
            );
    }
}
