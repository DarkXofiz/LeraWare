package neva.ware.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_2172;
import neva.ware.core.Managers;
import neva.ware.features.cmd.Command;
import neva.ware.features.modules.Module;
import neva.ware.setting.impl.Bind;
import org.jetbrains.annotations.NotNull;

public class ResetBindsCommand extends Command {
    public ResetBindsCommand() {
        super("resetbinds", "unbind", "fuckbinds");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<class_2172> builder) {
        builder.executes(context -> {
            for (Module mod : Managers.MODULE.modules) {
                mod.setBind(new Bind(-1, false, false));
            }

            return 1;
        });
    }
}
