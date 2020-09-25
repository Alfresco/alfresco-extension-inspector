#!/usr/bin/env bash

echo "========================== Starting Prepare Staging Deploy Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

export ARTIFACT="$(find extension-inspector-packaging/target -name "alfresco-extension-inspector-*.jar" -printf "%f\n" | head -1)"

# Identify latest annotated tag (latest version)
export VERSION=$(git describe --abbrev=0 --tags)

mkdir -p deploy_dir

# Download the WhiteSource report
mvn -B org.alfresco:whitesource-downloader-plugin:inventoryReport \
    -N \
    "-Dorg.whitesource.product=alfresco-extension-inspector" \
    -DsaveReportAs=deploy_dir/3rd-party.xlsx

# Hard-link the JAR artifacts into "deploy_dir"
ln "extension-inspector-packaging/target/${ARTIFACT}" "deploy_dir/${ARTIFACT}"

echo "Local deploy directory content:"
ls -lA deploy_dir

popd
set +vex
echo "========================== Finishing Prepare Staging Deploy Script =========================="
