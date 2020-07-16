# amp-a-lyser

**amp-a-lyser** is a tool that scans and validates an Alfresco extension (amp or jar) against an `alfresco.war` file.

The tool parses an extension and generates a report on possible overrides, discouraged usage of non-public API, Alfresco's 3rd-party libraries.

The tools has two modules, one for parsing the war files - the **Inventory**, and one for analysing custom extensions against the inventory - the **Analyser**.

## Inventory

The `InventoryApplication` is a Spring Boot application, implemented in the module **alfresco-ampalyser-inventory**.
The application generates a report file in json format for a war file.
  
Use `mvn clean package` to build the project.
This creates an executable jar, `alfresco-ampalyser-inventory-0.0.1-SNAPSHOT-application.jar`.

### Usage:
```shell script
java -jar alfresco-ampalyser-inventory-<version>-application.jar <alfresco_war_path> [--o=<report_file_path>]
```
- the first parameter is a path to a valid war file
- the optional `--o` parameter is for the output of the report, a given file or a folder location where a report with the default name, `<war_name>.inventory.json`, is generated. 

### Output
Example structure of the report:
```json
{
  "schemaVersion" : "1.0",
  "alfrescoVersion" : "6.2.1",
  "resources" : 
    {
      "ALFRESCO_PUBLIC_API" : 
      [ 
        {
         "type" : "ALFRESCO_PUBLIC_API",
         "id" : "package.ClassName1",
         "deprecated" : false
        },
        {
         "type" : "ALFRESCO_PUBLIC_API",
         "id" : "package.ClassName2",
         "deprecated" : true
        },
        ...
      ],
      "CLASSPATH_ELEMENT" : 
      [
        {
          "type" : "CLASSPATH_ELEMENT",
          "id" : "org/alfresco/package1/AClass.class",
          "definingObject" : "WEB-INF/lib/alfresco-library.jar"
        },
        {
          "type" : "CLASSPATH_ELEMENT",
          "id" : "com/3rdparty/packageA/AClass.class",
          "definingObject" : "WEB-INF/lib/3rdparty-library.jar"
        },
        ...
      ],
      "BEAN" : 
      [
        {
         "type" : "BEAN",
         "id" : "beanName",
         "definingObject" : "alfresco/aContext.xml@WEB-INF/lib/alfresco-library.jar"
        },
       ...
      ],
      "FILE" : 
      [
        {
          "type" : "FILE",
          "id" : "WEB-INF/classes/aFile.ext",
          "definingObject" : "WEB-INF/classes/aFile.ext"
        },
        {
          "type" : "FILE",
          "id" : "WEB-INF/lib/aLibrary.jar",
          "definingObject" : "WEB-INF/lib/aLibrary.jar"
        },
        ...
      ]
}
```

## Analyser

The `AnalyserApplication` is a Spring Boot application, implemented in the module **alfresco-ampalyser-analyser**.
This tool analyses custom extensions against war inventories.

Use `mvn clean package` to build the project.
This creates an executable jar, `alfresco-ampalyser-analyser-0.0.1-SNAPSHOT-application.jar`.

### Usage:
```shell script
# Analyse a given Alfresco extension
java -jar alfresco-ampalyser-analyser-<version>-application.jar <extension-filename> [--target-version=6.1.0[-7.0.0] | --target-inventory =/path/to/war_inventory.json] [--beanOverrideWhitelist=/path/to/bean_overriding_whitelist.json ] [--beanClassWhitelist=/path/to/bean_restricted_classes_whitelist.json] [--verbose=[true | false]]

# Help command
java -jar alfresco-ampalyser-analyser-<version>-application.jar --help

# List all versions with bundled inventories
java -jar alfresco-ampalyser-analyser-<version>-application.jar --list-known-alfresco-versions
```
Options:
```bash
   --target-version                     An Alfresco version or a range of Alfresco versions.
   --target-inventory                   A file path of an existing WAR inventory.
   --beanOverrideWhitelist              A file path of a JSON containing a list of beans that can be overridden.
   --beanClassWhitelist                 A file path of a JSON containing a list of classes that can be instantiated.
   --verbose                            Verbose output.
   
   --help                               Shows this screen.
   --list-known-alfresco-versions       Lists all Alfresco versions with inventory reports included in the tool.
```

### Output
When running the analysing command, **Amp-a-lyser** writes the conflicts directly to the console, grouped by their type.

Example:
```text
Found bean overwrites! Spring beans defined by Alfresco constitute a fundamental building block of the repository and must not be overwritten unless explicitly allowed. Found the following beans overwriting default Alfresco functionality:

aService_security defined in alfresco/extension-services-context.xml@lib/extension-lib.jar in conflict with bean defined in alfresco/services-context.xml@WEB-INF/lib/war-lib.jar

aService_transaction defined in alfresco/extension-services-context.xml@lib/extension-lib.jar in conflict with bean defined in alfresco/services-context.xml@WEB-INF/lib/war-lib.jar

[...]

-------------------------------------------------------------------

Found classpath conflicts! Although it might be possible to install this extension, its behaviour is undefined. The following resources in your extension are in conflict with resources on the classpath in the Alfresco repository:

Multiple resources in /lib/extension-lib.jar conflicting with /WEB-INF/lib/war-lib.jar

[...]

-------------------------------------------------------------------

[...]

-------------------------------------------------------------------

(use option --verbose for version details)
```

## Build and release process

For a complete walk-through check out the
[build-and-release-101.MD](docs/build-and-release-101.md)
under the `docs` folder.
