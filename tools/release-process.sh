#!/bin/bash
# The purpose of this script is to sequentially execute the different steps from the release
# process one after the other, with the intent of partially automating the given process.

if [ -z "$1" ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 1.0.0"
  exit 1
fi

VERSION_NAME="$1"

# Ensure all scripts are executable
echo "🔧 Setting script permissions..."
chmod +x tools/release/release-step1.sh
chmod +x tools/release/release-step2.sh
chmod +x tools/release/release-step3.sh
chmod +x tools/release/release-step4.sh
chmod +x tools/update-app-version.sh

echo "🚀 Starting automated release process for v$VERSION_NAME"
echo "=================================================="

# Function to run a step and handle errors
run_step() {
  local step_script="$1"
  local step_name="$2"

  echo ""
  echo "Running $step_name... ⏳"

  if ! "$step_script" "$VERSION_NAME"; then
    echo "______________________________"
    echo "❌  Error: $step_name failed!"
    echo "Release process aborted."
    exit 1
  fi

  echo "☑️ $step_name completed successfully"
}

# Execute all steps
run_step "./tools/release/release-step1.sh" "Step 1: Branch Setup"
run_step "./tools/release/release-step2.sh" "Step 2: Version Update"
run_step "./tools/release/release-step3.sh" "Step 3: Branch Merging"
run_step "./tools/release/release-step4.sh" "Step 4: Push to Origin"

echo "=========================================="
echo "🎉 Release process completed successfully!"
echo "📦 Android: GitHub Actions will build and distribute v$VERSION_NAME via Firebase App Distribution."
echo "📦 iOS:     Xcode Cloud will build and push v$VERSION_NAME to TestFlight."
echo "🔗 Check: https://github.com/ivangarzab/kluvs-mobile/actions"