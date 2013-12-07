package org.kitteh.tenjava;

import org.bukkit.entity.Player;

import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelPromise;

public final class CatChannelHandler extends ChannelOutboundHandlerAdapter {
    private final Player player;
    private final CatPacketHax hax;

    CatChannelHandler(Player player, CatPacketHax hax) {
        this.player = player;
        this.hax = hax;
    }

    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
        // Is it bad that I have this memorized at this point?
        this.hax.handlePacket(this.player, packet);
        super.write(context, packet, promise);
    }
}