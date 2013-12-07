package org.kitteh.tenjava;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.bukkit.command.CommandSender;
import org.junit.Assert;
import org.junit.Test;

public class CommandTest {
    @Test
    public void duplicateArgs() {
        for (final Method method : CatCommandExecutor.class.getDeclaredMethods()) {
            final CatCommand command = method.getAnnotation(CatCommand.class);
            if (command == null) {
                continue;
            }
            final String n = method.getName() + ": ";
            Assert.assertTrue(n + "STATIC", !Modifier.isStatic(method.getModifiers()));
            final Class<?>[] params = method.getParameterTypes();
            Assert.assertTrue(n + "WRONGARGCOUNT " + params.length, params.length == 2);
            Assert.assertTrue(n + "COMMANDSENDER SHOULD BE FIRST ARG", params[0].equals(CommandSender.class));
            Assert.assertTrue(n + "NEED MORE HAIRBALL", params[1].equals(Hairball.class));
        }
    }
}