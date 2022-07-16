import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.beryx.runtime.JPackageTask
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

// With no "class X {...", just add "Kt" to the end of the class file name
val applicationMainClass = "fish.ExampleAppKt"

/**  ## additional ORX features to be added to this project */
val orxFeatures = setOf(
//  "orx-boofcv",
//  "orx-camera",
//  "orx-chataigne",
    "orx-color",
    "orx-compositor",
//  "orx-dnk3",
//  "orx-easing",
//  "orx-file-watcher",
//  "orx-filter-extension",
    "orx-fx",
//  "orx-glslify",
//  "orx-gradient-descent",
//    "orx-git-archiver",
    "orx-gui",
    "orx-image-fit",
//  "orx-integral-image",
//  "orx-interval-tree",
//  "orx-jumpflood",
//  "orx-kdtree",
//  "orx-keyframer",
//  "orx-kinect-v1",
//  "orx-kotlin-parser",
//  "orx-mesh-generators",
//  "orx-midi",
//  "orx-minim",
//  "orx-no-clear",
    "orx-noise",
//  "orx-obj-loader",
    "orx-olive",
//  "orx-osc",
//  "orx-palette",
    "orx-panel",
//  "orx-parameters",
//  "orx-poisson-fill",
//  "orx-rabbit-control",
//  "orx-realsense2",
//  "orx-runway",
    "orx-shade-styles",
//  "orx-shader-phrases",
    "orx-shapes",
//  "orx-syphon",
//  "orx-temporal-blur",
//  "orx-tensorflow",
//  "orx-time-operators",
//  "orx-timer",
//  "orx-triangulation",
//  "orx-video-profiles",
    null
).filterNotNull()

/** ## additional ORML features to be added to this project */
val ormlFeatures = setOf<String>(
//    "orml-blazepose",
//    "orml-dbface",
//    "orml-facemesh",
//    "orml-image-classifier",
//    "orml-psenet",
//    "orml-ssd",
//    "orml-style-transfer",
//    "orml-super-resolution",
//    "orml-u2net"
)

/** ## additional OPENRNDR features to be added to this project */
val openrndrFeatures = setOfNotNull(
    if (DefaultNativePlatform("current").architecture.name != "arm-v8") "video" else null
)

/** ## configure the type of logging this project uses */
enum class Logging { NONE, SIMPLE, FULL }

val applicationLogging = Logging.FULL

@Suppress(
    "DSL_SCOPE_VIOLATION",
    "MISSING_DEPENDENCY_CLASS",
    "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
    "FUNCTION_CALL_EXPECTED"
)
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runtime)
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.kotlinx.coroutines.core)

    // logging
    // runtimeOnly(libs.slf4j.api)
    implementation(libs.kotlin.logging)
    when (applicationLogging) {
        Logging.NONE -> {
            implementation(libs.slf4j.nop)
        }
        Logging.SIMPLE -> {
            implementation(libs.slf4j.simple)
        }
        Logging.FULL -> {
            // can't get full logging to work, throws errors about missing java.beans.PropertyChangeEvent when run in the exe version, ok in IDE!
            implementation(libs.log4j.slf4j)
//            implementation(libs.logback.classic)
//            implementation(libs.jul2slf4j)
//            implementation(libs.logstash)
            implementation(libs.jackson.databind)
            implementation(libs.jackson.json)
        }
    }
}

application {
    mainClass.set(applicationMainClass)
}

tasks {
    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        manifest {
            attributes["Main-Class"] = applicationMainClass
        }
//        minimize {
//            exclude(dependency("org.openrndr:openrndr-gl3:.*"))
//            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
//        }
    }

    named<JPackageTask>("jpackage") {
        doLast {
            val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
            when (val name = os.toFamilyName()) {
                OperatingSystemFamily.WINDOWS, OperatingSystemFamily.LINUX -> {
                    copy {
                        // We need "../" because this is a sub-module called "app"
                        from("../data") {
                            include("**/*")
                        }
                        into("build/jpackage/openrndr-application/data")
                    }
                }
                OperatingSystemFamily.MACOS -> {
                    copy {
                        from("../data") {
                            include("**/*")
                        }
                        into("build/jpackage/openrndr-application.app/data")
                    }
                }
                else -> throw Exception("Unknown OS: $os")
            }
        }
    }

    named<KotlinCompile>("compileKotlin") {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
        }
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
        }
    }

    withType<Test> {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    }

    named<Test>("test") {
        useJUnitPlatform()
    }

    register<Zip>("jpackageZip") {
        archiveFileName.set("openrndr-application.zip")
        from("$buildDir/jpackage") {
            include("**/*")
        }
    }

}

