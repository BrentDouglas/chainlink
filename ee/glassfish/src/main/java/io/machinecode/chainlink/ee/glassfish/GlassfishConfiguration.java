package io.machinecode.chainlink.ee.glassfish;

import org.glassfish.api.admin.config.ConfigExtension;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
//import org.jvnet.hk2.config.Configured;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
//@Configured
public interface GlassfishConfiguration extends ConfigBeanProxy, ConfigExtension {

    @Attribute(defaultValue = "concurrent/__defaultManagedThreadFactory")
    String getThreadFactory();

    void setThreadFactory(final String value);
}
