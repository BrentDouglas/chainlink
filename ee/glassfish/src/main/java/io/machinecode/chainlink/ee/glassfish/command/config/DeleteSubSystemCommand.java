package io.machinecode.chainlink.ee.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.core.configuration.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.ee.glassfish.command.BaseCommand;
import io.machinecode.chainlink.ee.glassfish.command.Code;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishDeclaration;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishDeployment;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishJobOperator;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishSubSystem;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishXml;
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
            final XmlChainlinkSubSystem xml = GlassfishXml.xmlSubSystem(subSystem);
            context.getActionReport().setMessage(GlassfishXml.writeSubSystem(xml));
            subSystem.setRef(null);
            subSystem.setDeployments(Collections.<GlassfishDeployment>emptyList());
            subSystem.setArtifactLoaders(Collections.<GlassfishDeclaration>emptyList());
            subSystem.setJobOperators(Collections.<GlassfishJobOperator>emptyList());
            return null;
        }
    }
}
