package io.machinecode.chainlink.test.core.jsl.groovy

import io.machinecode.chainlink.core.jsl.impl.JobImpl
import io.machinecode.chainlink.core.factory.JobFactory
import io.machinecode.chainlink.jsl.groovy.Dsl
import io.machinecode.chainlink.spi.jsl.Job
import io.machinecode.chainlink.spi.jsl.task.Chunk.CheckpointPolicy
import io.machinecode.chainlink.core.ExpressionTest
import io.machinecode.chainlink.core.jsl.InheritanceJobTest
import junit.framework.Assert
import org.junit.Test

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GroovyJobTest {

    @Test
    public void jobTest() {
        final Job j = Dsl.job {
            id "i1"
            restartable "false"
            version "1.0"
            props {
                prop "job-prop", "job-value"
            }
            listeners {
                listener {
                    ref "something"
                    props {
                        prop "name", "value"
                    }
                }
            }
            step {
                id "step1"
                next "step2"
                allowStartIfComplete "true"
                startLimit "0"
                end {
                    exitStatus ""
                    on "ERROR"
                }
                fail {
                    on "ERROR"
                }
                next {
                    to "step3"
                    on "ERROR"
                }
                stop {
                    on "ERROR"
                }
                partition {
                    plan {
                        threads "4"
                        partitions "7"
                        props {
                            partition "1"
                            prop "something", "else"
                        }
                    }
                }
                batchlet {
                    ref "batch1"
                    props {
                        prop "a", "prop"
                    }
                }
            }
            step {
                id "step2"
                next "step3"
                allowStartIfComplete "true"
                end {
                    exitStatus ""
                    on "ERROR"
                }
                fail {
                    on "ERROR"
                }
                next {
                    on "ERROR"
                    to "step4"
                }
                stop {
                    on "ERROR"
                }
                partition {
                    mapper {
                        ref "mapper1"
                        props {
                            prop "something", "else"
                        }
                    }
                }
                batchlet {
                    ref "batch1"
                    props {
                        prop "a", "prop"
                    }
                }
            }
            step {
                id "step3"
                next "step4"
                allowStartIfComplete "true"
                end {
                    exitStatus ""
                    on "ERROR"
                }
                fail {
                    on "ERROR"
                }
                next {
                    on "ERROR"
                    to "step4"
                }
                stop {
                    on "ERROR"
                }
                partition {
                    plan {
                        threads "4"
                        partitions "7"
                        props {
                            partition "2"
                            prop "something", "else"
                        }
                    }
                }
                chunk {
                    itemCount "4"
                    skipLimit "3"
                    retryLimit "3"
                    timeLimit "24"
                    checkpointPolicy CheckpointPolicy.CUSTOM
                    checkpointAlgorithm {
                        ref "checkpoint-algorithm"
                        props {
                            prop "", ""
                        }
                    }
                    skippableExceptionClasses {
                        include Exception.class
                        exclude "Throwable"
                        exclude "JAXBException"
                    }
                    retryableExceptionClasses {
                        include Exception.class
                        exclude "Throwable"
                        exclude "JAXBException"
                    }
                    noRollbackExceptionClasses {
                        include Exception.class
                        exclude "Throwable"
                        exclude "JAXBException"
                    }
                    reader {
                        ref "Reader"
                        props {
                            prop "", ""
                        }
                    }
                    processor {
                        ref "Processor"
                        props {
                            prop "", ""
                        }
                    }
                    writer {
                        ref "Writer"
                        props {
                            prop "", ""
                        }
                    }
                }
            }
            step {
                id "step4"
                allowStartIfComplete "false"
                startLimit "7"
                end {
                    exitStatus ""
                    on "ERROR"
                }
                fail {
                    on "ERROR"
                }
                stop {
                    on "ERROR"
                }
                partition {
                    mapper {
                        ref "mapper2"
                        props {
                            prop "something", "else"
                        }
                    }
                }
                chunk {
                        itemCount "4"
                        skipLimit "3"
                        retryLimit "3"
                        timeLimit "24"
                        checkpointPolicy CheckpointPolicy.ITEM
                        checkpointAlgorithm {
                            ref "other-checkpoint-algorithm"
                            props {
                                prop "", ""
                            }
                        }
                        skippableExceptionClasses {
                            include Exception.class
                            exclude "Throwable"
                            exclude "JAXBException"
                        }
                        retryableExceptionClasses {
                            include Exception.class
                            exclude "Throwable"
                            exclude "JAXBException"
                        }
                        noRollbackExceptionClasses {
                            include Exception.class
                            exclude "Throwable"
                            exclude "JAXBException"
                        }
                        reader {
                            ref "Reader2"
                            props {
                                prop "", ""
                            }
                        }
                        processor {
                            ref "Processor2"
                            props {
                                prop "", ""
                            }
                        }
                        writer {
                            ref "Writer2"
                            props {
                                prop "", ""
                            }
                        }
                }
            }
        }
        final Job x = JobFactory.produce j, ExpressionTest.PARAMETERS
        JobFactory.validate(x);

        Assert.assertEquals("i1", x.getId());
    }


    @Test
    public void defaultValuesTest() {
        final Job j = Dsl.job {
            id "i1"
            //restartable "false"
            //version "1.0"
            props {
                prop "job-prop", "job-value"
            }
            listeners {
                listener {
                    ref "something"
                    props {
                        prop "name", "value"
                    }
                }
            }
            step {
                id "step1"
                //next "step2"
                //allowStartIfComplete "false"
                //startLimit "0"
                partition {
                    plan {
                        //partitions "1"
                        //threads "1"
                    }
                }
                chunk {
                    //checkpointPolicy "item"
                    //itemCount "10"
                    //skipLimit "0"
                    //retryLimit "0"
                    //timeLimit "0"
                    checkpointAlgorithm {
                        ref "other-checkpoint-algorithm"
                        props {
                            prop "", ""
                        }
                    }
                    skippableExceptionClasses {
                        include Exception.class
                        exclude "Throwable"
                        exclude "JAXBException"
                    }
                    retryableExceptionClasses {
                        include Exception.class
                        exclude "Throwable"
                        exclude "JAXBException"
                    }
                    noRollbackExceptionClasses {
                        include Exception.class
                        exclude "Throwable"
                        exclude "JAXBException"
                    }
                    reader {
                        ref "Reader2"
                        props {
                            prop "", ""
                        }
                    }
                    processor {
                        ref "Processor2"
                        props {
                            prop "", ""
                        }
                    }
                    writer {
                        ref "Writer2"
                        props {
                            prop "", ""
                        }
                    }
                }
            }
            step {
                id "step2"
            }
        }
        final Job x = JobFactory.produce j, ExpressionTest.PARAMETERS

        InheritanceJobTest.testDefaults(x);
    }

    @Test
    public void validFlowTransitionScopeTest() {
        final Job j = Dsl.job {
            id "job1"
            restartable "false"
            version "1.0"
            flow {
                id "flow1"
                next "step2"
            }
            step {
                id "step3"
                next "step4"
            }
            step {
                id "step4"
            }
            step {
                id "step2"
            }
        }
        final JobImpl impl = JobFactory.produce j, ExpressionTest.PARAMETERS
        JobFactory.validate(impl);
    }
}