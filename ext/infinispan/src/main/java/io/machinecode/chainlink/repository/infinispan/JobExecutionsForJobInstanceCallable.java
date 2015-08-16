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
package io.machinecode.chainlink.repository.infinispan;

import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import javax.batch.runtime.JobExecution;
import java.util.ArrayList;
import java.util.List;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobExecutionsForJobInstanceCallable extends BaseCallable<Long, ExtendedJobExecution, List<JobExecution>> {
    private static final long serialVersionUID = 1L;

    private final long jobInstanceId;

    public JobExecutionsForJobInstanceCallable(final long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    @Override
    public List<JobExecution> call() throws Exception {
        final List<JobExecution> ret = new ArrayList<JobExecution>();
        for (final ExtendedJobExecution jobExecution : cache.values()) {
            if (jobInstanceId == jobExecution.getJobInstanceId()) {
                ret.add(jobExecution);
            }
        }
        return ret;
    }
}
