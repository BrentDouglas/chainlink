/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.rt.glassfish.command.config;

import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import io.machinecode.chainlink.core.schema.xml.XmlJobOperator;
import io.machinecode.chainlink.rt.glassfish.command.BaseCommand;
import io.machinecode.chainlink.rt.glassfish.command.Code;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishJobOperator;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishSubSystem;
import io.machinecode.chainlink.core.schema.xml.XmlSchema;
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
            final GlassfishJobOperator operator = subSystem.removeJobOperator(jobOperator);
            final XmlJobOperator xml = XmlSchema.xmlJobOperator(operator);
            context.getActionReport().setMessage(XmlSchema.writeJobOperator(xml));
            return null;
        }
    }
}
