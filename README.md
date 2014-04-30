# Chainlink

A JSR352 implementation.

## License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Running TCK Tests from surefire

To run the TCK from within the build you will need to get the sources of the TCK with

` git clone git://java.net/jbatch~jsr-352-git-repository <target>`

Then copy `test.template.properties` to `test.properties` and set `tck.source` to
the directory you checked the sources out at (`<target>` in the above command).

## Infinispan and JGroups Tests

Make sure the relevant properties are set in `test.properties`. You will
also need to add this route:

`sudo route add -net 224.0.0.0 netmask 240.0.0.0 dev lo`

And allow jgroups udp traffic:

`sudo iptables -A INPUT -i lo -d 224.0.0.0/4 -j ACCEPT`
