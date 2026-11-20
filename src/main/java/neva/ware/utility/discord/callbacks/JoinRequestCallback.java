package neva.ware.utility.discord.callbacks;

import com.sun.jna.Callback;
import neva.ware.utility.discord.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(DiscordUser var1);
}
