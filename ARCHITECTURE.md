# Architecture

Status labels: **Implemented** exists in the repository and builds in CI.
**Planned** is design intent only.

## Modules

| Module | Status | Purpose |
|---|---|---|
| `:app` | Implemented (skeleton) | Android application, Hilt root, manifest, later the Compose companion UI |
| `:engine` | Implemented (empty) | Pure Kotlin/JVM. Language rules, layouts as data, prediction, autocorrect. No Android dependency, so tests run in seconds |
| `:ime` | Planned (Phase 1) | `InputMethodService`, keyboard view, input handling, haptics, sound |
| `:data` | Implemented (settings only) | DataStore settings shared by the app and the keyboard. Room (personal dictionary, n-gram counts) is planned for Phase 4 |

Dependency direction: `:app` -> `:ime`, `:data`; `:ime` -> `:data`, `:engine`. `:engine` never depends on an Android module. `:data` will depend on `:engine` once it stores engine data.
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
- **Learning (implemented):** unigram and bigram counts with recency decay, stored locally.
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

## Bangla phonetic input (implemented, basic)

Typing `ami` shows আমি. Latin letters are collected by `PhoneticComposer`,
converted by `PhoneticTransliterator` (pure Kotlin in `:engine`, covered by
unit tests) and shown in the text field's composing region. Space, enter,
punctuation, a page switch or a language switch keeps the word as ordinary
text. If the text before the cursor no longer matches the word, the user moved
the cursor and a new word starts.

Rules follow Avro Phonetic: capitals change the sound (`t` ত, `T` ট), adjacent
consonants form conjuncts (`rokto` রক্ত), `o` is the inherent vowel and `O` the
ো sign, `ng` is ং.

Not implemented yet: ঃ ঁ ৎ, Bangla digits, suggestions and alternatives for
ambiguous spellings, long-press popups, the traditional (non-phonetic) layout.

## Suggestions and learning (implemented, first version)

- `WordStore` (in `:engine`) is the only thing the engine knows about storage.
  `:data` implements it with Room: one table of words (uses, last used) and
  one of word pairs. Words are stored in lowercase, and everything stays in the
  app's private storage. Cloud backup is off.
- `WordContext` finds the current and the previous word in the text before
  the cursor. A comma or full stop ends the sentence context.
- `Ranker` scores by use count with a gentle recency decay (half-life 30
  days, never below a quarter of the score). A word that followed the previous
  word before counts double.
- `SuggestionProvider` completes the word being typed or predicts the next
  word. While a Bangla word is being typed it offers the default spelling, the
  best alternative (a spelling you used before first, then single-letter
  variants such as o/O, i/I, t/T) and the Latin text for mixed typing.
- Suggestions are shown in `SuggestionStrip` (Canvas, inside the keyboard
  view). A change crossfades with a small horizontal shift, and is instant
  when system animations are off.

Privacy rules (`LearningPolicy`, `EditorTraits`, unit-tested):

- Password, email address and web address fields: no learning, no suggestions.
- A field that sets `IME_FLAG_NO_PERSONALIZED_LEARNING`: no learning.
- Incognito mode (a setting in the app): no learning, suggestions still shown.
- Only words of 2-32 letters are learned; digits are never learned.
- The app can delete everything that was learned.

Known limits: no built-in dictionary, so English suggestions start empty and
come only from your own typing. No autocorrect yet. The database is not
pruned yet and the schema is not exported yet (do both before the first
public release). The Room store is not unit-tested because it needs a device;
the logic around it is tested in `:engine`.
