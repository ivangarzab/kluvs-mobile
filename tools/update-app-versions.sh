#!/bin/bash
# The purpose of this script is to automatically update the app version across both platforms:
#   - Android: versionName in composeApp/build.gradle.kts
#   - iOS:     MARKETING_VERSION in iosApp/Configuration/Config.xcconfig

if [ -z "$1" ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 1.0.0"
  exit 1
fi

VERSION_NAME="$1"
ANDROID_BUILD_FILE="composeApp/build.gradle.kts"
IOS_CONFIG_FILE="iosApp/Configuration/Config.xcconfig"

if [ ! -f "$ANDROID_BUILD_FILE" ]; then
  echo "Error: $ANDROID_BUILD_FILE not found!"
  exit 1
fi

if [ ! -f "$IOS_CONFIG_FILE" ]; then
  echo "Error: $IOS_CONFIG_FILE not found!"
  exit 1
fi

echo "Updating versions to: $VERSION_NAME"

# Android: read current versionCode and increment
CURRENT_VERSION_CODE=$(grep -m1 'versionCode' "$ANDROID_BUILD_FILE" | sed 's/[^0-9]//g')
NEW_VERSION_CODE=$((CURRENT_VERSION_CODE + 1))

if [[ "$OSTYPE" == "darwin"* ]]; then
  sed -i '' "s/versionCode = [0-9]*/versionCode = $NEW_VERSION_CODE/" "$ANDROID_BUILD_FILE"
  sed -i '' "s/versionName = \".*\"/versionName = \"$VERSION_NAME\"/" "$ANDROID_BUILD_FILE"
  sed -i '' "s/MARKETING_VERSION=.*/MARKETING_VERSION=$VERSION_NAME/" "$IOS_CONFIG_FILE"
else
  sed -i "s/versionCode = [0-9]*/versionCode = $NEW_VERSION_CODE/" "$ANDROID_BUILD_FILE"
  sed -i "s/versionName = \".*\"/versionName = \"$VERSION_NAME\"/" "$ANDROID_BUILD_FILE"
  sed -i "s/MARKETING_VERSION=.*/MARKETING_VERSION=$VERSION_NAME/" "$IOS_CONFIG_FILE"
fi

echo "🆙 Android version updated in $ANDROID_BUILD_FILE (versionCode: $CURRENT_VERSION_CODE → $NEW_VERSION_CODE)"
echo "🆙 iOS version updated in $IOS_CONFIG_FILE"