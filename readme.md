# Nock

A JSR352 implementation.

## License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Running TCK Tests from surefire

To run the TCK from within the build you will need to get the sources of the TCK with

` git clone git://java.net/jbatch~jsr-352-git-repository <target>`

Then copy `build.properties.template` to `build.properties` and set `tck.source` to
the directory you checked the sources out at (`<target>` in the above command).

You should also copy `tck/tck.properties.template` to `tck/tck.properties`
