#!/usr/bin/env bash
set -o pipefail
gradle build --no-daemon 2>&1 | tee build.log
rc=$?
{
echo "Toplam hata: $(grep -c ' error: ' build.log)"
echo "<pre>"
grep ' error: ' build.log | sed -E 's/^.* error: //; s/[A-Za-z0-9_.$]*class_[0-9]+/class_N/g' | sort | uniq -c | sort -rn | head -25
echo "</pre>"
echo "<pre>"
grep ' error: ' build.log | sed -E 's|^.*/src/main/java/||; s/:[0-9]+: error:.*//' | sort | uniq -c | sort -rn | head -25
echo "</pre>"
} >> "$GITHUB_STEP_SUMMARY"
exit $rc
