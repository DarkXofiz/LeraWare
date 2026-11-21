package neva.ware.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_2172;
import neva.ware.api.IAddon;
import neva.ware.core.Managers;
import neva.ware.features.cmd.Command;

public class AddonsCommand extends Command {
    public AddonsCommand() {
        super("addons");
    }

    @Override
    public void executeBuild(LiteralArgumentBuilder<class_2172> builder) {
        builder.executes(context -> {
            List<IAddon> sortedAddons = Managers.ADDON.getAddons().stream().filter(Objects::nonNull).sorted(Comparator.comparing(IAddon::getName)).toList();
            if (sortedAddons.isEmpty()) {
                sendMessage("No addons installed.");
                return 1;
            } else {
                sortedAddons.forEach(iAddon -> sendMessage(iAddon.getName() + " by " + iAddon.getAuthor()));
                return 1;
            }
        });
    }
}
