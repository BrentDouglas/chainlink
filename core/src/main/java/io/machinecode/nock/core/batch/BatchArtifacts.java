package io.machinecode.nock.core.batch;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;
import static io.machinecode.nock.core.batch.BatchArtifacts.NAMESPACE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
