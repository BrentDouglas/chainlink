package io.machinecode.chainlink.core.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StringsTest extends Assert {

    @Test
    public void testJoin() {
        assertEquals("'foo','bar'", Strings.join("foo", "bar"));
        assertEquals("", Strings.join());
    }
}
