package io.machinecode.chainlink.rt.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.rt.glassfish.GlassfishService;
import io.machinecode.chainlink.rt.glassfish.command.BaseCommand;
import io.machinecode.chainlink.rt.glassfish.command.Code;
import io.machinecode.chainlink.rt.glassfish.configuration.GlassfishDeployment;
import io.machinecode.chainlink.rt.glassfish.configuration.GlassfishSubSystem;
import io.machinecode.chainlink.spi.management.Op;
import io.machinecode.chainlink.spi.schema.DeploymentSchema;
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
        private final DeploymentSchema<?,?,?> that;

        public CreateDeployment(final DeploymentSchema<?,?,?> that) {
            this.that = that;
        }

        @Override
        public Object code(final GlassfishSubSystem subSystem) throws Exception {
            final GlassfishDeployment dep = subSystem.createChild(GlassfishDeployment.class);
            BaseCommand.<DeploymentSchema<?,?,?>,GlassfishDeployment>unlockedUpdate(dep, that, Op.ADD);
            subSystem.getDeployments().add(dep);
            return null;
        }
    }
}
