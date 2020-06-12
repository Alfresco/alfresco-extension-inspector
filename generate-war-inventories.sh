#!/usr/bin/env bash
# On MacOS run the script through the Homebrew bash version: "$ /usr/local/bin/bash generate-war-inventory.sh"

pushd "$(dirname "${BASH_SOURCE[0]}")"

# Wars and their S3 locations:

declare -A WAR_S3_LOCATIONS=(
["6.2.0"]="https://s3-eu-west-1.amazonaws.com/eu.dl.alfresco.com/release/enterprise/ACS/6.2/6.2.0/5/alfresco.war"
["6.2.1"]="https://s3-eu-west-1.amazonaws.com/eu.dl.alfresco.com/release/enterprise/ACS/6.2/6.2.1/2500/alfresco.war"
)

echo "S3 WARs:"
for k in "${!WAR_S3_LOCATIONS[@]}" ; do
  echo "${k} -> ${WAR_S3_LOCATIONS[$k]}"
done

mkdir -p "wars/"

# Won't work without AWS Authorization header
#echo "Downloading S3 WARs:"
#for k in "${!WAR_S3_LOCATIONS[@]}"; do
#  if [ ! -f "wars/${k}.war" ]; then
#    curl -s -S "${WAR_S3_LOCATIONS[$k]}" -o "wars/${k}.war"
#  fi
#done

echo "Rebuilding project..."
mvn clean install -DskipTests &>/dev/null || exit 1

JAR_EXECUTABLE=$(find . -name 'alfresco-ampalyser-inventory*application.jar' | head -1)
JAR_EXECUTABLE="${JAR_EXECUTABLE:-$(find . -name 'alfresco-ampalyser-inventory*.jar' | head -1)}"

echo "Inventory tool executable jar: ${JAR_EXECUTABLE}"

INVENTORY_DIR="alfresco-ampalyser-analyser/src/main/resources/bundled-inventories"
echo "Inventory dir: ${INVENTORY_DIR}"
rm ${INVENTORY_DIR}/*.json

for file in wars/*.war ; do
  version=${file#"wars/"}
  version=${version%".war"}
  echo "Generating report for version [${version}]..."
  java -jar "${JAR_EXECUTABLE}" "${file}" --o "${INVENTORY_DIR}/${version}.json"
done

popd
exit 0
