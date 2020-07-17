#!/usr/bin/env bash

echo "=========================== Starting Static Analysis Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

# Run in a sandbox for every branch, run normally on master
[ "${TRAVIS_BRANCH}" != "master" ] && RUN_IN_SANDBOX="-sandboxname AIS_Analysis" || RUN_IN_SANDBOX=""

java -jar vosp-api-wrappers-java-$VERACODE_WRAPPER_VERSION.jar -vid $VERACODE_API_ID \
     -vkey $VERACODE_API_KEY -action uploadandscan -appname "Intelligence Service" \
     ${RUN_IN_SANDBOX} -createprofile false \
     -filepath alfresco-ai-analysis-model/target/alfresco-ai-analysis-model-*.jar \
     alfresco-ai-analysis-shared-services/target/alfresco-ai-analysis-shared-services-*.jar \
     alfresco-ai-image-analysis/target/alfresco-ai-image-analysis-*.jar \
     alfresco-ai-image-text-analysis/target/alfresco-ai-image-text-analysis-*.jar \
     alfresco-ai-text-analysis/target/alfresco-ai-text-analysis-*.jar \
     -version "$TRAVIS_JOB_ID - $TRAVIS_JOB_NUMBER" -scantimeout 3600

popd
set +vex
echo "=========================== Finishing Static Analysis Script =========================="