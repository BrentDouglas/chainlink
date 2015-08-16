/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
        location = SCHEMA_URL,
        namespace = NAMESPACE,
        elementFormDefault = QUALIFIED
)
package io.machinecode.chainlink.core.jsl.xml.execution;

import javax.xml.bind.annotation.XmlSchema;

import static io.machinecode.chainlink.spi.jsl.Job.NAMESPACE;
import static io.machinecode.chainlink.spi.jsl.Job.SCHEMA_URL;
import static javax.xml.bind.annotation.XmlNsForm.QUALIFIED;