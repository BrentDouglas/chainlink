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
package example;

import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.ListModel;
import io.machinecode.chainlink.spi.security.Security;

import javax.inject.Named;
import java.lang.management.ManagementFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Named
public class TheJobOperatorConfiguration implements JobOperatorConfiguration {

    @Override
    public void configureJobOperator(final JobOperatorModel model) throws Exception {
        model.setProperty("foo", "bar");
        model.getMBeanServer().setValue(ManagementFactory.getPlatformMBeanServer());
        final ListModel<Security> securities = model.getSecurities();
        securities.clear();
        securities.add().setRef("otherSecurity");
        securities.add().setRef("theSecurity");
    }
}
