#!/bin/sh
set -e
set -x

echo "🚀 Starting post-cloning task for KMP build"

# 1. Install Java 17 (Skip auto-update to save time)
HOMEBREW_NO_AUTO_UPDATE=1 brew install openjdk@17

# 2. Set JAVA_HOME using the brew prefix (Avoids sudo/symlinks)
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# 3. Verify Java
echo "✅ Java version installed:"
java -version

# 4. Fix Gradle Permissions (Moving up two levels from iosApp/ci_scripts)
cd ../..
if [ -f "./gradlew" ]; then
    echo "✅ Found gradlew at $(pwd). Setting permissions..."
    chmod +x gradlew
else
    echo "❌ ERROR: gradlew not found! Current dir is: $(pwd)"
    ls -F
    exit 1
fi

# 5. SETUP SENTRY (Must happen first!)
# This ensures the .xcframework is downloaded and ready for the test linking
echo "⬇️ Fetching Sentry Framework..."
./gradlew setupSentryForCi --refresh-dependencies

# 6. RUN TESTS
# We run this here to gate the build. If this fails, the build stops.
echo "🧪 Running KMP Simulator Tests..."
./gradlew iosSimulatorArm64Test '-PexcludeTests=**/*IntegrationTest*' --continue

echo "✅ Post-clone complete. Tests Passed. Environment ready for Xcode build. 🏁"