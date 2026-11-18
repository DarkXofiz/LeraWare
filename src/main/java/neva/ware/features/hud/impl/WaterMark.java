package neva.ware.features.hud.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import neva.ware.NevaWare;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwHud;
import neva.ware.features.modules.client.Media;
import neva.ware.features.modules.misc.NameProtect;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.setting.Setting;
import neva.ware.utility.math.FrameRateCounter;

public class WaterMark extends HudElement {
   public final Setting<Boolean> nwNick = new Setting<>("Nick", true);
   public final Setting<Boolean> nwFps = new Setting<>("Fps", true);
   public final Setting<Boolean> nwTime = new Setting<>("Time", true);
   public final Setting<Boolean> infoRow = new Setting<>("InfoRow", true);
   public final Setting<Boolean> nwCoords = new Setting<>("Coords", true, var1 -> this.infoRow.getValue());
   public final Setting<Boolean> nwPing = new Setting<>("Ping", true, var1 -> this.infoRow.getValue());
   public final Setting<Boolean> nwTicks = new Setting<>("Ticks", true, var1 -> this.infoRow.getValue());
   public final Setting<Boolean> nwBps = new Setting<>("Bps", true, var1 -> this.infoRow.getValue());
   private final NwHud.Anim w1 = new NwHud.Anim();
   private final NwHud.Anim w2 = new NwHud.Anim();
   private final NwHud.Anim row2 = new NwHud.Anim();
   private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

   public WaterMark() {
      super("WaterMark", 100, 35);
   }

   private static String nick() {
      try {
         if (ModuleManager.nameProtect != null && ModuleManager.nameProtect.isEnabled()) {
            return NameProtect.getCustomName();
         } else {
            return ModuleManager.media != null && ModuleManager.media.isEnabled() && Media.nickProtect.getValue()
               ? "Protected"
               : mc.method_1548().method_1676();
         }
      } catch (Throwable var1) {
         return "Player";
      }
   }

   @Override
   public void onRender2D(class_332 var1) {
      super.onRender2D(var1);
      class_4587 var2 = var1.method_51448();
      float var3 = this.getPosX();
      float var4 = this.getPosY();
      ArrayList var5 = new ArrayList();
      var5.add(new NwHud.Seg(NwHud.Icon.LOGO, "NevaWare", null, true));
      if (this.nwNick.getValue()) {
         var5.add(new NwHud.Seg(NwHud.Icon.USER, nick(), null, false));
      }

      if (this.nwFps.getValue()) {
         var5.add(new NwHud.Seg(NwHud.Icon.FPS, String.valueOf(FrameRateCounter.INSTANCE.getFps()), "Fps", false));
      }

      if (this.nwTime.getValue()) {
         var5.add(new NwHud.Seg(NwHud.Icon.CLOCK, this.timeFormat.format(new Date(System.currentTimeMillis())), null, false));
      }

      float var6 = NwHud.segBar(var2, var3, var4, var5, this.w1);
      float var7 = 15.0F;
      ArrayList var8 = new ArrayList();
      if (this.infoRow.getValue()) {
         class_746 var9 = mc.field_1724;
         if (this.nwCoords.getValue() && var9 != null) {
            var8.add(
               new NwHud.Seg(
                  NwHud.Icon.PIN,
                  (int)Math.floor(var9.method_23317()) + " " + (int)Math.floor(var9.method_23318()) + " " + (int)Math.floor(var9.method_23321()),
                  null,
                  false
               )
            );
         }

         if (this.nwPing.getValue()) {
            var8.add(new NwHud.Seg(NwHud.Icon.PING, String.valueOf(Managers.SERVER.getPing()), "Ping", false));
         }

         if (this.nwTicks.getValue()) {
            var8.add(new NwHud.Seg(NwHud.Icon.TPS, String.format("%.1f", Managers.SERVER.getTPS()), "Ticks", false));
         }

         if (this.nwBps.getValue()) {
            float var10 = Managers.PLAYER.currentPlayerSpeed * 20.0F * NevaWare.TICK_TIMER;
            var8.add(new NwHud.Seg(NwHud.Icon.SPEED, String.format("%.1f", var10), "bps", false));
         }
      }

      float var12 = this.row2.to(var8.isEmpty() ? 0.0F : 1.0F, 12.0F);
      if (var12 > 0.01F && !var8.isEmpty()) {
         float var13 = NwDraw.alpha;
         NwDraw.alpha = var13 * var12;
         float var11 = NwHud.segBar(var2, var3, var4 + 15.0F + 3.0F - (1.0F - var12) * 4.0F, var8, this.w2);
         NwDraw.alpha = var13;
         var6 = Math.max(var6, var11);
         var7 += 18.0F * var12;
      }

      this.setBounds(var3, var4, var6, var7);
   }
}
