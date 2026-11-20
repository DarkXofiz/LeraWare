package neva.ware.core.manager.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import neva.ware.NevaWare;
import neva.ware.core.manager.IManager;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.impl.ArmorHud;
import neva.ware.features.hud.impl.AutoCrystalInfo;
import neva.ware.features.hud.impl.CandleHud;
import neva.ware.features.hud.impl.ChestCounter;
import neva.ware.features.hud.impl.Companion;
import neva.ware.features.hud.impl.Cooldowns;
import neva.ware.features.hud.impl.Coords;
import neva.ware.features.hud.impl.Crosshair;
import neva.ware.features.hud.impl.CrosshairArrows;
import neva.ware.features.hud.impl.FpsCounter;
import neva.ware.features.hud.impl.GapplesHud;
import neva.ware.features.hud.impl.Hotbar;
import neva.ware.features.hud.impl.KeyBinds;
import neva.ware.features.hud.impl.KillFeed;
import neva.ware.features.hud.impl.KillStats;
import neva.ware.features.hud.impl.LegacyHud;
import neva.ware.features.hud.impl.MemoryHud;
import neva.ware.features.hud.impl.ModuleList;
import neva.ware.features.hud.impl.PVPResources;
import neva.ware.features.hud.impl.PingHud;
import neva.ware.features.hud.impl.PotionHud;
import neva.ware.features.hud.impl.Radar;
import neva.ware.features.hud.impl.RadarRewrite;
import neva.ware.features.hud.impl.Speedometer;
import neva.ware.features.hud.impl.StaffBoard;
import neva.ware.features.hud.impl.TPSCounter;
import neva.ware.features.hud.impl.TargetHud;
import neva.ware.features.hud.impl.TimerIndicator;
import neva.ware.features.hud.impl.TotemCounter;
import neva.ware.features.hud.impl.WaterMark;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.AntiCrash;
import neva.ware.features.modules.client.AntiPacketException;
import neva.ware.features.modules.client.AntiServerAdd;
import neva.ware.features.modules.client.AntiServerRP;
import neva.ware.features.modules.client.BaritoneSettings;
import neva.ware.features.modules.client.ClickGui;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.client.ClientSpoof;
import neva.ware.features.modules.client.FastLatency;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.features.modules.client.Media;
import neva.ware.features.modules.client.Notifications;
import neva.ware.features.modules.client.OptifineCapes;
import neva.ware.features.modules.client.RPC;
import neva.ware.features.modules.client.Rotations;
import neva.ware.features.modules.client.SoundFX;
import neva.ware.features.modules.client.UnHook;
import neva.ware.features.modules.client.WayPoints;
import neva.ware.features.modules.client.Windows;
import neva.ware.features.modules.combat.AntiBot;
import neva.ware.features.modules.combat.Aura;
import neva.ware.features.modules.combat.AutoAnchor;
import neva.ware.features.modules.combat.AutoBuff;
import neva.ware.features.modules.combat.AutoCrystal;
import neva.ware.features.modules.combat.AutoGApple;
import neva.ware.features.modules.combat.AutoTotem;
import neva.ware.features.modules.combat.AutoTrap;
import neva.ware.features.modules.combat.Blocker;
import neva.ware.features.modules.combat.Breaker;
import neva.ware.features.modules.combat.Burrow;
import neva.ware.features.modules.combat.Criticals;
import neva.ware.features.modules.combat.ElytraTarget;
import neva.ware.features.modules.combat.HitBox;
import neva.ware.features.modules.combat.HoleFill;
import neva.ware.features.modules.combat.PistonAura;
import neva.ware.features.modules.combat.Reach;
import neva.ware.features.modules.combat.Surround;
import neva.ware.features.modules.combat.TargetStrafe;
import neva.ware.features.modules.misc.AutoGear;
import neva.ware.features.modules.misc.ChatUtils;
import neva.ware.features.modules.misc.ChestStealer;
import neva.ware.features.modules.misc.ExtraTab;
import neva.ware.features.modules.misc.FakePlayer;
import neva.ware.features.modules.misc.ItemScroller;
import neva.ware.features.modules.misc.ItemSwapFix;
import neva.ware.features.modules.misc.MiddleClick;
import neva.ware.features.modules.misc.NameProtect;
import neva.ware.features.modules.misc.NoCommentExploit;
import neva.ware.features.modules.misc.Nuker;
import neva.ware.features.modules.misc.PingSpoof;
import neva.ware.features.modules.misc.ServerHelper;
import neva.ware.features.modules.misc.TotemPopCounter;
import neva.ware.features.modules.misc.Tracker;
import neva.ware.features.modules.misc.VisualRange;
import neva.ware.features.modules.movement.AntiWeb;
import neva.ware.features.modules.movement.AutoSprint;
import neva.ware.features.modules.movement.Blink;
import neva.ware.features.modules.movement.BoatFly;
import neva.ware.features.modules.movement.ElytraBooster;
import neva.ware.features.modules.movement.ElytraMotion;
import neva.ware.features.modules.movement.ElytraPlus;
import neva.ware.features.modules.movement.ElytraRecast;
import neva.ware.features.modules.movement.EntityControl;
import neva.ware.features.modules.movement.Flight;
import neva.ware.features.modules.movement.GuiMove;
import neva.ware.features.modules.movement.Jesus;
import neva.ware.features.modules.movement.NoFall;
import neva.ware.features.modules.movement.NoPush;
import neva.ware.features.modules.movement.NoSlow;
import neva.ware.features.modules.movement.NoWaterCollision;
import neva.ware.features.modules.movement.Phase;
import neva.ware.features.modules.movement.SafeWalk;
import neva.ware.features.modules.movement.Scaffold;
import neva.ware.features.modules.movement.Speed;
import neva.ware.features.modules.movement.Step;
import neva.ware.features.modules.movement.Strafe;
import neva.ware.features.modules.movement.Timer;
import neva.ware.features.modules.movement.TridentBoost;
import neva.ware.features.modules.movement.Velocity;
import neva.ware.features.modules.movement.WaterSpeed;
import neva.ware.features.modules.player.AntiBallPlace;
import neva.ware.features.modules.player.AutoArmor;
import neva.ware.features.modules.player.AutoRespawn;
import neva.ware.features.modules.player.AutoTool;
import neva.ware.features.modules.player.ElytraHelper;
import neva.ware.features.modules.player.ElytraSwap;
import neva.ware.features.modules.player.FastUse;
import neva.ware.features.modules.player.InventoryCleaner;
import neva.ware.features.modules.player.NoEntityTrace;
import neva.ware.features.modules.player.NoInteract;
import neva.ware.features.modules.player.PortalInventory;
import neva.ware.features.modules.player.SpeedMine;
import neva.ware.features.modules.player.ViewLock;
import neva.ware.features.modules.render.Animations;
import neva.ware.features.modules.render.AspectRatio;
import neva.ware.features.modules.render.BlockESP;
import neva.ware.features.modules.render.BreadCrumbs;
import neva.ware.features.modules.render.Chams;
import neva.ware.features.modules.render.ChatAnimationModule;
import neva.ware.features.modules.render.ChatKiller;
import neva.ware.features.modules.render.ChunkAnim;
import neva.ware.features.modules.render.CustomSky;
import neva.ware.features.modules.render.ESP;
import neva.ware.features.modules.render.FOV;
import neva.ware.features.modules.render.FreeCam;
import neva.ware.features.modules.render.Fullbright;
import neva.ware.features.modules.render.HandOutline;
import neva.ware.features.modules.render.HitColor;
import neva.ware.features.modules.render.HitParticles;
import neva.ware.features.modules.render.InventoryAnimation;
import neva.ware.features.modules.render.ItemESP;
import neva.ware.features.modules.render.JumpCircle;
import neva.ware.features.modules.render.LegitSpin;
import neva.ware.features.modules.render.Models;
import neva.ware.features.modules.render.NameTags;
import neva.ware.features.modules.render.NoBob;
import neva.ware.features.modules.render.NoCameraClip;
import neva.ware.features.modules.render.NoRender;
import neva.ware.features.modules.render.Particles;
import neva.ware.features.modules.render.PopGhost;
import neva.ware.features.modules.render.Scoreboard;
import neva.ware.features.modules.render.Shaders;
import neva.ware.features.modules.render.SoundESP;
import neva.ware.features.modules.render.StorageEsp;
import neva.ware.features.modules.render.TargetESP;
import neva.ware.features.modules.render.Tooltips;
import neva.ware.features.modules.render.TotemAnimation;
import neva.ware.features.modules.render.Tracers;
import neva.ware.features.modules.render.Trails;
import neva.ware.features.modules.render.Trajectories;
import neva.ware.features.modules.render.VietNamHat;
import neva.ware.features.modules.render.ViewModel;
import neva.ware.features.modules.render.XRay;
import neva.ware.gui.clickui.ClickGUI;
import neva.ware.gui.font.FontRenderers;
import neva.ware.utility.render.NwCursor;
import org.lwjgl.glfw.GLFW;

