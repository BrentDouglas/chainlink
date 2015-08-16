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
package io.machinecode.chainlink.core.validation;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.PropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class PropertyReferenceValidator<T extends PropertyReference> extends ValidatingVisitor<T> {

    protected PropertyReferenceValidator(final String element) {
        super(element);
    }

    @Override
    public void doVisit(final T that, final VisitorNode context) {
        if (that.getRef() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "ref"));
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.visit(that.getProperties(), context);
        }
    }
}
