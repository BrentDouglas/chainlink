package io.machinecode.chainlink.core.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StringsTest extends Assert {

    public static final Strings.To<String> DUPLICATE = new Strings.To<String>() {
        @Override
        public String to(final String that) {
            return that + that;
        }
    };

    @Test
    public void testJoin() {
        assertEquals("foo,bar", Strings.join(',', "foo", "bar"));
        assertEquals("", Strings.join(','));
    }

    @Test
    public void testJoinCollection() {
        assertEquals("foo,bar", Strings.join(',', Arrays.asList("foo", "bar")));
        assertEquals("", Strings.join(',', Collections.<String>emptyList()));
    }

    @Test
    public void testToJoin() {
        assertEquals("foofoo,barbar", Strings.join(',', DUPLICATE, "foo", "bar"));
        assertEquals("", Strings.join(',', DUPLICATE, Collections.<String>emptyList()));
    }

    @Test
    public void testToJoinCollection() {
        assertEquals("foofoo,barbar", Strings.join(',', DUPLICATE, Arrays.asList("foo", "bar")));
        assertEquals("", Strings.join(',', DUPLICATE, Collections.<String>emptyList()));
    }
}
