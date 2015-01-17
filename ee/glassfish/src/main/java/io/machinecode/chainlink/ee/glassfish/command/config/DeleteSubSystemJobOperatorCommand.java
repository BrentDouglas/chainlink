package io.machinecode.chainlink.ee.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.core.configuration.xml.XmlJobOperator;
import io.machinecode.chainlink.ee.glassfish.command.BaseCommand;
import io.machinecode.chainlink.ee.glassfish.command.Code;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishJobOperator;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishSubSystem;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishXml;
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
import org.jvnet.hk2.config.TransactionFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Service(name="delete-chainlink-job-operator")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value = {RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@RestEndpoints({
        @RestEndpoint(configBean = Domain.class,
                opType = RestEndpoint.OpType.DELETE,
                path = "delete-chainlink-job-operator",
                description = "Delete Chainlink Job Operator")
})
public class DeleteSubSystemJobOperatorCommand extends BaseCommand {

    @Param(name = "job-operator", shortName = "j", optional = false)
    protected String jobOperator;

    @Override
    public void exec(final Config config, final AdminCommandContext context) throws Exception {
        final GlassfishSubSystem subSystem = config.getExtensionByType(GlassfishSubSystem.class);
        if (subSystem == null) {
            throw new Exception("Chainlink not configured");
        }
        locked(subSystem, new DeleteSubSystemJobOperator(context));
    }

    private class DeleteSubSystemJobOperator extends Code<GlassfishSubSystem> {

        final AdminCommandContext context;

        private DeleteSubSystemJobOperator(final AdminCommandContext context) {
            this.context = context;
        }

        @Override
        public Object code(final GlassfishSubSystem subSystem) throws Exception {
            final List<GlassfishJobOperator> jobOperators = new ArrayList<>(subSystem.getJobOperators());
            final ListIterator<GlassfishJobOperator> it = jobOperators.listIterator();
            while (it.hasNext()) {
                final GlassfishJobOperator that = it.next();
                if (jobOperator.equals(that.getName())) {
                    final XmlJobOperator xml = GlassfishXml.xmlJobOperator(that);
                    context.getActionReport().setMessage(GlassfishXml.writeJobOperator(xml));
                    it.remove();
                    subSystem.setJobOperators(jobOperators);
                    return null;
                }
            }
            throw new TransactionFailure("No job operator with name " + jobOperator);
        }
    }
}
