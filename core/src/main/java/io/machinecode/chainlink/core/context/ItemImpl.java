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
package io.machinecode.chainlink.core.context;

import io.machinecode.chainlink.spi.context.Item;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ItemImpl implements Item, Serializable {
    private static final long serialVersionUID = 1L;

    private final Serializable data;
    private final BatchStatus batchStatus;
    private final String exitStatus;

    public ItemImpl(final Serializable data, final BatchStatus batchStatus, final String exitStatus) {
        this.data = data;
        this.batchStatus = batchStatus;
        this.exitStatus = exitStatus;
    }

    @Override
    public Serializable getData() {
        return data;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }
}
