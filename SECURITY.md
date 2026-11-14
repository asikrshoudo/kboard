# Security Policy

## Supported versions

Kboard has no public release yet. Once releases exist, the latest release is
the supported one.

## Reporting a vulnerability

Please do **not** open a public issue for security problems. Email
**dev@thekami.tech** with a description, steps to reproduce and the affected
version or commit. We will acknowledge your report as soon as we can and
keep you updated on the fix. Please allow time for a fix before disclosing
the issue publicly.

## What counts as a security issue

Because Kboard is a keyboard, privacy bugs are security bugs. Please report:

- typed text leaving the device in any way
- learning or storing text in password fields or incognito mode
- learned data that cannot be reset
- a leaked signing key or secret in the repository or CI logs
- vulnerabilities in dependencies that affect Kboard

## Design commitments

Kboard is offline-first, needs no account and is designed to ship without
network access or analytics. See [ARCHITECTURE.md](ARCHITECTURE.md) for what
is implemented today and what is still planned.
