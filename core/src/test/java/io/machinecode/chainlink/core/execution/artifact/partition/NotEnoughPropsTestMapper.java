/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.core.execution.artifact.partition;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;

import javax.batch.api.partition.PartitionMapper;
import javax.batch.api.partition.PartitionPlan;
import javax.batch.api.partition.PartitionPlanImpl;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NotEnoughPropsTestMapper implements PartitionMapper {

    @Override
    public PartitionPlan mapPartitions() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.MAP);
        return new PartitionPlanImpl() {

            @Override
            public boolean getPartitionsOverride() {
                return false;
            }

            @Override
            public int getPartitions() {
                return 4;
            }

            @Override
            public int getThreads() {
                return 2;
            }

            @Override
            public Properties[] getPartitionProperties() {
                return new Properties[]{
                        new Properties(){{
                            put("foo","bar");
                        }},
                        new Properties(){{
                            put("foo","baz");
                        }}
                };
            }
        };
    }
}
