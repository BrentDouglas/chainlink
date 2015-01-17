package io.machinecode.chainlink.ee.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.core.configuration.xml.XmlDeployment;
import io.machinecode.chainlink.ee.glassfish.command.BaseCommand;
import io.machinecode.chainlink.ee.glassfish.command.Code;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishDeployment;
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
@Service(name="delete-chainlink-deployment")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value = {RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@RestEndpoints({
        @RestEndpoint(configBean = Domain.class,
                opType = RestEndpoint.OpType.DELETE,
                path = "delete-chainlink-deployment",
                description = "Delete Chainlink Deployment")
})
public class DeleteDeploymentCommand extends BaseCommand {

    @Param(name = "deployment", shortName = "d", optional = false)
    protected String deployment;

    @Override
    public void exec(final Config config, final AdminCommandContext context) throws Exception {
        final GlassfishSubSystem subSystem = config.getExtensionByType(GlassfishSubSystem.class);
        if (subSystem == null) {
            throw new Exception("Chainlink not configured"); //TODO Message
        }
        locked(subSystem, new DeleteDeployment(context));
    }

    private class DeleteDeployment extends Code<GlassfishSubSystem> {

        final AdminCommandContext context;

        private DeleteDeployment(final AdminCommandContext context) {
            this.context = context;
        }

        @Override
        public Object code(final GlassfishSubSystem subSystem) throws Exception {
            final List<GlassfishDeployment> deployments = new ArrayList<>(subSystem.getDeployments());
            final ListIterator<GlassfishDeployment> it = deployments.listIterator();
            while (it.hasNext()) {
                final GlassfishDeployment that = it.next();
                if (deployment.equals(that.getName())) {
                    final XmlDeployment xml = GlassfishXml.xmlDeployment(that);
                    context.getActionReport().setMessage(GlassfishXml.writeDeployment(xml));
                    it.remove();
                    subSystem.setDeployments(deployments);
                    return null;
                }
            }
            throw new TransactionFailure("No deployment with name " + deployment); //TODO Message
        }
    }
}
