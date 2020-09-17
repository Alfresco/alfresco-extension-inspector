#!/usr/bin/env basARTIFACT_ANALYSERh

echo "========================== Starting Prepare Staging Deploy Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

export ARTIFACT_INVENTORY="$(find . -name "alfresco-extension-inspector-inventory-*-application.jar" -printf "%f\n" | head -1)"
export ARTIFACT_ANALYSER="$(find . -name "alfresco-extension-inspector-analyser-*-application.jar" -printf "%f\n" | head -1)"

# Identify latest annotated tag (latest version)
export VERSION=$(git describe --abbrev=0 --tags)

mkdir -p deploy_dir

# Download the WhiteSource report
mvn -B org.alfresco:whitesource-downloader-plugin:inventoryReport \
    -N \
    "-Dorg.whitesource.product=alfresco-extension-inspector" \
    -DsaveReportAs=deploy_dir/3rd-party.xlsx

# Hard-link the JAR artifacts into "deploy_dir"
ln "extension-inspector-inventory/target/${ARTIFACT_INVENTORY}" "deploy_dir/${ARTIFACT_INVENTORY}"
ln "extension-inspector-analyser/target/${ARTIFACT_ANALYSER}"   "deploy_dir/${ARTIFACT_ANALYSER}"

echo "Local deploy directory content:"
ls -lA deploy_dir

popd
set +vex
echo "========================== Finishing Prepare Staging Deploy Script =========================="
