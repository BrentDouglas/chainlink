package io.machinecode.chainlink.tck.se;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.management.JobOperatorView;
import io.machinecode.chainlink.se.management.SeEnvironment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class SeTckJobOperator extends JobOperatorView {

    static {
        Chainlink.setEnvironment(new SeEnvironment());
    }

    public SeTckJobOperator() {
        super();
    }

    public SeTckJobOperator(final String id) {
        super(id);
    }

    public SeTckJobOperator(final ExtendedJobOperator delegate) {
        super(delegate);
    }
}
