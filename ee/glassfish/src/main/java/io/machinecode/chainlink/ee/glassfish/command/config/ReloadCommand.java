package io.machinecode.chainlink.ee.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.core.configuration.def.DeploymentDef;
import io.machinecode.chainlink.core.configuration.op.Op;
import io.machinecode.chainlink.ee.glassfish.GlassfishService;
import io.machinecode.chainlink.ee.glassfish.command.BaseCommand;
import io.machinecode.chainlink.ee.glassfish.command.Code;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishDeployment;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishSubSystem;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.CommandLock;
import org.glassfish.api.admin.ExecuteOn;
import org.glassfish.api.admin.RestEndpoint;
import org.glassfish.api.admin.RestEndpoints;
import org.glassfish.api.admin.RuntimeType;
import org.glassfish.config.support.CommandTarget;
import org.glassfish.config.support.TargetType;
import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Service;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Service(name="reload-chainlink")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value = {RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@RestEndpoints({
        @RestEndpoint(configBean = Domain.class,
                opType = RestEndpoint.OpType.POST,
                path = "reload-chainlink",
                description = "Reload Chainlink")
})
public class ReloadCommand extends BaseCommand {

    @Override
    public void exec(final Config config, final AdminCommandContext context) throws Exception {
        final GlassfishService service = locator.getService(GlassfishService.class);
        service.reload();
    }

    private static class CreateDeployment extends Code<GlassfishSubSystem> {
        private final DeploymentDef<?,?,?> that;

        public CreateDeployment(final DeploymentDef<?, ?, ?> that) {
            this.that = that;
        }

        @Override
        public Object code(final GlassfishSubSystem subSystem) throws Exception {
            final GlassfishDeployment dep = subSystem.createChild(GlassfishDeployment.class);
            BaseCommand.<DeploymentDef<?,?,?>,GlassfishDeployment>unlockedUpdate(dep, that, Op.ADD);
            subSystem.getDeployments().add(dep);
            return null;
        }
    }
}
