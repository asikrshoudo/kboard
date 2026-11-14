# Contributing to Kboard

Thanks for helping. Kboard aims to be a production-quality keyboard, so
changes are reviewed for quality, privacy and maintainability.

Please read the [Code of Conduct](CODE_OF_CONDUCT.md) first.

## Workflow

1. Open an issue describing the problem or idea before large changes.
2. Fork, branch from `main`, keep the change small and focused.
3. Open a pull request. CI must be green: build, unit tests, Lint, Detekt.
4. No local Gradle build is required. CI builds everything, so a phone with
   Git is enough. You may use any editor or IDE you like.

Commit messages start with a type: `feat`, `fix`, `docs`, `build`, `ci`,
`test`, `refactor` or `chore`, for example `feat: add Bangla layout data`.

## Code quality

Reviewers will reject:

- dead code, unused dependencies, duplicated logic
- giant classes or "god objects"
- unexplained magic numbers (name them or document them)
- fake or placeholder implementations presented as finished
- hardcoded secrets

Prefer small files with one clear responsibility. Add tests for logic in
text input, cursor handling, prediction, autocorrect, language switching,
the personal dictionary and privacy rules. New dependencies need a reason in
the pull request and must be pinned in `gradle/libs.versions.toml`.

## Privacy rules (non-negotiable)

- Never add the `INTERNET` permission or any network call.
- Never add analytics or crash-reporting SDKs.
- Never log, store or transmit typed text outside the local, user-controlled
  personal dictionary.
- Learning must stay disabled in password fields and incognito mode.

## Documentation

Documentation must match the code. Do not describe unfinished work as done.
Record significant decisions in `docs/decisions/` and update `CHANGELOG.md`
under `[Unreleased]`.

## License

By contributing you agree that your work is licensed under GPL-3.0, the
license of this project.
