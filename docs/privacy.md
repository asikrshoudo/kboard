# Privacy

Kboard is offline-first. This page says what is stored, what never happens,
and which rules are enforced by code, unit tests or CI.

## What is stored on the phone

- Words the user has finished typing, in lowercase, with how often and when they
  were used, and which word followed which (Room database in the app's private
  storage).
- Settings (DataStore): vibration, sound, autocorrect, incognito, language and
  layout choices.
- Words for which the user undid an autocorrection (DataStore).

Typed text is not logged and not stored in any other form.

## What never happens

- No network access. No `INTERNET` permission anywhere. The only permission is
  `VIBRATE`. `scripts/ci/check-permissions.sh` fails the build if the debug APK
  (every push) or the release APK asks for `INTERNET`.
- No cloud backup: `allowBackup` is false, so settings and learned words are
  never copied off the phone.
- No analytics and no account.

## Rules for each text field (unit-tested)

| Field | Learning | Suggestions | Autocorrect |
|---|---|---|---|
| Password, email address, web address | off | off | off |
| Field that asks for no suggestions | on | off | off |
| Field that asks for no personalized learning | off | on | on |
| Any field while incognito mode is on | off | on | on |

Only words of 2 to 32 letters are learned; digits are never learned.

## User control

- Incognito mode (a switch in the app): nothing new is learned.
- Autocorrect can be turned off; sound and vibration are separate switches.
- "Reset learned data" in the app deletes every learned word and pair, and the
  corrections the user had undone.

## Known limits

- The learned-words database is not pruned yet and its schema is not exported
  yet. Do both before the first public release.
- The Room store is not unit-tested because it needs a device; the logic around
  it is tested in `:engine`.
