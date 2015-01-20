package io.machinecode.chainlink.ee.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.ee.glassfish.command.BaseCommand;
import io.machinecode.chainlink.ee.glassfish.command.Code;
import io.machinecode.chainlink.ee.glassfish.command.SetCommand;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishDeployment;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishSubSystem;
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
@Service(name="set-chainlink-deployment")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value = {RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@RestEndpoints({
        @RestEndpoint(configBean = Domain.class,
                opType = RestEndpoint.OpType.POST,
                path = "set-chainlink-deployment",
                description = "Set Chainlink Deployment")
})
public class SetDeploymentCommand extends SetCommand {

    @Override
    public void exec(final Config config, final AdminCommandContext context) throws Exception {
        final GlassfishSubSystem subSystem = requireSubsystem(config);
        final DeploymentSchema<?,?,?> that = readDeployment();
        final GlassfishDeployment dep = subSystem.getDeployment(that.getName());
        if (dep == null) {
            locked(subSystem, new CreateDeployment(that));
        } else {
            BaseCommand.<DeploymentSchema<?,?,?>,GlassfishDeployment>lockedUpdate(dep, that, Op.values());
        }
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
