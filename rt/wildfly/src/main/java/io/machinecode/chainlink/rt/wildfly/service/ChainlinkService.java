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
package io.machinecode.chainlink.rt.wildfly.service;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.rt.wildfly.WildFlyConstants;
import io.machinecode.chainlink.rt.wildfly.WildFlyEnvironment;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkService implements Service<WildFlyEnvironment> {

    public static final ServiceName SERVICE_NAME = ServiceName.of(WildFlyConstants.SERVICE_NAME);

    private WildFlyEnvironment environment;

    public ChainlinkService(final WildFlyEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void start(final StartContext context) throws StartException {
        Chainlink.setEnvironment(environment);
    }

    @Override
    public void stop(final StopContext context) {
        Chainlink.setEnvironment(null);
    }

    @Override
    public WildFlyEnvironment getValue() throws IllegalStateException, IllegalArgumentException {
        return environment;
    }
}
