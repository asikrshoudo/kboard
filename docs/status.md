# Status

Kboard is **pre-alpha**. This page lists what works and what does not. A
feature is only listed as working when it is in the repository and builds in CI.
The newest items are covered by unit tests where the logic allows, but are still
being checked on a real device.

## Works today

- English (US) QWERTY and Bangla phonetic (Avro-style) typing, switched with the
  globe key or by holding the space bar; the current language is shown on the
  space bar
- Number and symbol pages (including the Taka sign), and a number pad that
  opens in number fields and from the toolbar
- Sentence auto-capitalization where the field asks for it (English)
- Toolbar: a toggle button at the left of the suggestion strip opens it. It has
  the number pad and a shortcut to the settings
- Optional number row, keyboard height from 80 to 140 percent, and key preview
  bubbles that can be turned off
- Suggestion strip: a small hand-picked word list for English and Bangla, plus
  words learned from your own typing (frequency, recency, word pairs). Bangla
  spelling alternatives. Each suggestion fades in place on its own, and the
  animation follows the system animation setting
- English autocorrect for well-known typos and missing apostrophes. Backspace
  right after a correction undoes it, and that word is never changed again
- A different vibration for each kind of key, optional key sounds with a volume
  setting, and backspace that repeats while held
- Choosing what holding the space bar does: switch language, choose another
  keyboard, or nothing
- Companion app (Material 3): setup guide, haptics, sound, autocorrect, layout,
  gestures, incognito mode, and deleting everything learned
- GitHub Actions: build, unit tests, Lint and Detekt on every push, a check that
  the APK asks for no internet permission, and signed APK and AAB releases

## Planned, in this order (none of it exists yet)

1. Clipboard history (hundreds of items, pinning, deleting)
2. Emoji picker with all emoji, and a layout editor for the bottom row
3. Voice typing through the phone's speech recognizer
4. A Bangla layout with Bengali letters on the keys (not Bijoy, which is licensed)
5. Themes, one-handed mode, other languages, autocorrect based on a full dictionary
