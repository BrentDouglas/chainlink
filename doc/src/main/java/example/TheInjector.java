package example;

import io.machinecode.chainlink.spi.inject.Injector;

import javax.inject.Named;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Named
public class TheInjector implements Injector {
    @Override
    public boolean inject(final Object bean) throws Exception {
        //Do injection here...
        return false;
    }
}
