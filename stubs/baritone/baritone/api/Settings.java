package baritone.api;

public final class Settings {
    public static class Setting<T> { public T value; }
    public final Setting<Boolean> allowBreak = new Setting<>();
    public final Setting<Boolean> allowPlace = new Setting<>();
    public final Setting<Boolean> allowSprint = new Setting<>();
    public final Setting<Boolean> chatDebug = new Setting<>();
    public final Setting<Boolean> enterPortal = new Setting<>();
    public final Setting<Boolean> desktopNotifications = new Setting<>();
}
