package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.ScopeModelImpl;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlScope {

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    @XmlElement(name = "artifact-loader", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlDeclaration> artifactLoader = new ArrayList<>(0);

    @XmlElement(name = "job-operator", namespace = XmlChainlink.NAMESPACE, required = false)
    protected List<XmlJobOperator> jobOperators = new ArrayList<>(0);

    public String getRef() {
        return ref;
    }

    public void setRef(final String ref) {
        this.ref = ref;
    }

    public List<XmlDeclaration> getArtifactLoader() {
        return artifactLoader;
    }

    public void setArtifactLoader(final List<XmlDeclaration> artifactLoader) {
        this.artifactLoader = artifactLoader;
    }

    public List<XmlJobOperator> getJobOperators() {
        return jobOperators;
    }

    public void setJobOperators(final List<XmlJobOperator> jobOperators) {
        this.jobOperators = jobOperators;
    }

    public void configureScope(final ScopeModelImpl model, final ClassLoader classLoader) throws Exception {
        for (final XmlDeclaration resource : this.artifactLoader) {
            model.getArtifactLoader(resource.getName())
                    .setRef(XmlJobOperator.ref(resource));
        }
        for (final XmlJobOperator operator : this.jobOperators) {
            operator.configureScope(model, classLoader);
        }
    }
}
