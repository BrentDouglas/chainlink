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
        xmlDescription = builder(ChainlinkDefinition.INSTANCE)
                .addChild(
                        builder(JobOperatorDefinition.INSTANCE)
                                .addAttribute(JobOperatorDefinition.ID)
                                .addChild(
                                        builder(JobOperatorDefinition.WORKER_FACTORY)
                                                .addAttribute(JobOperatorDefinition.WORKER_FACTORY.clazz)
                                ).addChild(
                                        builder(JobOperatorDefinition.MARSHALLER_FACTORY)
                                                .addAttribute(JobOperatorDefinition.MARSHALLER_FACTORY.clazz)
                                ).addChild(
                                        builder(JobOperatorDefinition.EXECUTION_REPOSITORY_FACTORY)
                                                .addAttribute(JobOperatorDefinition.EXECUTION_REPOSITORY_FACTORY.clazz)
                                ).addChild(
                                        builder(JobOperatorDefinition.JOB_LOADER_FACTORY)
                                                .addAttribute(JobOperatorDefinition.JOB_LOADER_FACTORY.clazz)
                                ).addChild(
                                        builder(JobOperatorDefinition.ARTIFACT_LOADER_FACTORY)
                                                .addAttribute(JobOperatorDefinition.ARTIFACT_LOADER_FACTORY.clazz)
                                ).addChild(
                                        builder(JobOperatorDefinition.INJECTOR_FACTORY)
                                                .addAttribute(JobOperatorDefinition.INJECTOR_FACTORY.clazz)
                                ).addChild(
                                        builder(JobOperatorDefinition.SECURITY_CHECK_FACTORY)
                                                .addAttribute(JobOperatorDefinition.SECURITY_CHECK_FACTORY.clazz)
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
