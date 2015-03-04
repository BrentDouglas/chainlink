package io.machinecode.chainlink.core.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ServicesTest extends Assert {

    @Test
    public void testLoad() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        final ClassLoader tccl = Tccl.get();
        {
            final List<TestService> x = Services.load(TestService.class, tccl);
            assertEquals(1, x.size());
            assertEquals("foo", x.get(0).test());

        }
        {
            final List<TestService> x = Services.load("asdf", TestService.class, tccl);
            assertEquals(1, x.size());
            assertEquals("foo", x.get(0).test());
        }
        System.setProperty("asdf", BarService.class.getName());
        try {
            final List<TestService> x = Services.load("asdf", TestService.class, tccl);
            assertEquals(1, x.size());
            assertEquals("bar", x.get(0).test());
        } finally {
            System.clearProperty("asdf");
        }
    }
}
