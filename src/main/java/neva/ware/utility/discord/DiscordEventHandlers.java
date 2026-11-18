package neva.ware.utility.discord;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import neva.ware.utility.discord.callbacks.DisconnectedCallback;
import neva.ware.utility.discord.callbacks.ErroredCallback;
import neva.ware.utility.discord.callbacks.JoinGameCallback;
import neva.ware.utility.discord.callbacks.JoinRequestCallback;
import neva.ware.utility.discord.callbacks.ReadyCallback;
import neva.ware.utility.discord.callbacks.SpectateGameCallback;

public class DiscordEventHandlers extends Structure {
   public DisconnectedCallback disconnected;
   public JoinRequestCallback joinRequest;
   public SpectateGameCallback spectateGame;
   public ReadyCallback ready;
   public ErroredCallback errored;
   public JoinGameCallback joinGame;

   protected List<String> getFieldOrder() {
      return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
   }
}
