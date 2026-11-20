package neva.ware.core.manager.client;

import java.io.File;

public final class ConfigMigrator {
    private ConfigMigrator() {
    }

    public static void run() {
        try {
            File var0 = ConfigManager.CONFIGS_FOLDER;
            File[] var1 = var0.listFiles();
            if (var1 == null) {
                return;
            }

            for (File var5 : var1) {
                String var6 = var5.getName();
                if (var5.isFile() && var6.endsWith(".th")) {
                    File var7 = new File(var0, var6.substring(0, var6.length() - 3) + ".nw");
                    if (!var7.exists()) {
                        var5.renameTo(var7);
                    }
                }
            }
        } catch (Throwable var8) {
        }
    }

    public static boolean isConfigName(String var0) {
        return var0 != null && (var0.contains(".nw") || var0.contains(".th"));
    }
}