public class ModuleManager implements IManager {
    public ArrayList<Module> modules;
    public List<Module> sortedModules;
    public List<Integer> activeMouseKeys;
    public static AntiPacketException antiPacketException = new AntiPacketException();
    public static InventoryCleaner inventoryCleaner = new InventoryCleaner();
    public static NoCommentExploit noCommentExploit = new NoCommentExploit();
    public static NoWaterCollision noWaterCollision = new NoWaterCollision();
    public static BaritoneSettings baritoneSettings = new BaritoneSettings();
    public static PortalInventory portalInventory = new PortalInventory();
    public static TotemPopCounter totemPopCounter = new TotemPopCounter();
    public static CrosshairArrows crosshairArrows = new CrosshairArrows();
    public static AutoCrystalInfo autoCrystalInfo = new AutoCrystalInfo();
    public static ClientSettings clientSettings = new ClientSettings();
    public static TimerIndicator timerIndicator = new TimerIndicator();
    public static TotemAnimation totemAnimation = new TotemAnimation();
    public static OptifineCapes optifineCapes = new OptifineCapes();
    public static Notifications notifications = new Notifications();
    public static NoEntityTrace noEntityTrace = new NoEntityTrace();
    public static EntityControl entityControl = new EntityControl();
    public static AntiServerAdd antiServerAdd = new AntiServerAdd();
    public static AntiBallPlace antiBallPlace = new AntiBallPlace();
    public static TridentBoost tridentBoost = new TridentBoost();
    public static Trajectories trajectories = new Trajectories();
    public static TargetStrafe targetStrafe = new TargetStrafe();
    public static RadarRewrite radarRewrite;
    public static PVPResources pvpResources = new PVPResources();
    public static NoCameraClip noCameraClip = new NoCameraClip();
    public static ItemScroller itemScroller = new ItemScroller();
    public static HitParticles hitParticles = new HitParticles();
    public static ElytraRecast elytraRecast = new ElytraRecast();
    public static ElytraBooster elytraBooster = new ElytraBooster();
    public static ElytraTarget elytratarget = new ElytraTarget();
    public static ChestStealer chestStealer = new ChestStealer();
    public static AntiServerRP antiServerRP = new AntiServerRP();
    public static TotemCounter totemCounter = new TotemCounter();
    public static ServerHelper serverHelper = new ServerHelper();
    public static ChestCounter chestCounter = new ChestCounter();
    public static FastLatency fastLatency = new FastLatency();
    public static VisualRange visualRange = new VisualRange();
    public static Speedometer speedometer = new Speedometer();
    public static NameProtect nameProtect = new NameProtect();
    public static MiddleClick middleClick = new MiddleClick();
    public static BreadCrumbs breadCrumbs = new BreadCrumbs();
    public static AutoRespawn autoRespawn = new AutoRespawn();
    public static AutoCrystal autoCrystal = new AutoCrystal();
    public static AspectRatio aspectRatio = new AspectRatio();
    public static ClientSpoof clientSpoof = new ClientSpoof();
    public static AutoAnchor autoAnchor = new AutoAnchor();
    public static WaterSpeed waterSpeed = new WaterSpeed();
    public static TPSCounter tpsCounter = new TPSCounter();
    public static StorageEsp storageEsp = new StorageEsp();
    public static StaffBoard staffBoard = new StaffBoard();
    public static PistonAura pistonAura = new PistonAura();
    public static NoInteract noInteract = new NoInteract();
    public static ModuleList moduleList = new ModuleList();
    public static JumpCircle jumpCircle = new JumpCircle();
    public static Fullbright fullbright = new Fullbright();
    public static FpsCounter fpsCounter = new FpsCounter();
    public static FakePlayer fakePlayer = new FakePlayer();
    public static ElytraSwap elytraSwap = new ElytraSwap();
    public static ElytraPlus elytraPlus = new ElytraPlus();
    public static AutoSprint autoSprint = new AutoSprint();
    public static AutoGApple autoGApple = new AutoGApple();
    public static Animations animations = new Animations();
    public static GapplesHud gapplesHud = new GapplesHud();
    public static KillStats killStats = new KillStats();
    public static CandleHud candleHud = new CandleHud();
    public static Particles particles = new Particles();
    public static WayPoints wayPoints = new WayPoints();
    public static WaterMark waterMark = new WaterMark();
    public static ViewModel viewModel = new ViewModel();
    public static TargetHud targetHud = new TargetHud();
    public static SpeedMine speedMine = new SpeedMine();
    public static PotionHud potionHud = new PotionHud();
    public static Models models = new Models();
    public static LegacyHud legacyHud = new LegacyHud();
    public static HudEditor hudEditor = new HudEditor();
    public static Crosshair crosshair = new Crosshair();
    public static Criticals criticals = new Criticals();
    public static ChatUtils chatUtils = new ChatUtils();
    public static AutoTotem autoTotem = new AutoTotem();
    public static AutoArmor autoArmor = new AutoArmor();
    public static Cooldowns cooldowns = new Cooldowns();
    public static Rotations rotations = new Rotations();
    public static MemoryHud memoryHud = new MemoryHud();
    public static Companion companion = new Companion();
    public static AntiCrash antiCrash = new AntiCrash();
    public static AutoGear autoGear = new AutoGear();
    public static ViewLock viewLock = new ViewLock();
    public static Velocity velocity = new Velocity();
    public static Tooltips tooltips = new Tooltips();
    public static Surround surround = new Surround();
    public static Scaffold scaffold = new Scaffold();
    public static NoRender noRender = new NoRender();
    public static NameTags nameTags = new NameTags();
    public static KeyBinds keyBinds = new KeyBinds();
    public static HoleFill holeFill = new HoleFill();
    public static ExtraTab extraTab = new ExtraTab();
    public static ClickGui clickGui = new ClickGui();
    public static AutoTrap autoTrap = new AutoTrap();
    public static AutoTool autoTool = new AutoTool();
    public static SoundESP soundESP = new SoundESP();
    public static AutoBuff autoBuff = new AutoBuff();
    public static ArmorHud armorHud = new ArmorHud();
    public static KillFeed killFeed = new KillFeed();
    public static BlockESP blockESP = new BlockESP();
    public static SafeWalk safeWalk = new SafeWalk();
    public static Windows windows = new Windows();
    public static Breaker breaker = new Breaker();
    public static SoundFX soundFX = new SoundFX();
    public static Tracker tracker = new Tracker();
    public static Shaders shaders = new Shaders();
    public static PingHud pingHud = new PingHud();
    public static ItemESP itemESP = new ItemESP();
    public static GuiMove guiMove = new GuiMove();
    public static FreeCam freeCam = new FreeCam();
    public static FastUse fastUse = new FastUse();
    public static BoatFly boatFly = new BoatFly();
    public static Blocker blocker = new Blocker();
    public static AntiWeb antiWeb = new AntiWeb();
    public static AntiBot antiBot = new AntiBot();
    public static Tracers tracers = new Tracers();
    public static Trails trails = new Trails();
    public static Strafe strafe = new Strafe();
    public static NoSlow noSlow = new NoSlow();
    public static NoFall noFall = new NoFall();
    public static Hotbar hotbar = new Hotbar();
    public static HitBox hitBox = new HitBox();
    public static Flight flight = new Flight();
    public static Coords coords = new Coords();
    public static Burrow burrow = new Burrow();
    public static NoPush noPush = new NoPush();
    public static UnHook unHook = new UnHook();
    public static Timer timer = new Timer();
    public static Speed speed = new Speed();
    public static Reach reach = new Reach();
    public static Radar radar = new Radar();
    public static Nuker nuker = new Nuker();
    public static Media media = new Media();
    public static Chams chams = new Chams();
    public static Blink blink = new Blink();
    public static Phase phase = new Phase();
    public static NoBob noBob = new NoBob();
    public static Jesus jesus = new Jesus();
    public static XRay xray = new XRay();
    public static Step step = new Step();
    public static Aura aura = new Aura();
    public static FOV fov = new FOV();
    public static ESP esp = new ESP();
    public static RPC rpc = new RPC();
    public static ElytraHelper elytraHelperV2 = new ElytraHelper();
    public static VietNamHat vietNamHat;
    public static ItemSwapFix itemSwapFix = new ItemSwapFix();
    public static ElytraMotion elytraMotion;
    public static ChatKiller chatKiller;
    public static Scoreboard scoreboard = new Scoreboard();
    public static TargetESP targetESP = new TargetESP();
    public static CustomSky customSky = new CustomSky();
    public static ChunkAnim chunkAnim = new ChunkAnim();
    public static ChatAnimationModule chatAnimation = new ChatAnimationModule();
    public static InventoryAnimation inventoryAnimation = new InventoryAnimation();
    public static HandOutline handOutline = new HandOutline();
    public static PopGhost popGhost = new PopGhost();
    public static HitColor hitColor = new HitColor();

