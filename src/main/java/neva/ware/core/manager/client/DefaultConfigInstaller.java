package neva.ware.core.manager.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

public final class DefaultConfigInstaller {
   private DefaultConfigInstaller() {
   }

   public static void installIfMissing(File var0) {
      if (var0 != null && "config.nw".equals(var0.getName()) && !var0.exists()) {
         try {
            try (InputStream var1 = DefaultConfigInstaller.class.getResourceAsStream("/default.nw")) {
               if (var1 != null) {
                  File var2 = var0.getParentFile();
                  if (var2 != null && !var2.exists() && !var2.mkdirs() && !var2.isDirectory()) {
                     return;
                  }

                  try {
                     Files.copy(var1, var0.toPath());
                  } catch (FileAlreadyExistsException var5) {
                  }

                  return;
               }
            }
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }
   }
}
