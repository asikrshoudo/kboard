# Architecture

Everything described as implemented exists in the repository and builds in CI.
Anything marked planned is design intent only.

## Modules

| Module | Purpose |
|---|---|
| `:app` | Android application: Compose companion app, Hilt root, manifest |
| `:ime` | `InputMethodService`, Canvas keyboard view, suggestion strip, haptics, sounds, per-field privacy rules |
| `:data` | DataStore settings, Room database of learned words |
| `:engine` | Pure Kotlin/JVM, no Android dependency: layouts, geometry, input logic, Bangla phonetic, suggestions, autocorrect |

Dependency direction: `:app` -> `:ime`, `:data`, `:engine`; `:ime` -> `:data`,
`:engine`; `:data` -> `:engine`. `:engine` never depends on an Android module,
so its tests run in seconds on CI. Base package: `com.kboard.thekami`.

## How a key press flows

1. `KeyboardView` (Canvas) finds the key under the finger on touch-down and
   tells `KboardImeService`.
2. The service plays the haptic and the sound, then hands the key to
   `KeyInputHandler` (`:engine`).
3. `KeyInputHandler` edits the text field through the `TextEditor` interface,
   which `InputConnectionEditor` (`:ime`) implements with Android's
   `InputConnection`. Tests use a fake `TextEditor`, so all input logic runs
   on the JVM.
4. The service asks `SuggestionProvider` for suggestions and shows them in the
   strip.

## Keyboard surface

A custom `View` drawn with Canvas, not Compose. Key rectangles are computed by
`LayoutGeometry` (pure, unit-tested) and drawing allocates nothing per frame.
Keys fire on touch-down and every finger is tracked. A transparent area above
the strip holds key previews and floats over the app. Compose is used for the
companion app only.

## Topics

- [Typing, layouts and Bangla phonetic input](docs/typing.md)
- [Suggestions, learning and autocorrect](docs/suggestions.md)
- [Haptics and key sounds](docs/feedback.md)
- [Privacy](docs/privacy.md)

## Build and release

See [ADR 0002](docs/decisions/0002-ci-only-builds-and-release.md). Toolchain
pins and the reasons for them are in [ADR 0001](docs/decisions/0001-toolchain.md).

## Decision records

- [0001 Toolchain](docs/decisions/0001-toolchain.md)
- [0002 CI-only builds and release](docs/decisions/0002-ci-only-builds-and-release.md)
