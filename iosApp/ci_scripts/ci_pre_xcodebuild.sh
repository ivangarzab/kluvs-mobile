#!/bin/sh
set -e
set -x

echo "👾 Running KMP iOS Simulator Tests"

# Re-export Java (each Xcode Cloud script runs in a fresh shell)
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# Navigate to project root (up two levels from iosApp/ci_scripts)
cd ../..

# Run KMP iOS simulator tests (with increased timeout and additional retries)
./gradlew iosSimulatorArm64Test \
  -Dorg.gradle.internal.http.connectionTimeout=300000 \
  -Dorg.gradle.internal.http.socketTimeout=300000 \
  '-PexcludeTests=**/*IntegrationTest*' --continue

echo "✅ KMP iOS tests passed"