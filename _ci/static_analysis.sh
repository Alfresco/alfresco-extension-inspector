#!/usr/bin/env bash

echo "=========================== Starting Static Analysis Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

# Run in a sandbox for every branch, run normally on master
[ "${TRAVIS_BRANCH}" != "master" ] && RUN_IN_SANDBOX="-sandboxname Amp-a-lyser" || RUN_IN_SANDBOX=""

java -jar vosp-api-wrappers-java-$VERACODE_WRAPPER_VERSION.jar -vid $VERACODE_API_ID \
     -vkey $VERACODE_API_KEY -action uploadandscan -appname "Amp-a-lyser" \
     ${RUN_IN_SANDBOX} -createprofile false \
     -filepath alfresco-ampalyser-analyser/target/alfresco-ampalyser-analyser-*.jar \
     alfresco-ampalyser-commons/target/alfresco-ampalyser-commons-*.jar \
     alfresco-ampalyser-inventory/target/alfresco-ampalyser-inventory-*.jar \
     -version "$TRAVIS_JOB_ID - $TRAVIS_JOB_NUMBER" -scantimeout 3600

popd
set +vex
echo "=========================== Finishing Static Analysis Script =========================="