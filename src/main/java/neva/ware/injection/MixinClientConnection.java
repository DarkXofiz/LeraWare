package neva.ware.injection;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_8042;
import net.minecraft.class_8762;
import neva.ware.NevaWare;
import neva.ware.core.Managers;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.core.manager.client.ProxyManager;
import neva.ware.events.impl.PacketEvent;
import neva.ware.features.modules.Module;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2535.class})
public class MixinClientConnection {
   @Inject(
      remap = false,
      method = {"Lnet/minecraft/class_2535;exceptionCaught(Lio/netty/channel/ChannelHandlerContext;Ljava/lang/Throwable;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void exceptionCaughtHook(ChannelHandlerContext context, Throwable t, CallbackInfo ci) {
      if (ModuleManager.antiPacketException.isEnabled()) {
         ModuleManager.antiPacketException.sendChatMessage(t.getMessage());
         ci.cancel();
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_2535;method_10759(Lnet/minecraft/class_2596;Lnet/minecraft/class_2547;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static <T extends class_2547> void onHandlePacket(class_2596<T> packet, class_2547 listener, CallbackInfo info) {
      if (!Module.fullNullCheck()) {
         if (packet instanceof class_8042 packs) {
            packs.method_48324().forEach(p -> {
               PacketEvent.Receive eventx = new PacketEvent.Receive((class_2596<?>)p);
               NevaWare.EVENT_BUS.post(eventx);
               if (eventx.isCancelled()) {
                  info.cancel();
               }
            });
         } else {
            PacketEvent.Receive event = new PacketEvent.Receive(packet);
            NevaWare.EVENT_BUS.post(event);
            if (event.isCancelled()) {
               info.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_2535;method_10759(Lnet/minecraft/class_2596;Lnet/minecraft/class_2547;)V"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private static <T extends class_2547> void onHandlePacketPost(class_2596<T> packet, class_2547 listener, CallbackInfo info) {
      if (!Module.fullNullCheck()) {
         if (packet instanceof class_8042 packs) {
            packs.method_48324().forEach(p -> {
               PacketEvent.ReceivePost eventx = new PacketEvent.ReceivePost((class_2596<?>)p);
               NevaWare.EVENT_BUS.post(eventx);
               if (eventx.isCancelled()) {
                  info.cancel();
               }
            });
         } else {
            PacketEvent.ReceivePost event = new PacketEvent.ReceivePost(packet);
            NevaWare.EVENT_BUS.post(event);
            if (event.isCancelled()) {
               info.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_2535;method_10743(Lnet/minecraft/class_2596;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendPacketPre(class_2596<?> packet, CallbackInfo info) {
      if (!Module.fullNullCheck()) {
         if (NevaWare.core.silentPackets.contains(packet)) {
            NevaWare.core.silentPackets.remove(packet);
         } else {
            PacketEvent.Send event = new PacketEvent.Send(packet);
            NevaWare.EVENT_BUS.post(event);
            if (event.isCancelled()) {
               info.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_2535;method_10743(Lnet/minecraft/class_2596;)V"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void onSendPacketPost(class_2596<?> packet, CallbackInfo info) {
      if (!Module.fullNullCheck()) {
         PacketEvent.SendPost event = new PacketEvent.SendPost(packet);
         NevaWare.EVENT_BUS.post(event);
         if (event.isCancelled()) {
            info.cancel();
         }
      }
   }

   @Inject(
      method = {"Lnet/minecraft/class_2535;method_48311(Lio/netty/channel/ChannelPipeline;Lnet/minecraft/class_2598;ZLnet/minecraft/class_8762;)V"},
      at = {@At("RETURN")}
   )
   private static void addHandlersHook(ChannelPipeline pipeline, class_2598 side, boolean local, class_8762 packetSizeLogger, CallbackInfo ci) {
      ProxyManager.ThProxy proxy = Managers.PROXY.getActiveProxy();
      if (proxy != null && side == class_2598.field_11942 && !local) {
         pipeline.addFirst(new ChannelHandler[]{new Socks5ProxyHandler(new InetSocketAddress(proxy.getIp(), proxy.getPort()), proxy.getL(), proxy.getP())});
      }
   }
}
