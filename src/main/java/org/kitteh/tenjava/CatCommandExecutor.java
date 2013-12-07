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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CatCommandExecutor implements TabExecutor {
    private static final Map<String, Method> METHODS;
    private static final List<String> OPTIONS;

    static {
        final ImmutableMap.Builder<String, Method> methods = ImmutableMap.builder();
        final ImmutableList.Builder<String> options = ImmutableList.builder();
        for (final Method method : CatCommandExecutor.class.getDeclaredMethods()) {
            final CatCommand command = method.getAnnotation(CatCommand.class);
            if ((command == null) || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            final Class<?>[] params = method.getParameterTypes();
            if ((params.length != 2) || !params[0].equals(CommandSender.class) || !params[1].equals(Hairball.class)) {
                continue;
            }
            methods.put(command.arg(), method);
            options.add(command.arg());
        }
        METHODS = methods.build();
        OPTIONS = options.build();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Method method = CatCommandExecutor.METHODS.get(command.getName().toLowerCase());
        if (method == null) {
            sender.sendMessage("Somebody dun goofed");
        }
        try {
            method.invoke(this, sender, new Hairball(args));
        } catch (final Exception e) {
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                return CatCommandExecutor.OPTIONS;
            case 1:
                return this.match(args[0], CatCommandExecutor.OPTIONS);
            default:
                return ImmutableList.of("MEOW", "PURR", "HISS", "MROW");
        }
    }

    private List<String> match(String needle, List<String> haystack) {
        if ((needle.length() == 0) || needle.equals(" ")) {
            return haystack;
        }
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        final String littleNeedle = needle.toLowerCase();
        for (final String hay : haystack) {
            if (hay.toLowerCase().startsWith(littleNeedle)) {
                builder.add(hay);
            }
        }
        return builder.build();
    }

    @CatCommand(arg = "getid")
    void getid(CommandSender sender, Hairball args) {
        if ((args.length() == 0) && !(sender instanceof Player)) {
            sender.sendMessage("Need a player, mr console");
            return;
        }
        Player target = null;
        if (args.length() > 0) {
            target = Bukkit.getPlayer(args.get(0));
        }
        if ((target == null) && (sender instanceof Player)) {
            target = (Player) sender;
        }
        if (target == null) {
            sender.sendMessage("Could not acquire a target");
        } else {
            sender.sendMessage("ID " + target.getEntityId());
        }
    }

    @CatCommand(arg = "track")
    void track(CommandSender sender, Hairball args) {
        if (args.length() < 1) {
            sender.sendMessage("Need an ID to track, silly");
            return;
        }
        int ID;
        try {
            ID = Integer.parseInt(args.get(0));
        } catch (final NumberFormatException e) {
            sender.sendMessage("Uh, " + args.get(0) + " is not an ID");
            return;
        }
        CatRegistry.track(ID);
        sender.sendMessage("Tracking ID " + ID);
    }

    @CatCommand(arg = "untrack")
    void untrack(CommandSender sender, Hairball args) {
        if (args.length() < 1) {
            sender.sendMessage("Need an ID to track, silly");
            return;
        }
        int ID;
        try {
            ID = Integer.parseInt(args.get(0));
        } catch (final NumberFormatException e) {
            sender.sendMessage("Uh, " + args.get(0) + " is not an ID");
            return;
        }
        CatRegistry.untrack(ID);
        sender.sendMessage("No longer tracking ID " + ID);
    }
}