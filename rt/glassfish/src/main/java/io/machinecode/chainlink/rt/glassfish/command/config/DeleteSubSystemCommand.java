package io.machinecode.chainlink.rt.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.rt.glassfish.command.BaseCommand;
import io.machinecode.chainlink.rt.glassfish.command.Code;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishDeclaration;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishDeployment;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishJobOperator;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishSubSystem;
import io.machinecode.chainlink.core.schema.xml.XmlSchema;
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

import java.util.Collections;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Service(name="delete-chainlink")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value = {RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@RestEndpoints({
        @RestEndpoint(configBean = Domain.class,
                opType = RestEndpoint.OpType.DELETE,
                path = "delete-chainlink",
                description = "Delete Chainlink SubSystem"),
})
public class DeleteSubSystemCommand extends BaseCommand {

    @Override
    public void exec(final Config config, final AdminCommandContext context) throws Exception {
        final GlassfishSubSystem subSystem = config.getExtensionByType(GlassfishSubSystem.class);
        if (subSystem == null) {
            throw new Exception("Chainlink not configured."); //TODO Message
        }
        locked(subSystem, new DeleteSubSystem(context));
    }

    private static class DeleteSubSystem extends Code<GlassfishSubSystem> {

        final AdminCommandContext context;

        private DeleteSubSystem(final AdminCommandContext context) {
            this.context = context;
        }

        @Override
        public Object code(final GlassfishSubSystem subSystem) throws Exception {
            final XmlChainlinkSubSystem xml = XmlSchema.xmlSubSystem(subSystem);
            context.getActionReport().setMessage(XmlSchema.writeSubSystem(xml));
            subSystem.setRef(null);
            subSystem.setDeployments(Collections.<GlassfishDeployment>emptyList());
            subSystem.setConfigurationLoaders(Collections.<GlassfishDeclaration>emptyList());
            subSystem.setJobOperators(Collections.<GlassfishJobOperator>emptyList());
            return null;
        }
    }
}
