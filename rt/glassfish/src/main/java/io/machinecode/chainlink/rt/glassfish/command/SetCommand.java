package io.machinecode.chainlink.rt.glassfish.command;

import io.machinecode.chainlink.core.configuration.xml.XmlDeployment;
import io.machinecode.chainlink.core.configuration.xml.XmlJobOperator;
import io.machinecode.chainlink.core.configuration.xml.subsystem.XmlChainlinkSubSystem;
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

    public SubSystemSchema<?,?,?,?> readSubsystem() throws Exception {
        try (final InputStream stream = stream()) {
            return XmlChainlinkSubSystem.read(stream);
        }
    }

    public DeploymentSchema<?,?,?> readDeployment() throws Exception {
        try (final InputStream stream = stream()) {
            return XmlDeployment.read(stream);
        }
    }

    public JobOperatorSchema<?,?> readJobOperator() throws Exception {
        try (final InputStream stream = stream()) {
            return XmlJobOperator.read(stream);
        }
    }
}
