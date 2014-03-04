@XmlSchema(
        location = SCHEMA_URL,
        namespace = NAMESPACE,
        elementFormDefault = QUALIFIED
)
package io.machinecode.chainlink.jsl.xml.task;

import javax.xml.bind.annotation.XmlSchema;

import static io.machinecode.chainlink.spi.element.Job.NAMESPACE;
import static io.machinecode.chainlink.spi.element.Job.SCHEMA_URL;
import static javax.xml.bind.annotation.XmlNsForm.QUALIFIED;