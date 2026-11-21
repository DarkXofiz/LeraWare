package neva.ware.core;

import neva.ware.NevaWare;
import neva.ware.core.manager.client.AddonManager;
import neva.ware.core.manager.client.AsyncManager;
import neva.ware.core.manager.client.CommandManager;
import neva.ware.core.manager.client.ConfigManager;
import neva.ware.core.manager.client.MacroManager;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.core.manager.client.NotificationManager;
import neva.ware.core.manager.client.ProxyManager;
import neva.ware.core.manager.client.ServerManager;
import neva.ware.core.manager.client.ShaderManager;
import neva.ware.core.manager.client.SoundManager;
import neva.ware.core.manager.client.TelemetryManager;
import neva.ware.core.manager.player.CombatManager;
import neva.ware.core.manager.player.FriendManager;
import neva.ware.core.manager.player.PlayerManager;
import neva.ware.core.manager.world.HoleManager;
import neva.ware.core.manager.world.WayPointManager;
import neva.ware.utility.NevaUtility;

public class Managers {
    public static final CombatManager COMBAT = new CombatManager();
    public static final FriendManager FRIEND = new FriendManager();
    public static final PlayerManager PLAYER = new PlayerManager();
    public static final HoleManager HOLE = new HoleManager();
    public static final WayPointManager WAYPOINT = new WayPointManager();
    public static final AddonManager ADDON = new AddonManager();
    public static final AsyncManager ASYNC = new AsyncManager();
    public static final ModuleManager MODULE = new ModuleManager();
    public static final ConfigManager CONFIG = new ConfigManager();
    public static final MacroManager MACRO = new MacroManager();
    public static final NotificationManager NOTIFICATION = new NotificationManager();
    public static final ProxyManager PROXY = new ProxyManager();
    public static final ServerManager SERVER = new ServerManager();
    public static final ShaderManager SHADER = new ShaderManager();
    public static final SoundManager SOUND = new SoundManager();
    public static final TelemetryManager TELEMETRY = new TelemetryManager();
    public static final CommandManager COMMAND = new CommandManager();

    public static void init() {
        ADDON.initAddons();
        CONFIG.load(CONFIG.getCurrentConfig());
        MODULE.onLoad("none");
        FRIEND.loadFriends();
        MACRO.onLoad();
        WAYPOINT.onLoad();
        PROXY.onLoad();
        SOUND.registerSounds();
        ASYNC.run(() -> {
            NevaUtility.syncContributors();
            NevaUtility.parseStarGazer();
            NevaUtility.parseCommits();
            TELEMETRY.fetchData();
        });
    }

    public static void subscribe() {
        NevaWare.EVENT_BUS.subscribe(NOTIFICATION);
        NevaWare.EVENT_BUS.subscribe(SERVER);
        NevaWare.EVENT_BUS.subscribe(PLAYER);
        NevaWare.EVENT_BUS.subscribe(COMBAT);
        NevaWare.EVENT_BUS.subscribe(ASYNC);
        NevaWare.EVENT_BUS.subscribe(TELEMETRY);
    }
}
