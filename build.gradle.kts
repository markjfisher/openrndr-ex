tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "7.5"
        distributionType = Wrapper.DistributionType.ALL
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}