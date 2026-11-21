package neva.ware.core.hooks;

import neva.ware.core.manager.client.ModuleManager;

public class ModuleShutdownHook extends Thread {
    public void run() {
        if (ModuleManager.unHook.isEnabled()) {
            ModuleManager.unHook.disable();
        }
    }
}
