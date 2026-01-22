#!/bin/sh
# Exit on any error and print commands for the logs
set -e
set -x

echo "🧪 Starting KMP iOS tests..."

# Re-export Java (each Xcode Cloud script runs in a fresh shell)
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# Navigate to project root (up two levels from iosApp/ci_scripts)
cd ../..

# Run KMP iOS simulator tests
./gradlew iosSimulatorArm64Test -PexcludeTests="**/*IntegrationTest*" --continue

echo "✅ KMP iOS tests passed"