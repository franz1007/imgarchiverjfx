plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("application")
}

group = "eu.franz1007"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/franz1007/BilderArchivierung")
        credentials {
            username = findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
    mavenLocal()
}

dependencies{
    implementation("eu.franz1007:bilderarchivierung:1.1.9-SNAPSHOT")
    implementation("org.jfxtras:jmetro:11.6.15")
    implementation("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

application {
    mainModule.set("imgarchiverjfx.main")
    mainClass.set("eu.franz1007.imagearchiverjfx.Main")
}

javafx {
    version = "18.0.2"
    modules("javafx.controls", "javafx.base", "javafx.graphics")
}