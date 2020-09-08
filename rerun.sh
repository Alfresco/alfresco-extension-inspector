#!/bin/bash

# This script uses the GNU utilities (not the default MacOS ones)
# Install with: $ brew install coreutils findutils gnu-tar gnu-sed gawk gnutls gnu-indent gnu-getopt grep

# Run with:
# $ bash rerun.sh  alfresco/extension/path.amp

EXTENSION_PATH="${1}"
EXTENSION_NAME=$(basename "${EXTENSION_PATH}")

find "${HOME}/.m2/repository"  -type d -name '*-SNAPSHOT*' | xargs -r -l rm -rf

mvn clean install -DskipTests || exit 1

pushd alfresco-ampalyser-analyser/target || exit 2

cp "${EXTENSION_PATH}" ./

time java -jar alfresco-extension-inspector-analyser-*-SNAPSHOT-application.jar "${EXTENSION_NAME}" --verbose

popd || exit 3

wall "Done!"

exit 0
