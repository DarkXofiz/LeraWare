package neva.ware.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_2172;
import neva.ware.features.cmd.Command;
import org.jetbrains.annotations.NotNull;

public class GarbageCleanerCommand extends Command {
    public GarbageCleanerCommand() {
        super("gc", "garbagecleaner", "clearram");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<class_2172> builder) {
        builder.executes(context -> {
            sendMessage("Cleaning RAM..");
            System.gc();
            sendMessage("Successfully cleaned RAM!");
            return 1;
        });
    }
}
