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
package io.machinecode.chainlink.core.security;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.security.Security;

import javax.batch.operations.JobSecurityException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SecurityImpl implements Security {

    private final Security[] securities;

    public SecurityImpl(final Security... securities) {
        this.securities = securities;
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        for (final Security security : securities) {
            security.open(configuration);
        }
    }

    @Override
    public void close() throws Exception {
        for (final Security security : securities) {
            security.close();
        }
    }

    @Override
    public void canStartJob(final String jslName) throws JobSecurityException {
        for (final Security security : securities) {
            security.canStartJob(jslName);
        }
    }

    @Override
    public void canRestartJob(final long jobExecutionId) throws JobSecurityException {
        for (final Security security : securities) {
            security.canRestartJob(jobExecutionId);
        }
    }

    @Override
    public void canStopJob(final long jobExecutionId) throws JobSecurityException {
        for (final Security security : securities) {
            security.canStopJob(jobExecutionId);
        }
    }

    @Override
    public void canAbandonJob(final long jobExecutionId) throws JobSecurityException {
        for (final Security security : securities) {
            security.canAbandonJob(jobExecutionId);
        }
    }

    @Override
    public void canAccessJob(final String jobName) throws JobSecurityException {
        for (final Security security : securities) {
            security.canAccessJob(jobName);
        }
    }

    @Override
    public void canAccessJobInstance(final long jobInstanceId) throws JobSecurityException {
        for (final Security security : securities) {
            security.canAccessJobInstance(jobInstanceId);
        }
    }

    @Override
    public void canAccessJobExecution(final long jobExecutionId) throws JobSecurityException {
        for (final Security security : securities) {
            security.canAccessJobExecution(jobExecutionId);
        }
    }

    @Override
    public void canAccessStepExecution(final long stepExecutionId) throws JobSecurityException {
        for (final Security security : securities) {
            security.canAccessStepExecution(stepExecutionId);
        }
    }

    @Override
    public void canAccessPartitionExecution(final long partitionExecutionId) throws JobSecurityException {
        for (final Security security : securities) {
            security.canAccessPartitionExecution(partitionExecutionId);
        }
    }

    @Override
    public boolean filterJobName(final String jobName) {
        for (final Security security : securities) {
            if (security.filterJobName(jobName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean filterJobInstance(final long jobInstanceId) {
        for (final Security security : securities) {
            if (security.filterJobInstance(jobInstanceId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean filterJobExecution(final long jobExecutionId) {
        for (final Security security : securities) {
            if (security.filterJobExecution(jobExecutionId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean filterStepExecution(final long stepExecutionId) {
        for (final Security security : securities) {
            if (security.filterStepExecution(stepExecutionId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean filterPartitionExecution(final long partitionExecutionId) {
        for (final Security security : securities) {
            if (security.filterPartitionExecution(partitionExecutionId)) {
                return true;
            }
        }
        return false;
    }
}
