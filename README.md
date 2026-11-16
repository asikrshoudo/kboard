# Kboard

> A keyboard that learns how you type, not how everyone types.

Kboard (Play Store title: *Kboard - TheKami Keyboard*) is an open-source,
privacy-first Android keyboard by [TheKami](https://www.thekami.tech).

## Status

**Pre-alpha. English (US) and Bangla (phonetic) typing work, with suggestions
and autocorrect. Many features are still missing.**

What exists today:

- English (US) QWERTY and Bangla phonetic (Avro-style) typing, switched with
  the globe key; the current language is shown on the space bar
- Number and symbol pages (including the Taka sign), sentence auto-capitalization
- Optional number row, keyboard height from 80 to 140 percent, and key
  preview bubbles that can be turned off
- Suggestion strip: a small hand-picked word list for English and Bangla, plus
  words learned from your own typing (frequency, recency, word pairs). Bangla
  spelling alternatives. Each suggestion fades in place on its own, and the
  animation follows the system animation setting
- English autocorrect for well-known typos and missing apostrophes. Backspace
  right after a correction undoes it, and that word is never changed again
- Backspace hold-to-repeat and a different vibration for each kind of key
- Companion app (Material 3): setup guide, haptics, autocorrect, layout
  (number row, height with a drawing of the result, key preview), incognito
  mode, and deleting everything learned
- GitHub Actions: build, unit tests, Lint and Detekt on every push, and signed
  APK and AAB releases

The newest items (layout settings, suggestion animation, built-in words,
autocorrect) have unit tests where the logic allows, but are still being
checked on a real device.

Not built yet: the Bangla traditional layout, Bangla digits, key sounds,
emoji, clipboard history, one-handed mode, themes, other languages,
autocorrect based on a full dictionary.

## Privacy

The app and keyboard request no internet permission (the only permission is
VIBRATE, for key haptics), and cloud backup is disabled. Kboard remembers the
words you use, only on this phone, to suggest them again. It never learns or
suggests in password, email or web address fields, never learns in incognito
mode or when an app asks keyboards not to learn, and you can delete everything
it learned from the app. See [ARCHITECTURE.md](ARCHITECTURE.md) for details.

## Getting builds

There is no local build. Everything is built by GitHub Actions:

- Debug APK: open the latest **CI** run in the Actions tab and download the
  `kboard-debug-apk` artifact.
- Signed releases: the Releases page.

## Repository layout

app/          Android application: companion app (Compose, Material 3)
data/         Settings (DataStore) and learned words (Room)
engine/       Pure Kotlin/JVM: layouts, input logic, Bangla phonetic,
suggestions, autocorrect
ime/          Keyboard service, Canvas keyboard view, suggestion strip, haptics
gradle/       Version catalog and wrapper
docs/         Architecture decision records
.github/      CI, release and tooling workflows
scripts/ci/   Helper scripts used by workflows
## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md), the [Code of Conduct](CODE_OF_CONDUCT.md)
and the [Security Policy](SECURITY.md).

## License

GPL-3.0. See [LICENSE](LICENSE).

## Contact

- General: info@thekami.tech
- Development: dev@thekami.tech
- Website: https://www.thekami.tech
- Discord: https://www.thekami.tech/discord/
