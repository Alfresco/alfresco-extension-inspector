#!/usr/bin/env bash
echo "========================== Starting Copy to Release Bucket Script ==========================="
set -ev

export AWS_ACCESS_KEY_ID="${RELEASE_AWS_ACCESS_KEY}"
export AWS_SECRET_ACCESS_KEY="${RELEASE_AWS_SECRET_KEY}"

# Identify latest annotated tag (latest version)
export VERSION="$(git describe --abbrev=0 --tags)"

SOURCE="s3://alfresco-artefacts-staging/enterprise/AlfrescoExtensionInspector/${VERSION}"
DESTINATION="s3://eu.dl.alfresco.com/release/enterprise/AlfrescoExtensionInspector/${VERSION}"

echo "Source:      ${SOURCE}"
echo "Destination: ${DESTINATION}"

aws s3 cp --acl private "${SOURCE}" "${DESTINATION}" --recursive

echo "========================== Finishing Copy to Release Bucket Script =========================="
