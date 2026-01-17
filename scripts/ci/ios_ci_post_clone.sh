#!/bin/sh

# Fail the build if any command fails
set -e

# Install and set Java 17
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
export JAVA_HOME="/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home"

echo "JAVA_HOME is set to $JAVA_HOME"
java -version