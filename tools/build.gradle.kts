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
  implementation("org.springframework:spring-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jsoup:jsoup:1.13.1")
  implementation("com.opencsv:opencsv:5.1")
  implementation("org.springframework.shell:spring-shell-starter:2.0.0.RELEASE")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.amazonaws:aws-lambda-java-core:1.2.0")
  implementation("com.amazonaws:aws-lambda-java-events:2.2.7")
  implementation("com.amazonaws:aws-java-sdk-s3:1.11.762")
  implementation("com.amazonaws:aws-java-sdk-cloudfront:1.11.762")
  implementation("com.google.api-client:google-api-client:1.30.9")
  implementation("org.freemarker:freemarker:2.3.30")
  implementation("com.squareup.okhttp3:okhttp:4.9.0")
  implementation("se.michaelthelin.spotify:spotify-web-api-java:6.0.0-RC1"){
    exclude(group = "org.slf4j")
  }
  implementation("com.github.redouane59.twitter:twittered:1.25")

  // had to add some additional code to allow for update of collections to twitter4j - need
  // this until aws updates to Java 15 for lambda's or twittered drops back to java 8
  //implementation(files("$projectDir/commonjar/3rdparty/twitter4j-core-4.0.8-SNAPSHOT.jar"))
  //implementation(files("$projectDir/commonjar/3rdparty/twitter4j-stream-4.0.8-SNAPSHOT.jar"))


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
