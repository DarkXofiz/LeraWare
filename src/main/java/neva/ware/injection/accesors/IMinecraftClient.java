package neva.ware.injection.accesors;

import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_5520;
import net.minecraft.class_7574;
import net.minecraft.class_7853;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_310.class})
public interface IMinecraftClient {
   @Accessor("field_1752")
   int getUseCooldown();

   @Accessor("field_1752")
   void setUseCooldown(int var1);

   @Invoker("method_1583()V")
   void idoItemUse();

   @Invoker("method_1536()Z")
   boolean idoAttack();

   @Mutable
   @Accessor("field_39068")
   void setProfileKeys(class_7853 var1);

   @Mutable
   @Accessor("field_1726")
   void setSessionT(class_320 var1);

   @Mutable
   @Accessor("field_26902")
   void setUserApiService(UserApiService var1);

   @Mutable
   @Accessor("field_26842")
   void setSocialInteractionsManagerT(class_5520 var1);

   @Mutable
   @Accessor("field_39492")
   void setAbuseReportContextT(class_7574 var1);
}
