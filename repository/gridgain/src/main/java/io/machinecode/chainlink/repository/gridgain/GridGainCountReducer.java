package io.machinecode.chainlink.repository.gridgain;

import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import org.gridgain.grid.lang.GridReducer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class GridGainCountReducer implements GridReducer<Map.Entry<Long, ExtendedJobInstance>, Integer> {
    private static final long serialVersionUID = 1L;

    private int count = 0;

    @Override
    public boolean collect(@Nullable final Map.Entry<Long, ExtendedJobInstance> entry) {
        ++count;
        return true;
    }

    @Override
    public Integer reduce() {
        return count;
    }
}
