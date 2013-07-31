package io.machinecode.nock.jsl.fluent.transition;

import io.machinecode.nock.jsl.api.transition.Next;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentNext extends FluentTransition<FluentNext> implements Next {

    private String to;

    @Override
    public String getTo() {
        return this.to;
    }

    public FluentNext setTo(final String to) {
        this.to = to;
        return this;
    }
}
