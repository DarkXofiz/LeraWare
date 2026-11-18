package neva.ware.features.hud.impl;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_2663;
import net.minecraft.class_332;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.features.modules.combat.Aura;
import neva.ware.features.modules.combat.AutoCrystal;
import neva.ware.gui.font.FontRenderers;
import neva.ware.setting.Setting;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.animation.AnimationUtility;
import org.jetbrains.annotations.NotNull;

public class KillFeed extends HudElement {
   private Setting<Boolean> resetOnDeath = new Setting<>("ResetOnDeath", true);
   private final List<KillFeed.KillComponent> players = new ArrayList<>();
   private float vAnimation;
   private float hAnimation;

   public KillFeed() {
      super("KillFeed", 50, 50);
   }

   @Override
   public void onRender2D(class_332 context) {
      super.onRender2D(context);
      int y_offset1 = 0;
      float scale_x = 30.0F;

      for (KillFeed.KillComponent kc : Lists.newArrayList(this.players)) {
         if (FontRenderers.modules.getStringWidth(kc.getString()) > scale_x) {
            scale_x = FontRenderers.modules.getStringWidth(kc.getString());
         }

         y_offset1 += 15;
      }

      this.vAnimation = AnimationUtility.fast(this.vAnimation, (float)(14 + y_offset1), 15.0F);
      this.hAnimation = AnimationUtility.fast(this.hAnimation, scale_x + 10.0F, 15.0F);
      NwHud.drawHudBase(context.method_51448(), this.getPosX(), this.getPosY(), this.hAnimation, this.vAnimation, HudEditor.hudRound.getValue());
      if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
         FontRenderers.sf_bold
            .drawCenteredString(
               context.method_51448(),
               "KillFeed",
               (double)(this.getPosX() + this.hAnimation / 2.0F),
               (double)(this.getPosY() + 4.0F),
               HudEditor.textColor.getValue().getColorObject()
            );
      } else {
         FontRenderers.sf_bold
            .drawGradientCenteredString(context.method_51448(), "KillFeed", this.getPosX() + this.hAnimation / 2.0F, this.getPosY() + 4.0F, 10);
      }

      if (y_offset1 > 0) {
         if (HudEditor.hudStyle.is(HudEditor.HudStyle.Glowing)) {
            Render2DEngine.horizontalGradient(
               context.method_51448(),
               this.getPosX() + 2.0F,
               this.getPosY() + 13.7F,
               this.getPosX() + 2.0F + this.hAnimation / 2.0F - 2.0F,
               this.getPosY() + 14.0F,
               Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0),
               HudEditor.textColor.getValue().getColorObject()
            );
            Render2DEngine.horizontalGradient(
               context.method_51448(),
               this.getPosX() + 2.0F + this.hAnimation / 2.0F - 2.0F,
               this.getPosY() + 13.7F,
               this.getPosX() + 2.0F + this.hAnimation - 4.0F,
               this.getPosY() + 14.0F,
               HudEditor.textColor.getValue().getColorObject(),
               Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0)
            );
         } else {
            Render2DEngine.drawRectDumbWay(
               context.method_51448(),
               this.getPosX() + 4.0F,
               this.getPosY() + 13.0F,
               this.getPosX() + this.getWidth() - 4.0F,
               this.getPosY() + 13.5F,
               new Color(1426063359, true)
            );
         }
      }

      Render2DEngine.addWindow(context.method_51448(), this.getPosX(), this.getPosY(), this.getPosX() + this.hAnimation, this.getPosY() + this.vAnimation, 1.0);
      int y_offset = 3;

      for (KillFeed.KillComponent kc : Lists.newArrayList(this.players)) {
         FontRenderers.modules
            .drawString(context.method_51448(), kc.getString(), (double)(this.getPosX() + 5.0F), (double)(this.getPosY() + 18.0F + (float)y_offset), -1);
         y_offset += 10;
      }

      Render2DEngine.popWindow();
      this.setBounds(this.getPosX(), this.getPosY(), this.hAnimation, this.vAnimation);
   }

   @EventHandler
   public void onPacket(@NotNull PacketEvent.Receive e) {
      if (e.getPacket() instanceof class_2663 pac) {
         if (pac.method_11470() == 3 && pac.method_11469(mc.field_1687) instanceof class_1657 pl) {
            if (pl == mc.field_1724 && this.resetOnDeath.getValue()) {
               this.players.clear();
               return;
            }

            if (Aura.target != null && Aura.target == pac.method_11469(mc.field_1687)
               || AutoCrystal.target != null && AutoCrystal.target == pac.method_11469(mc.field_1687)) {
               for (KillFeed.KillComponent kc : Lists.newArrayList(this.players)) {
                  if (Objects.equals(kc.getName(), pl.method_5477().getString())) {
                     kc.increase();
                     return;
                  }
               }

               this.players.add(new KillFeed.KillComponent(pl.method_5477().getString()));
            }
         }
      }
   }

   @Override
   public void onDisable() {
      this.players.clear();
   }

   private class KillComponent {
      private String name;
      private int count;

      public KillComponent(String name) {
         this.name = name;
         this.count = 1;
      }

      public void increase() {
         this.count++;
      }

      public String getName() {
         return this.name;
      }

      public String getString() {
         return class_124.field_1061
            + "EZ - "
            + class_124.field_1070
            + this.name
            + (this.count > 1 ? " [" + class_124.field_1080 + "x" + this.count + class_124.field_1070 + "]" : "");
      }
   }
}
