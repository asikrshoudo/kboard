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
- Keyboard service with an English (US) QWERTY layout: letters, shift,
  space, backspace, enter, comma and period. Not yet tested on a device.
- Key previews, backspace hold-to-repeat and per-key haptics (adds the
  `VIBRATE` permission). CI debug builds on pushes are signed with the
  project key so installed builds can be updated in place.
- Companion app (Jetpack Compose, Material 3, dynamic color): setup guide,
  haptics setting and a typing test field. Settings are stored with
  DataStore in the new `:data` module and read by the keyboard.
- Number and symbol pages (with the Taka sign), page switching, number and
  phone fields opening on the symbols page, and sentence auto-capitalization
  for fields that ask for it.
- Language switching with a globe key (language shown on the space bar and
  remembered). Bangla (Bangladesh) phonetic typing in the Avro style, with
  conjuncts, vowel signs, anusvara and the dari (।) as the full stop.
- Suggestion strip with an animated crossfade, on-device learning (word
  frequency, recency and word pairs, stored with Room), Bangla spelling
  alternatives and mixed Bangla-English typing, incognito mode, and a button
  to delete everything learned. No learning or suggestions in password, email
  and web address fields.
- Layout settings: an optional number row, keyboard height from 80 to 140
  percent (with a drawing of the result in the app), and a switch for the key
  preview bubble. Symbol pages keep the keyboard height by using taller keys.
- Key sounds: the phone's own key clicks (different for letters, space, delete
  and enter) with a volume setting, off by default and separate from vibration.
- CI now fails if the APK asks for the internet permission.
- Documentation split by topic under `docs/`.
