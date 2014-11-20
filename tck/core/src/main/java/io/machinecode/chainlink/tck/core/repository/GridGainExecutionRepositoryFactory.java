package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.gridgain.GridGainExecutionRepository;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.GridGainState;
import org.gridgain.grid.cache.GridCacheAtomicityMode;
import org.gridgain.grid.cache.GridCacheConfiguration;
import org.gridgain.grid.spi.discovery.tcp.GridTcpDiscoverySpi;
import org.gridgain.grid.spi.discovery.tcp.ipfinder.multicast.GridTcpDiscoveryMulticastIpFinder;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainExecutionRepositoryFactory implements ExecutionRepositoryFactory {

    public static final String CHAINLINK_TCK_GRID = "chainlink-tck-grid";

    static {
        final GridCacheConfiguration[] caches = new GridCacheConfiguration[11];
        for (int i = 0; i < 11; ++i) {
            caches[i] = new GridCacheConfiguration();
        }
        setCacheConf(caches[0], GridGainExecutionRepository.IDS);
        setIndexeddCacheConf(caches[1], GridGainExecutionRepository.JOB_INSTANCES);
        setIndexeddCacheConf(caches[2], GridGainExecutionRepository.JOB_EXECUTIONS);
        setCacheConf(caches[3], GridGainExecutionRepository.STEP_EXECUTIONS);
        setCacheConf(caches[4], GridGainExecutionRepository.PARTITION_EXECUTIONS);
        setCacheConf(caches[5], GridGainExecutionRepository.JOB_INSTANCE_EXECUTIONS);
        setCacheConf(caches[6], GridGainExecutionRepository.JOB_EXECUTION_INSTANCES);
        setCacheConf(caches[7], GridGainExecutionRepository.JOB_EXECUTION_STEP_EXECUTIONS);
        setCacheConf(caches[8], GridGainExecutionRepository.LATEST_JOB_EXECUTION_FOR_INSTANCE);
        setCacheConf(caches[9], GridGainExecutionRepository.STEP_EXECUTION_PARTITION_EXECUTIONS);
        setCacheConf(caches[10], GridGainExecutionRepository.JOB_EXECUTION_HISTORY);
        final GridConfiguration configuration = new GridConfiguration();
        configuration.setCacheConfiguration(caches);
        configuration.setGridName(CHAINLINK_TCK_GRID);
        final GridTcpDiscoverySpi discovery = new GridTcpDiscoverySpi();
        discovery.setLocalAddress(System.getProperty("gridgain.host", "127.0.0.1"));
        discovery.setLocalPort(Integer.parseInt(System.getProperty("gridgain.port", "47500")));
        final GridTcpDiscoveryMulticastIpFinder finder = new GridTcpDiscoveryMulticastIpFinder();
        finder.setMulticastGroup(System.getProperty("gridgain.multicast.group", "224.1.2.4"));
        discovery.setIpFinder(finder);
        configuration.setDiscoverySpi(discovery);
        try {
            if (GridGain.state(CHAINLINK_TCK_GRID) != GridGainState.STARTED) {
                GridGain.start(configuration);
            }
        } catch (GridException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setCacheConf(final GridCacheConfiguration conf, final String name) {
        conf.setName(name);
        conf.setAtomicityMode(GridCacheAtomicityMode.ATOMIC);
    }

    private static void setIndexeddCacheConf(final GridCacheConfiguration conf, final String name) {
        setCacheConf(conf, name);
        conf.setQueryIndexEnabled(true);
    }

    @Override
    public ExecutionRepository produce(final RepositoryConfiguration configuration) throws Exception {
        return new GridGainExecutionRepository(
                configuration.getMarshallingProviderFactory().produce(configuration),
                GridGain.grid(CHAINLINK_TCK_GRID)
        );
    }
}
