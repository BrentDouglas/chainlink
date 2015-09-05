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
package io.machinecode.chainlink.core.validation.visitor;

import io.machinecode.chainlink.spi.jsl.Element;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class Visitor<T extends Element> {

    private final String element;

    protected Visitor(final String element) {
        this.element = element;
    }

    public final VisitorNode visit(T that) {
        final VisitorNode root = new VisitorNode(element, that);
        doVisit(that, root);
        return root;
    }

    /**
     * Needs to be called internally
     * @param that
     * @param parent
     */
    public void visit(T that, final VisitorNode parent) {
        final VisitorNode child;
        child = new VisitorNode(element, that, parent);
        parent.addChild(child);
        doVisit(that, child);
    }

    protected abstract void doVisit(T that, final VisitorNode node);
}
