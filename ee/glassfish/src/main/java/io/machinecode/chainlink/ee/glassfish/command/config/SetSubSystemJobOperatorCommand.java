package io.machinecode.chainlink.ee.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.core.configuration.def.JobOperatorDef;
import io.machinecode.chainlink.core.configuration.op.Op;
import io.machinecode.chainlink.ee.glassfish.command.BaseCommand;
import io.machinecode.chainlink.ee.glassfish.command.Code;
import io.machinecode.chainlink.ee.glassfish.command.SetCommand;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishJobOperator;
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
@Service(name="set-chainlink-job-operator")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value = {RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@RestEndpoints({
        @RestEndpoint(configBean = Domain.class,
                opType = RestEndpoint.OpType.POST,
                path = "set-chainlink-job-operator",
                description = "Set Chainlink SubSystem Job Operator")
})
public class SetSubSystemJobOperatorCommand extends SetCommand {

    @Override
    public void exec(final Config config, final AdminCommandContext context) throws Exception {
        final GlassfishSubSystem subSystem = requireSubsystem(config);
        final JobOperatorDef<?,?> that = readJobOperator();
        final GlassfishJobOperator op = subSystem.getJobOperator(that.getName());
        if (op == null) {
            locked(subSystem, new CreateSubSystemJobOperator(that));
        } else {
            BaseCommand.<JobOperatorDef<?,?>,GlassfishJobOperator>lockedUpdate(op, that, Op.values());
        }
    }

    private static class CreateSubSystemJobOperator extends Code<GlassfishSubSystem> {
        private final JobOperatorDef<?,?> that;

        public CreateSubSystemJobOperator(final JobOperatorDef<?, ?> that) {
            this.that = that;
        }

        @Override
        public Object code(final GlassfishSubSystem subSystem) throws Exception {
            final GlassfishJobOperator op = subSystem.createChild(GlassfishJobOperator.class);
            BaseCommand.<JobOperatorDef<?,?>,GlassfishJobOperator>unlockedUpdate(op, that, Op.ADD);
            subSystem.getJobOperators().add(op);
            return null;
        }
    }
}
