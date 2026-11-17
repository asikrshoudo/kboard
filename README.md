# Kboard

> A keyboard that learns how you type, not how everyone types.

Kboard (Play Store title: *Kboard - TheKami Keyboard*) is an open-source,
privacy-first Android keyboard by [TheKami](https://www.thekami.tech).

## Status

**Pre-alpha.** English (US) and Bangla (phonetic) typing work, with suggestions
and autocorrect, and many features are still missing. See
[docs/status.md](docs/status.md) for exactly what works and what is planned.

## Privacy

The app and keyboard request no internet permission (the only permission is
VIBRATE, for key haptics), and CI fails if that ever changes. Cloud backup is
disabled. Kboard remembers the words you use, only on this phone, to suggest
them again. It never learns or suggests in password, email or web address
fields, never learns in incognito mode or when an app asks keyboards not to
learn, and you can delete everything it learned from the app. See
[docs/privacy.md](docs/privacy.md).

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
ime/          Keyboard service, Canvas keyboard view, suggestion strip,
toolbar, haptics and sounds
gradle/       Version catalog and wrapper
docs/         Topic documentation and architecture decision records
.github/      CI, release and tooling workflows
scripts/ci/   Helper scripts used by workflows

How it fits together: [ARCHITECTURE.md](ARCHITECTURE.md).

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
