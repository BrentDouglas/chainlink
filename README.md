# Chainlink
[![Build Status](https://travis-ci.org/machinecode-io/chainlink.svg)](https://travis-ci.org/machinecode-io/chainlink)

An implementation of JSR-352 that allows jobs to be distributed using existing
clustering frameworks.

## Features

### Dependency injection frameworks
- CDI
- Spring
- Seam 2

### Repositories
- Memory
- MongoDB
- JPA
- JDBC (though it has only been tested against H2, PostgreSQL and MariaDB)
- Redis
- Infinispan
- EHCache
- Hazelcast
- GridGain

### Transports
- Infinispan
- Hazelcast
- GridGain

### Job loaders
- XML
- Java
- Groovy DSL

These built in loaders all support the job inheritance proposal. A simple spi
and utilities to allow user defined loaders to also support job inheritance
are provided.

### Job Management
- JMX

## Building

To build run `mvn clean install`.

To build with the coherence modules use `mvn clean install -Pcoherence`.
The test profiles `tr-coherence` and `re-coherence` will not work if
coherence is not installed.

To build with the seam modules use `mvn clean install -Pseam`. You will
need to add the JBoss public repository to your setting.xml. See
[the instructions from JBoss](https://developer.jboss.org/wiki/MavenGettingStarted-Users)
for more details. The test profile `in-seam` will not work if seam is
not installed.

## Run the integration tests

Copy [test.template.properties](test.template.properties) to `test.properties`.
This file is used to configure maven to run the tests. All properties in
this file will be passed to Surefire and available from
`System.getProperty(...)` in the test JVM.

Copy [log4j.template.properties](log4j.template.properties)
to `log4j.properties`. You will probably want so set the root logger
to INFO and both the chainlink and then loggers to TRACE.

Repository tests requiring a dependency to be installed are protected by the
`-Prepository` profile, though enabling will activate all of them current this
means you should have mongodb and redis installed and ready
to accept connections as per the properties you configured in `test.properties`.
. If using the `-Pdb-postgresql` or `-Pdb-mariadb` profiles you will also require
postgresql and mariadb running respectively

Container tests are available under the `-Ptest` profile and can be run with
`mvn integration-test -Ptest,<other profiles>`.

There is also a utility script `./testsuite` for running the tests. Run
`./testsuite -h` to see it's options or have a look in [.travis.yml](.travis.yml)
for some examples (note, the travis ones are laid out as they are primarily to
increase parallelism and avoid the job time limit).

## Running the SE TCK Tests

Copy [test.template.properties](test.template.properties)
to `test.properties`. This file is used to configure maven to run the
tests. All properties in this file will be passed to Failsafe and
available from `System.getProperty(...)` in the test JVM.

Copy [chainlink-tck.template.properties](tck/chainlink-tck.template.properties)
to `chainlink-tck.properties` though it is only used for transports
running on two JVM's. All properties in this file will be passed to
Failsafe and available from `System.getProperty(...)` in the secondary
JVM.

Copy [log4j.template.properties](log4j.template.properties)
to `log4j.properties`. You will probably want so set the root logger
to INFO and both the chainlink and then loggers to TRACE.

The test process will create these files from the templates when
running the tests if they are not found.

Create the directories `/var/run/chainlink` and `/var/log/chainlink`
and make sure the user running the tests has write permissions to them.

Maven will start a chainlink daemon before running the tests and stop
it afterwards. If the maven process is killed before shutting down the
second process you might need to shut it down manually, which you can
do by running `./tck/se/target/chainlink-<version>/bin/chainlink -k`.

To run the TCK you must select an artifact loader via a maven profile (there is
no default) and you may select an alternate repository (an in memory
repository will be used by default) and/or an alternate transport.

Run `mvn verify -Ptck,se,in-x,re-x,tr-x,ma-x` where the
in-x, re-x and tr-x are the profiles of artifact loader, repository, transport
and marshaller you wish to use. You can see the available tck profiles
in [the tck modules pom](tck/pom.xml).

An example minimum command to run the TCK is `mvn verify -Ptck,se`.

## Running the EE TCK Tests

No extra setup is required to run the EE TCK tests. You will have to add
the relevant profile for the container you wish to run them in, e.g.
`mvn verify -Ptck,se,glassfish,tomee,wildfly` will run them in all
the supported containers. Note that currently the `tr-X` profiles will only
work against the `se` profile, and most of the extensions probably won't
work in wildfly yet.

## Remote transport tests

Some transports are designed to run on multiple JVM's, one owned by
failsafe and one other chainlink process. You can configure the second
process in two ways.

Primarily, you can provide options to the java process running the tck
by setting the environment variable `CHAINLINK_OPTS`. For example, to
enable debugging on both the first and the second process you could
use:

```shell
CHAINLINK_OPTS="-server -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=9009" \
    mvn clean install \
        -Ptck,se,tr-<x> \
        -Dmaven.failsafe.debug
```

Properties specified in the file `chainlink-tck.properties` will also
be provided to the chainlink daemon.

## Infinispan and JGroups Tests

You will need to add this route:

`sudo route add -net 224.0.0.0 netmask 240.0.0.0 dev lo`

And allow traffic through iptables:

`sudo iptables -A INPUT -i lo -d 224.0.0.0/4 -j ACCEPT`

The background process should have the following properties passed to
it, either through the `CHAINLINK_OPTS` environment variable or
`chainlink-tck.properties`:

```ini
jgroups.bind_address=127.0.0.1
java.net.preferIPv4Stack=true
```

Failsafe should also be provided the same properties. To execute the
tests you can run something like this (though any artifact loader and
repository profile can be used):

`mvn clean install -Ptck,se,tr-infinispan`

## Known issues

### MariaDB

- MariaDB may not work correctly with both the JDBC and JPA repositories
  due to the `create_time` field only having second resolution. This may
  or may not be a problem depending on the workload (i.e. if your steps
  run for over 1 second it will be fine).

## Work in progress components

### JGroups Transport

- Distribution doesn't actually work.

### Coherence Transport

- Need to look into how to correctly configure Coherence.

### Coherence Repository

- Has some issues, see above

### Guice ArtifactLoader

- @javax.inject.Inject does not allow null injection of batch properties
- Module provider system probably won't work with a real Guice project

## Installing coherence

First you need to sign up for an account on [oracles site](http://www.oracle.com).
Once you have one you need to [download the coherence installer](http://www.oracle.com/technetwork/middleware/coherence/downloads/coherence-archive-165749.html)
by selecting 'Coherence Stand-Alone Install'. Run the installer with
`java -jar /path/to/download/coherence_version.jar` and follow the
prompts. Now you need to install the coherence jar into your local
maven repository:

```shell
mvn install:install-file  \
      -DgroupId=com.oracle.coherence  \
      -DartifactId=coherence  \
      -Dversion=<version> \
      -Dfile=${ORACLE_HOME}/coherence/lib/coherence.jar  \
      -Dpackaging=jar \
      -DgeneratePom=true
```

Where `ORACLE_HOME` is where you set it in the installer and `<version>`
is the version of coherence you are installing (which should match the
version in [pom.xml](pom.xml).

Using the build profile `coherence` or the test profiles `tr-coherence`
and `re-coherence` will not work if coherence is not installed.

## License

[Apache 2.0](LICENSE.txt)
