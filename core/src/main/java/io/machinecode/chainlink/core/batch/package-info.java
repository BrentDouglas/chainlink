@XmlSchema(
        location = SCHEMA_URL,
        namespace = NAMESPACE,
        elementFormDefault = QUALIFIED
)
package io.machinecode.chainlink.core.batch;

import javax.xml.bind.annotation.XmlSchema;

import static io.machinecode.chainlink.core.batch.BatchArtifacts.NAMESPACE;
import static io.machinecode.chainlink.core.batch.BatchArtifacts.SCHEMA_URL;
import static javax.xml.bind.annotation.XmlNsForm.QUALIFIED;