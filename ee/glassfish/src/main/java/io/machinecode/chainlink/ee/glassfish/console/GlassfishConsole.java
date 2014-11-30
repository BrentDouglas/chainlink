package io.machinecode.chainlink.ee.glassfish.console;

import org.glassfish.api.admingui.ConsoleProvider;
import org.jvnet.hk2.annotations.Service;

import java.net.URL;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Service
public class GlassfishConsole implements ConsoleProvider {
    @Override
    public URL getConfiguration() {
        return null;
    }
}
