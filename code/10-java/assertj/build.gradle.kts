plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    implementation("org.junit.jupiter:junit-jupiter-engine")
    implementation("org.junit.jupiter:junit-jupiter-params")

    implementation("org.assertj:assertj-core:3.19.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}