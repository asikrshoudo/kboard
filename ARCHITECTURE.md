# Architecture

Status labels: **Implemented** exists in the repository and builds in CI.
**Planned** is design intent only.

## Modules

| Module | Status | Purpose |
|---|---|---|
| `:app` | Implemented (skeleton) | Android application, Hilt root, manifest, later the Compose companion UI |
| `:engine` | Implemented (empty) | Pure Kotlin/JVM. Language rules, layouts as data, prediction, autocorrect. No Android dependency, so tests run in seconds |
| `:ime` | Planned (Phase 1) | `InputMethodService`, keyboard view, input handling, haptics, sound |
| `:data` | Planned (Phase 4) | Room (personal dictionary, n-gram counts), DataStore (settings) |

Dependency direction: `:app` -> `:ime`, `:data` -> `:engine`. `:engine`
never depends on an Android module. Modules are created when their first
real code arrives, not before.

Base package: `com.kboard.thekami`.

## Design decisions (Planned)

- **Keyboard surface (implemented):** custom `View` drawn with Canvas, not Compose. Key
  rectangles are precomputed and `onDraw` allocates nothing. Compose is for
  the companion app only.
- **Suggestion animation:** platform `ViewPropertyAnimator` with crossfade
  and a small horizontal shift. Respects the reduced-motion setting.
- **Threading:** committing a keypress happens on the main thread with
  minimal work. Prediction runs on a background dispatcher and is cancelled
  when a newer key arrives.
- **Learning:** unigram and bigram counts with recency decay, stored locally.
  Learning is off in password fields, when the editor asks for no
  personalized learning, and in incognito mode.
- **Testability (implemented):** text commit and cursor logic sit behind a small interface
  so it can be tested on the JVM with a fake input connection.

## Privacy

Implemented: the only permission is `VIBRATE` (key haptics), there is no `INTERNET` permission, and `allowBackup` is false.

Planned: a CI check that fails the build if the manifest ever requests
`INTERNET`. This is **not** enforced yet.

## Build and release

See [ADR 0002](docs/decisions/0002-ci-only-builds-and-release.md). Toolchain
pins and the reasons for them are in [ADR 0001](docs/decisions/0001-toolchain.md).

## Decision records

- [0001 Toolchain](docs/decisions/0001-toolchain.md)
- [0002 CI-only builds and release](docs/decisions/0002-ci-only-builds-and-release.md)
