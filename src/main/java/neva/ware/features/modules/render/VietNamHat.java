package neva.ware.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_4050;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_7833;
import net.minecraft.class_293.class_5596;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.setting.impl.SettingGroup;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.Render3DEngine;

public class VietNamHat extends Module {
   private final Setting<Float> radius = new Setting<>("Radius", 0.54F, 0.1F, 1.0F);
   private final Setting<VietNamHat.ColorMode> colorMode = new Setting<>("ColorMode", VietNamHat.ColorMode.Sync);
   private final Setting<ColorSetting> color = new Setting<>(
      "Color", new ColorSetting(Color.GRAY.getRGB()), v -> this.colorMode.is(VietNamHat.ColorMode.Custom)
   );
   private final Setting<SettingGroup> selection = new Setting<>("Selection", new SettingGroup(false, 0));
   private final Setting<Boolean> self = new Setting<>("Self", true).addToGroup(this.selection);
   private final Setting<Boolean> friends = new Setting<>("Friends", true).addToGroup(this.selection);
   private final Setting<Boolean> others = new Setting<>("Others", true).addToGroup(this.selection);

   public VietNamHat() {
      super("VietNamHat", Module.Category.RENDER);
   }

   @Override
   public void onRender3D(class_4587 stack) {
      boolean isFirstPerson = mc.field_1690.method_31044().method_31034();

      for (class_1657 pl : mc.field_1687.method_18456()) {
         if (!this.shouldSkip(pl, isFirstPerson)) {
            float tickDelta = Render3DEngine.getTickDelta();
            class_4050 pose = pl.method_18376();
            float eyeHeight = pl.method_18381(pose);
            float entityX = (float)(pl.field_6014 + (pl.method_23317() - pl.field_6014) * (double)tickDelta);
            float entityY = (float)(pl.field_6036 + (pl.method_23318() - pl.field_6036) * (double)tickDelta);
            float entityZ = (float)(pl.field_5969 + (pl.method_23321() - pl.field_5969) * (double)tickDelta);
            float camX = (float)mc.method_1561().field_4686.method_19326().field_1352;
            float camY = (float)mc.method_1561().field_4686.method_19326().field_1351;
            float camZ = (float)mc.method_1561().field_4686.method_19326().field_1350;
            float yaw = pl.field_6259 + (pl.field_6241 - pl.field_6259) * tickDelta;
            float headY = entityY + eyeHeight;
            float x = entityX - camX;
            float y = headY - camY;
            float z = entityZ - camZ;
            stack.method_22903();
            stack.method_46416(x, y, z);
            stack.method_22907(class_7833.field_40716.rotationDegrees(-yaw));
            boolean noRenderArmor = ModuleManager.noRender.isEnabled() && ModuleManager.noRender.armor.getValue();
            boolean hasHat = !pl.method_6118(class_1304.field_6169).method_7960();
            float offset = noRenderArmor ? 0.17F : (hasHat ? 0.27F : 0.17F);
            stack.method_46416(0.0F, offset, 0.0F);
            Render3DEngine.setupRender();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(515);
            RenderSystem.setShader(class_757::method_34540);
            int segments = 128;
            class_287 bufferBuilder = class_289.method_1348().method_60827(class_5596.field_27380, class_290.field_1576);

            for (int i = 0; i <= segments; i++) {
               int c = this.colorMode.is(VietNamHat.ColorMode.Sync)
                  ? Render2DEngine.applyOpacity(HudEditor.getColor(i * 360 / segments).getRGB(), 0.4F)
                  : this.color.getValue().getColor();
               bufferBuilder.method_22918(stack.method_23760().method_23761(), 0.0F, 0.25F, 0.0F).method_1336(c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF, 127);
               float x2 = -((float)(Math.sin((double)i * Math.PI * 2.0 / (double)segments) * (double)this.radius.getValue().floatValue()));
               float z2 = (float)(Math.cos((double)i * Math.PI * 2.0 / (double)segments) * (double)this.radius.getValue().floatValue());
               bufferBuilder.method_22918(stack.method_23760().method_23761(), x2, 0.0F, z2).method_1336(c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF, 127);
            }

            class_286.method_43433(bufferBuilder.method_60800());
            RenderSystem.lineWidth(2.0F);
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();
            stack.method_22909();
         }
      }
   }

   private boolean shouldSkip(class_1657 pl, boolean isFirstPerson) {
      if (pl != mc.field_1724) {
         return Managers.FRIEND.isFriend(pl) ? !this.friends.getValue() : !this.others.getValue();
      } else {
         return isFirstPerson || !this.self.getValue();
      }
   }

   public static enum ColorMode {
      Sync,
      Custom;
   }
}
