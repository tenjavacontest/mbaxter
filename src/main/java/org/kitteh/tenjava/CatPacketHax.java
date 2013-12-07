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

import java.lang.reflect.Field;
import java.util.logging.Level;

import net.minecraft.server.v1_7_R1.NetworkManager;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelPipeline;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class CatPacketHax implements Listener {
    private final CatPlugin plugin;
    private final Field channelField;

    CatPacketHax(CatPlugin plugin) throws NoSuchFieldException, SecurityException {
        this.plugin = plugin;
        this.channelField = NetworkManager.class.getDeclaredField("k");
        this.channelField.setAccessible(true);

        for (final Player player : plugin.getServer().getOnlinePlayers()) {
            this.inject(player);
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        this.inject(event.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        this.unject(event.getPlayer());
    }

    private Channel getChannel(Player player) throws IllegalArgumentException, IllegalAccessException {
        return (Channel) this.channelField.get(((CraftPlayer) player).getHandle().playerConnection.networkManager);
    }

    private void inject(Player player) {
        try {
            this.getChannel(player).pipeline().addLast(new CatChannelHandler(player, this));
        } catch (final Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not inject into player " + player.getName(), e);
        }
    }

    private void unject(Player player) {
        try {
            final ChannelPipeline pipeline = this.getChannel(player).pipeline();
            if (pipeline.get(CatChannelHandler.class) != null) {
                pipeline.remove(CatChannelHandler.class);
            }
        } catch (final Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not unject from player " + player.getName(), e);
        }
    }

    void handlePacket(Player player, Object packet) {
        final String out = CatRegistry.getOutput(packet);
        if (out != null) {
            this.plugin.getServer().broadcastMessage(out);
        }
    }

    void onDisable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.unject(player);
        }
    }
}