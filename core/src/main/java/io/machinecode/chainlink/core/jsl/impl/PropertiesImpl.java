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
package io.machinecode.chainlink.core.jsl.impl;

import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.inherit.ForwardingList;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertiesImpl extends ForwardingList<PropertyImpl> implements Properties {
    private static final long serialVersionUID = 1L;

    private final String partition;

    public PropertiesImpl(final String partition, final List<PropertyImpl> properties) {
        super(properties == null
                ? Collections.<PropertyImpl>emptyList()
                : properties
        );
        this.partition = partition;
    }

    @Override
    public List<PropertyImpl> getProperties() {
        return this.delegate;
    }

    @Override
    public String getPartition() {
        return this.partition;
    }
}
