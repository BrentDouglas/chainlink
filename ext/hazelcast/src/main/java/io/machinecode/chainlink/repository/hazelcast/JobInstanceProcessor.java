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
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.batch.runtime.JobInstance;
import java.util.Map;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobInstanceProcessor extends AbstractEntryProcessor<Long, ExtendedJobInstance> {
    private static final long serialVersionUID = 1L;

    final String jobName;

    public JobInstanceProcessor(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public Object process(final Map.Entry<Long, ExtendedJobInstance> entry) {
        final JobInstance jobInstance = entry.getValue();
        if (jobName.equals(jobInstance.getJobName())) {
            return jobInstance;
        }
        return null;
    }
}
