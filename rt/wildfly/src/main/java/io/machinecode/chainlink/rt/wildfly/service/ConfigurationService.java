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

import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.rt.wildfly.WildFlyConstants;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ConfigurationService implements Service<SubSystemModelImpl> {

    public static final ServiceName SERVICE_NAME = ChainlinkService.SERVICE_NAME.append(WildFlyConstants.MODEL);

    private SubSystemModelImpl model;
    private final InjectedValue<ClassLoader> loader;

    public ConfigurationService(final InjectedValue<ClassLoader> loader) {
        this.loader = loader;
    }

    @Override
    public void start(final StartContext context) throws StartException {
        final ClassLoader loader = this.loader.getOptionalValue();
        if (loader == null) {
            throw new StartException();
        }
        this.model = new SubSystemModelImpl(loader);
    }

    @Override
    public void stop(final StopContext context) {
        this.model = null;
    }

    @Override
    public SubSystemModelImpl getValue() throws IllegalStateException, IllegalArgumentException {
        return model;
    }
}
