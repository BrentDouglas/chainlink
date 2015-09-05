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
package io.machinecode.chainlink.core.validation.transition;

import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.jsl.transition.End;
import io.machinecode.chainlink.spi.jsl.transition.Fail;
import io.machinecode.chainlink.spi.jsl.transition.Next;
import io.machinecode.chainlink.spi.jsl.transition.Stop;
import io.machinecode.chainlink.spi.jsl.transition.Transition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class TransitionValidator {

    private TransitionValidator(){}

    public static void visit(final Transition that, final VisitorNode context) {
        if (that instanceof Stop) {
            StopValidator.INSTANCE.visit((Stop) that, context);
        } else if (that instanceof Fail) {
            FailValidator.INSTANCE.visit((Fail) that, context);
        } else if (that instanceof Next) {
            NextValidator.INSTANCE.visit((Next) that, context);
        } else if (that instanceof End) {
            EndValidator.INSTANCE.visit((End) that, context);
        }
    }
}
