package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertyImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertiesConverterTest extends Assert {

    @Test
    public void testConvert() {
        final Properties out = PropertiesConverter.convert(new PropertiesImpl(null, new ArrayList<PropertyImpl>(){{
            add(new PropertyImpl("foo", "bar"));
            add(new PropertyImpl("bar", "baz"));
        }}));

        assertEquals(2, out.size());
        assertEquals("bar", out.getProperty("foo"));
        assertEquals("baz", out.getProperty("bar"));
    }
}
