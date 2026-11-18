package neva.ware.injection;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.class_268;
import net.minecraft.class_269;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_269.class})
public abstract class MixinScoreBoard {
   @Final
   @Shadow
   private Object2ObjectMap<String, class_268> field_1427;

   @Inject(
      method = {"Lnet/minecraft/class_269;method_1157(Ljava/lang/String;Lnet/minecraft/class_268;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void removeScoreHolderFromTeamHook(String scoreHolderName, class_268 team, CallbackInfo ci) {
      ci.cancel();
      if (this.field_1427.get(scoreHolderName) == team) {
         this.field_1427.remove(scoreHolderName);
         team.method_1204().remove(scoreHolderName);
      }
   }
}
