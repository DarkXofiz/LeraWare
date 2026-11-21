package neva.ware.utility.render;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.glfw.GLFW;

public final class NwCursor {
    private static final Map<Integer, Long> CACHE = new HashMap();

    private NwCursor() {
    }

    public static long get(int var0) {
        Long var1 = (Long)CACHE.get(var0);
        if (var1 == null || var1 == 0L) {
            var1 = GLFW.glfwCreateStandardCursor(var0);
            if (var1 != 0L) {
                CACHE.put(var0, var1);
            }
        }

        return var1;
    }

    public static float quantize(float var0) {
        return Math.max(6.0F, Math.round(var0 / 6.0F) * 6.0F);
    }
}
