package neva.ware.injection;

import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1934;
import net.minecraft.class_268;
import net.minecraft.class_355;
import net.minecraft.class_640;
import net.minecraft.class_8144;
import neva.ware.NevaWare;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_355.class})
public class MixinPlayerListHud {
   private static final Comparator<Object> ENTRY_ORDERING = Comparator.<Object>comparingInt(
         entry -> ((class_640)entry).method_2958() == class_1934.field_9219 ? 1 : 0
      )
      .thenComparing(entry -> (String)class_8144.method_49078(((class_640)entry).method_2955(), class_268::method_1197, ""))
      .thenComparing(entry -> ((class_640)entry).method_2966().getName(), String::compareToIgnoreCase);

   @Inject(
      method = {"collectPlayerEntries"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void collectPlayerEntriesHook(CallbackInfoReturnable<List<class_640>> cir) {
      if (!ClientSettings.futureCompatibility.getValue()) {
         if (!NevaWare.isFuturePresent()) {
            if (ModuleManager.extraTab.isEnabled()) {
               cir.setReturnValue(Module.mc.field_1724.field_3944.method_45732().stream().sorted(ENTRY_ORDERING).limit(1000L).toList());
            } else {
               cir.setReturnValue(Module.mc.field_1724.field_3944.method_45732().stream().sorted(ENTRY_ORDERING).limit(80L).toList());
            }
         }
      }
   }
}
