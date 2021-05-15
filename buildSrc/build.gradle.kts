plugins {
    kotlin("jvm") version "1.5.0"
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:4.2.1") {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation("org.javassist:javassist:3.27.0-GA")
}

gradlePlugin {
    plugins {
        create("riru") {
            id = "riru"
            implementationClass = "RiruPlugin"
        }
        create("hideapi-redefine") {
            id = "hideapi-redefine"
            implementationClass = "HideapiRedefinePlugin"
        }
    }
}
