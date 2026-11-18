package neva.ware.features.modules.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventAttack;
import neva.ware.features.modules.Module;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;

public class HitColor extends Module {
   public final Setting<ColorSetting> color = new Setting<>("Color", new ColorSetting(-65536));
   public final Setting<Float> opacity = new Setting<>("Opacity", 0.65F, 0.05F, 1.0F);
   public final Setting<Integer> lifeTime = new Setting<>("LifeTime", 400, 50, 3000);
   public final Setting<Boolean> fade = new Setting<>("Fade", true);
   public final Setting<Boolean> onlyMyHits = new Setting<>("OnlyMyHits", true);
   public final Setting<Boolean> onlyPlayers = new Setting<>("OnlyPlayers", true);
   public final Setting<Boolean> self = new Setting<>("Self", false);
   public final Setting<Boolean> removeVanillaRed = new Setting<>("RemoveVanillaRed", true);
   private static final Map<Integer, Long> hitStart = new HashMap<>();
   private static final Map<Integer, Long> lastAttack = new HashMap<>();
   private static final Map<Integer, Integer> lastHurt = new HashMap<>();
   public static class_1309 renderEntity;

   public HitColor() {
      super("HitColor", Module.Category.RENDER);
   }

   private static HitColor get() {
      return ModuleManager.hitColor;
   }

   @Override
   public void onDisable() {
      hitStart.clear();
      lastAttack.clear();
      lastHurt.clear();
      renderEntity = null;
   }

   @EventHandler
   public void onAttack(EventAttack var1) {
      class_1297 var2 = var1.getEntity();
      if (var2 != null) {
         lastAttack.put(var2.method_5628(), System.currentTimeMillis());
      }
   }

   @Override
   public void onUpdate() {
      if (!fullNullCheck() && mc.field_1687 != null) {
         long var1 = System.currentTimeMillis();
         HashMap var3 = new HashMap();

         for (class_1297 var5 : mc.field_1687.method_18112()) {
            if (var5 instanceof class_1309) {
               class_1309 var6 = (class_1309)var5;
               int var7 = var5.method_5628();
               int var8 = var6.field_6235;
               var3.put(var7, var8);
               Integer var9 = lastHurt.get(var7);
               boolean var10 = var8 > 0 && (var9 == null || var8 > var9);
               if (var10 && this.isValid(var6)) {
                  if (this.onlyMyHits.getValue() && var6 != mc.field_1724) {
                     Long var11 = lastAttack.get(var7);
                     if (var11 == null || var1 - var11 > 1000L) {
                        continue;
                     }
                  }

                  hitStart.put(var7, var1);
               }
            }
         }

         lastHurt.clear();
         lastHurt.putAll(var3);
         long var12 = (long)this.lifeTime.getValue().intValue();
         Iterator var13 = hitStart.entrySet().iterator();

         while (var13.hasNext()) {
            Entry var14 = (Entry)var13.next();
            if (var1 - (Long)var14.getValue() > var12 || !var3.containsKey(var14.getKey())) {
               var13.remove();
            }
         }

         lastAttack.entrySet().removeIf(var2 -> var1 - var2.getValue() > 1500L);
      }
   }

   private boolean isValid(class_1309 var1) {
      return var1 == mc.field_1724 ? this.self.getValue() : !this.onlyPlayers.getValue() || var1 instanceof class_1657;
   }

   private float progress(class_1309 var1) {
      Long var2 = hitStart.get(var1.method_5628());
      if (var2 == null) {
         return -1.0F;
      } else {
         long var3 = System.currentTimeMillis() - var2;
         long var5 = (long)this.lifeTime.getValue().intValue();
         return var3 > var5 ? -1.0F : (float)var3 / (float)var5;
      }
   }

   public static int modifyOverlay(int var0) {
      HitColor var1 = get();
      class_1309 var2 = renderEntity;
      if (var1 != null && var2 != null && var1.isEnabled() && var1.removeVanillaRed.getValue() && var1.isValid(var2)) {
         int var3 = var0 >>> 16 & 65535;
         return var3 < 8 ? var0 & 65535 | 655360 : var0;
      } else {
         return var0;
      }
   }

   public static int modifyColor(int var0) {
      HitColor var1 = get();
      class_1309 var2 = renderEntity;
      if (var1 != null && var2 != null && var1.isEnabled()) {
         float var3 = var1.progress(var2);
         if (var3 < 0.0F) {
            return var0;
         } else {
            float var4 = var1.opacity.getValue();
            if (var1.fade.getValue()) {
               float var5 = 1.0F - var3;
               var4 *= var5 * var5 * (3.0F - 2.0F * var5);
            }

            if (var4 <= 0.0F) {
               return var0;
            } else {
               int var13 = var1.color.getValue().getColor();
               float var6 = (float)(var13 >> 16 & 0xFF) / 255.0F;
               float var7 = (float)(var13 >> 8 & 0xFF) / 255.0F;
               float var8 = (float)(var13 & 0xFF) / 255.0F;
               int var9 = var0 >>> 24 & 0xFF;
               int var10 = var0 >> 16 & 0xFF;
               int var11 = var0 >> 8 & 0xFF;
               int var12 = var0 & 0xFF;
               var10 = Math.round((float)var10 * (1.0F + (var6 - 1.0F) * var4));
               var11 = Math.round((float)var11 * (1.0F + (var7 - 1.0F) * var4));
               var12 = Math.round((float)var12 * (1.0F + (var8 - 1.0F) * var4));
               return var9 << 24 | clamp(var10) << 16 | clamp(var11) << 8 | clamp(var12);
            }
         }
      } else {
         return var0;
      }
   }

   private static int clamp(int var0) {
      return var0 < 0 ? 0 : (var0 > 255 ? 255 : var0);
   }
}