    public ModuleManager() {
        vietNamHat = new VietNamHat();
        this.modules = new ArrayList();
        this.sortedModules = new ArrayList();
        this.activeMouseKeys = new ArrayList();

        for (Field field : this.getClass().getDeclaredFields()) {
            if (Module.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);

                try {
                    this.modules.add((Module)field.get(this));
                } catch (IllegalAccessException var6) {
                    var6.printStackTrace();
                }
            }
        }

        ArrayList var10000 = this.modules;
        RemovedModules.purge(this.modules);
        CombatModuleFilter.filter(var10000);
        this.modules.add(itemSwapFix);
        elytraMotion = new ElytraMotion();
        this.registerModule(elytraMotion);
        chatKiller = new ChatKiller();
        this.registerModule(chatKiller);
        this.registerModule(new LegitSpin());
        this.registerModule(new PingSpoof());
    }

    public Module get(String name) {
        for (Module module : this.modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }

        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList();

        for (Module module : this.modules) {
            if (module.isEnabled()) {
                enabledModules.add(module);
            }
        }

        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return new ArrayList(Module.Category.values());
    }

    public void onLoad(String category) {
        try {
            NevaWare.EVENT_BUS.unsubscribe(unHook);
        } catch (Exception var3) {
        }

        unHook.setEnabled(false);
        this.modules.sort(Comparator.comparing(ModuleManager::sortKey));
        this.modules.forEach(m -> {
            if (m.isEnabled() && (m.getCategory().getName().equalsIgnoreCase(category) || category.equals("none"))) {
                NevaWare.EVENT_BUS.subscribe(m);
            }
        });
        if (ConfigManager.firstLaunch) {
            notifications.enable();
            rpc.enable();
            soundFX.enable();
        }
    }

    public void onUpdate() {
        if (!Module.fullNullCheck()) {
            this.modules.stream().filter(Module::isEnabled).forEach(Module::onUpdate);
        }
    }

    public void onRender2D(class_332 context) {
        if (!mc.method_53526().method_53536() && !mc.field_1690.field_1842) {
            HudElement.anyHovered = false;
            this.modules.stream().filter(Module::isEnabled).forEach(module -> module.onRender2D(context));
            if (!HudElement.anyHovered && !ClickGUI.anyHovered && GLFW.glfwGetPlatform() != 393219) {
                GLFW.glfwSetCursor(mc.method_22683().method_4490(), NwCursor.get(221185));
            }

            NevaWare.core.onRender2D(context);
        }
    }

    public void onRender3D(class_4587 stack) {
        RenderOrder.render3D(this.modules, stack);
    }

    public void sortModules() {
        this.sortedModules = (List<Module>)this.getEnabledModules()
            .stream()
            .filter(Module::isDrawn)
            .sorted(Comparator.comparing(module -> FontRenderers.getModulesRenderer().getStringWidth(module.getFullArrayString()) * -1.0F))
            .collect(Collectors.toList());
    }

    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload(String category) {
        this.modules.forEach(module -> {
            if (module.isEnabled() && (module.getCategory().getName().equalsIgnoreCase(category) || category.equals("none"))) {
                NevaWare.EVENT_BUS.unsubscribe(module);
                module.setEnabled(false);
            }
        });
        this.modules.forEach(Module::onUnload);
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey != -1 && eventKey != 0 && !(mc.field_1755 instanceof ClickGUI)) {
            this.modules.forEach(module -> {
                if (module.getBind().getKey() == eventKey) {
                    module.toggle();
                }
            });
        }
    }

    public void onKeyReleased(int eventKey) {
        if (eventKey != -1 && eventKey != 0 && !(mc.field_1755 instanceof ClickGUI)) {
            this.modules.forEach(module -> {
                if (module.getBind().getKey() == eventKey && module.getBind().isHold()) {
                    module.disable();
                }
            });
        }
    }

    public void onMoseKeyPressed(int eventKey) {
        if (eventKey != -1 && !(mc.field_1755 instanceof ClickGUI)) {
            this.modules.forEach(module -> {
                if (Objects.equals(module.getBind().getBind(), "M" + eventKey)) {
                    module.toggle();
                }
            });
        }
    }

    public void onMoseKeyReleased(int eventKey) {
        if (eventKey != -1 && !(mc.field_1755 instanceof ClickGUI)) {
            this.activeMouseKeys.add(eventKey);
            this.modules.forEach(module -> {
                if (Objects.equals(module.getBind().getBind(), "M" + eventKey) && module.getBind().isHold()) {
                    module.disable();
                }
            });
        }
    }

    public ArrayList<Module> getModulesSearch(String string) {
        ArrayList<Module> modulesCategory = new ArrayList();
        this.modules.forEach(module -> {
            if (module.getName().toLowerCase().contains(string.toLowerCase())) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public void registerModule(Module module) {
        if (module != null) {
            this.modules.add(module);
            if (module.isEnabled()) {
                NevaWare.EVENT_BUS.subscribe(module);
            }
        }
    }

    public void registerHudElement(HudElement hudElement) {
        if (hudElement != null) {
            this.modules.add(hudElement);
        }
    }

    private static String sortKey(Module var0) {
        String var1 = var0.getName();
        if (var1.equals("LegitAura")) {
            return "Aura~";
        } else if (var1.equals("LegitTarget")) {
            return "ElytraTarget~";
        } else {
            return var1.equals("LegitHelper") ? "ElytraHelper~" : var1;
        }
    }
}
