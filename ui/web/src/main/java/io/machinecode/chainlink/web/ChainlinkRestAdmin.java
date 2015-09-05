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
package io.machinecode.chainlink.web;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.schema.xml.XmlDeployment;
import io.machinecode.chainlink.core.schema.xml.XmlJobOperator;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.Environment;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.Configure;
import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.MutableDeploymentSchema;
import io.machinecode.chainlink.core.schema.MutableJobOperatorSchema;
import io.machinecode.chainlink.core.schema.MutableSubSystemSchema;
import io.machinecode.chainlink.core.schema.NoDeploymentWithNameException;
import io.machinecode.chainlink.core.schema.NoJobOperatorWithNameException;
import io.machinecode.chainlink.core.schema.SubSystemSchema;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Consumes({ MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_XML })
@Path("/")
public class ChainlinkRestAdmin {

    private Environment environment;

    @PostConstruct
    public void postConstruct() throws Exception {
        environment = Chainlink.getEnvironment();
    }

    @GET
    @Path("subsystem")
    public XmlChainlinkSubSystem getSubsystem() throws Exception {
        final XmlChainlinkSubSystem xml = new XmlChainlinkSubSystem();
        xml.accept(environment.getConfiguration(), Op.values());
        return xml;
    }

    @POST
    @Path("subsystem")
    public void insertSubsystem(final XmlChainlinkSubSystem insert) {
        environment.setConfiguration(new SetSubSystem(insert));
    }

    @PUT
    @Path("subsystem")
    public void updateSubsystem(final XmlChainlinkSubSystem update) {
        environment.setConfiguration(new SetSubSystem(update));
    }

    @DELETE
    @Path("subsystem")
    public XmlChainlinkSubSystem deleteSubsystem() throws Exception {
        final SubSystemSchema<?,?,?> that = environment.setConfiguration(new DeleteSubSystem());
        final XmlChainlinkSubSystem xml = new XmlChainlinkSubSystem();
        xml.accept(that, Op.values());
        return xml;
    }

    @GET
    @Path("subsystem/job-operator/{jobOperator}")
    public XmlJobOperator getSubsystemJobOperator(@PathParam("jobOperator") final  String jobOperator) throws Exception {
        final SubSystemSchema<?,?,?> subsystem = environment.getConfiguration();
        final JobOperatorSchema<?> op = subsystem.getJobOperator(jobOperator);
        if (op == null) {
            throw new NoJobOperatorWithNameException("No job-operator with name " + jobOperator);
        }
        final XmlJobOperator xml = new XmlJobOperator();
        xml.accept(op, Op.values());
        return xml;
    }

    @POST
    @Path("subsystem/job-operator")
    public void insertSubsystemJobOperator(final XmlJobOperator insert) throws Exception {
        environment.setConfiguration(new Configure() {
            @Override
            public void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception {
                final MutableJobOperatorSchema<?> operator = subsystem.getJobOperator(insert.getName());
                if (operator != null) {
                    throw new Exception("job-operator already exists with name " + insert.getName());
                }
                subsystem.addJobOperator(insert);
            }
        });
    }

    @PUT
    @Path("subsystem/job-operator")
    public void updateSubsystemJobOperator(final XmlJobOperator update) throws Exception {
        environment.setConfiguration(new Configure() {
            @Override
            public void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception {
                final MutableJobOperatorSchema<?> operator = subsystem.getJobOperator(update.getName());
                if (operator == null) {
                    throw new NoJobOperatorWithNameException("No job-operator with name " + update.getName());
                }
                operator.accept(update, Op.values());
            }
        });
    }

    @DELETE
    @Path("subsystem/job-operator/{jobOperator}")
    public XmlJobOperator deleteSubsystemJobOperator(@PathParam("jobOperator") final  String jobOperator) throws Exception {
        final SubSystemSchema<?,?,?> that = environment.setConfiguration(new Configure() {
            @Override
            public void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception {
                subsystem.removeJobOperator(jobOperator);
            }
        });
        final XmlJobOperator op = new XmlJobOperator();
        op.accept(that.getJobOperator(jobOperator), Op.values());
        return op;
    }

    @GET
    @Path("deployment/{deployment}")
    public XmlDeployment getDeployment(@PathParam("deployment") final  String deployment) throws Exception {
        final SubSystemSchema<?,?,?> subsystem = environment.getConfiguration();
        final DeploymentSchema<?,?> dep = subsystem.getDeployment(deployment);
        if (dep == null) {
            throw new NoDeploymentWithNameException("No deployment with name" + deployment);
        }
        final XmlDeployment xml = new XmlDeployment();
        xml.accept(dep, Op.values());
        return xml;
    }

    @POST
    @Path("deployment")
    public void insertDeployment(final XmlDeployment insert) throws Exception {
        environment.setConfiguration(new Configure() {
            @Override
            public void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception {
                final MutableDeploymentSchema<?,?> dep = subsystem.getDeployment(insert.getName());
                if (dep != null) {
                    throw new Exception("Deployment already exists with name " + insert.getName());
                }
                subsystem.addDeployment(insert);
            }
        });
    }

