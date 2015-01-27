@XmlSchema(
        location = SCHEMA_URL,
        namespace = NAMESPACE,
        elementFormDefault = QUALIFIED
)
package io.machinecode.chainlink.core.jsl.xml;

import javax.xml.bind.annotation.XmlSchema;

import static io.machinecode.chainlink.spi.jsl.Job.NAMESPACE;
import static io.machinecode.chainlink.spi.jsl.Job.SCHEMA_URL;
import static javax.xml.bind.annotation.XmlNsForm.QUALIFIED;