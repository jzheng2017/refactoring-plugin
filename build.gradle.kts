plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.8.21"
  id("org.jetbrains.intellij") version "1.13.3"
}

group = "nl.jiankai"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2023.2.1")
  type.set("IU") // Target IDE Platform
  plugins.set(listOf("com.intellij.java"))
}

dependencies {
  implementation("org.eclipse.jgit:org.eclipse.jgit:6.6.0.202305301015-r")
  implementation("org.slf4j:slf4j-api:2.0.7")
  implementation("com.github.javaparser:javaparser-core:3.25.4")
  implementation("com.github.javaparser:javaparser-symbol-solver-core:3.25.4")
  implementation("org.apache.maven:maven-model:4.0.0-alpha-7")
  implementation("org.apache.maven.shared:maven-invoker:3.2.0")
  implementation("commons-io:commons-io:2.13.0")
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    sinceBuild.set("222")
    untilBuild.set("232.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
