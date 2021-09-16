import org.gradle.api.Describable
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.util.*

abstract class LocalPropertiesLoader : BuildService<LocalPropertiesLoader.Parameters> {
    interface Parameters : BuildServiceParameters {
        val rootDir: DirectoryProperty
    }

    val localProperties by lazy {
        Properties().apply {
            val localPropertiesFile = parameters.rootDir.file("local.properties").orNull?.asFile
            if (localPropertiesFile != null && localPropertiesFile.exists()) {
                localPropertiesFile.reader().use(::load)
            }
        }
    }
}

abstract class LocalPropertyValueSource : ValueSource<String, LocalPropertyValueSource.Parameters>, Describable {
    interface Parameters : ValueSourceParameters {
        val key: Property<String>
        val localPropertiesLoader: Property<LocalPropertiesLoader>
    }

    override fun obtain() = parameters.localPropertiesLoader.get().localProperties.getProperty(parameters.key.get())

    override fun getDisplayName() = "local property '${parameters.key.get()}'"
}

fun Project.getLocalProperty(key: String) =  providers.of(LocalPropertyValueSource::class.java) {
    parameters.key.set(key)
    parameters.localPropertiesLoader.set(gradle.sharedServices.registerIfAbsent("local-properties", LocalPropertiesLoader::class.java) {
        parameters.rootDir.set(rootProject.projectDir)
    })
}.forUseAtConfigurationTime().orNull
