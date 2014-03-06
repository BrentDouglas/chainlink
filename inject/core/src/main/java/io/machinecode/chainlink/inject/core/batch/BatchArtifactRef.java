package io.machinecode.chainlink.inject.core.batch;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlAccessorType(NONE)
public class BatchArtifactRef {

    public static final String ELEMENT = "ref";

    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "class", required = true)
    private String clazz;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(final String clazz) {
        this.clazz = clazz;
    }
}
