package io.machinecode.chainlink.ee.wildfly.schema;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PersistentResourceXMLDescription;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.List;

import static org.jboss.as.controller.PersistentResourceXMLDescription.PersistentResourceXMLBuilder;
import static org.jboss.as.controller.PersistentResourceXMLDescription.builder;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkParser_1_0 implements XMLElementReader<List<ModelNode>>, XMLStreamConstants {

    public static final ChainlinkParser_1_0 INSTANCE = new ChainlinkParser_1_0();

    private static final PersistentResourceXMLDescription xmlDescription;

    static {
        xmlDescription = builder(ChainlinkDefinition.INSTANCE)
                .addChild(jobOperator(JobOperatorDefinition.GLOBAL_INSTANCE))
                .addChild(builder(DeploymentDefinition.INSTANCE)
                        .addChild(jobOperator(JobOperatorDefinition.DEPLOYMENT_INSTANCE))
                ).build();
    }

    private static PersistentResourceXMLBuilder jobOperator(final JobOperatorDefinition definition) {
        return builder(definition)
                .addChild(builder(JobOperatorDefinition.EXECUTOR)
                        .addAttributes(JobOperatorDefinition.EXECUTOR.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.TRANSPORT)
                        .addAttributes(JobOperatorDefinition.TRANSPORT.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.REGISTRY)
                        .addAttributes(JobOperatorDefinition.REGISTRY.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.MARSHALLING)
                        .addAttributes(JobOperatorDefinition.MARSHALLING.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.MBEAN_SERVER)
                        .addAttributes(JobOperatorDefinition.MBEAN_SERVER.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.EXECUTION_REPOSITORY)
                        .addAttributes(JobOperatorDefinition.EXECUTION_REPOSITORY.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.CLASS_LOADER)
                        .addAttributes(JobOperatorDefinition.CLASS_LOADER.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.TRANSACTION_MANAGER)
                        .addAttributes(JobOperatorDefinition.TRANSACTION_MANAGER.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.JOB_LOADER)
                        .addAttributes(JobOperatorDefinition.JOB_LOADER.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.ARTIFACT_LOADER)
                        .addAttributes(JobOperatorDefinition.ARTIFACT_LOADER.getRawAttributes())
                ).addChild(builder(JobOperatorDefinition.SECURITY)
                        .addAttributes(JobOperatorDefinition.SECURITY.getRawAttributes())
                ).addChild(builder(PropertyDefinition.INSTANCE)
                        .addAttribute(PropertyDefinition.NAME)
                        .addAttribute(PropertyDefinition.VALUE)
                );
    }

    @Override
    public void readElement(final XMLExtendedStreamReader reader, final List<ModelNode> operations) throws XMLStreamException {
        xmlDescription.parse(reader, PathAddress.EMPTY_ADDRESS, operations);
    }
}
