#!/usr/bin/env bash

echo "========================== Starting Prepare Release Deploy Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

# Identify latest annotated tag (latest version)
export VERSION=$(git describe --abbrev=0 --tags)

mkdir -p deploy_dir

# Download the WhiteSource report
mvn -B org.alfresco:whitesource-downloader-plugin:inventoryReport \
    -N \
    "-Dorg.whitesource.product=alfresco-extension-inspector" \
    -DsaveReportAs=deploy_dir/3rd-party.xlsx

# Download the JAR artifacts
mvn -B org.apache.maven.plugins:maven-dependency-plugin:3.1.1:copy \
    -Dartifact=org.alfresco.extension-inspector:alfresco-extension-inspector-inventory:${VERSION}:jar:application \
    -DoutputDirectory=deploy_dir

mvn -B org.apache.maven.plugins:maven-dependency-plugin:3.1.1:copy \
    -Dartifact=org.alfresco.extension-inspector:alfresco-extension-inspector-analyser:${VERSION}:jar:application \
    -DoutputDirectory=deploy_dir

echo "Local deploy directory content:"
ls -lA deploy_dir

popd
set +vex
echo "========================== Finishing Prepare Release Deploy Script =========================="
