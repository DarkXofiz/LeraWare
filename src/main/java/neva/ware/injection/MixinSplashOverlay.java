package neva.ware.injection;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.class_156;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4011;
import net.minecraft.class_425;
import net.minecraft.class_4587;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.gui.mainmenu.LeraAssets;
import neva.ware.gui.mainmenu.PanoramaBackground;
import neva.ware.utility.render.NwCursor;
import neva.ware.utility.render.Render2DEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_425.class})
public abstract class MixinSplashOverlay {
   @Final
   @Shadow
   private boolean field_18219;
   @Shadow
   private float field_17770;
   @Shadow
   private long field_17771 = -1L;
   @Shadow
   private long field_18220 = -1L;
   @Final
   @Shadow
   private class_4011 field_17767;
   @Final
   @Shadow
   private Consumer<Optional<Throwable>> field_18218;

   @Inject(
      method = {"Lnet/minecraft/class_425;method_25394(Lnet/minecraft/class_332;IIF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void render(class_332 var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (!ModuleManager.unHook.isEnabled() && ClientSettings.customLoadingScreen.getValue()) {
         var5.cancel();
         this.renderCustom(var1, var2, var3, var4);
      }
   }

   public void renderCustom(class_332 var1, int var2, int var3, float var4) {
      int var5 = Module.mc.method_22683().method_4486();
      int var6 = Module.mc.method_22683().method_4502();
      long var7 = class_156.method_658();
      if (this.field_18219 && this.field_18220 == -1L) {
         this.field_18220 = var7;
      }

      float var9 = this.field_17771 > -1L ? (float)(var7 - this.field_17771) / 600.0F : -1.0F;
      float var10 = this.field_18220 > -1L ? (float)(var7 - this.field_18220) / 500.0F : -1.0F;
      boolean var13 = false;
      boolean var14 = false;
      float var12;
      float var38;
      if (var9 >= 0.0F) {
         if (Module.mc.field_1755 != null) {
            Module.mc.field_1755.method_25394(var1, 0, 0, var4);
         }

         float var11 = 1.0F - class_3532.method_15363(var9 - 0.0F, 0.0F, 1.0F);
         var38 = var11 * var11 * (3.0F - 2.0F * var11);
         var12 = var38;
         var13 = true;
      } else if (this.field_18219) {
         if (Module.mc.field_1755 != null && var10 < 1.0F) {
            Module.mc.field_1755.method_25394(var1, var2, var3, var4);
         }

         var38 = (float)class_3532.method_15350((double)var10, 0.15, 1.0);
         var12 = class_3532.method_15363(var10, 0.0F, 1.0F);
         var13 = true;
      } else {
         var38 = 1.0F;
         var12 = 1.0F;
         var14 = true;
      }

      boolean var15 = ClientSettings.customPanorama.getValue();
      boolean var16 = false;
      if (var15 && (var14 || var38 >= -1.0F || this.field_18219 && var9 < 1.0F)) {
         try {
            class_4587 var10000 = var1.method_51448();
            float var10003 = (float)var5;
            float var10004 = (float)var6;
            PanoramaBackground.fadeAlpha = var38;
            PanoramaBackground.drawMainMenuShader(var10000, 0.0F, 0.0F, var10003, var10004);
            var16 = true;
         } catch (Throwable var37) {
         }
      }

      if (!var16 || var13) {
         if (var14) {
            float var17 = 0.019607844F;
            float var18 = 0.019607844F;
            float var19 = 0.039215688F;
            GlStateManager._clearColor(var17, var18, var19, 1.0F);
            GlStateManager._clear(16384, false);
         } else if (!var15) {
            int var39 = class_3532.method_15386(var38 * 255.0F);
            var1.method_25294(0, 0, var5, var6, withAlpha(328970, var39));
         }
      }

      float var41 = this.field_17770;
      Color var42 = HudEditor.getColor(0);
      Color var43 = HudEditor.getColor(90);
      int guiW = var1.method_51421();
      int guiH = var1.method_51443();
      LeraAssets.Tex logo = LeraAssets.logo();
      int logoW = Math.min((int)((float)guiW * 0.5F), 420);
      int logoH = logo != null ? Math.max(1, Math.round((float)logoW * (float)logo.h / (float)logo.w)) : 0;
      int logoX = (guiW - logoW) / 2;
      int logoY = (guiH - logoH) / 2;
      RenderSystem.enableBlend();

      try {
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var12);
         if (logo != null) {
            var1.method_25293(logo.id, logoX, logoY, logoW, logoH, 0.0F, 0.0F, logo.w, logo.h, logo.w, logo.h);
         }
      } catch (Throwable ignoredLogo) {
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

      try {
         float var26 = (float)logoW * 0.6F;
         float var27 = 4.0F;
         float var28 = ((float)guiW - var26) / 2.0F;
         float var29 = (float)(logoY + logoH) + 14.0F;
         float var30 = Math.max(var27, var26 * class_3532.method_15363(var41, 0.0F, 1.0F));
         Color var31 = new Color(255, 255, 255, (int)(35.0F * var12));
         Render2DEngine.drawRound(var1.method_51448(), var28, var29, var26, var27, 1.5F, var31);
         Color var32 = Render2DEngine.applyOpacity(var42, var12);
         Color var33 = Render2DEngine.applyOpacity(var43, var12);
         Render2DEngine.drawBlurredShadow(
            var1.method_51448(), var28, var29, NwCursor.quantize(var30), var27, 8, Render2DEngine.applyOpacity(var42, var12 * 0.5F)
         );
         Render2DEngine.drawGradientRound(var1.method_51448(), var28, var29, var30, var27, 1.5F, var32, var33, var33, var32);
      } catch (Throwable ignoredBar) {
      }

      float var44 = this.field_17767.method_18229();
      this.field_17770 = class_3532.method_15363(this.field_17770 * 0.95F + var44 * 0.050000012F, 0.0F, 1.0F);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      if (var9 >= 1.0F) {
         Module.mc.method_18502(null);
      }

      if (this.field_17771 == -1L && this.field_17767.method_18787() && (!this.field_18219 || var10 >= 1.0F)) {
         try {
            this.field_17767.method_18849();
            this.field_18218.accept(Optional.empty());
         } catch (Throwable var34) {
            this.field_18218.accept(Optional.of(var34));
         }

         this.field_17771 = class_156.method_658();
         if (Module.mc.field_1755 != null) {
            Module.mc.field_1755.method_25423(Module.mc, Module.mc.method_22683().method_4486(), Module.mc.method_22683().method_4502());
            this.field_17771 = class_156.method_658();
         }
      }
   }

   private static int withAlpha(int var0, int var1) {
      return var0 & 16777215 | var1 << 24;
   }
}
