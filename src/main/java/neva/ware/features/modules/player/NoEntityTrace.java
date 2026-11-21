package neva.ware.features.modules.player;

import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public final class NoEntityTrace extends Module {
    public static final Setting<Boolean> ponly = new Setting<>("Pickaxe Only", true);
    public static final Setting<Boolean> noSword = new Setting<>("No Sword", true);

    public NoEntityTrace() {
        super("NoEntityTrace", Module.Category.PLAYER);
    }
}
