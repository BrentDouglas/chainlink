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
run-test -Pall

# Jpa
run-test -Pall -Pjpa
run-test -Pall -Pjpa -Ppostgresql

# Jdbc
run-test -Pall -Pjdbc
run-test -Pall -Pjdbc -Ppostgresql

if [ $RETVAL -eq 0 ]; then
  echo -e "[${GREEN}OK${RESET}]"
else
  echo -e "[${RED}FAILED${RESET}] Failed profiles were: ${FAILED}"
fi
