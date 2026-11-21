package neva.ware.utility;

public final class BindNames {
    private BindNames() {
    }

    public static String normalize(String var0) {
        if (var0 == null) {
            return null;
        } else {
            String var1 = var0.trim().toUpperCase().replace(' ', '_');
            switch (var1) {
                case "LEFT_SHIFT":
                case "RIGHT_SHIFT":
                case "LEFT_CONTROL":
                case "RIGHT_CONTROL":
                case "LEFT_ALT":
                case "RIGHT_ALT":
                    return var1;
                default:
                    return var0;
            }
        }
    }
}
