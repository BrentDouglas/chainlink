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

import io.machinecode.then.core.Messages;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Timeout {

    /**
     * @param timeout Timeout duration
     * @param unit The {@link TimeUnit} of the param {@code timeout}.
     * @return The number of milliseconds from midnight, January 1, 1970 UTC
     *         after which this period will have elapsed.
     */
    public static long end(final long timeout, final TimeUnit unit) {
        return System.currentTimeMillis() + unit.toMillis(timeout);
    }

    /**
     * @param end The number of milliseconds from midnight, January 1, 1970 UTC
     *            after which this period will have elapsed.
     * @return The number of milliseconds remaining between the current time and
     *         the {@code end} parameter.
     * @throws TimeoutException If {@code end} refers to a time that is in the past.
     */
    public static long after(final long end) throws TimeoutException {
        final long timeout = end - System.currentTimeMillis();
        if (timeout <= 0) {
            throw new TimeoutException(Messages.get("CHAINLINK-034000.timeout"));
        }
        return timeout;
    }
}
