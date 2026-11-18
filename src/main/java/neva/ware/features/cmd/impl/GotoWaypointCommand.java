package neva.ware.features.cmd.impl;

import baritone.api.BaritoneAPI;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_2172;
import neva.ware.NevaWare;
import neva.ware.core.manager.world.WayPointManager;
import neva.ware.features.cmd.Command;
import neva.ware.features.cmd.args.WayPointArgumentType;
import neva.ware.features.modules.client.ClientSettings;

public class GotoWaypointCommand extends Command {
   public GotoWaypointCommand() {
      super("goto");
   }

   @Override
   public void executeBuild(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(
         arg("name", WayPointArgumentType.create())
            .executes(
               context -> {
                  if (!NevaWare.baritone) {
                     sendMessage(
                        ClientSettings.isRu()
                           ? "Баритон не найден (можешь скачать на https://meteorclient.com)"
                           : "Baritone not found (you can download it at https://meteorclient.com)"
                     );
                     return 1;
                  } else {
                     WayPointManager.WayPoint wp = (WayPointManager.WayPoint)context.getArgument("name", WayPointManager.WayPoint.class);
                     if (!mc.field_1687.method_27983().method_29177().method_12832().equals(wp.getDimension())) {
                        sendMessage(ClientSettings.isRu() ? "Метка в другом измерении" : "Waypoint is in another dimension");
                        return 1;
                     } else {
                        BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("goto " + wp.getX() + " " + wp.getY() + " " + wp.getZ());
                        return 1;
                     }
                  }
               }
            )
      );
   }
}
