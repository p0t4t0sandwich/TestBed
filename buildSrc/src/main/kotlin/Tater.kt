import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.bundling.Jar
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import xyz.wagyourtail.unimined.util.sourceSets
import java.io.File

object Tater

val Project.projectName: String get() = properties["project_name"].toString()
val Project.projectId: String get() = properties["project_id"].toString()
val Project.projectDescription: String get() = properties["project_description"].toString()
val Project.projectLicense: String get() = properties["license"].toString()

val Project.homepageUrl: String get() = properties["homepage_url"].toString()
val Project.issueUrl: String get() = properties["issue_url"].toString()
val Project.sourceUrl: String get() = properties["source_url"].toString()

val Project.apiVersion: String get() = properties["api_version"].toString()
val Project.fabricVersion: String get() = properties["fabric_version"].toString()
val Project.fabricLoaderVersion: String get() = properties["fabric_loader_version"].toString()
val Project.forgeVersion: String get() = properties["forge_version"].toString()
val Project.neoForgeVersion: String get() = properties["neoforge_version"].toString()
val Project.spongeVersion: String get() = properties["sponge_version"].toString()
val Project.loaderVersion: String get() = properties["loader_version"].toString()
val Project.minecraftVersion: String get() = properties["minecraft_version"].toString()

val Project.mappingsChannel: String get() = properties["mapping_channel"].toString()
val Project.mappingsVersion: String get() = properties["mapping_version"].toString()

val Project.javaVersion: String get() = properties["java_version"].toString()

val Project.jarToFiles: (String) -> FileTree get() = {
    val jar: Jar = when (val task = tasks.getByName(it)) {
        is Jar -> task
        is RemapJarTask -> task.asJar
        else -> throw IllegalArgumentException("Task $it is not a Jar task")
    }
    zipTree(jar.archiveFile.get().asFile)
}

val Project.srcSetAsDep: (String, String) -> ConfigurableFileCollection get() = { projName, srcSetName ->
    files(rootProject.project(projName).sourceSets.getByName(srcSetName).output)
}

@Suppress("UNCHECKED_CAST")
val Project.mergeMixinConfigs: (List<File>)  -> FileTree get() = { mixinConfigs ->
    val json = JsonSlurper()
    val mixins = mutableListOf<String>()
    val client = mutableListOf<String>()
    val server = mutableListOf<String>()
    mixinConfigs.forEach { mixinConfig ->
        val mixinJson = json.parse(mixinConfig) as Map<*, *>
        if (mixinJson.contains("mixins")) {
            mixins.addAll(mixinJson["mixins"] as List<String>)
        }
        if (mixinJson.contains("server")) {
            server.addAll(mixinJson["server"] as List<String>)
        }
        if (mixinJson.contains("client")) {
            client.addAll(mixinJson["client"] as List<String>)
        }
    }
    mixins.sortWith { a, b -> a.compareTo(b) }
    client.sortWith { a, b -> a.compareTo(b) }
    server.sortWith { a, b -> a.compareTo(b) }

    val merged = file("./build/tmp/testbed.mixins.json")
    val mixinConfigJson = mutableMapOf(
        "injectors" to mapOf(
            "defaultRequire" to 1,
        ),
        "required" to false,
        "minVersion" to "0.8",
        "plugin" to "dev.neuralnexus.testbed.mixin.plugin.SaddleUpMixinPlugin",
        "package" to "dev.neuralnexus.testbed.mixin",
        "compatibilityLevel" to "JAVA_8",
        "mixins" to mixins,
        "server" to server,
        "client" to client
    )
    merged.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(mixinConfigJson)))
    fileTree(merged)
}

val Project.bundleJars: (List<File>) -> List<FileTree> get() = { jars ->
    val zipped = jars.map { zipTree(it).matching { exclude("testbed.mixins.*") } }
    val mixinConfigs = mutableListOf<File>()
    jars.forEach { jar ->
        zipTree(jar).files.forEach { file ->
            if (file.name.startsWith("testbed.mixins.")) {
                mixinConfigs.add(file)
            }
        }
    }
    zipped + mergeMixinConfigs(mixinConfigs)
    zipped
}
