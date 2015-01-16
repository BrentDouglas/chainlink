package io.machinecode.chainlink.spi;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Constants {

    String NAMESPACE                        = "io.machinecode.chainlink";

    String DEFAULT_CONFIGURATION            = "default";

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

    interface Defaults {
        String CHAINLINK_XML                = "chainlink.xml";
        String CHAINLINK_SUBSYSTEM_XML      = "chainlink-subsystem.xml";

        String JMX_DOMAIN                   = "io.machinecode.chainlink";
        String THREAD_POOL_SIZE             = "4";

        String NETWORK_TIMEOUT              = "2";
        String NETWORK_TIMEOUT_UNIT         = "MINUTES";
    }
}
