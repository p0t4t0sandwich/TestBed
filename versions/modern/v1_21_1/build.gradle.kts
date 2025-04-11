import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
    alias(libs.plugins.shadow)
    id(libs.plugins.unimined.get().pluginId)
}

base {
    archivesName = "${projectId}-${minecraftVersion}"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
java.sourceCompatibility = JavaVersion.toVersion(javaVersion)
java.targetCompatibility = JavaVersion.toVersion(javaVersion)

sourceSets {
    setupSourceSets("fabric", "forge", "neoforge", "sponge")
}

@Suppress("UnstableApiUsage")
configurations {
    val mainCompileOnly by creating
    val modImplementation by creating
    setupConfigurations("fabric", "forge", "neoforge", "sponge")
}

// ------------------------------------------- Vanilla -------------------------------------------
unimined.minecraft {
    version(minecraftVersion)
    mappings {
        mojmap()
        devFallbackNamespace("official")
    }
    defaultRemapJar = false
}

tasks.jar {
    archiveClassifier.set("vanilla")
}

// ------------------------------------------- Fabric -------------------------------------------
unimined.minecraft(sourceSets.getByName("fabric")) {
    combineWith(sourceSets.main.get())
    fabric {
        loader(fabricLoaderVersion)
    }
    defaultRemapJar = true
}

tasks.named<RemapJarTask>("remapFabricJar") {
    asJar.archiveClassifier.set("fabric-remap")
    mixinRemap {
        disableRefmap()
    }
}

tasks.register<ShadowJar>("relocateFabricJar") {
    dependsOn("remapFabricJar")
    from(jarToFiles("remapFabricJar"))
    archiveClassifier.set("fabric")
    dependencies {
        exclude("dev/neuralnexus/testbed/mixin/v1_21_1/vanilla/**")
    }
    relocate("dev.neuralnexus.testbed.v1_21_1.vanilla", "dev.neuralnexus.testbed.v1_21_1.y_intmdry")
}

// ------------------------------------------- Forge -------------------------------------------
unimined.minecraft(sourceSets.getByName("forge")) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
    }
    defaultRemapJar = false
}

tasks.register<ShadowJar>("relocateForgeJar") {
    from(sourceSets.getByName("forge").output)
    archiveClassifier.set("forge")
    dependencies {
        exclude("dev/neuralnexus/testbed/mixin/v1_21_1/vanilla/**")
        exclude("dev/neuralnexus/testbed/v1_21_1/vanilla/**")
    }
}

// ------------------------------------------- NeoForge -------------------------------------------
unimined.minecraft(sourceSets.getByName("neoforge")) {
    combineWith(sourceSets.main.get())
    neoForge {
        loader(neoForgeVersion)
    }
    defaultRemapJar = false
}

tasks.register<ShadowJar>("relocateNeoForgeJar") {
    from(sourceSets.getByName("neoforge").output)
    archiveClassifier.set("neoforge")
    dependencies {
        exclude("dev/neuralnexus/testbed/mixin/v1_21_1/vanilla/**")
        exclude("dev/neuralnexus/testbed/v1_21_1/vanilla/**")
    }
}

// ------------------------------------------- Sponge -------------------------------------------
tasks.register<Jar>("spongeJar") {
    archiveClassifier.set("sponge")
    from(sourceSets.getByName("sponge").output)
}

// ------------------------------------------- Common -------------------------------------------
dependencies {
    setupCompileDeps(listOf(
        project(":api"),
        ), "main", "fabric", "forge", "neoforge", "sponge")

    setupCompileDeps(listOf(
        libs.mixin,
        ), "main")

    setupRuntimeDeps(listOf(
        project(":api"),
        ), "fabric", "forge", "neoforge")

    listOf(
        "fabric-api-base",
        "fabric-command-api-v2",
        "fabric-lifecycle-events-v1",
        "fabric-networking-api-v1"
    ).forEach {
        "fabricModImplementation"(fabricApi.fabricModule(it, fabricVersion))
    }

    "spongeCompileOnly"("org.spongepowered:spongeapi:${spongeVersion}")
}

tasks.shadowJar {
    listOf(
        "relocateFabricJar",
        "relocateForgeJar",
        "relocateNeoForgeJar",
        "spongeJar"
    ).forEach {
        dependsOn(it)
        from(jarToFiles(it))
    }
    archiveClassifier.set("")
}

tasks.build.get().dependsOn(tasks.shadowJar)
