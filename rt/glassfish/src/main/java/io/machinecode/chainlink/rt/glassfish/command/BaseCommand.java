/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.rt.glassfish.command;

import com.sun.enterprise.config.serverbeans.Config;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishDeployment;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishJobOperator;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishSubSystem;
import io.machinecode.chainlink.rt.glassfish.schema.Hack;
import io.machinecode.chainlink.core.util.Op;
import org.glassfish.api.ActionReport;
import org.glassfish.api.Param;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.internal.api.Target;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.ConfigSupport;

import javax.inject.Inject;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class BaseCommand implements AdminCommand {

    @Param(name = "target", optional = true, defaultValue = "server")
    private String target;

    @Inject
    private Target targetService;

    @Inject
    protected ServiceLocator locator;

    @Override
    public void execute(final AdminCommandContext context) {
        final ActionReport report = context.getActionReport();
        try {
            final Config config = targetService.getConfig(target);
            exec(config, context);
            report.setActionExitCode(ActionReport.ExitCode.SUCCESS);
        } catch (final Throwable e) {
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            report.setFailureCause(t);
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
        }
    }

    public abstract void exec(final Config config, final AdminCommandContext context) throws Exception;

    protected GlassfishSubSystem requireSubsystem(final Config config) throws Exception {
        if (!config.checkIfExtensionExists(GlassfishSubSystem.class)) {
            throw new Exception("Chainlink not configured");
        }
        return config.getExtensionByType(GlassfishSubSystem.class);
    }

    protected GlassfishJobOperator requireJobOperator(final GlassfishSubSystem subSystem, final String name) throws Exception {
        final GlassfishJobOperator op = subSystem.getJobOperator(name);
        if (op == null) {
            throw new Exception("Chainlink has no job operator with name " + name);
        }
        return op;
    }

    protected GlassfishDeployment requireDeployment(final GlassfishSubSystem subSystem, final String name) throws Exception {
        final GlassfishDeployment dep = subSystem.getDeployment(name);
        if (dep == null) {
            throw new Exception("Chainlink has no deployment with name " + name);
        }
        return dep;
    }

    protected GlassfishJobOperator requireDeploymentJobOperator(final GlassfishDeployment deployment, final String name) throws Exception {
        final GlassfishJobOperator op = deployment.getJobOperator(name);
        if (op == null) {
            throw new Exception("Chainlink deployment " + deployment.getName() + " has no job operator with name " + name);
        }
        return op;
    }

    public static <F, T extends ConfigBeanProxy & Hack<F>> void unlockedUpdate(final T to, final F from, final Op... ops) throws Exception {
        to.hack().accept(from, ops);
    }

    public static <F, T extends ConfigBeanProxy & Hack<F>> Object lockedUpdate(final T to, final F from, final Op... ops) throws Exception {
        return ConfigSupport.apply(new AcceptHack<F, T>(from, ops), to);
    }

    public <T extends ConfigBeanProxy> Object locked(final T that, final Code<T> code) throws Exception {
        return ConfigSupport.apply(code, that);
    }
}