    @PUT
    @Path("deployment")
    public void updateDeployment(final XmlDeployment update) throws Exception {
        environment.setConfiguration(new Configure() {
            @Override
            public void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception {
                final MutableDeploymentSchema<?,?> dep = subsystem.getDeployment(update.getName());
                if (dep == null) {
                    throw new Exception("No deployment exists with name " + update.getName());
                }
                dep.accept(update, Op.values());
            }
        });
    }

    @DELETE
    @Path("deployment/{deployment}")
    public XmlDeployment deleteDeployment(@PathParam("deployment") final  String deployment) throws Exception {
        final SubSystemSchema<?,?,?> that = environment.setConfiguration(new Configure() {
            @Override
            public void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception {
                subsystem.removeDeployment(deployment);
            }
        });
        final XmlDeployment dep = new XmlDeployment();
        dep.accept(that.getDeployment(deployment), Op.values());
        return dep;
    }

    @GET
    @Path("deployment/{deployment}/job-operator/{jobOperator}")
    public XmlJobOperator getDeploymentJobOperator(@PathParam("deployment") final  String deployment,
                                                   @PathParam("jobOperator") final  String jobOperator) throws Exception {
        final SubSystemSchema<?,?,?> subsystem = environment.getConfiguration();
        final DeploymentSchema<?,?> dec = subsystem.getDeployment(deployment);
        if (dec == null) {
            throw new NoDeploymentWithNameException("No deployment with name" + deployment);
        }
        final JobOperatorSchema<?> op = dec.getJobOperator(jobOperator);
        if (op == null) {
            throw new NoJobOperatorWithNameException("No job-operator with name " + jobOperator);
        }
        final XmlJobOperator xml = new XmlJobOperator();
        xml.accept(op, Op.values());
        return xml;
    }

    @POST
    @Path("deployment/{deployment}/job-operator")
    public void insertDeploymentJobOperator(@PathParam("deployment") final String deployment,
                                         final XmlJobOperator insert) throws Exception {
        environment.setConfiguration(new Configure() {
            @Override
            public void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception {
                final MutableDeploymentSchema<?,?> dep = subsystem.getDeployment(deployment);
                if (dep == null) {
                    throw new Exception("No deployment exists with name " + deployment);
                }
                final MutableJobOperatorSchema<?> operator = dep.getJobOperator(insert.getName());
                if (operator != null) {
                    throw new Exception("job-operator already exists with name " + insert.getName());
                }
                dep.addJobOperator(insert);
            }
        });
    }

    @PUT
    @Path("deployment/{deployment}/job-operator")
    public void updateDeploymentJobOperator(@PathParam("deployment") final String deployment,
                                            final XmlJobOperator update) throws Exception {
        environment.setConfiguration(new Configure() {
            @Override
            public void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception {
                final MutableDeploymentSchema<?,?> dep = subsystem.getDeployment(deployment);
                if (dep == null) {
                    throw new Exception("No deployment exists with name " + deployment);
                }
                final MutableJobOperatorSchema<?> operator = dep.getJobOperator(update.getName());
                if (operator == null) {
                    throw new NoJobOperatorWithNameException("No job-operator with name " + update.getName());
                }
                operator.accept(update, Op.values());
            }
        });
    }

    @DELETE
    @Path("deployment/{deployment}/job-operator/{jobOperator}")
    public XmlJobOperator deleteDeploymentJobOperator(@PathParam("deployment") final String deployment,
                                                      @PathParam("jobOperator") final String jobOperator) throws Exception {
        final SubSystemSchema<?,?,?> that = environment.setConfiguration(new Configure() {
            @Override
            public void configure(final MutableSubSystemSchema<?,?,?> subsystem) throws Exception {
                final MutableDeploymentSchema<?,?> dep = subsystem.getDeployment(deployment);
                if (dep == null) {
                    throw new Exception("No deployment exists with name " + deployment);
                }
                dep.removeJobOperator(jobOperator);
            }
        });
        final XmlJobOperator op = new XmlJobOperator();
        op.accept(that.getDeployment(deployment).getJobOperator(jobOperator), Op.values());
        return op;
    }

    private static class SetSubSystem implements Configure {
        private final XmlChainlinkSubSystem subSystem;

        public SetSubSystem(final XmlChainlinkSubSystem subSystem) {
            this.subSystem = subSystem;
        }

        @Override
        public void configure(final MutableSubSystemSchema<?,?,?> existing) throws Exception {
            existing.accept(subSystem, Op.values());
        }
    }

    private static class DeleteSubSystem implements Configure {
        @Override
        public void configure(final MutableSubSystemSchema<?,?,?> existing) throws Exception {
            existing.accept(new XmlChainlinkSubSystem(), Op.REMOVE);
        }
    }
}
