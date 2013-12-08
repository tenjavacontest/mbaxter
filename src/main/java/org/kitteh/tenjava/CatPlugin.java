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

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class CatPlugin extends JavaPlugin {
    private CatPacketHax hax;

    @Override
    public void onDisable() {
        if (this.hax != null) {
            this.hax.onDisable();
        }
    }

    @Override
    public void onEnable() {
        this.getLogger().info("CAT PLUGIN CAT PLUGIN");
        try {
            this.hax = new CatPacketHax(this);
        } catch (final Exception e) {
            this.getLogger().log(Level.SEVERE, "Could not start up!", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        CatCommandExecutor exec = new CatCommandExecutor();
        this.getCommand("track").setExecutor(exec);
        this.getCommand("untrack").setExecutor(exec);
        this.getCommand("getid").setExecutor(exec);
    }
}