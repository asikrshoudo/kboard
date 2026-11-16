# Architecture

Everything described as implemented exists in the repository and builds in CI.
Anything marked planned is design intent only.

## Modules

| Module | Purpose |
|---|---|
| `:app` | Android application: Compose companion app, Hilt root, manifest |
| `:ime` | `InputMethodService`, Canvas keyboard view, suggestion strip, haptics, per-field privacy rules |
| `:data` | DataStore settings, Room database of learned words |
| `:engine` | Pure Kotlin/JVM, no Android dependency: layouts, geometry, input logic, Bangla phonetic, suggestions, autocorrect |

Dependency direction: `:app` -> `:ime`, `:data`, `:engine`; `:ime` -> `:data`,
`:engine`; `:data` -> `:engine`. `:engine` never depends on an Android module,
so its tests run in seconds on CI. Base package: `com.kboard.thekami`.

## Keyboard surface (implemented)

A custom `View` drawn with Canvas, not Compose. Key rectangles are computed by
`LayoutGeometry` (pure, unit-tested) and drawing allocates nothing per frame.
Keys fire on touch-down and every finger is tracked. A transparent area above
the strip holds key previews and floats over the app. Compose is used for the
companion app only.

Text commit and cursor logic sit behind the `TextEditor` interface, so
`KeyInputHandler` is tested on the JVM with a fake text field.

## Layout settings (implemented)

Stored as `KeyboardAppearance` in DataStore and applied by the keyboard as soon
as they change:

- **Number row:** a row of digits above the letters (Latin digits, also in
  Bangla mode; Bangla digits are not implemented).
- **Height:** 80 to 140 percent of the default key height, in steps of 5. The
  value is kept inside that range when it is saved and when it is read.
- **Key preview:** the bubble above a pressed key can be turned off.

The keyboard is as tall as the letters page on every page (4 rows, or 5 with
the number row). A page with fewer rows, such as the symbol pages, shares that
height, so its keys are taller and switching pages never moves the app above.

In the companion app the slider updates a drawing of the key grid while it is
dragged and saves the value when the finger lifts. The real keyboard is in the
same process, so it can be watched changing in the "Try it" field. How the
system resizes the keyboard window while it is open has not been checked on
many devices yet.

## Suggestion strip (implemented)

Three slots: best suggestion in the middle (bold), second on its left, third on
its right. Every slot animates on its own: only a slot whose text changed
fades the old text out and the new one in, in place, with a 4dp upward move,
and slots that change together start 35 ms apart. With system animations off
the change is instant. Durations and distances are resources in `:ime`.

## Bangla phonetic input (implemented, basic)

Typing `ami` shows আমি. Latin letters are collected by `PhoneticComposer`,
converted by `PhoneticTransliterator` and shown in the text field's composing
region. Space, enter, punctuation, a page switch or a language switch keeps the
word as ordinary text. If the text before the cursor no longer matches the
word, the user moved the cursor and a new word starts. Rules follow Avro
Phonetic: capitals change the sound (`t` ত, `T` ট), adjacent consonants form
conjuncts (`rokto` রক্ত), `o` is the inherent vowel and `O` the ো sign.

Not implemented: ঃ ঁ ৎ, Bangla digits, long-press popups, the traditional layout.

## Suggestions and learning (implemented)

- `WordStore` (in `:engine`) is all the engine knows about storage. `:data`
  implements it with Room: a table of words (uses, last used) and one of word
  pairs. Words are stored in lowercase in the app's private storage.
- `LanguageSeed` is a small hand-picked word list per language (about 200
  English words, about 200 Bangla words, plus next-word rules). It gives
  suggestions on a fresh keyboard. It is not a frequency dictionary.
- `Ranker` scores learned words by use count with a gentle recency decay
  (half-life 30 days, never below a quarter of the score). Seed words score
  at most 0.5, so a word the user has typed once outranks every seed word. A
  word that followed the previous word before counts double.
- `SuggestionProvider` completes the word being typed or predicts the next
  word. While a Bangla word is being typed it offers completions, the spelling
  as typed, an alternative spelling (one the user chose before first) and the
  Latin text, for English words typed in Bangla mode.

## Autocorrect (implemented, English only)

`MisspellingTable` holds about 150 well-known typos and missing apostrophes
(`teh` -> `the`, `dont` -> `don't`, `i` -> `I`). Only mistakes that are almost
never intended are listed, so a correct word is never changed.

- A correction happens when the word is ended by space, enter or `, . ! ? ; :`.
  A single letter (`i`) is only corrected before a space or a new line, so
  `i.e.` is untouched.
- Backspace right after a correction restores the typed word. That word is then
  stored (DataStore) and never corrected again. Resetting learned data forgets it.
- Capitalization is kept (`Teh` -> `The`). Bangla typing is never corrected.
- Off in password, email and web address fields, in fields that ask for no
  suggestions, and when the user turns it off in the app.

Not implemented: correction by spelling distance. That needs a full dictionary,
because with a small word list a correct word such as `cat` would be changed to
`can`. A dictionary with a suitable open licence has to be chosen first.

## Privacy

- No `INTERNET` permission anywhere. The only permission is `VIBRATE`.
  Planned: a CI check that fails the build if the manifest ever requests
  `INTERNET`. It is **not** enforced yet.
- `allowBackup` is false: settings and learned words are never copied to
  cloud backups.
- Password, email address and web address fields: no learning, no suggestions,
  no autocorrect. Fields with `IME_FLAG_NO_PERSONALIZED_LEARNING`: no learning.
  Incognito mode (a switch in the app): no learning.
- Only words of 2-32 letters are learned; digits are never learned.
- The app deletes everything learned on request.
- Known limits: the learned-words database is not pruned yet and its schema is
  not exported yet (do both before the first public release). The Room store
  is not unit-tested because it needs a device; the logic around it is tested in
  `:engine`.

## Build and release

See [ADR 0002](docs/decisions/0002-ci-only-builds-and-release.md). Toolchain
pins and the reasons for them are in [ADR 0001](docs/decisions/0001-toolchain.md).

## Decision records

- [0001 Toolchain](docs/decisions/0001-toolchain.md)
- [0002 CI-only builds and release](docs/decisions/0002-ci-only-builds-and-release.md)
