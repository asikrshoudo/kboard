# Kboard

> A keyboard that learns how you type, not how everyone types.

Kboard (Play Store title: *Kboard - TheKami Keyboard*) is an open-source,
privacy-first Android keyboard by [TheKami](https://www.thekami.tech).

## Status

**Pre-alpha. A basic English (US) keyboard works on a real device; most features are missing.**

What exists today:

- Gradle build (Kotlin DSL, version catalog, pinned toolchain)
- `:app` (Hilt application class), `:engine` (layout, geometry and input logic, with tests) and `:ime` (keyboard service and Canvas view)
- English (US) QWERTY keyboard: shift, backspace with hold-to-repeat, key previews and per-key haptics
- GitHub Actions: build, unit tests, Lint and Detekt on every push
- Signed APK and AAB release pipeline

What does not exist yet: other languages, number and symbol layers,
prediction, autocorrect, key sounds, settings, the companion app UI.

## Planned goals (not implemented)

- Bangla (Bangladesh) and English (US) first. Later English UK/CA, German,
  Arabic, Japanese. Each language needs real input handling, not just a layout.
- Suggestions learned on-device from your own typing. Prediction and
  autocorrect are separate systems, and you stay in control.
- No learning in password fields or incognito mode. Learned data can be
  viewed and reset.
- Smooth suggestion animations, distinct haptics, soft key sounds.
- Material 3 companion app with dynamic color and accessibility support.

## Privacy

Today: the only permission requested is VIBRATE (key haptics), with no internet
access, and cloud backup is disabled. Typed text is only committed to the text field:
it is not stored, logged or sent anywhere.

These are design rules for all future code: offline-first, no account, no
analytics, no typed text leaves the device. See [ARCHITECTURE.md](ARCHITECTURE.md).

## Getting builds

There is no local build. Everything is built by GitHub Actions:

- Debug APK: open the latest **CI** run in the Actions tab and download the
  `kboard-debug-apk` artifact.
- Signed releases: the Releases page.

## Repository layout

```

app/          Android application module
engine/       Pure Kotlin/JVM module (language, prediction, autocorrect)
gradle/       Version catalog and wrapper
docs/         Architecture decision records
.github/      CI, release and tooling workflows
scripts/ci/   Helper scripts used by workflows
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md), the [Code of Conduct](CODE_OF_CONDUCT.md) and the [Security Policy](SECURITY.md).

## License

GPL-3.0. See [LICENSE](LICENSE).

## Contact

- General: info@thekami.tech
- Development: dev@thekami.tech
- Website: https://www.thekami.tech
- Discord: https://www.thekami.tech/discord/
