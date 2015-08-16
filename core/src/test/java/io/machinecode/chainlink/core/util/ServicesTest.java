/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
