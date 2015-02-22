package io.machinecode.chainlink.core.schema.xml;

import io.machinecode.chainlink.core.util.Creator;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class CreateXmlProperty implements Creator<XmlProperty> {
    @Override
    public XmlProperty create() throws Exception {
        return new XmlProperty();
    }
}
