#!/bin/sh
set -e
set -x

echo "👾 Running KMP iOS Simulator Tests"

# Re-export Java (each Xcode Cloud script runs in a fresh shell)
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

cd ../..

# Setup Sentry -> Run Tests
./gradlew setupSentryForCi
./gradlew iosSimulatorArm64Test '-PexcludeTests=**/*IntegrationTest*' --continue

echo "✅ KMP iOS tests passed"