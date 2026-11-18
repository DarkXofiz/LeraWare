package neva.ware.features.modules.client;

import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import neva.ware.utility.discord.DiscordRichPresence;

public final class RPC extends Module {
   public static Setting<RPC.Mode> mode = new Setting<>("Picture", RPC.Mode.Recode);
   public static Setting<Boolean> showIP = new Setting<>("ShowIP", Boolean.FALSE);
   public static Setting<RPC.sMode> smode = new Setting<>("StateMode", RPC.sMode.Stats);
   public static Setting<String> state = new Setting<>("State", "Beta? Recode? NextGen?");
   public static Setting<Boolean> nickname = new Setting<>("Nickname", Boolean.FALSE);
   public static DiscordRichPresence presence = new DiscordRichPresence();
   public static boolean started;
   static String String1 = "none";

   public RPC() {
      super("DiscordRPC", Module.Category.CLIENT);
   }

   public static void readFile() {
   }

   public static void WriteFile(String var0, String var1) {
   }

   @Override
   public void onDisable() {
      started = false;
   }

   @Override
   public void onUpdate() {
   }

   public void startRpc() {
      started = false;
   }

   public static enum Mode {
      Custom,
      MegaCute,
      Recode;
   }

   public static enum sMode {
      Custom,
      Stats,
      Version;
   }
}
