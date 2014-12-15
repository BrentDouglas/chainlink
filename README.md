# Chainlink
[![Build Status](https://travis-ci.org/machinecode-io/chainlink.svg)](https://travis-ci.org/machinecode-io/chainlink)

An implementation of JSR-352.

## Aims

Chainlink aims to implement JSR-352 in a modular and easily extensible
fashion. Each of the major components of the system should be user
replaceable without requiring an in depth knowledge of the specification
if possible. It also aims to provide reasonable default implementations
of these components for a wide range of external libraries.

## Features

Dependency injection frameworks:
- CDI
- Spring
- Seam 2

Execution repositories:
- Memory
- MongoDB
- JPA
- JDBC (though it has only been tested against H2, PostgreSQL and
  MariaDB)
- Redis
- Infinispan
- Coherence
- Hazelcast
- GridGain

Transports:
- Local JVM
- Infinispan

Job loaders that support the job inheritance proposal and a simple api
and utilities to allow user defined loaders to also support job
inheritance. Built in loaders support:
- XML
- Java
- Groovy DSL

JMX job management.

## Installing coherence

Chainlink has support for running on top of Coherence. Unfortunately
installing Coherence is a bit of a pain as Oracle is to cool to just
upload the jars to central.

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

## Building

To build without coherence run `mvn clean install`, if you do want it
use `mvn clean install -Pcoherence`.
`tr-coherence` and `re-coherence` will not work dependencies if
coherence is not installed.

## Run the tests

Copy [test.template.properties](test.template.properties) to `test.properties`.
This file is used to configure maven to run the tests. All properties in
this file will be passed to Surefire and available from
`System.getProperty(...)` in the test JVM.

To run the full suite of tests available under the `-Ptest` profile you
will need to have redis and mongodb running. If using the
`-Pdb-postgresql` or `-Pdb-mariadb` profiles you will also require
postgresql and mariadb running respectively. Connection settings for
these are configured in `test.properties`.

Run the tests with `mvn clean install -Ptest,<profiles>` or better
`./testsuite` for a more comprehensive run. Several options are
available, run `./testsuite -h` to see them.

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

The test process will create these files from the templates when
running the tests if they are not found.

Create the directories `/var/run/chainlink` and `/var/log/chainlink`
and make sure the user running the tests has write permissions to them.

Maven will start a chainlink daemon before running the tests and stop
it afterwards. If the maven process is killed before shutting down the
second process you might need to shut it down manually, which you can
do by running `./tck/se/target/chainlink-<version>/bin/chainlink -k`.

To run the TCK you must select an injector via a maven profile (there is
no default) and you may select an alternate  repository (an in memory
repository will be used by default) and/or an alternate transport.

Run `mvn clean install -Ptck,se,in-x,re-x,tr-x,ma-x` where the
in-x, re-x and tr-x are the profiles of injector, repository, transport
and marshaller you wish to use. You can see the available tck profiles
in [the tck modules pom](tck/pom.xml).

An example minimum command to run the TCK is `mvn clean install -Ptck,se`.

## Running the EE TCK Tests

No extra setup is required to run the EE TCK tests. You will have to add
the relevent profile for the container you wish to run them in, e.g.
`mvn clean install -Ptck,se,glassfish,tomee,wildfly` will run them in all
the supported containers. Note that currently the `tr-X` profiles won't
work, and most of the extensions probably won't work in wildfly yet.

## Remote transports

Some transports are designed to run on multiple JVM's, one owned by
failsafe and one other chainlink process. You can configure the second
process in two ways.

Primarily, you can provide options to the java process running the tck
by setting the environment variable `CHAINLINK_OPTS`. For example, to
enable debugging on both the first and the second process you could
use:

```shell
CHAINLINK_OPTS="-server -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5006" \
    mvn clean install \
        -Ptck,se,tr-<x> \
        -Dmaven.failsafe.debug
```

Properties specified in the file `chainlink-tck.properties` will also
be provided to the chainlink daemon.

Most/all of the remote transports can be configured for multicast
discovery in which case you will need to ensure your network is
correctly configured for it.

You will also need to add this route:

`sudo route add -net 224.0.0.0 netmask 240.0.0.0 dev lo`

And allow traffic through iptables:

`sudo iptables -A INPUT -i lo -d 224.0.0.0/4 -j ACCEPT`

## Infinispan and JGroups Tests

The background process should have the following properties passed to
it, either through the `CHAINLINK_OPTS` environment variable or
`chainlink-tck.properties`:

```ini
jgroups.bind_address=127.0.0.1
java.net.preferIPv4Stack=true
```

Failsafe should also be provided the same properties. To execute the
tests you can run something like this (though any injector and
repository profile can be used):

`mvn clean install -Ptck,se,tr-infinispan`

## Known issues

_MariaDB_

- MariaDB may not work correctly with both the JDBC and JPA repositories
  due to the `create_time` field only having second resolution. This may
  or may not be a problem depending on the workload (i.e. if your steps
  run for over 1 second it will be fine).

# Work in progress components

_JGroups Transport_

- Distribution doesn't actually work

_Hazelcast Transport_

- Distribution doesn't actually work

_GridGain Transport_

- Distribution doesn't actually work

_Coherence Transport_

- I can't work out how to configure Coherence but it's likely that when
  configured correctly distribution will fail for a similar reason to
  the Hazelcast and JGroups transports.

_Guice Injector_

- @javax.inject.Inject does not allow null injection of batch properties
- Module provider system probably won't work with a real Guice project

## License

[Apache 2.0](LICENSE.txt)
