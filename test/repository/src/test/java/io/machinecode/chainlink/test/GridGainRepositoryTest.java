package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.gridgain.GridGainExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.cache.GridCacheAtomicityMode;
import org.gridgain.grid.cache.GridCacheConfiguration;
import org.gridgain.grid.spi.discovery.tcp.GridTcpDiscoverySpi;
import org.gridgain.grid.spi.discovery.tcp.ipfinder.multicast.GridTcpDiscoveryMulticastIpFinder;
import org.junit.BeforeClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainRepositoryTest extends RepositoryTest {

    @BeforeClass
    public static void beforeClass() {
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
        configuration.setGridName("test-grid");
        final GridTcpDiscoverySpi discovery = new GridTcpDiscoverySpi();
        discovery.setLocalAddress(System.getProperty("gridgain.host", "127.0.0.1"));
        discovery.setLocalPort(Integer.parseInt(System.getProperty("gridgain.port", "47500")));
        final GridTcpDiscoveryMulticastIpFinder finder = new GridTcpDiscoveryMulticastIpFinder();
        finder.setMulticastGroup(System.getProperty("gridgain.multicast.group", "224.1.2.4"));
        discovery.setIpFinder(finder);
        configuration.setDiscoverySpi(discovery);
        try {
            GridGain.start(configuration);
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
    protected ExecutionRepository _repository() throws Exception {
        return new GridGainExecutionRepository(
                marshallerFactory().produce(null),
                GridGain.grid("test-grid")
        );
    }
}
