#!/bin/bash


find ~/.m2/repository  -type d -name '*-SNAPSHOT*' | xargs -r -l rm -rf
mvn clean install -DskipTests
pushd alfresco-ampalyser-analyser/target
cp ${HOME}/work/github.com/Alfresco/media-management/alfresco-mm-repo-parent/alfresco-mm-repo/target/amps/alfresco-mm-repo-*.amp ./
time java -jar alfresco-ampalyser-analyser-*-SNAPSHOT-application.jar alfresco-mm-repo-*.amp --verbose

popd

wall "Done!"

exit 0
