package io.machinecode.chainlink.rt.wildfly.schema;

import io.machinecode.chainlink.core.Chainlink;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.dmr.ModelNode;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class ChainlinkRemove extends ReloadRequiredRemoveStepHandler {

    static final ChainlinkRemove INSTANCE = new ChainlinkRemove();

    @Override
    protected void performRuntime(final OperationContext context, final ModelNode operation, final ModelNode model) throws OperationFailedException {
        super.performRuntime(context, operation, model);
        Chainlink.setEnvironment(null);
    }
}
