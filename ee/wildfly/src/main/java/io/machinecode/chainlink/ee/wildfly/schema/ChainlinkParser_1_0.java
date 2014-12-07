package io.machinecode.chainlink.ee.wildfly.schema;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PersistentResourceXMLDescription;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.List;

import static org.jboss.as.controller.PersistentResourceXMLDescription.builder;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkParser_1_0 implements XMLElementReader<List<ModelNode>>, XMLStreamConstants {

    public static final ChainlinkParser_1_0 INSTANCE = new ChainlinkParser_1_0();

    private static final PersistentResourceXMLDescription xmlDescription;

    static {
        xmlDescription = builder(ChainlinkDefinition.INSTANCE).addChild(
                builder(JobOperatorDefinition.INSTANCE)
                        .addChild(
                                builder(JobOperatorDefinition.EXECUTOR_FACTORY)
                                        .addAttributes(JobOperatorDefinition.EXECUTOR_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.REGISTRY_FACTORY)
                                        .addAttributes(JobOperatorDefinition.REGISTRY_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.WORKER_FACTORY)
                                        .addAttributes(JobOperatorDefinition.WORKER_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.MARSHALLING_PROVIDER_FACTORY)
                                        .addAttributes(JobOperatorDefinition.MARSHALLING_PROVIDER_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.MBEAN_SERVER_FACTORY)
                                        .addAttributes(JobOperatorDefinition.MBEAN_SERVER_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.EXECUTION_REPOSITORY_FACTORY)
                                        .addAttributes(JobOperatorDefinition.EXECUTION_REPOSITORY_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.CLASS_LOADER_FACTORY)
                                        .addAttributes(JobOperatorDefinition.CLASS_LOADER_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.TRANSACTION_MANAGER_FACTORY)
                                        .addAttributes(JobOperatorDefinition.TRANSACTION_MANAGER_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.JOB_LOADER_FACTORY)
                                        .addAttributes(JobOperatorDefinition.JOB_LOADER_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.ARTIFACT_LOADER_FACTORY)
                                        .addAttributes(JobOperatorDefinition.ARTIFACT_LOADER_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.INJECTOR_FACTORY)
                                        .addAttributes(JobOperatorDefinition.INJECTOR_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(JobOperatorDefinition.SECURITY_CHECK_FACTORY)
                                        .addAttributes(JobOperatorDefinition.SECURITY_CHECK_FACTORY.getRawAttributes())
                        ).addChild(
                                builder(PropertyDefinition.INSTANCE)
                                        .addAttribute(PropertyDefinition.NAME)
                                        .addAttribute(PropertyDefinition.VALUE)
                        )
                ).build();
    }
    @Override
    public void readElement(final XMLExtendedStreamReader reader, final List<ModelNode> operations) throws XMLStreamException {
        xmlDescription.parse(reader, PathAddress.EMPTY_ADDRESS, operations);
    }
}
