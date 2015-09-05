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
package io.machinecode.chainlink.core;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Constants {

    String NAMESPACE                        = "io.machinecode.chainlink";

    String DEFAULT = "default";

    String GLOBAL_TRANSACTION_TIMEOUT       = "javax.transaction.global.timeout";

    String CHAINLINK_XML                    = NAMESPACE + ".chainlink_xml";
    String CHAINLINK_SUBSYSTEM_XML          = NAMESPACE + ".chainlink_subsystem_xml";
    String ENVIRONMENT                      = NAMESPACE + ".environment";

    String THREAD_POOL_SIZE                 = NAMESPACE + ".spi.management.thread_pool_size";
    String JMX_DOMAIN                       = NAMESPACE + ".spi.management.jmx_domain";

    String TIMEOUT                          = NAMESPACE + ".transport.timeout";
    String TIMEOUT_UNIT                     = NAMESPACE + ".transport.timeout_unit";

    String TRANSACTION_MANAGER_JNDI_NAME    = NAMESPACE + ".transaction_manager.jndi_name";
    String THREAD_FACTORY_JNDI_NAME         = NAMESPACE + ".executor.thread_factory.jndi_name";

    String CACHE_MANAGER_JNDI_NAME          = NAMESPACE + ".infinispan.cache_manager.jndi_name";

    String COHERENCE_INVOCATION_SERVICE     = NAMESPACE + ".coherence.invocation_service";

    interface Defaults {
        String CHAINLINK_XML                = "chainlink.xml";
        String CHAINLINK_SUBSYSTEM_XML      = "chainlink-subsystem.xml";

        String JMX_DOMAIN                   = "io.machinecode.chainlink";
        String THREAD_POOL_SIZE             = "4";

        String NETWORK_TIMEOUT              = "2";
        String NETWORK_TIMEOUT_UNIT         = "MINUTES";

        String COHERENCE_INVOCATION_SERVICE = "InvocationService";
    }
}
