package neva.ware.injection;

import net.minecraft.class_1007;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_591;
import net.minecraft.class_742;
import net.minecraft.class_7833;
import net.minecraft.class_922;
import net.minecraft.class_5617.class_5618;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.models.CustomModelRenderer;
import neva.ware.features.modules.render.Models;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1007.class})
public abstract class MixinPlayerEntityRenderer extends class_922<class_742, class_591<class_742>> {
   @Unique
   private CustomModelRenderer customModelRenderer;

   public MixinPlayerEntityRenderer(class_5618 ctx, class_591<class_742> model, float shadowRadius) {
      super(ctx, model, shadowRadius);
   }

   @Inject(
      method = {"Lnet/minecraft/class_1007;<init>(Lnet/minecraft/class_5617$class_5618;Z)V"},
      at = {@At("RETURN")}
   )
   private void initCustomModels(class_5618 ctx, boolean slim, CallbackInfo ci) {
      this.customModelRenderer = new CustomModelRenderer();
   }

   @Inject(
      method = {"Lnet/minecraft/class_1007;method_4215(Lnet/minecraft/class_742;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderCustomModel(class_742 player, float yaw, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
      if (ModuleManager.models != null && ModuleManager.models.isEnabled()) {
         class_310 mc = class_310.method_1551();
         boolean isSelf = player == mc.field_1724;
         boolean isFriend = Managers.FRIEND.isFriend(player.method_5477().getString());
         boolean shouldRenderCustom;
         if (ModuleManager.models.onlySelf.getValue()) {
            shouldRenderCustom = isSelf || isFriend && ModuleManager.models.friends.getValue();
         } else {
            shouldRenderCustom = true;
         }

         if (shouldRenderCustom) {
            float limbSwing = player.field_42108.method_48572(tickDelta);
            float limbSwingAmount = player.field_42108.method_48570(tickDelta);
            ((class_591)this.field_4737)
               .method_17087(player, limbSwing, limbSwingAmount, (float)player.field_6012, player.field_6241 - player.field_6283, player.method_36455());
            matrices.method_22903();
            matrices.method_22904(0.0, (double)player.method_17682() / 2.0, 0.0);
            matrices.method_22907(class_7833.field_40716.rotationDegrees(180.0F - player.field_6283));
            Models.Mode mode = ModuleManager.models.mode.getValue();
            if (mode == Models.Mode.Amogus || mode == Models.Mode.Rabbit) {
               matrices.method_22907(new Quaternionf().rotationX((float) Math.PI));
               if (ModuleManager.models.changeZ.getValue()) {
                  matrices.method_22907(new Quaternionf().rotationY((float) Math.PI));
               }
            }

            matrices.method_22904(0.0, (double)(-player.method_17682()) / 2.0, 0.0);
            if (mode == Models.Mode.Rabbit) {
               matrices.method_22904(0.0, -0.05, 0.0);
            } else if (mode == Models.Mode.Amogus) {
               matrices.method_22904(0.0, -0.5, 0.0);
            }

            float scale = mode == Models.Mode.Rabbit ? 1.25F : 1.8F;
            matrices.method_22905(scale, scale, scale);
            this.customModelRenderer.render(player, yaw, tickDelta, matrices, vertexConsumers, light, mode, (class_591)this.field_4737);
            matrices.method_22909();
            ci.cancel();
         }
      }
   }
}
