package neva.ware.features.modules.client;

import baritone.api.BaritoneAPI;
import neva.ware.NevaWare;
import neva.ware.features.modules.Module;
import neva.ware.gui.clickui.ClickGUI;
import neva.ware.setting.Setting;

public class ClickGui extends Module {
   public final Setting<Boolean> descriptions = new Setting<>("Descriptions", true);
   public final Setting<Boolean> blur = new Setting<>("Blur", true);
   public final Setting<Boolean> tips = new Setting<>("Tips", true);
   public final Setting<Boolean> pauseBaritone = new Setting<>("PauseBaritone", false);
   public final Setting<NwCloseAnim> openAnimation;
   public final Setting<NwCloseAnim> closeAnimation = new Setting<>("CloseAnimation", NwCloseAnim.V2);

   public ClickGui() {
      super("ClickGui", Module.Category.CLIENT);
      this.openAnimation = new Setting<>("OpenAnimation", NwCloseAnim.V2);
   }

   @Override
   public void onEnable() {
      if (this.pauseBaritone.getValue() && !fullNullCheck() && NevaWare.baritone) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("pause");
      }

      this.setGui();
   }

   @Override
   public void onDisable() {
      if (this.pauseBaritone.getValue() && !fullNullCheck() && NevaWare.baritone) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("resume");
      }
   }

   public void setGui() {
      mc.method_1507(ClickGUI.getClickGui());
   }

   @Override
   public void onUpdate() {
      if (!(mc.field_1755 instanceof ClickGUI)) {
         this.disable();
      }
   }
}
