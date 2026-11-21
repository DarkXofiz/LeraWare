package neva.ware.core.manager.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

public final class DefaultConfigInstaller {
    private DefaultConfigInstaller() {
    }

    public static void installIfMissing(File var0) {
        if (var0 != null && "config.nw".equals(var0.getName()) && !var0.exists()) {
            try {
                InputStream var1 = DefaultConfigInstaller.class.getResourceAsStream("/default.nw");

                label79: {
                    label85: {
                        try {
                            if (var1 == null) {
                                break label85;
                            }

                            File var2 = var0.getParentFile();
                            if (var2 != null && !var2.exists() && !var2.mkdirs() && !var2.isDirectory()) {
                                break label79;
                            }

                            try {
                                Files.copy(var1, var0.toPath(), new CopyOption[0]);
                            } catch (FileAlreadyExistsException var5) {
                            }
                        } catch (Throwable var6) {
                            if (var1 != null) {
                                try {
                                    var1.close();
                                } catch (Throwable var4) {
                                    var6.addSuppressed(var4);
                                }
                            }

                            throw var6;
                        }

                        if (var1 != null) {
                            var1.close();
                        }

                        return;
                    }

                    if (var1 != null) {
                        var1.close();
                    }

                    return;
                }

                if (var1 != null) {
                    var1.close();
                }
            } catch (IOException var7) {
                var7.printStackTrace();
            }
        }
    }
}
