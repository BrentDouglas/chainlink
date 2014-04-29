#!/bin/bash
#
# @author Brent Douglas <brent.n.douglas@gmail.com>
#

CLASSPATH="$(find lib -regex '.*\.jar$' -type f -printf lib/%f:)$(find dep -regex '.*\.jar$' -type f -printf dep/%f:)"

java -cp ${CLASSPATH} io.machinecode.chainlink.server.chainlinkd.Chainlinkd --configuration cnf/chainlink.xml ${@}