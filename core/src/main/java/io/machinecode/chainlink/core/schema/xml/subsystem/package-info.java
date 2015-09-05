/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@XmlSchema(
        location = XmlChainlinkSubSystem.SCHEMA_URL,
        namespace = XmlChainlinkSubSystem.NAMESPACE,
        elementFormDefault = QUALIFIED,
        attributeFormDefault = UNQUALIFIED
)
package io.machinecode.chainlink.core.schema.xml.subsystem;

import javax.xml.bind.annotation.XmlSchema;

import static javax.xml.bind.annotation.XmlNsForm.QUALIFIED;
import static javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED;