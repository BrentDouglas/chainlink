package io.machinecode.chainlink.inject.core.batch;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static io.machinecode.chainlink.inject.core.batch.BatchArtifacts.NAMESPACE;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = NAMESPACE, name = BatchArtifacts.ELEMENT)
@XmlAccessorType(NONE)
public class BatchArtifacts {

    public static final String ELEMENT = "batch-artifacts";

    public static final String SCHEMA_URL = "http://xmlns.jcp.org/xml/ns/javaee/batchXML_1_0.xsd";
    public static final String NAMESPACE = "http://xmlns.jcp.org/xml/ns/javaee";


    @XmlElement(name = BatchArtifactRef.ELEMENT, namespace = NAMESPACE, required = false)
    private List<BatchArtifactRef> refs = new ArrayList<BatchArtifactRef>(0);

    public List<BatchArtifactRef> getRefs() {
        return refs;
    }

    public void setRefs(final List<BatchArtifactRef> refs) {
        this.refs = refs;
    }
}
