/*
 * Copyright 2012-2013 Matt Baxter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kitteh.tenjava;

import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelPromise;

import org.bukkit.entity.Player;

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