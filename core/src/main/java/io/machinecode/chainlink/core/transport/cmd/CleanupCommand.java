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
package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CleanupCommand implements Command<Void> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;

    public CleanupCommand(final long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public Void perform(final Configuration configuration, final Object origin) throws Throwable {
        final Transport transport = configuration.getTransport();
        configuration.getRegistry().unregisterJob(jobExecutionId).get(transport.getTimeout(), transport.getTimeUnit());
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CleanupCommand{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append('}');
        return sb.toString();
    }
}
