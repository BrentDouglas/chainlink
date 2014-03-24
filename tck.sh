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
run-test -Pin-all

# Infinispan
run-test -Pin-all -Pre-infinispan

# Jpa
run-test -Pin-all -Pre-jpa
run-test -Pin-all -Pre-jpa -Pdb-postgresql

# Jdbc
run-test -Pin-all -Pre-jdbc
run-test -Pin-all -Pre-jdbc -Pdb-postgresql

# Redis
run-test -Pin-all -Pre-redis

if [ $RETVAL -eq 0 ]; then
  echo -e "[${GREEN}OK${RESET}]"
else
  echo -e "[${RED}FAILED${RESET}] Failed profiles were: ${FAILED}"
fi
