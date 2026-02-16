---
name: bark-logging
description: >
  Logging rules for this project. Auto-loads whenever writing, reviewing,
  or discussing log statements, Bark calls, log levels, debug output,
  error handling with logs, caught and handled exceptions, or any code that produces log output.
user-invocable: false
---

# Bark Logging Rules

This project uses **barK** for all logging. Never use `println`, `Log.*`, or any other logging mechanism.

## Import

```kotlin
import com.ivangarzab.bark.Bark
```

## Tagging

- Do NOT declare a `TAG` constant. barK auto-detects the calling class name.
- Only use `Bark.tag("NAME")` when initializing a sub-module where all logs must share one identifier (e.g., SDK init).

## Log Levels — Use the Right One

| Call | When to use |
|------|-------------|
| `Bark.v(...)` | High-frequency data or large payloads (raw API responses, scroll offsets) |
| `Bark.d(...)` | Internal logic flow — verifying a function completed its task during development |
| `Bark.i(...)` | User-centric milestones — describe the user's journey at a high level |
| `Bark.w(...)` | Recoverable issues or unexpected states that don't stop the app |
| `Bark.e(...)` | Failed operations that impact the user but are not fatal |
| `Bark.c(...)` | Fatal failures — app state is fundamentally compromised |

## Message Formatting

- **Always capitalize** the first letter of every log message.
- Follow the pattern: `"[Action]: [Details] (ID: [identifier])"`
- Always include relevant IDs (user ID, club ID, session ID, etc.)

```kotlin
// Correct
Bark.d("Fetching club (ID: $clubId)")
Bark.i("Navigation: Clubs → Club Details (ID: $clubId)")
Bark.w("Cache miss for user (ID: $userId); fetching from network")

// Wrong
Bark.d("fetching club")
Bark.d("got result: $result")
```

## Exceptions

- Always pass the exception object as the second argument to `Bark.w` or `Bark.e`.
- Provide a one-sentence summary before the stack trace.
- Log the recovery action if one is taken.

```kotlin
Bark.e("Failed to upload profile photo for user (ID: $userId). Retrying.", exception)
Bark.w("Network timeout fetching clubs. Serving cached data.", exception)
```

## API Logging

- Log requests and responses at `Bark.v(...)` only.
- Never log passwords, auth tokens, or personally identifiable information.
- In release builds: only log simple success summaries — no response bodies.
- In debug builds: full request/response data is fine (excluding sensitive fields).

## Prohibited

- Never use `println`, `Log.d`, `Log.e`, or any Android/platform log directly.
- Never call `Bark.muzzle()` inside feature logic — only in global config or test setup.
- Never log passwords, auth tokens, or PII.

## In Tests

Use `BarkTestRule` (defined in `shared/src/commonTest/`) to configure barK for test output.
Do not call `Bark.muzzle()` or set up trainers manually inside individual test cases.