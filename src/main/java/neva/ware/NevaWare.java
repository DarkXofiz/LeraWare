package neva.ware;

import com.mojang.logging.LogUtils;
import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import neva.ware.core.Core;
import neva.ware.core.Managers;
import neva.ware.core.hooks.ManagerShutdownHook;
import neva.ware.core.hooks.ModuleShutdownHook;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.utility.NevaUtility;
import neva.ware.utility.render.Render2DEngine;
import org.slf4j.Logger;

public class NevaWare implements ModInitializer {
   public static final ModMetadata MOD_META = ((ModContainer)FabricLoader.getInstance().getModContainer("nevaware").orElseThrow()).getMetadata();
   public static final String MOD_ID = "nevaware";
   public static final String VERSION = "1.7b2407";
   public static String GITHUB_HASH = "0";
   public static String BUILD_DATE = "1 Jan 1970";
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final Runtime RUNTIME = Runtime.getRuntime();
   public static final boolean baritone = FabricLoader.getInstance().isModLoaded("baritone") || FabricLoader.getInstance().isModLoaded("baritone-meteor");
   public static final IEventBus EVENT_BUS = new EventBus();
   public static String[] contributors = new String[32];
   public static Color copy_color = new Color(-1);
   public static NevaWare.KeyListening currentKeyListener;
   public static boolean isOutdated = false;
   public static class_2338 gps_position;
   public static float TICK_TIMER = 1.0F;
   public static class_310 mc;
   public static long initTime;
   public static Core core = new Core();

   public void onInitialize() {
      mc = class_310.method_1551();
      initTime = System.currentTimeMillis();
      BUILD_DATE = NevaUtility.readManifestField("Build-Timestamp");
      GITHUB_HASH = NevaUtility.readManifestField("Git-Commit");
      NevaUtility.syncVersion();
      EVENT_BUS.registerLambdaFactory("neva.ware", (lookupInMethod, klass) -> (Lookup)lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
      EVENT_BUS.subscribe(core);
      Managers.init();
      Managers.subscribe();
      Render2DEngine.initShaders();
      ModuleManager.rpc.startRpc();
      LOGGER.info("[NevaWare] Init time: {} ms.", System.currentTimeMillis() - initTime);
      initTime = System.currentTimeMillis();
      RUNTIME.addShutdownHook(new ManagerShutdownHook());
      RUNTIME.addShutdownHook(new ModuleShutdownHook());
   }

   public static boolean isFuturePresent() {
      return FabricLoader.getInstance().getModContainer("future").isPresent();
   }

   public static enum KeyListening {
      NevaGui,
      ClickGui,
      Search,
      Sliders,
      Strings;
   }
}
