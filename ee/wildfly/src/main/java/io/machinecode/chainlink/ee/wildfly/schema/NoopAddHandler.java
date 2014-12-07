package io.machinecode.chainlink.ee.wildfly.schema;

import org.jboss.as.controller.AbstractAddStepHandler;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class NoopAddHandler extends AbstractAddStepHandler {
    public static final NoopAddHandler INSTANCE = new NoopAddHandler();
}
