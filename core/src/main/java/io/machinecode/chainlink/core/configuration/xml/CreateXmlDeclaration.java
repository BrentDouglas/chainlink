package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.op.Creator;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class CreateXmlDeclaration implements Creator<XmlDeclaration> {
    @Override
    public XmlDeclaration create() throws Exception {
        return new XmlDeclaration();
    }
}
