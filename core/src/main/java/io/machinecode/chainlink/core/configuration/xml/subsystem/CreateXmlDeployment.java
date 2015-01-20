package io.machinecode.chainlink.core.configuration.xml.subsystem;

import io.machinecode.chainlink.core.configuration.op.Creator;
import io.machinecode.chainlink.core.configuration.xml.XmlDeployment;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class CreateXmlDeployment implements Creator<XmlDeployment> {
    @Override
    public XmlDeployment create() throws Exception {
        return new XmlDeployment();
    }
}
