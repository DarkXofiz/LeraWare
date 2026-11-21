package neva.ware.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_2172;
import neva.ware.features.cmd.Command;
import neva.ware.features.modules.client.ClientSettings;
import org.jetbrains.annotations.NotNull;

public class GetNbtCommand extends Command {
    public GetNbtCommand() {
        super("nbt", "getnbt");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<class_2172> builder) {
        builder.executes(
            context -> {
                sendMessage(
                    mc.field_1724.method_6047().method_57353() != null
                        ? mc.field_1724.method_6047().method_57353().toString()
                        : (ClientSettings.isRu() ? "У этого предмета нет nbt тегов!" : "This item don't contains nbt tags!")
                );
                return 1;
            }
        );
    }
}
