# Logging Strategy

**NOTE:** This docs has been spiritually migrated into .claude/skills/bark-logging.md

This project uses [**barK**](http://github.com/ivangarzab/barK) for all logging. 

To maintain a clean and actionable Logcat, all contributors (including AI agents) must adhere to these guidelines.

## 1. Tagging Protocol
* **Automatic Detection:** By default, do not provide a `TAG` constant. **barK** automatically detects the calling class name.
* **Global Overrides:** Use `Bark.tag("SDK_NAME")` only when initializing a specific sub-module where all logs should be grouped under a single identifier.

## 2. Level Definitions
We use the six levels defined in **barK**. Use them as follows:

| Level | Usage in kluvs-mobile | Example |
| :--- | :--- | :--- |
| **VERBOSE** | High-frequency data or large payloads. Used for raw API responses or scroll offsets. | `Bark.v("Raw JSON: $json")` |
| **DEBUG** | Internal logic flow. Use this to verify that a specific function completed its task during development. | `Bark.d("Calculation finished: $result")` |
| **INFO** | **User-centric milestones.** These logs should describe the user's journey at a high level. | `Bark.i("Navigation: Profile -> Settings")` |
| **WARNING** | Recoverable issues or unexpected states that don't stop the app. | `Bark.w("Cache miss; fetching from network")` |
| **ERROR** | Failed operations that impact the user experience but are not fatal. | `Bark.e("Failed to upload profile photo", e)` |
| **CRITICAL** | **Fatal failures.** Use this when the app state is fundamentally compromised (maps to `Log.wtf` on Android). | `Bark.c("Database corruption detected")` |

## 3. Environment Configuration
* **Debug Builds:** All levels are active via `AndroidLogTrainer(volume = Level.VERBOSE)`.
* **Unit Tests:** Use the `ColoredTestTrainer` to ensure logs are visible and readable in the IDE console.
* **Release Builds:** All logs below `Level.WARNING` must be silenced or redirected to a crash reporting trainer.

## 4. Message Formatting
* **Capitalization:** Every log message must start with a capital letter.
* **Consistent Pattern:** Use a pattern like `"[Action]: [Details] (ID: [identifier])"` to maintain clarity.
* **Include IDs:** Always include relevant IDs (user ID, club ID, etc.) so operations are identifiable.
* **Examples:**
  - `Bark.d("Fetching club (ID: 123)")`
  - `Bark.i("Navigation: Clubs → Club Details (ID: 456)")`
  - `Bark.w("Cache miss for user (ID: 789); fetching from network")`

## 5. API Logging
* **Request/Response Details:** Log API requests and responses at **VERBOSE** level only.
* **Never Log Sensitive Data:** Exclude passwords, auth tokens, and personal information from logs.
* **Release Builds:** Only log simple success summaries like `"Fetched clubs (count: 5)"` — no response bodies or request details.
* **Debug Builds:** Log full request/response data (minus sensitive fields) for troubleshooting.

## 6. Exception Handling
* **Always Include Stack Traces:** When logging exceptions in WARNING/ERROR, always include the exception object.
* **Simple Summaries:** Provide a one-sentence summary of the issue before the stack trace.
* **Include Recovery:** If applicable, log the recovery action being taken.
* **Examples:**
  - `Bark.e("Failed to update profile for user (ID: 456). Retrying in 5 seconds.", exception)`
  - `Bark.w("Network timeout while fetching clubs. Serving cached data.", exception)`

## 7. Prohibited Logging
* **No PII:** Never log passwords, auth tokens, or personally identifiable information.
* **No Muzzle in Logic:** `Bark.muzzle()` should only be used in global configuration or test setup, never within feature logic.