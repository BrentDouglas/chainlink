package io.machinecode.chainlink.spi;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Constants {

    String GLOBAL_TRANSACTION_TIMEOUT   = "javax.transaction.global.timeout";

    String CHAINLINK_XML                = "io.machinecode.chainlink.chainlink_xml";
    String ENVIRONMENT                  = "io.machinecode.chainlink.environment";
    String CONFIGURATION_FACTORY_CLASS  = "io.machinecode.chainlink.configuration_factory";

    String THREAD_POOL_SIZE             = "io.machinecode.chainlink.spi.management.thread_pool_size";
    String JMX_DOMAIN                   = "io.machinecode.chainlink.spi.management.jmx_domain";

    String DEFAULT_CONFIGURATION        = "default";

    String TIMEOUT                      = "io.machinecode.chainlink.transport.timeout";
    String TIMEOUT_UNIT                 = "io.machinecode.chainlink.transport.timeout_unit";

    interface Defaults {
        String JMX_DOMAIN               = "io.machinecode.chainlink";
        String THREAD_POOL_SIZE         = "4";

        String NETWORK_TIMEOUT          = "2";
        String NETWORK_TIMEOUT_UNIT     = "MINUTES";
    }
}
