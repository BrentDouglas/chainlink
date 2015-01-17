package io.machinecode.chainlink.ee.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.core.configuration.xml.XmlJobOperator;
import io.machinecode.chainlink.ee.glassfish.command.BaseCommand;
import io.machinecode.chainlink.ee.glassfish.command.Code;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishDeployment;
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
@Service(name="delete-chainlink-deployment-job-operator")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value = {RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@RestEndpoints({
        @RestEndpoint(configBean = Domain.class,
                opType = RestEndpoint.OpType.DELETE,
                path = "delete-chainlink-deployment-job-operator",
                description = "Delete Chainlink Deployment Job Operator")
})
public class DeleteDeploymentJobOperatorCommand extends BaseCommand {

    @Param(name = "deployment", shortName = "d", optional = false)
    protected String deployment;

    @Param(name = "job-operator", shortName = "j", optional = false)
    protected String jobOperator;

    @Override
    public void exec(final Config config, final AdminCommandContext context) throws Exception {
        final GlassfishSubSystem subSystem = config.getExtensionByType(GlassfishSubSystem.class);
        if (subSystem == null) {
            throw new Exception("Chainlink not configured"); //TODO Message
        }
        final GlassfishDeployment dep = subSystem.getDeployment(deployment);
        if (dep == null) {
            throw new Exception("No Chainlink deployment with name " + deployment); //TODO Message
        }
        locked(dep, new DeleteDeploymentJobOperator(context));
    }

    private class DeleteDeploymentJobOperator extends Code<GlassfishDeployment> {

        final AdminCommandContext context;

        private DeleteDeploymentJobOperator(final AdminCommandContext context) {
            this.context = context;
        }

        @Override
        public Object code(final GlassfishDeployment dep) throws Exception {
            final List<GlassfishJobOperator> jobOperators = new ArrayList<>(dep.getJobOperators());
            final ListIterator<GlassfishJobOperator> it = jobOperators.listIterator();
            while (it.hasNext()) {
                final GlassfishJobOperator that = it.next();
                if (jobOperator.equals(that.getName())) {
                    final XmlJobOperator xml = GlassfishXml.xmlJobOperator(that);
                    context.getActionReport().setMessage(GlassfishXml.writeJobOperator(xml));
                    it.remove();
                    dep.setJobOperators(jobOperators);
                    return null;
                }
            }
            throw new TransactionFailure("Chainlink deployment " + deployment + " has no job operator with name " + jobOperator); //TODO Message
        }
    }
}
