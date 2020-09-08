# alfresco-extension-inspector

**alfresco-extension-inspector** is a tool that scans and validates an Alfresco extension (amp or jar) against an `alfresco.war` file.

The tool parses an extension and generates a report on possible overrides, discouraged usage of non-public API, Alfresco's 3rd-party libraries.

The tools has two modules, one for parsing the war files - the **Inventory**, and one for analysing custom extensions against the inventory - the **Analyser**.

## Inventory

The `InventoryApplication` is a Spring Boot application, implemented in the module **alfresco-extension-inspector-inventory**.
The application generates a report file in json format for a war file.
  
Use `mvn clean package` to build the project.
This creates an executable jar, `alfresco-ampalyser-inventory-<version>-application.jar`.

### Usage:
```shell script
java -jar alfresco-extension-inspector-inventory-<version>-application.jar <alfresco_war_path> [--o=<report_file_path>]
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
         "deprecated" : false,
         "implicit" : false
        },
        {
         "type" : "ALFRESCO_PUBLIC_API",
         "id" : "package.ClassName2",
         "deprecated" : true,
         "implicit" : true
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

The `AnalyserApplication` is a Spring Boot application, implemented in the module **alfresco-extension-inspector-analyser**.
This tool analyses custom extensions against war inventories.

Use `mvn clean package` to build the project.
This creates an executable jar, `alfresco-extension-inspector-analyser-<version>-application.jar`.

### Usage:
```shell script
# Analyse a given Alfresco extension
java -jar alfresco-extension-inspector-analyser-<version>-application.jar <extension-filename> [--target-version=6.1.0[-7.0.0] | --target-inventory =/path/to/war_inventory.json] [--verbose=[true | false]]

# Help command
java -jar alfresco-extension-inspector-analyser-<version>-application.jar --help

# List all versions with bundled inventories
java -jar alfresco-extension-inspector-analyser-<version>-application.jar --list-known-alfresco-versions
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
When running the analysing command, **alfresco-extension-inspector** writes the conflicts directly to the console, grouped by their type.

The conflict types that can be detected by **alfresco-extension-inspector** are the following:
* File overwrites (`FILE_OVERWRITE`)
* Bean overwrites (`BEAN_OVERWRITE`)
* Classpath conflicts (`CLASSPATH_CONFLICT`)
* Beans instantiating restricted classes (`BEAN_RESTRICTED_CLASS`)
* Usage of non @AlfrescoPublicAPI classes (`ALFRESCO_INTERNAL_USAGE`)
* Usage of 3rd party libraries (`WAR_LIBRARY_USAGE`)

Example of output:
```text
Bean naming conflicts
---------------------
The following Spring beans defined in the extension module are in conflict with beans defined in the ACS repository:
    extension_bean
Spring beans are the basic building blocks of the ACS repository. Replacing these will alter the behaviour of the system and can lead to unexpected behaviour.
Since all these beans are subject to change between Alfresco versions and even in service packs, these modifications are typically bound to a specific Alfresco version.
You should avoid redefining default beans of the ACS repository in your extensions to reduce the cost of upgrades.
It is possible that these conflicts only exist in specific ACS versions. Run this tool with the -verbose option to get a complete list of versions where each of these files has conflicts.

Beans instantiating internal classes
------------------------------------
The following Spring beans defined in the extension module instantiate internal classes:
    extension_bean (class=org.alfresco.repo.... )

These classes are considered an internal implementation detail of the ACS repository and do not constitute a supported extension point. They might change or completely disappear between ACS versions and even in service packs.

Classpath conflicts
-------------------
The following files and libraries in the extension module cause conflicts on the Java classpath:
    /lib/alfresco-test.jar

Ambiguous resources on the Java classpath render the behaviour of the JVM undefined (see Java specification).
Although it might be possible that the repository can still start-up, you can expect erroneous behavior in certain situations. Problems of this kind are typically very hard to detect and trace back to their root cause.
It is possible that these conflicts only exist in specific ACS versions. Run this tool with the -verbose option to get a complete list of versions where each of these files has conflicts.

Custom code using internal classes
----------------------------------
The following classes defined in the extension module are using internal repository classes:
    org.alfresco.repo...

Internal repository classes:
    org.alfresco.repo...

These classes are considered an internal implementation detail of the ACS repository and might change or completely disappear between ACS versions and even between service packs.
For a complete usage matrix, use the -verbose option of this tool.

Custom code using 3rd party libraries managed by the ACS repository
-------------------------------------------------------------------
The code provided by the extension module is using these 3rd party libraries brought by the ACS repository:
    /WEB-INF/lib/test-1.0.0.jar

These 3rd party libraries are managed by the ACS repository and are subject to constant change, even in service packs and hotfixes.
Each of these libraries has its own backward compatibility strategy, which will make it really hard for this extension to keep up with these changes.

REPORT SUMMARY
Across the provided target versions, the following number of conflicts have been found:
+-----------------------+-----+
|Type                   |Total|
+-----------------------+-----+
|BEAN_OVERWRITE         |1    |
+-----------------------+-----+
|BEAN_RESTRICTED_CLASS  |1    |
+-----------------------+-----+
|CLASSPATH_CONFLICT     |1    |
+-----------------------+-----+
|ALFRESCO_INTERNAL_USAGE|2    |
+-----------------------+-----+
|WAR_LIBRARY_USAGE      |1    |
+-----------------------+-----+

(use option --verbose for version details)
```

### Implementation details

Alfresco extensions might hide conflicts of types `BEAN_RESTRICTED_CLASS`, `WAR_LIBRARY_USAGE` and `ALFRESCO_INTERNAL_USAGE` if they contain Alfresco specific libraries.

That's because the aforementioned types of conflicts exclude from processing all the classes in the extension's classpath.

**Note:**

Including in the processing a class present in both extension and war would partially solve the issue because:
1. Two classes with the same canonical name could come from two different libraries, e.g. an extension specific library and an Alfresco one, or two different versions of the same Alfresco library. Thus checking the class name is not enough.
2. Comparing their libraries would help only when the same library with the same version is used in both the extension and the war. In case of different versions of the same library, the class won't be recognized as Alfresco internal class.

## Build and release process

For a complete walk-through check out the
[build-and-release-101.MD](docs/build-and-release-101.md)
under the `docs` folder.
