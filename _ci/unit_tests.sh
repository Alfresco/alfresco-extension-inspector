#!/bin/bash -e

echo "=========================== Starting Unit Test Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"


mvn -B -U clean install


popd
set +vex
echo "=========================== Finishing Unit Test Script =========================="