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
package io.machinecode.chainlink.repository.hazelcast;

import com.hazelcast.map.AbstractEntryProcessor;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import java.util.Map;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobExecutionsForJobInstanceProcessor extends AbstractEntryProcessor<Long, ExtendedJobExecution> {
    private static final long serialVersionUID = 1L;

    final long jobInstanceId;

    public JobExecutionsForJobInstanceProcessor(final long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    @Override
    public Object process(final Map.Entry<Long, ExtendedJobExecution> entry) {
        final ExtendedJobExecution jobExecution = entry.getValue();
        if (jobInstanceId == jobExecution.getJobInstanceId()) {
            return jobExecution;
        }
        return null;
    }
}
