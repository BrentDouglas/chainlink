/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.gridgain.GridGainRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.GridGainState;
import org.gridgain.grid.cache.GridCacheAtomicityMode;
import org.gridgain.grid.cache.GridCacheConfiguration;
import org.gridgain.grid.spi.discovery.tcp.GridTcpDiscoverySpi;
import org.gridgain.grid.spi.discovery.tcp.ipfinder.multicast.GridTcpDiscoveryMulticastIpFinder;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainRepositoryFactory implements RepositoryFactory {

    public static final String CHAINLINK_TCK_GRID = "chainlink-tck-grid";

    static {
        final GridCacheConfiguration[] caches = new GridCacheConfiguration[11];
        for (int i = 0; i < 11; ++i) {
            caches[i] = new GridCacheConfiguration();
        }
        setCacheConf(caches[0], GridGainRepository.IDS);
        setIndexeddCacheConf(caches[1], GridGainRepository.JOB_INSTANCES);
        setIndexeddCacheConf(caches[2], GridGainRepository.JOB_EXECUTIONS);
        setCacheConf(caches[3], GridGainRepository.STEP_EXECUTIONS);
        setCacheConf(caches[4], GridGainRepository.PARTITION_EXECUTIONS);
        setCacheConf(caches[5], GridGainRepository.JOB_INSTANCE_EXECUTIONS);
        setCacheConf(caches[6], GridGainRepository.JOB_EXECUTION_INSTANCES);
        setCacheConf(caches[7], GridGainRepository.JOB_EXECUTION_STEP_EXECUTIONS);
        setCacheConf(caches[8], GridGainRepository.LATEST_JOB_EXECUTION_FOR_INSTANCE);
        setCacheConf(caches[9], GridGainRepository.STEP_EXECUTION_PARTITION_EXECUTIONS);
        setCacheConf(caches[10], GridGainRepository.JOB_EXECUTION_HISTORY);
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
    public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new GridGainRepository(
                dependencies.getMarshalling(),
                GridGain.grid(CHAINLINK_TCK_GRID)
        );
    }
}
