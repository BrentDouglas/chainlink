# Chainlink

A JSR-352 implementation.

## Aims

Chainlink aims to implement JSR-352 in a modular and easily extensible
fashion. Each of the major components of the system should be user
replacable without requiring an in depth knowledge of the specification
if possible. It also aims to provide reasonable default implementations
of these components for a wide range of external libraries.

## Features

Support for several DI frameworks:
- CDI
- Spring
- Seam 2 (2.2 and 2.3)

Job repositories:
- In memory
- JPA
- JDBC (though it has only been tested against postgresql and h2)
- Redis
- Infinispan

Job loaders that support the job inheritance proposal and a simple api
and utilities to allow user defined loaders to also support job
inheritance. Built in loaders support:
- XML
- Fluent style

JMX job management.

## Experimental things that don't work yet

_Guice Injector_

- @javax.inject.Inject does not allow null injection of batch properties
- Module provider system probably won't work with a real guice project

_Infinispan Transport_

- Doesn't pass the TCK yet as one of the TCK classes `ExternalizableString`
  is not actually `Externalizable`.
- There is also an issue when calling JobOperator#stop() where the runtime
  reports that the process has stopped before it actually has which provides
  false results.

## Building

Run `mvn clean install`

## Run the tests

Copy [test.template.properties](test.template.properties) to `test.properties`. 

You will have to have both postgresql and redis installed and running. These
are both configured in `test.properties`.

Run `mvn clean install -Ptest`.

## Running TCK Tests from failsafe

To run the TCK from within the build you will need to get the sources
of the TCK with:

` git clone git://java.net/jbatch~jsr-352-git-repository <target>`

Copy [test.template.properties](test.template.properties)
to `test.properties` and set `tck.source` to the directory you checked
the sources out at (`<target>` in the above command).

Copy [chainlink-tck.template.properties](tck/chainlink-tck.template.properties)
to `chainlink-tck.properties` though it is only used for transports
running on two JVM's.

Create the directories `/var/run/chainlink` and `/var/log/chainlink`
and make sure the user running the tests has write permissions to them.

Maven will copy the chainlink daemon and relevant test jars into a
directory specified by the property `tck.work.dir` in `test.properties`
(/tmp by default), start it before running the tests and stop it afterwards.

If the maven process is killed before shutting down the second process
you might need to shut it down manually, which you can do by running
`<tck.work.dir>/chainlink-<version>/bin/chainlink -k`.

To run the TCK you must select an injector via a maven profile (there is
no default) and you may select an alternate  repository (an in memory
repository will be used by default) and/or an alternate transport.

Run `mvn clean install -Ptck -Pin-x -Pre-x -Ptr-x` where the in-x, re-x
and tr-x are the profiles of injector, repository and transport you
wish to use. You can see the available tck profiles in [the tck modules pom](tck/pom.xml).

An example minimum command to run the TCK is `mvn clean install -Ptck -Pin-cdi`.

## Remote transports

Some transports are designed to run on multiple JVM's, one owned by
failsafe and one other chainlink process. You can configure the second
process in two ways.

Primarily, you can provide options to the java process running the tck
by setting the environment variable `CHAINLINK_OPTS`. For example, to
enable debugging on both the first and the second process you could
use:

>     CHAINLINK_OPTS="-server -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5006" \
>         mvn clean install \
>             -Ptck -Pin-cdi -Ptr-<x> \
>             -Dmaven.failsafe.debug

Properties specified in the file `chainlink-tck.properties` will also
be provided to the chainlink daemon.

## Infinispan and JGroups Tests

Make sure `tck.source` and `tck.work.dir` are set in `test.properties`.
You will also need to add this route:

`sudo route add -net 224.0.0.0 netmask 240.0.0.0 dev lo`

And allow jgroups udp traffic:

`sudo iptables -A INPUT -i lo -d 224.0.0.0/4 -j ACCEPT`

The background process should have the following properties passed to
it, either through the `CHAINLINK_OPTS` environment variable or
`chainlink-tck.properties`:

>     jgroups.bind_address=127.0.0.1
>     java.net.preferIPv4Stack=true

Failsafe should also be provided the same properties. To execute the
tests you can run something like this (though any injector and
repository profile can be used):

`mvn clean install -Ptck -Pin-cdi -Ptr-infinispan -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true`

## License

[Apache 2.0](LICENSE.txt)
