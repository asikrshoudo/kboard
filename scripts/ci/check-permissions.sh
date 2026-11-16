#!/usr/bin/env bash
# Lists the permissions an APK asks for and fails if it asks for internet access.
# Kboard works offline: nothing typed may leave the phone.
# Usage: check-permissions.sh path/to/app.apk
set -euo pipefail

apk="${1:?usage: check-permissions.sh <apk>}"
aapt2="$(ls -d "$ANDROID_HOME"/build-tools/* | sort -V | tail -1)/aapt2"

permissions="$("$aapt2" dump permissions "$apk")"
echo "Permissions requested by $apk:"
echo "$permissions"

if echo "$permissions" | grep -q "android.permission.INTERNET"; then
  echo "ERROR: the app requests INTERNET. Kboard must work offline." >&2
  exit 1
fi
echo "OK: no INTERNET permission."
