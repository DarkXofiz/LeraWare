package neva.ware.core.hooks;

import neva.ware.core.manager.client.ModuleManager;

public class ModuleShutdownHook extends Thread {
   @Override
   public void run() {
      if (ModuleManager.unHook.isEnabled()) {
         ModuleManager.unHook.disable();
      }
   }
}
