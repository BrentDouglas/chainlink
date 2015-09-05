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
