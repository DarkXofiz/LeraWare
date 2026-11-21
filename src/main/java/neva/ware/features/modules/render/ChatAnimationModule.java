package neva.ware.features.modules.render;

import com.ezzenix.chatanimation.config.ModConfig;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;

public class ChatAnimationModule extends Module {
    public final Setting<Boolean> messageAnim = new Setting<>("Message Animation", Boolean.TRUE);
    public final Setting<Integer> messageTime = new Setting<>("Message Time", 150, 50, 1000);
    public final Setting<Boolean> fadeOpacity = new Setting<>("Fade Opacity", Boolean.TRUE);
    public final Setting<Boolean> removeIndicator = new Setting<>("Remove Indicator", Boolean.TRUE);
    public final Setting<Boolean> inputAnim = new Setting<>("Input Animation", Boolean.TRUE);
    public final Setting<Integer> inputTime = new Setting<>("Input Time", 170, 50, 1000);

    public ChatAnimationModule() {
        super("ChatAnimation", Module.Category.RENDER);
        off();
    }

    private static void off() {
        ModConfig.enableMessageAnimation = false;
        ModConfig.enableOpacity = false;
        ModConfig.removeMessageIndicator = false;
        ModConfig.enableTextFieldAnimation = false;
    }

    private void sync() {
        ModConfig.enableMessageAnimation = this.messageAnim.getValue();
        ModConfig.fadeTimeMessage = this.messageTime.getValue();
        ModConfig.enableOpacity = this.fadeOpacity.getValue();
        ModConfig.removeMessageIndicator = this.removeIndicator.getValue();
        ModConfig.enableTextFieldAnimation = this.inputAnim.getValue();
        ModConfig.fadeTimeTextField = this.inputTime.getValue();
    }

    @Override
    public void onEnable() {
        this.sync();
    }

    @Override
    public void onDisable() {
        off();
    }

    @Override
    public void onUpdate() {
        this.sync();
    }
}
