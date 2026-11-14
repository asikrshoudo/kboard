#!/usr/bin/env bash
# Prints the newest stable versions of the toolchain Kboard depends on.
# Pre-releases are filtered out. Every result line starts with "VER".
set -uo pipefail

GOOGLE="https://dl.google.com/dl/android/maven2"
CENTRAL="https://repo.maven.apache.org/maven2"
PRERELEASE='(alpha|beta|rc|dev|eap|snapshot|titan|-m[0-9])'

# latest_stable <label> <artifact base url>: newest 3 stable versions.
latest_stable() {
  local label="$1" url="$2" versions
  versions=$(curl -fsSL "$url/maven-metadata.xml" \
    | grep -oP '(?<=<version>)[^<]+' \
    | grep -Eiv "$PRERELEASE" \
    | sort -V | tail -3 | tr '\n' ' ') || versions=""
  echo "VER $label: ${versions:-lookup failed ($url)}"
}

gradle=$(curl -fsSL https://services.gradle.org/versions/current \
  | grep -oP '"version"\s*:\s*"\K[^"]+' | head -1)
echo "VER Gradle: ${gradle:-lookup failed}"

latest_stable "AGP" "$GOOGLE/com/android/tools/build/gradle"
latest_stable "Kotlin" "$CENTRAL/org/jetbrains/kotlin/kotlin-gradle-plugin"
latest_stable "KSP" "$CENTRAL/com/google/devtools/ksp/symbol-processing-gradle-plugin"
latest_stable "Hilt" "$CENTRAL/com/google/dagger/hilt-android-gradle-plugin"
latest_stable "Compose BOM" "$GOOGLE/androidx/compose/compose-bom"
latest_stable "Room" "$GOOGLE/androidx/room/room-runtime"
latest_stable "DataStore" "$GOOGLE/androidx/datastore/datastore-preferences"
latest_stable "Coroutines" "$CENTRAL/org/jetbrains/kotlinx/kotlinx-coroutines-core"
latest_stable "Detekt (io.gitlab)" "$CENTRAL/io/gitlab/arturbosch/detekt/detekt-gradle-plugin"

# Newest 5 versions in repository order, pre-releases included
# (detekt 2.x may still be alpha, so the stable filter would hide it).
latest_any() {
  local label="$1" url="$2" versions
  versions=$(curl -fsSL "$url/maven-metadata.xml" \
    | grep -oP '(?<=<version>)[^<]+' | tail -5 | tr '\n' ' ') || versions=""
  echo "VER $label: ${versions:-lookup failed ($url)}"
}

latest_any "Detekt 2.x marker" "$CENTRAL/dev/detekt/dev.detekt.gradle.plugin"
latest_any "Detekt 2.x artifact" "$CENTRAL/dev/detekt/detekt-gradle-plugin"
