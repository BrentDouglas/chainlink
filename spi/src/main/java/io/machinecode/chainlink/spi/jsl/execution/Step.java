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
package io.machinecode.chainlink.spi.jsl.execution;

import io.machinecode.chainlink.spi.jsl.Listeners;
import io.machinecode.chainlink.spi.jsl.PropertiesElement;
import io.machinecode.chainlink.spi.jsl.partition.Partition;
import io.machinecode.chainlink.spi.jsl.partition.Strategy;
import io.machinecode.chainlink.spi.jsl.task.Task;
import io.machinecode.chainlink.spi.jsl.transition.Transition;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Step<T extends Task, U extends Strategy> extends TransitionExecution, PropertiesElement {

    String ELEMENT = "step";

    String ZERO = "0";

    String getStartLimit();

    String getAllowStartIfComplete();

    Listeners getListeners();

    T getTask();

    List<? extends Transition> getTransitions();

    Partition<? extends U> getPartition();
}
