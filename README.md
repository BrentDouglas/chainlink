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
and utilitied to allow user defined loaders to also support job
inheritance. Built in loaders support:
- XML
- Fluent style

JMX job Management.

## Experimental things that don't work yet

_Guice Injector_

- @javax.inject.Inject does not allow null injection of batch properties
- Module provider system probably won't work with a real guice project

_Infinispan Transport_

- Doesn't work at all currently, Chain#await() promises are resolved
  before the chain has completely terminated.
- Need to consider implications of network and repository writes.

## Building

Run `mvn clean install`

## Run the tests

Copy `test.template.properties` to `test.properties`. 

You will have to have both postgresql and redis installed and running
and redis installed and running. These are both configured in
`test.properties`.

Run `mvn clean install -Ptest`.

## Running TCK Tests from failsafe

To run the TCK from within the build you will need to get the sources
of the TCK with:

` git clone git://java.net/jbatch~jsr-352-git-repository <target>`

Make sure you have copied `test.template.properties` to `test.properties`
and set `tck.source` to the directory you checked the sources out at
(`<target>` in the above command).

To run the tck you must select an injector via a maven profile (there is
no default) and you may select a repository (an in memory repository
will be used by default).

Run `mvn clean install -Ptck -Pin-x -Pre-x` where the in-x and
re-x are the profiles of injector and repository you wish to use. You
can see the available tck profiles in [the tck modules pom](tck/pom.xml).

## Infinispan and JGroups Tests

Make sure the relevant properties are set in `test.properties`. You will
also need to add this route:

`sudo route add -net 224.0.0.0 netmask 240.0.0.0 dev lo`

And allow jgroups udp traffic:

`sudo iptables -A INPUT -i lo -d 224.0.0.0/4 -j ACCEPT`

Create the directories `/var/run/chainlink` and `/var/log/chainlink`.

Maven will copy the chainlink daemon and relevent test jars into a directory
specified in `test.properties` (/tmp by default), start it before running
the tests and stop it afterwards.

To execute the tests you can run something like this (though any
injector and repository profile can be used):

`mvn clean install -Ptck -Pin-cdi -Ptr-infinispan -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true`

If you kill maven halfway through execution you might need to shut it down
afterwards which you can do by running
`/tmp/chainlink-<version>/bin/chainlink -k`.

## License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
