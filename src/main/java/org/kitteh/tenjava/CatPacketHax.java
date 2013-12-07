package org.kitteh.tenjava;

import java.lang.reflect.Field;
import java.util.logging.Level;

import net.minecraft.server.v1_7_R1.NetworkManager;
import net.minecraft.util.io.netty.channel.Channel;

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
            this.getChannel(player).pipeline().remove(CatChannelHandler.class);
        } catch (final Exception e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not inject into player " + player.getName(), e);
        }
    }

    void handlePacket(Player player, Object packet) {
        // TODO
    }

    void onDisable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.unject(player);
        }
    }
}