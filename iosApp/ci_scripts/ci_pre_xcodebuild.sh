#!/bin/sh
set -e
set -x

echo "::group::👾 Running KMP iOS Simulator Tests"

# Re-export Java (each Xcode Cloud script runs in a fresh shell)
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# Navigate to project root (up two levels from iosApp/ci_scripts)
cd ../..

# Run KMP iOS simulator tests
./gradlew iosSimulatorArm64Test -PexcludeTests="**/*IntegrationTest*" --continue

echo "::endgroup::"

echo "✅ KMP iOS tests passed"