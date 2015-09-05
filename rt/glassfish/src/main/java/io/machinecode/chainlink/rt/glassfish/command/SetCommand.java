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
package io.machinecode.chainlink.rt.glassfish.command;

import io.machinecode.chainlink.core.schema.xml.XmlDeployment;
import io.machinecode.chainlink.core.schema.xml.XmlJobOperator;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
import org.apache.commons.codec.binary.Base64;
import org.glassfish.api.Param;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class SetCommand extends BaseCommand {

    @Param(name = "file", shortName = "f", optional = true)
    protected String file;

    @Param(name = "base64", shortName = "b", optional = true)
    protected String base64;

    protected final Base64 decoder = new Base64(true);

    protected InputStream stream() throws Exception {
        if (file != null) {
            if (base64 != null) {
                throw new Exception("Only one of the arguments file or base64 may be provided.");
            }
            return new FileInputStream(file);
        }
        if (base64 != null) {
            return new ByteArrayInputStream(decoder.decode(base64));
        }
        throw new Exception("One of the arguments file or base64 must be provided.");
    }

    public SubSystemSchema<?,?,?> readSubsystem() throws Exception {
        try (final InputStream stream = stream()) {
            return XmlChainlinkSubSystem.read(stream);
        }
    }

    public DeploymentSchema<?,?> readDeployment() throws Exception {
        try (final InputStream stream = stream()) {
            return XmlDeployment.read(stream);
        }
    }

    public JobOperatorSchema<?> readJobOperator() throws Exception {
        try (final InputStream stream = stream()) {
            return XmlJobOperator.read(stream);
        }
    }
}
