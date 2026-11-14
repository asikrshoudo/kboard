# Changelog

All notable changes are recorded here. Format based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/). Kboard has not
had a public release yet.

## [Unreleased]

### Added

- Gradle build with Kotlin DSL, version catalog and wrapper (generated in CI).
- `:app` module with Hilt, and an empty `:engine` module.
- CI: debug APK, unit tests, Lint and Detekt on every push.
- Release pipeline: a signed APK and AAB published when `version.txt` changes.
- Documentation: README, ARCHITECTURE, CONTRIBUTING, CODE_OF_CONDUCT, SECURITY and decision records.
