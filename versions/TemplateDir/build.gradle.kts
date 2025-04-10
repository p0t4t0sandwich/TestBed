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
    create("fabric") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
    create("forge") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
    create("neoforge") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
    create("sponge") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

@Suppress("UnstableApiUsage")
configurations {
    val mainCompileOnly by creating
    named("compileOnly") {
        extendsFrom(configurations.getByName("fabricCompileOnly"))
        extendsFrom(configurations.getByName("forgeCompileOnly"))
        extendsFrom(configurations.getByName("neoforgeCompileOnly"))
        extendsFrom(configurations.getByName("spongeCompileOnly"))
    }
    val modImplementation by creating
    named("modImplementation") {
        extendsFrom(configurations.getByName("fabricImplementation"))
    }
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
        exclude("dev/neuralnexus/testbed/mixin/vERS/vanilla/**")
    }
    relocate("dev.neuralnexus.testbed.vERS.vanilla", "dev.neuralnexus.testbed.vERS.y_intmdry")
}

// ------------------------------------------- Forge -------------------------------------------
unimined.minecraft(sourceSets.getByName("forge")) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
        mixinConfig("testbed.mixins.vERS.forge.json")
    }
    defaultRemapJar = true
}

tasks.named<RemapJarTask>("remapForgeJar") {
    asJar.archiveClassifier.set("forge-remap")
    mixinRemap {
        disableRefmap()
    }
}

tasks.register<ShadowJar>("relocateForgeJar") {
    dependsOn("remapForgeJar")
    from(jarToFiles("remapForgeJar"))
    archiveClassifier.set("forge")
    dependencies {
        exclude("dev/neuralnexus/testbed/mixin/vERS/vanilla/**")
    }
    relocate("dev.neuralnexus.testbed.vERS.vanilla", "dev.neuralnexus.testbed.vERS.searge")
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
        exclude("dev/neuralnexus/testbed/mixin/vERS/vanilla/**")
        exclude("dev/neuralnexus/testbed/vERS/vanilla/**")
    }
}

// ------------------------------------------- Sponge -------------------------------------------
tasks.register<Jar>("spongeJar") {
    archiveClassifier.set("sponge")
    from(sourceSets.getByName("sponge").output)
}

// ------------------------------------------- Common -------------------------------------------
dependencies {
    listOf(
        libs.mixin,
    ).forEach {
        "mainCompileOnly"(it)
        "fabricCompileOnly"(it)
        "forgeCompileOnly"(it)
        "neoforgeCompileOnly"(it)
        "spongeCompileOnly"(it)
    }

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
