package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.ScopeModelImpl;
import io.machinecode.chainlink.core.configuration.def.ScopeDef;

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
public class XmlScope implements ScopeDef<XmlDeclaration, XmlProperty, XmlJobOperator> {

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    @XmlElement(name = "artifact-loader", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlDeclaration> artifactLoaders = new ArrayList<>(0);

    @XmlElement(name = "job-operator", namespace = XmlChainlink.NAMESPACE, required = false)
    protected List<XmlJobOperator> jobOperators = new ArrayList<>(0);

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public void setRef(final String ref) {
        this.ref = ref;
    }

    @Override
    public List<XmlDeclaration> getArtifactLoaders() {
        return artifactLoaders;
    }

    @Override
    public void setArtifactLoaders(final List<XmlDeclaration> artifactLoaders) {
        this.artifactLoaders = artifactLoaders;
    }

    @Override
    public List<XmlJobOperator> getJobOperators() {
        return jobOperators;
    }

    @Override
    public void setJobOperators(final List<XmlJobOperator> jobOperators) {
        this.jobOperators = jobOperators;
    }

    public void configureScope(final ScopeModelImpl model, final ClassLoader classLoader) throws Exception {
        for (final XmlDeclaration resource : this.artifactLoaders) {
            model.getArtifactLoader(resource.getName())
                    .setRef(XmlJobOperator.ref(resource));
        }
        for (final XmlJobOperator operator : this.jobOperators) {
            operator.configureScope(model, classLoader);
        }
    }
}
