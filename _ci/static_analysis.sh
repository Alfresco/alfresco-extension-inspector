#!/usr/bin/env bash

echo "=========================== Starting Static Analysis Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

# Run in a sandbox for every branch, run normally on master
[ "${TRAVIS_BRANCH}" != "master" ] && RUN_IN_SANDBOX="-sandboxname Alfresco_Extension_Inspector" || RUN_IN_SANDBOX=""

java -jar vosp-api-wrappers-java-$VERACODE_WRAPPER_VERSION.jar -vid $VERACODE_API_ID \
     -vkey $VERACODE_API_KEY -action uploadandscan -appname "Alfresco Extension Inspector" \
     ${RUN_IN_SANDBOX} -createprofile false \
     -filepath extension-inspector-analyser/target/alfresco-extension-inspector-analyser-*.jar \
     extension-inspector-commons/target/alfresco-extension-inspector-commons-*.jar \
     extension-inspector-inventory/target/alfresco-extension-inspector-inventory-*.jar \
     -version "$TRAVIS_JOB_ID - $TRAVIS_JOB_NUMBER" -scantimeout 3600

popd
set +vex
echo "=========================== Finishing Static Analysis Script =========================="