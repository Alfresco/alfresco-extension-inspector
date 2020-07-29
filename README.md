# amp-a-lyser

**amp-a-lyser** is a tool that scans and validates an Alfresco extension (amp or jar) against an `alfresco.war` file.

The tool parses an extension and generates a report on possible overrides, discouraged usage of non-public API, Alfresco's 3rd-party libraries.

The tools has two modules, one for parsing the war files - the **Inventory**, and one for analysing custom extensions against the inventory - the **Analyser**.

## Inventory

The `InventoryApplication` is a Spring Boot application, implemented in the module **alfresco-ampalyser-inventory**.
The application generates a report file in json format for a war file.
  
Use `mvn clean package` to build the project.
This creates an executable jar, `alfresco-ampalyser-inventory-<version>-application.jar`.

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
This creates an executable jar, `alfresco-ampalyser-analyser-<version>-application.jar`.

### Usage:
```shell script
# Analyse a given Alfresco extension
java -jar alfresco-ampalyser-analyser-<version>-application.jar <extension-filename> [--target-version=6.1.0[-7.0.0] | --target-inventory =/path/to/war_inventory.json] [--verbose=[true | false]]

# Help command
java -jar alfresco-ampalyser-analyser-<version>-application.jar --help

# List all versions with bundled inventories
java -jar alfresco-ampalyser-analyser-<version>-application.jar --list-known-alfresco-versions
```
Options:
```bash
   --target-version                     An Alfresco version or a range of Alfresco versions.
   --target-inventory                   A file path of an existing WAR inventory.
   --verbose                            Verbose output.
   
   --help                               Shows this screen.
   --list-known-alfresco-versions       Lists all Alfresco versions with inventory reports included in the tool.
```

### Output
When running the analysing command, **Amp-a-lyser** writes the conflicts directly to the console, grouped by their type.

The conflict types that can be detected by **Amp-a-lyser** are the following:
* File overwrites (`FILE_OVERWRITE`)
* Bean overwrites (`BEAN_OVERWRITE`)
* Classpath conflicts (`CLASSPATH_CONFLICT`)
* Beans instantiating restricted classes (`BEAN_RESTRICTED_CLASS`)
* Usage of non @AlfrescoPublicAPI classes (`CUSTOM_CODE`)
* Usage of 3rd party libraries (`WAR_LIBRARY_USAGE`)

Example of output:
```text
Across the provided target versions, the following number of conflicts have been found:
╔═════════════════════╤═════╗
║REPORT SUMMARY       │     ║
╠═════════════════════╪═════╣
║Type                 │Total║
╟─────────────────────┼─────╢
║BEAN_OVERWRITE       │1    ║
╟─────────────────────┼─────╢
║BEAN_RESTRICTED_CLASS│2    ║
╟─────────────────────┼─────╢
║CUSTOM_CODE          │6    ║
╟─────────────────────┼─────╢
║WAR_LIBRARY_USAGE    │4    ║
╟─────────────────────┼─────╢
║                     │     ║
╚═════════════════════╧═════╝


╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║BEAN_OVERWRITE CONFLICTS                                                                                                                                          ║
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║Found bean overwrites. Spring beans defined by Alfresco are a fundamental building block of the repository, and must not be overwritten unless explicitly allowed.║
║The following beans overwrite default functionality:                                                                                                              ║
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝



╔══════════════════════════════════╤═════════════════════════════════════════════════════════════════════════════╤═════════════════════════════════════════════════════════════════════╗
║Extension Bean Resource ID        │Extension Defining Objects                                                   │WAR Defining object                                                  ║
╠══════════════════════════════════╪═════════════════════════════════════════════════════════════════════════════╪═════════════════════════════════════════════════════════════════════╣
║extension_bean                    │config/alfresco/module/org.alfresco.module.x/module-context.xml              │alfresco/war-context.xml@WEB-INF/lib/some-war-lib.jar                ║
╚══════════════════════════════════╧═════════════════════════════════════════════════════════════════════════════╧═════════════════════════════════════════════════════════════════════╝



╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║BEAN_RESTRICTED_CLASS CONFLICTS                                                                                             ║
╠════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║Found beans that instantiate internal classes.                                                                              ║
║The following beans instantiate classes from Alfresco or 3rd party libraries which must not be instantiated by custom beans:║
╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝


╔═══════════════════════════════════════════════════╤══════════════════════════════════════════════════════════════════════════╗
║Extension Bean Resource ID                         │Restricted Class                                                          ║
╠═══════════════════════════════════════════════════╪══════════════════════════════════════════════════════════════════════════╣
║org_alfresco_module_x_SomeClass                    │org.alfresco.web.config.WebClientConfigBootstrap                          ║
╟───────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────╢
║org_alfresco_module_x_AnotherClass                 │org.alfresco.web.config.WebClientConfigBootstrap                          ║
╚═══════════════════════════════════════════════════╧══════════════════════════════════════════════════════════════════════════╝



╔═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║CUSTOM_CODE CONFLICTS                                                                                                                                                                                                                                        ║
╠═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║Found usage of internal classes. Alfresco provides a Java API that is clearly marked as @AlfrescoPublicAPI. Any other classes or interfaces in the repository are considered our internal implementation detail and might change or even disappear in service║
║packs and new versions without prior notice.                                                                                                                                                                                                                 ║
║The following classes use internal Alfresco classes:                                                                                                                                                                                                         ║
╚═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝


╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║Extension Resource ID using Custom Code                                                                       ║
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║/org/alfresco/module/blogIntegration/ui/AnExtensionClass.class@/lib/extension-lib.jar                         ║
╟──────────────────────────────────────────────────────────────────────────────────────────────────────────────╢
║[...]                                                                                                         ║
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝



╔═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║WAR_LIBRARY_USAGE CONFLICTS                                                                                                                                                                                                                                  ║
╠═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║Found 3rd party library usage. Although this isn't an immediate problem, all 3rd party libraries that are delivered with the repository are considered as our internal implementation detail. These libraries will change or may be removed in future service║
║packs without notice.                                                                                                                                                                                                                                        ║
║The following classes use 3rd party libraries:                                                                                                                                                                                                               ║
╚═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝


╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
║Extension Resource ID using 3rd Party library code                                                            ║
╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
║/org/alfresco/module/blogIntegration/ui/AnExtensionClass.class@/lib/extension-lib.jar                         ║
╟──────────────────────────────────────────────────────────────────────────────────────────────────────────────╢
║[...]                                                                                                         ║
╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝



(use option --verbose for version details)
```

### Implementation details

Alfresco extensions might hide conflicts of types `BEAN_RESTRICTED_CLASS`, `WAR_LIBRARY_USAGE` and `CUSTOM_CODE` if they contain Alfresco specific libraries.

That's because the aforementioned types of conflicts exclude from processing all the classes in the extension's classpath.

**Note:**

Including in the processing a class present in both extension and war would partially solve the issue because:
1. Two classes with the same canonical name could come from two different libraries, e.g. an extension specific library and an Alfresco one, or two different versions of the same Alfresco library. Thus checking the class name is not enough.
2. Comparing their libraries would help only when the same library with the same version is used in both the extension and the war. In case of different versions of the same library, the class won't be recognized as Alfresco internal class.

## Build and release process

For a complete walk-through check out the
[build-and-release-101.MD](docs/build-and-release-101.md)
under the `docs` folder.
