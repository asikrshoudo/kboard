# 0001: Toolchain pins

Status: accepted, 2026-11-14

## Context

Development happens on a phone, so every version must be proven by CI, not
assumed. Versions were read from Maven Central and Google Maven by the
"Version check" workflow.

## Decision

Gradle 9.9.1, AGP 9.5.1, Kotlin 2.4.21, KSP 2.3.14, Hilt 2.60.1, Detekt
2.0.0-alpha.6, JDK 21 on CI, minSdk 26, compileSdk and targetSdk 36. The
exact values live in `gradle/libs.versions.toml`.

## Consequences

- AGP 9 has built-in Kotlin, so the `org.jetbrains.kotlin.android` plugin is
  not used. Annotation processing uses KSP, never kapt.
- Hilt 2.60.1 with Kotlin 2.4.21 compiled in CI on 2026-11-14. This is
  evidence for the current code only; re-check when real injected classes
  are added.
- Detekt 2.0 has no stable release. 1.23.x was rejected because it predates
  AGP 9 built-in Kotlin and Kotlin 2.4. An alpha linter can break on update,
  so bump it deliberately and watch CI.
- compileSdk 37 is supported by AGP 9.5 but not adopted yet.
