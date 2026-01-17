#!/bin/sh

# Move up to the repository root to find gradlew
cd ..
chmod +x gradlew

# Install Java 17
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
export JAVA_HOME="/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home"

echo "✅ Environment ready. JAVA_HOME is $JAVA_HOME"