package neva.ware.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import net.minecraft.class_156;
import net.minecraft.class_2172;
import neva.ware.features.cmd.Command;
import org.jetbrains.annotations.NotNull;

public class OpenFolderCommand extends Command {
    public OpenFolderCommand() {
        super("openfolder", "folder");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<class_2172> builder) {
        builder.executes(context -> {
            class_156.method_668().method_672(new File("NevaWare/configs/"));
            return 1;
        });
    }
}
