package neva.ware.features.hud.impl;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_332;
import neva.ware.core.Managers;
import neva.ware.events.impl.TotemPopEvent;
import neva.ware.features.hud.HudElement;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.combat.AntiBot;
import neva.ware.gui.font.FontRenderers;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;
import neva.ware.utility.math.MathUtility;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.TextureStorage;
import org.jetbrains.annotations.NotNull;

public class Companion extends HudElement {
   public Setting<Integer> scale = new Setting<>("Scale", 50, 0, 100);
   public Setting<Companion.Mode> mode = new Setting<>("Mode", Companion.Mode.Boykisser);
   public static int currentFrame;
   private String message = "";
   private Timer lastPop = new Timer();
   private Timer frameRate = new Timer();

   public Companion() {
      super("2DCompanion", 50, 10);
   }

   @Override
   public void onUpdate() {
      for (class_1657 player : mc.field_1687.method_18456()) {
         if (player != mc.field_1724
            && !AntiBot.bots.contains(player)
            && !(player.method_6032() > 0.0F)
            && Managers.COMBAT.popList.containsKey(player.method_5477().getString())) {
            if (ClientSettings.isRu()) {
               this.message = player.method_5477().getString()
                  + " попнул "
                  + (
                     Managers.COMBAT.popList.get(player.method_5477().getString()) > 1
                        ? Managers.COMBAT.popList.get(player.method_5477().getString()) + " тотемов и сдох! ИЗЗЗЗИИ"
                        : "тотем и сдох! ИЗЗЗЗИИ"
                  );
            } else {
               this.message = player.method_5477().getString()
                  + " popped "
                  + (
                     Managers.COMBAT.popList.get(player.method_5477().getString()) > 1
                        ? Managers.COMBAT.popList.get(player.method_5477().getString()) + " totems and died EZ LMAO!"
                        : "totem and died EZ LMAO!"
                  );
            }

            this.lastPop.reset();
         }
      }
   }

   @Override
   public void onRender2D(class_332 context) {
      super.onRender2D(context);
      context.method_51448().method_22903();
      context.method_51448().method_46416((float)((int)this.getPosX() + 100), (float)((int)this.getPosY() + 100), 0.0F);
      context.method_51448().method_22905((float)this.scale.getValue().intValue() / 100.0F, (float)this.scale.getValue().intValue() / 100.0F, 1.0F);
      context.method_51448().method_46416((float)(-((int)this.getPosX() + 100)), (float)(-((int)this.getPosY() + 100)), 0.0F);
      if (this.mode.getValue() == Companion.Mode.Boykisser) {
         context.method_25290(TextureStorage.boykisser, (int)this.getPosX(), (int)this.getPosY(), 0.0F, (float)(currentFrame * 128), 130, 128, 130, 6784);
      } else if (this.mode.getValue() == Companion.Mode.Paimon) {
         context.method_25290(TextureStorage.paimon, (int)this.getPosX(), (int)this.getPosY(), 0.0F, (float)(currentFrame * 200), 200, 200, 200, 10600);
      } else if (this.mode.getValue() == Companion.Mode.Baltika) {
         context.method_25290(TextureStorage.baltika, (int)this.getPosX(), (int)this.getPosY(), 0.0F, 0.0F, 421, 800, 421, 800);
      } else if (this.mode.getValue() == Companion.Mode.Kowk) {
         context.method_25290(TextureStorage.kowk, (int)this.getPosX(), (int)this.getPosY(), 0.0F, 0.0F, 287, 252, 287, 252);
      }

      context.method_51448().method_22909();
      if (!this.lastPop.passedMs(2000L)) {
         float w = FontRenderers.sf_bold.getStringWidth(this.message) + 8.0F;
         float factor = MathUtility.clamp((float)this.lastPop.getPassedTimeMs(), 0.0F, 500.0F) / 500.0F;
         Render2DEngine.drawRound(
            context.method_51448(),
            this.getPosX() + (float)this.scale.getValue().intValue() / 3.0F,
            this.getPosY() + 70.0F - (float)this.scale.getValue().intValue(),
            factor * w,
            10.0F,
            3.0F,
            new Color(16570333)
         );
         Render2DEngine.addWindow(
            context.method_51448(),
            this.getPosX() + (float)this.scale.getValue().intValue() / 3.0F,
            this.getPosY() + 72.0F - (float)this.scale.getValue().intValue(),
            factor * w + this.getPosX() + (float)this.scale.getValue().intValue() / 3.0F,
            20.0F + this.getPosY() + 72.0F - (float)this.scale.getValue().intValue(),
            1.0
         );
         FontRenderers.sf_bold
            .drawString(
               context.method_51448(),
               this.message,
               (double)(this.getPosX() + 2.0F + (float)this.scale.getValue().intValue() / 3.0F),
               (double)(this.getPosY() + 72.0F - (float)this.scale.getValue().intValue()),
               new Color(4737096).getRGB()
            );
         Render2DEngine.popWindow();
      }

      if (this.frameRate.passedMs(64L)) {
         this.frameRate.reset();
         currentFrame++;
         if (currentFrame > 52) {
            currentFrame = 0;
         }
      }

      if (this.mode.getValue() == Companion.Mode.Baltika) {
         this.setBounds(
            this.getPosX() + 100.0F, this.getPosY() + 100.0F, (float)this.scale.getValue().intValue() * 3.0F, (float)this.scale.getValue().intValue() * 3.0F
         );
      } else {
         this.setBounds(this.getPosX(), this.getPosY(), (float)this.scale.getValue().intValue() * 3.0F, (float)this.scale.getValue().intValue() * 3.0F);
      }
   }

   @EventHandler
   public void onTotemPop(@NotNull TotemPopEvent event) {
      if (event.getEntity() != mc.field_1724) {
         if (ClientSettings.isRu()) {
            this.message = event.getEntity().method_5477().getString() + " попнул " + (event.getPops() > 1 ? event.getPops() + " тотемов!" : "тотем!");
         } else {
            this.message = event.getEntity().method_5477().getString() + " popped " + (event.getPops() > 1 ? event.getPops() + " totems!" : " a totem!");
         }

         this.lastPop.reset();
      }
   }

   private static enum Mode {
      Boykisser,
      Paimon,
      Baltika,
      Kowk;
   }
}
