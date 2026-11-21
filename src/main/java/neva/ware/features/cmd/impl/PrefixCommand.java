package neva.ware.features.cmd.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import neva.ware.core.Managers;
import neva.ware.features.cmd.Command;
import neva.ware.features.modules.client.ClientSettings;
import org.jetbrains.annotations.NotNull;

public class PrefixCommand extends Command {
    public PrefixCommand() {
        super("prefix");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<class_2172> builder) {
        builder.then(literal("set").then(arg("prefix", StringArgumentType.greedyString()).executes(context -> {
            String prefix = (String)context.getArgument("prefix", String.class);
            Managers.COMMAND.setPrefix(prefix);
            sendMessage(class_124.field_1060 + (ClientSettings.isRu() ? "Префикс изменен на " : "Changed prefix to ") + prefix);
            ClientSettings.prefix.setValue(prefix);
            return 1;
        })));
        builder.executes(context -> {
            sendMessage(class_124.field_1060 + (ClientSettings.isRu() ? "Текущий префикс: " : "Current prefix: ") + Managers.COMMAND.getPrefix());
            return 1;
        });
    }
}
