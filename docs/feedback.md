# Haptics and key sounds

Haptics and sound are separate settings. Either, both or neither can be on.

## Haptics (on by default)

Each kind of key has its own vibration (`KeyHaptics`). The amplitude is used
only on phones that can control it; others use their default strength.

| Key | Duration | Strength (1-255) |
|---|---|---|
| Letters and symbols | 12 ms | 60 |
| Shift, page and language keys | 14 ms | 80 |
| Space | 16 ms | 50 |
| Backspace | 20 ms | 110 |
| Enter | 26 ms | 150 |
| Choosing a suggestion | 10 ms | 40 |

The numbers are constants in `KeyHaptics` so they can be tuned on a real device.

## Key sounds (off by default)

`KeySounds` plays the phone's own key click sounds through `AudioManager`, so
no sound files are bundled: letters use the standard click, space, delete and
enter each have their own, and choosing a suggestion uses the standard click.

- The volume is 0 to 100 percent in steps of 10 (default 40). It is curved
  (squared) before it is given to Android, because Android treats it as a raw
  gain.
- It stays quiet when the phone is silent or on vibrate.
- Android plays these sounds only when "Touch sounds" is on in the phone's Sound
  settings. The companion app says so next to the switch.
- Which sound each key gets is unit-tested. How it sounds and whether it plays
  on every phone has not been checked on many devices yet.
