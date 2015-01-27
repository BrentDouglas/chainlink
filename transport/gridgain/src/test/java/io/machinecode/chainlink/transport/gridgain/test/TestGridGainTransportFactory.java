package io.machinecode.chainlink.transport.gridgain.test;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.transport.gridgain.GridGainTransport;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.cache.GridCacheAtomicityMode;
import org.gridgain.grid.cache.GridCacheConfiguration;
import org.gridgain.grid.spi.discovery.tcp.GridTcpDiscoverySpi;
import org.gridgain.grid.spi.discovery.tcp.ipfinder.vm.GridTcpDiscoveryVmIpFinder;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestGridGainTransportFactory implements TestTransportFactory {

    private static final AtomicInteger id = new AtomicInteger();
    public static final String CHAINLINK_TEST_GRID = "chainlink-test-grid";

    final Grid grid;

    public TestGridGainTransportFactory() throws GridException {
        final GridCacheConfiguration cache = new GridCacheConfiguration();
        cache.setName("test");
        cache.setAtomicityMode(GridCacheAtomicityMode.ATOMIC);
        final GridConfiguration configuration = new GridConfiguration();
        configuration.setGridName(CHAINLINK_TEST_GRID + "-" + id.getAndIncrement());
        configuration.setCacheConfiguration(cache);
        final GridTcpDiscoverySpi discovery = new GridTcpDiscoverySpi();
        discovery.setLocalAddress("127.0.0.1");
        final GridTcpDiscoveryVmIpFinder finder = new GridTcpDiscoveryVmIpFinder(true);
        finder.setAddresses(Arrays.asList("127.0.0.1"));
        discovery.setIpFinder(finder);
        configuration.setDiscoverySpi(discovery);
        this.grid = GridGain.start(configuration);
    }

    @Override
    public GridGainTransport produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new GridGainTransport(
                dependencies,
                properties,
                grid
        );
    }

    @Override
    public void close() throws Exception {
        grid.close();
    }
}
