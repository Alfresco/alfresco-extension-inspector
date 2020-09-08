#!/usr/bin/env bash
# On MacOS run the script through the Homebrew bash version: "$ /usr/local/bin/bash generate-war-inventory.sh"

pushd "$(dirname "${BASH_SOURCE[0]}")" || exit 3

if [ ! -d "wars" ] ; then
    echo "Missing \"$(pwd)/wars/\" directory. Exiting..."
    exit 1
fi

if ! ls wars/*.war &>/dev/null ; then
    echo "Directory \"$(pwd)/wars/\" contains no wars. Exiting..."
    exit 2
fi

echo "Rebuilding project..."
mvn clean install -DskipTests &>/dev/null || exit 1

JAR_EXECUTABLE=$(find . -name 'alfresco-extension-inspector-inventory*application.jar' | head -1)
JAR_EXECUTABLE="${JAR_EXECUTABLE:-$(find . -name 'alfresco-extension-inspector-inventory*.jar' | head -1)}"

echo "Inventory tool executable jar: ${JAR_EXECUTABLE}"

INVENTORY_DIR="alfresco-ampalyser-analyser/src/main/resources/bundled-inventories"
echo "Inventory dir: ${INVENTORY_DIR}"
rm ${INVENTORY_DIR}/*.json

for file in wars/*.war ; do
  version=${file#"wars/"}
  version=${version%".war"}
  echo "Generating report for version [${version}]..."
  java -jar "${JAR_EXECUTABLE}" "${file}" --o="${INVENTORY_DIR}/${version}.json"

  #mv -f "${version}.inventory.json" "${INVENTORY_DIR}/${version}.json"
done

popd || exit 4
exit 0
