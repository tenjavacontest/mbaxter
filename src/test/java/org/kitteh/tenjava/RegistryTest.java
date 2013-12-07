package org.kitteh.tenjava;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.v1_7_R1.Packet;

import org.junit.Assert;
import org.junit.Test;

public class RegistryTest {
    @Test
    public void duplication() {
        final Set<Class<? extends Packet>> set = new HashSet<>();
        for (final CatRegistry registry : CatRegistry.values()) {
            Assert.assertTrue("Duplicate class " + registry.getClazz(), set.add(registry.getClazz()));
        }
    }
}