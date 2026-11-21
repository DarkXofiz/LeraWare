package neva.ware.api;

import com.mojang.logging.LogUtils;
import java.util.List;
import neva.ware.features.cmd.Command;
import neva.ware.features.hud.HudElement;
import neva.ware.features.modules.Module;

public interface IAddon {
    void onInitialize();

    List<Module> getModules();

    List<Command> getCommands();

    List<HudElement> getHudElements();

    String getPackage();

    String getName();

    String getAuthor();

    String getRepo();

    String getVersion();

    default String getDescription() {
        return "";
    }

    default void onShutdown() {
        LogUtils.getLogger().info("Shutting down addon: " + this.getName());
    }
}