tasks.findByName("jpackageZip")?.dependsOn("jpackage")

runtime {
    jpackage {
        imageName = "openrndr-application"
        skipInstaller = true
        if (DefaultNativePlatform.getCurrentOperatingSystem().toFamilyName() == OperatingSystemFamily.MACOS) jvmArgs.add("-XstartOnFirstThread")
    }
    options.set(listOf("--strip-debug", "--compress", "1", "--no-header-files", "--no-man-pages"))
    modules.set(listOf("jdk.unsupported", "java.management"))
}

class Openrndr {
    private val openrndrVersion = libs.versions.openrndr.get()
    private val orxVersion = libs.versions.orx.get()
    private val ormlVersion = libs.versions.orml.get()

    // choices are "orx-tensorflow-gpu", "orx-tensorflow"
    private val orxTensorflowBackend = "orx-tensorflow"

    private val os = if (project.hasProperty("targetPlatform")) {
        val supportedPlatforms = setOf("windows", "macos", "linux-x64", "linux-arm64")
        val platform: String = project.property("targetPlatform") as String
        if (platform !in supportedPlatforms) {
            throw IllegalArgumentException("target platform not supported: $platform")
        } else {
            platform
        }
    } else when (val name = DefaultNativePlatform.getCurrentOperatingSystem().toFamilyName()) {
        OperatingSystemFamily.WINDOWS -> "windows"
        OperatingSystemFamily.MACOS -> when (val h = DefaultNativePlatform("current").architecture.name) {
            "arm-v8" -> "macos-arm64"
            else -> "macos"
        }
        OperatingSystemFamily.LINUX -> when (val h = DefaultNativePlatform("current").architecture.name) {
            "x86-64" -> "linux-x64"
            "aarch64" -> "linux-arm64"
            else -> throw IllegalArgumentException("architecture not supported: $h")
        }
        else -> throw IllegalArgumentException("os $name not supported")
    }

    private fun orx(module: String) = "org.openrndr.extra:$module:$orxVersion"
    private fun orml(module: String) = "org.openrndr.orml:$module:$ormlVersion"
    private fun openrndr(module: String) = "org.openrndr:openrndr-$module:$openrndrVersion"
    private fun openrndrNatives(module: String) = "org.openrndr:openrndr-$module-natives-$os:$openrndrVersion"
    private fun orxNatives(module: String) = "org.openrndr.extra:$module-natives-$os:$orxVersion"

    init {
        repositories {
            listOf(openrndrVersion, orxVersion, ormlVersion).any { it.contains("SNAPSHOT") }.ifTrue { mavenLocal() }
            maven(url = "https://maven.openrndr.org")
        }
        dependencies {
            runtimeOnly(openrndr("gl3"))
            runtimeOnly(openrndrNatives("gl3"))
            implementation(openrndr("openal"))
            runtimeOnly(openrndrNatives("openal"))
            implementation(openrndr("application"))
            implementation(openrndr("svg"))
            implementation(openrndr("animatable"))
            implementation(openrndr("extensions"))
            implementation(openrndr("filter"))
            if ("video" in openrndrFeatures) {
                implementation(openrndr("ffmpeg"))
                runtimeOnly(openrndrNatives("ffmpeg"))
            }
            for (feature in orxFeatures) {
                implementation(orx(feature))
            }
            for (feature in ormlFeatures) {
                implementation(orml(feature))
            }
            if ("orx-tensorflow" in orxFeatures) runtimeOnly("org.openrndr.extra:$orxTensorflowBackend-natives-$os:$orxVersion")
            if ("orx-kinect-v1" in orxFeatures) runtimeOnly(orxNatives("orx-kinect-v1"))
            if ("orx-olive" in orxFeatures) implementation(libs.kotlin.script.runtime)
        }
    }
}
val openrndr = Openrndr()