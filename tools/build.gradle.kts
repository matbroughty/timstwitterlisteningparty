import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.0.M4"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
  id("com.github.johnrengelman.shadow") version "5.2.0"
	kotlin("jvm") version "1.3.71"
	kotlin("plugin.spring") version "1.3.71"
}

group = "com.timstwitterlisteningparty"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.jsoup:jsoup:1.13.1")
  implementation("com.opencsv:opencsv:5.1")
  implementation("org.springframework.shell:spring-shell-starter:2.0.0.RELEASE")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.amazonaws:aws-lambda-java-core:1.2.0")
  implementation("com.amazonaws:aws-lambda-java-events:2.2.7")
  implementation("com.amazonaws:aws-java-sdk-s3:1.11.762")
  runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.1.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
