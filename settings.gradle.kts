dependencyResolutionManagement {
    versionCatalogs {
       create("mylibs") {
            from(files("./gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "openrndr-ex"

include(
    "app"
)