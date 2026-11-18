package neva.ware.injection;

import com.mojang.authlib.GameProfile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import net.minecraft.class_156;
import net.minecraft.class_2960;
import net.minecraft.class_640;
import net.minecraft.class_8685;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.utility.NevaUtility;
import neva.ware.utility.OptifineCapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_640.class})
public class MixinPlayerListEntry {
   @Unique
   private boolean loadedCapeTexture;
   @Unique
   private class_2960 customCapeTexture;

   @Inject(
      method = {"<init>(Lcom/mojang/authlib/GameProfile;Z)V"},
      at = {@At("TAIL")}
   )
   private void initHook(GameProfile profile, boolean secureChatEnforced, CallbackInfo ci) {
      this.getTexture(profile);
   }

   @Inject(
      method = {"getSkinTextures"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private void getCapeTexture(CallbackInfoReturnable<class_8685> cir) {
      if (this.customCapeTexture != null) {
         class_8685 prev = (class_8685)cir.getReturnValue();
         class_8685 newTextures = new class_8685(
            prev.comp_1626(), prev.comp_1911(), this.customCapeTexture, this.customCapeTexture, prev.comp_1629(), prev.comp_1630()
         );
         cir.setReturnValue(newTextures);
      }
   }

   @Unique
   private void getTexture(GameProfile profile) {
      if (!this.loadedCapeTexture) {
         this.loadedCapeTexture = true;
         class_156.method_18349().execute(() -> {
            try {
               URL capesList = new URL("https://raw.githubusercontent.com/Pan4ur/THRecodeUtil/main/capes/capeBase.txt");
               BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));

               String inputLine;
               while ((inputLine = in.readLine()) != null) {
                  String colune = inputLine.trim();
                  String name = colune.split(":")[0];
                  String cape = colune.split(":")[1];
                  if (Objects.equals(profile.getName(), name)) {
                     this.customCapeTexture = class_2960.method_60655("nevaware", "textures/capes/" + cape + ".png");
                     return;
                  }
               }
            } catch (Exception var8) {
            }

            for (String str : NevaUtility.starGazer) {
               if (profile.getName().toLowerCase().equals(str.toLowerCase())) {
                  this.customCapeTexture = class_2960.method_60655("nevaware", "textures/capes/starcape.png");
               }
            }

            if (ModuleManager.optifineCapes.isEnabled()) {
               OptifineCapes.loadPlayerCape(profile, id -> this.customCapeTexture = id);
            }
         });
      }
   }
}
