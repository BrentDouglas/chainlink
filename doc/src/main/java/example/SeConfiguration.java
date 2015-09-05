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
package example;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.rt.se.SeEnvironment;

import javax.batch.runtime.BatchRuntime;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SeConfiguration {
    public static void main(final String... args) throws Throwable {
        try (final SeEnvironment environment = new SeEnvironment()) {
            Chainlink.setEnvironment(environment);
            // Run code here e.g.
            BatchRuntime.getJobOperator().start("a_job", new Properties());
        } finally {
            Chainlink.setEnvironment(null);
        }
    }
}
