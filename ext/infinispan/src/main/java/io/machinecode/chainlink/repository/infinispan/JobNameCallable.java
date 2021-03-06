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
package io.machinecode.chainlink.repository.infinispan;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import java.util.Set;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobNameCallable extends BaseCallable<Long, ExtendedJobInstance, Set<String>> {
    private static final long serialVersionUID = 1L;

    @Override
    public Set<String> call() throws Exception {
        final Set<String> ret = new THashSet<String>();
        for (final ExtendedJobInstance jobInstance : cache.values()) {
            ret.add(jobInstance.getJobName());
        }
        return ret;
    }
}
