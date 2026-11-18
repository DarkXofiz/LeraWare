package neva.ware.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import neva.ware.features.cmd.Command;
import neva.ware.features.cmd.args.ModuleArgumentType;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import org.jetbrains.annotations.NotNull;

public class DrawCommand extends Command {
   public DrawCommand() {
      super("draw");
   }

   @Override
   public void executeBuild(@NotNull LiteralArgumentBuilder<class_2172> builder) {
      builder.then(
         arg("module", ModuleArgumentType.create())
            .executes(
               context -> {
                  Module module = (Module)context.getArgument("module", Module.class);
                  module.setDrawn(!module.isDrawn());
                  if (ClientSettings.isRu()) {
                     sendMessage(
                        "Модуль "
                           + class_124.field_1060
                           + module.getName()
                           + class_124.field_1068
                           + " теперь "
                           + (module.isDrawn() ? "виден в ArrayList" : "не виден в ArrayList")
                     );
                  } else {
                     sendMessage(
                        class_124.field_1060
                           + module.getName()
                           + class_124.field_1068
                           + " is now "
                           + (module.isDrawn() ? "visible in ArrayList" : "invisible in ArrayList")
                     );
                  }

                  return 1;
               }
            )
      );
   }
}
