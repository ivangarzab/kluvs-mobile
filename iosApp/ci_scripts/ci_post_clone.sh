#!/bin/sh
# Exit on any error and print commands for the logs
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

echo "🏁 Environment ready. Returning to build..."