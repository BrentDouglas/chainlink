/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
