package io.machinecode.chainlink.core.util;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BarService implements TestService {
    @Override
    public String test() {
        return "bar";
    }
}
