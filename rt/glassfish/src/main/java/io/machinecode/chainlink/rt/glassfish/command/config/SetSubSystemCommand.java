package io.machinecode.chainlink.rt.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.rt.glassfish.command.BaseCommand;
import io.machinecode.chainlink.rt.glassfish.command.Code;
import io.machinecode.chainlink.rt.glassfish.command.SetCommand;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishSubSystem;
import io.machinecode.chainlink.core.schema.xml.XmlSchema;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
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
@Service(name="set-chainlink")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value = {RuntimeType.DAS})
@TargetType(value = {CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER})
@RestEndpoints({
        @RestEndpoint(configBean = Domain.class,
                opType = RestEndpoint.OpType.POST,
                path = "set-chainlink",
                description = "Set Chainlink SubSystem")
})
public class SetSubSystemCommand extends SetCommand {

    @Override
    public void exec(final Config config, final AdminCommandContext context) throws Exception {
        final GlassfishSubSystem subSystem = config.getExtensionByType(GlassfishSubSystem.class);
        final SubSystemSchema<?,?,?,?> that = readSubsystem();
        if (subSystem == null) {
            locked(config, new CreateSubSystem(that));
        } else {
            final XmlChainlinkSubSystem xml = XmlSchema.xmlSubSystem(subSystem);
            BaseCommand.<SubSystemSchema<?,?,?,?>,GlassfishSubSystem>lockedUpdate(subSystem, that, Op.values());
            context.getActionReport().setMessage(XmlSchema.writeSubSystem(xml));
        }
    }

    private static class CreateSubSystem extends Code<Config> {
        private final SubSystemSchema<?,?,?,?> that;

        public CreateSubSystem(final SubSystemSchema<?,?,?,?> that) {
            this.that = that;
        }

        @Override
        public Object code(final Config config) throws Exception {
            final GlassfishSubSystem subSystem = config.createChild(GlassfishSubSystem.class);
            BaseCommand.<SubSystemSchema<?,?,?,?>,GlassfishSubSystem>unlockedUpdate(subSystem, that, Op.ADD);
            return null;
        }
    }
}
