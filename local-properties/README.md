# Local properties file

This sample shows how you can use a properties file with values specific to developer machine (`local.properties`)

## Problem ##
Configuration cache entry should be invalidated when value used at configuration time is changed.

## Possible solutions ##

### Invalidate entry on file change
Use the [whole properties file](https://docs.gradle.org/current/javadoc/org/gradle/api/provider/ProviderFactory.html#fileContents-org.gradle.api.file.RegularFile-) as an input for configuration cache entry.

```kotlin
val localPropertiesContents: String = providers.fileContents(rootProject.layout.projectDirectory.file("local.properties")).asText.forUseAtConfigurationTime().get()
...
```

**Pros:**
- Easy
- Entry will be invalidated when `local.properties` file is changed

**Cons:**
- Entry will be invalidated even if property that isn't required for requested tasks is changed

###  Invalidate entry on required properties change ###
Create your own input that will check only required local properties.

This approach is demonstrated in the sources. Most of the logic is located at [buildSrc/src/main/kotlin/properties.kt](buildSrc/src/main/kotlin/properties.kt)

You can run task `printMyProp`: it captures `myProp` value 
from `local.properties` file (that should be created manually) at configuration time and then prints 
the value at execution time. Yeah, it looks like overengineering, but it's an example, isn't it?

**Pros:**
- Entry will be invalidated only when required for configuration properties are changed

**Cons:**
- The solution requires more thinking