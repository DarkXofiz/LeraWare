package neva.ware.injection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1011;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import net.minecraft.class_3262;
import net.minecraft.class_4071;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_542;
import net.minecraft.class_6417;
import net.minecraft.class_642;
import net.minecraft.class_8518;
import neva.ware.NevaWare;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.events.impl.EventAttack;
import neva.ware.events.impl.EventHandleBlockBreaking;
import neva.ware.events.impl.EventPostTick;
import neva.ware.events.impl.EventScreen;
import neva.ware.events.impl.EventTick;
import neva.ware.features.modules.Module;
import neva.ware.gui.clickui.ClickGUI;
import neva.ware.gui.font.FontRenderers;
import neva.ware.utility.render.WindowResizeCallback;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_310.class})
public abstract class MixinMinecraftClient {
   @Shadow
   @Final
   private class_1041 field_1704;
   @Unique
   private String[] shittyServers = new String[]{"mineblaze", "musteryworld", "dexland", "masedworld", "vimeworld", "hypemc", "vimemc"};

   @Shadow
   public abstract void method_1507(@Nullable class_437 var1);

   @Inject(
      method = {"Lnet/minecraft/class_310;<init>(Lnet/minecraft/class_542;)V"},
      at = {@At("TAIL")}
   )
   void postWindowInit(class_542 args, CallbackInfo ci) {
      try {
         FontRenderers.settings = FontRenderers.create(12.0F, "comfortaa");
         FontRenderers.modules = FontRenderers.create(15.0F, "comfortaa");
         FontRenderers.categories = FontRenderers.create(18.0F, "comfortaa");
         FontRenderers.thglitch = FontRenderers.create(36.0F, "glitched");
         FontRenderers.thglitchBig = FontRenderers.create(72.0F, "glitched");
         FontRenderers.monsterrat = FontRenderers.create(18.0F, "monsterrat");
         FontRenderers.sf_bold = FontRenderers.create(16.0F, "sf_bold");
         FontRenderers.sf_medium = FontRenderers.create(16.0F, "sf_medium");
         FontRenderers.sf_medium_mini = FontRenderers.create(12.0F, "sf_medium");
         FontRenderers.sf_medium_modules = FontRenderers.create(14.0F, "sf_medium");
         FontRenderers.sf_bold_mini = FontRenderers.create(14.0F, "sf_bold");
         FontRenderers.sf_bold_micro = FontRenderers.create(12.0F, "sf_bold");
         FontRenderers.profont = FontRenderers.create(16.0F, "profont");
         FontRenderers.icons = FontRenderers.create(20.0F, "icons");
         FontRenderers.mid_icons = FontRenderers.create(46.0F, "icons");
         FontRenderers.big_icons = FontRenderers.create(72.0F, "icons");
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_310;method_1574()V"},
      at = {@At("HEAD")}
   )
   void preTickHook(CallbackInfo ci) {
      if (!Module.fullNullCheck()) {
         NevaWare.EVENT_BUS.post(new EventTick());
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_310;method_1574()V"},
      at = {@At("RETURN")}
   )
   void postTickHook(CallbackInfo ci) {
      if (!Module.fullNullCheck()) {
         NevaWare.EVENT_BUS.post(new EventPostTick());
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_310;method_15993()V"},
      at = {@At("TAIL")}
   )
   private void captureResize(CallbackInfo ci) {
      ((WindowResizeCallback)WindowResizeCallback.EVENT.invoker()).onResized((class_310)this, this.field_1704);
   }

   @Inject(
      method = {"Lnet/minecraft/class_310;method_1511()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void doItemPickHook(CallbackInfo ci) {
      if (ModuleManager.middleClick.isEnabled() && ModuleManager.middleClick.antiPickUp.getValue()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_310;method_18502(Lnet/minecraft/class_4071;)V"},
      at = {@At("HEAD")}
   )
   public void setOverlay(class_4071 overlay, CallbackInfo ci) {
   }

   @Inject(
      method = {"Lnet/minecraft/class_310;method_1507(Lnet/minecraft/class_437;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void setScreenHookPre(class_437 screen, CallbackInfo ci) {
      if (!Module.fullNullCheck()) {
         EventScreen event = new EventScreen(screen);
         NevaWare.EVENT_BUS.post(event);
         if (event.isCancelled() || ClickGUI.close && screen == null) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_310;method_1507(Lnet/minecraft/class_437;)V"},
      at = {@At("RETURN")}
   )
   public void setScreenHookPost(class_437 screen, CallbackInfo ci) {
      if (!Module.fullNullCheck()) {
         if (screen instanceof class_500 mScreen && ModuleManager.antiServerAdd.isEnabled() && mScreen.method_2529() != null) {
            for (int i = 0; i < mScreen.method_2529().method_2984(); i++) {
               class_642 info = mScreen.method_2529().method_2982(i);

               for (String server : this.shittyServers) {
                  if (info != null && info.field_3761 != null && info.field_3761.toLowerCase().contains(server.toLowerCase())) {
                     mScreen.method_2529().method_2983(info);
                     mScreen.method_2529().method_2987();
                     this.method_1507(screen);
                     break;
                  }
               }
            }
         }
      }
   }

   @Redirect(
      method = {"Lnet/minecraft/class_310;<init>(Lnet/minecraft/class_542;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1041;method_4491(Lnet/minecraft/class_3262;Lnet/minecraft/class_8518;)V"
      )
   )
   private void onChangeIcon(class_1041 instance, class_3262 resourcePack, class_8518 icons) throws IOException {
      if (GLFW.glfwGetPlatform() == 393218) {
         class_6417.method_41718(icons.method_51420(resourcePack));
      } else {
         this.setWindowIcon(NevaWare.class.getResourceAsStream("/icon.png"), NevaWare.class.getResourceAsStream("/icon.png"));
      }
   }

   public void setWindowIcon(InputStream img16x16, InputStream img32x32) {
      try {
         MemoryStack memorystack = MemoryStack.stackPush();

         try {
            Buffer buffer = GLFWImage.malloc(2, memorystack);
            List<InputStream> imgList = List.of(img16x16, img32x32);
            List<ByteBuffer> buffers = new ArrayList<>();

            for (int i = 0; i < imgList.size(); i++) {
               class_1011 nativeImage = class_1011.method_4309(imgList.get(i));
               ByteBuffer bytebuffer = MemoryUtil.memAlloc(nativeImage.method_4307() * nativeImage.method_4323() * 4);
               bytebuffer.asIntBuffer().put(nativeImage.method_48463());
               buffer.position(i);
               buffer.width(nativeImage.method_4307());
               buffer.height(nativeImage.method_4323());
               buffer.pixels(bytebuffer);
               buffers.add(bytebuffer);
            }

            try {
               if (GLFW.glfwGetPlatform() != 393219) {
                  GLFW.glfwSetWindowIcon(Module.mc.method_22683().method_4490(), buffer);
               }
            } catch (Exception var11) {
            }

            buffers.forEach(MemoryUtil::memFree);
         } catch (Throwable var12) {
            if (memorystack != null) {
               try {
                  memorystack.close();
               } catch (Throwable var10) {
                  var12.addSuppressed(var10);
               }
            }

            throw var12;
         }

         if (memorystack != null) {
            memorystack.close();
         }
      } catch (IOException var13) {
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_310;method_1536()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void doAttackHook(CallbackInfoReturnable<Boolean> cir) {
      EventAttack event = new EventAttack(null, true);
      NevaWare.EVENT_BUS.post(event);
      if (event.isCancelled()) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_310;method_1590(Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void handleBlockBreakingHook(boolean breaking, CallbackInfo ci) {
      EventHandleBlockBreaking event = new EventHandleBlockBreaking();
      NevaWare.EVENT_BUS.post(event);
      if (event.isCancelled()) {
         ci.cancel();
      }
   }
}
