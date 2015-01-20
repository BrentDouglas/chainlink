package io.machinecode.chainlink.ee.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.ee.glassfish.command.BaseCommand;
import io.machinecode.chainlink.ee.glassfish.command.Code;
import io.machinecode.chainlink.ee.glassfish.command.SetCommand;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishDeployment;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishJobOperator;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishSubSystem;
import io.machinecode.chainlink.spi.management.Op;
import io.machinecode.chainlink.spi.schema.JobOperatorSchema;
import org.glassfish.api.Param;
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
@Service(name="set-chainlink-deployment-job-operator")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value = {RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@RestEndpoints({
        @RestEndpoint(configBean = Domain.class,
                opType = RestEndpoint.OpType.POST,
                path = "set-chainlink-deployment-job-operator",
                description = "Set Chainlink Deployment Job Operator")
})
public class SetDeploymentJobOperatorCommand extends SetCommand {

    @Param(name = "deployment", shortName = "d", optional = false)
    protected String deployment;

    @Override
    public void exec(final Config config, final AdminCommandContext context) throws Exception {
        final GlassfishSubSystem subSystem = requireSubsystem(config);
        final GlassfishDeployment dep = requireDeployment(subSystem, deployment);
        final JobOperatorSchema<?,?> that = readJobOperator();
        final GlassfishJobOperator op = dep.getJobOperator(that.getName());
        if (op == null) {
            locked(dep, new CreateDeploymentJobOperator(that));
        } else {
            BaseCommand.<JobOperatorSchema<?,?>,GlassfishJobOperator>lockedUpdate(op, that, Op.values());
        }
    }

    private static class CreateDeploymentJobOperator extends Code<GlassfishDeployment> {
        private final JobOperatorSchema<?,?> that;

        public CreateDeploymentJobOperator(final JobOperatorSchema<?,?> that) {
            this.that = that;
        }

        @Override
        public Object code(final GlassfishDeployment dep) throws Exception {
            final GlassfishJobOperator op = dep.createChild(GlassfishJobOperator.class);
            BaseCommand.<JobOperatorSchema<?,?>,GlassfishJobOperator>unlockedUpdate(op, that, Op.ADD);
            dep.getJobOperators().add(op);
            return null;
        }
    }
}
