package io.machinecode.chainlink.spi;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Constants {

    public static final String GLOBAL_TRANSACTION_TIMEOUT = "javax.transaction.global.timeout";

    public static final String CHAINLINK_XML = "io.machinecode.chainlink.chainlink_xml";

    public static final String ENVIRONMENT = "io.machinecode.chainlink.environment";

    public static final String CONFIGURATION_FACTORY_CLASS = "io.machinecode.chainlink.configuration_factory";

    public static final String THREAD_POOL_SIZE = "io.machinecode.chainlink.spi.management.thread_pool_size";
    public static final String JMX_DOMAIN = "io.machinecode.chainlink.spi.management.jmx_domain";

    public static final String DEFAULT_CONFIGURATION= "default";

    public static final class Defaults {
        public static final String JMX_DOMAIN = "io.machinecode.chainlink";
        public static final String THREAD_POOL_SIZE = "4";
    }
}
