package mint.mixins;

import io.netty.channel.ChannelHandlerContext;
import mint.events.PacketEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class)
public abstract class MixinNetworkManager {

    /*
        1. You dont need the {} for @Inject parameters they are ugly asf
        2. All mixins should be abstract for convinience
        3. ALL METHODS MUST BE PUBLIC
    */
    
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
        PacketEvent.Send event = new PacketEvent.Send(0, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At(value = "HEAD"), cancellable = true)
    public void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketEvent.Receive event = new PacketEvent.Receive(0, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }
    
}
