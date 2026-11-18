package neva.ware.features.cmd.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_2172;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.cmd.Command;
import org.jetbrains.annotations.NotNull;

public class TrackerCommand extends Command {
   public TrackerCommand() {
      super("tracker");
   }

   @Override
   public void executeBuild(@NotNull LiteralArgumentBuilder<class_2172> builder) {
      builder.executes(context -> {
         if (ModuleManager.tracker.isEnabled()) {
            ModuleManager.tracker.sendTrack();
         }

         return 1;
      });
   }
}
