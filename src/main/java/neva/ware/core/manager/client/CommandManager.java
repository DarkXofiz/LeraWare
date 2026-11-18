package neva.ware.core.manager.client;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_310;
import net.minecraft.class_637;
import neva.ware.core.manager.IManager;
import neva.ware.features.cmd.Command;
import neva.ware.features.cmd.impl.AddonsCommand;
import neva.ware.features.cmd.impl.BenchMarkCommand;
import neva.ware.features.cmd.impl.BindCommand;
import neva.ware.features.cmd.impl.BlockESPCommand;
import neva.ware.features.cmd.impl.CfgCommand;
import neva.ware.features.cmd.impl.ChestStealerCommand;
import neva.ware.features.cmd.impl.DrawCommand;
import neva.ware.features.cmd.impl.DropAllCommand;
import neva.ware.features.cmd.impl.EClipCommand;
import neva.ware.features.cmd.impl.FriendCommand;
import neva.ware.features.cmd.impl.GamemodeCommand;
import neva.ware.features.cmd.impl.GarbageCleanerCommand;
import neva.ware.features.cmd.impl.GetNbtCommand;
import neva.ware.features.cmd.impl.GotoWaypointCommand;
import neva.ware.features.cmd.impl.GpsCommand;
import neva.ware.features.cmd.impl.HClipCommand;
import neva.ware.features.cmd.impl.HelpCommand;
import neva.ware.features.cmd.impl.HorseSpeedCommand;
import neva.ware.features.cmd.impl.InvCleanerCommand;
import neva.ware.features.cmd.impl.KitCommand;
import neva.ware.features.cmd.impl.LoginCommand;
import neva.ware.features.cmd.impl.MacroCommand;
import neva.ware.features.cmd.impl.ModuleCommand;
import neva.ware.features.cmd.impl.NukerCommand;
import neva.ware.features.cmd.impl.OpenFolderCommand;
import neva.ware.features.cmd.impl.PrefixCommand;
import neva.ware.features.cmd.impl.RctCommand;
import neva.ware.features.cmd.impl.ResetBindsCommand;
import neva.ware.features.cmd.impl.RpcCommand;
import neva.ware.features.cmd.impl.StaffCommand;
import neva.ware.features.cmd.impl.TabParseCommand;
import neva.ware.features.cmd.impl.TrackerCommand;
import neva.ware.features.cmd.impl.TreasureCommand;
import neva.ware.features.cmd.impl.VClipCommand;
import neva.ware.features.cmd.impl.WayPointCommand;
import org.jetbrains.annotations.NotNull;

public class CommandManager implements IManager {
   private String prefix = "@";
   private final CommandDispatcher<class_2172> dispatcher = new CommandDispatcher();
   private final class_2172 source = new class_637(null, class_310.method_1551());
   private final List<Command> commands = new ArrayList<>();

   public CommandManager() {
      this.add(new RpcCommand());
      this.add(new KitCommand());
      this.add(new GpsCommand());
      this.add(new CfgCommand());
      this.add(new RctCommand());
      this.add(new BindCommand());
      this.add(new DrawCommand());
      this.add(new HelpCommand());
      this.add(new NukerCommand());
      this.add(new EClipCommand());
      this.add(new HClipCommand());
      this.add(new LoginCommand());
      this.add(new MacroCommand());
      this.add(new StaffCommand());
      this.add(new VClipCommand());
      this.add(new AddonsCommand());
      this.add(new GetNbtCommand());
      this.add(new FriendCommand());
      this.add(new ModuleCommand());
      this.add(new PrefixCommand());
      this.add(new TrackerCommand());
      this.add(new GamemodeCommand());
      this.add(new DropAllCommand());
      this.add(new TreasureCommand());
      this.add(new WayPointCommand());
      this.add(new TabParseCommand());
      this.add(new BlockESPCommand());
      this.add(new BenchMarkCommand());
      this.add(new HorseSpeedCommand());
      this.add(new OpenFolderCommand());
      this.add(new ResetBindsCommand());
      this.add(new InvCleanerCommand());
      this.add(new GotoWaypointCommand());
      this.add(new ChestStealerCommand());
      this.add(new GarbageCleanerCommand());
   }

   private void add(@NotNull Command command) {
      command.register(this.dispatcher);
      this.commands.add(command);
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public Command get(Class<? extends Command> commandClass) {
      for (Command command : this.commands) {
         if (command.getClass().equals(commandClass)) {
            return command;
         }
      }

      return null;
   }

   @NotNull
   public static String getClientMessage() {
      return class_124.field_1068 + "⌊" + class_124.field_1065 + "⚡" + class_124.field_1068 + "⌉" + class_124.field_1070;
   }

   public List<Command> getCommands() {
      return this.commands;
   }

   public class_2172 getSource() {
      return this.source;
   }

   public CommandDispatcher<class_2172> getDispatcher() {
      return this.dispatcher;
   }

   public void registerCommand(Command command) {
      if (command != null) {
         command.register(this.dispatcher);
         this.commands.add(command);
      }
   }
}
