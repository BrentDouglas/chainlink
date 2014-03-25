#!/bin/bash
#
# @author Brent Douglas <brent.n.douglas@gmail.com>
#

GREEN="${GREEN-\033[32m}"
RED="${RED-\033[31m}"
RESET="${RESET-\033[00m}"

run-test() {
  local profile="${@}"
  local maven="mvn test -Ptck ${@} -rf :chainlink-tck"
  local ret=0

  echo -e "${GREEN}Running TCK for ${profile}${RESET}"
  echo -e "Command is: ${maven}"
  (
    cd tck
    ${maven}
    ret=${?}
  )
  [ ${ret} -eq 0 ] || {
    echo -e "${RED}Failed TCK for ${profile}${RESET}"
    FAILED+="\n${profile}"
    RETVAL=${ret}
  }

  return ${ret}
}

RETVAL=0
FAILED=""

# Memory
run-test -Pin-batch
run-test -Pin-cdi
run-test -Pin-guice
run-test -Pin-seam
run-test -Pin-spring

# Infinispan
run-test -Pin-batch -Pre-infinispan
run-test -Pin-cdi -Pre-infinispan
run-test -Pin-guice -Pre-infinispan
run-test -Pin-seam -Pre-infinispan
run-test -Pin-spring -Pre-infinispan

# Jpa
run-test -Pin-batch -Pre-jpa
run-test -Pin-cdi -Pre-jpa
run-test -Pin-guice -Pre-jpa
run-test -Pin-seam -Pre-jpa
run-test -Pin-spring -Pre-jpa

run-test -Pin-batch -Pre-jpa -Pdb-postgresql
run-test -Pin-cdi -Pre-jpa -Pdb-postgresql
run-test -Pin-guice -Pre-jpa -Pdb-postgresql
run-test -Pin-seam -Pre-jpa -Pdb-postgresql
run-test -Pin-spring -Pre-jpa -Pdb-postgresql

# Jdbc
run-test -Pin-batch -Pre-jdbc
run-test -Pin-cdi -Pre-jdbc
run-test -Pin-guice -Pre-jdbc
run-test -Pin-seam -Pre-jdbc
run-test -Pin-spring -Pre-jdbc

run-test -Pin-batch -Pre-jdbc -Pdb-postgresql
run-test -Pin-cdi -Pre-jdbc -Pdb-postgresql
run-test -Pin-guice -Pre-jdbc -Pdb-postgresql
run-test -Pin-seam -Pre-jdbc -Pdb-postgresql
run-test -Pin-spring -Pre-jdbc -Pdb-postgresql

# Redis
run-test -Pin-batch -Pre-redis
run-test -Pin-cdi -Pre-redis
run-test -Pin-guice -Pre-redis
run-test -Pin-seam -Pre-redis
run-test -Pin-spring -Pre-redis

if [ $RETVAL -eq 0 ]; then
  echo -e "[${GREEN}OK${RESET}]"
else
  echo -e "[${RED}FAILED${RESET}] Failed profiles were: ${FAILED}"
fi
