package io.machinecode.nock.core.model.transition;

import io.machinecode.nock.spi.element.transition.Next;
import org.jboss.logging.Logger;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextImpl extends TransitionImpl implements Next {

    private static final Logger log = Logger.getLogger(NextImpl.class);

    private final String to;

    public NextImpl(final String on, final String to) {
        super(on);
        this.to = to;
    }

    @Override
    public String getTo() {
        return this.to;
    }

    @Override
    public Result runTransition() throws Exception {
        return Result.next(this.to);
    }

    @Override
    public String element() {
        return Next.ELEMENT;
    }
}
