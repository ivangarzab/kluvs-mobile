# :core:model

Domain models shared across the entire application.

## Purpose

This module contains the core domain entities that represent the business objects in Kluvs. These models are pure Kotlin data classes with no dependencies on frameworks or platform-specific code.

## Contents

| Model | Description |
|-------|-------------|
| `User` | Authenticated user with provider info |
| `Member` | Book club member with points and reading stats |
| `Club` | Book club with members, sessions, and metadata |
| `Book` | Book information (title, author, ISBN, etc.) |
| `Session` | Reading session with book and discussions |
| `Discussion` | Scheduled discussion event |
| `Server` | Discord server information |

## Dependencies

- `kotlinx-serialization` - For JSON serialization
- `kotlinx-datetime` - For date/time handling

## Usage

This module is a dependency of nearly all other modules. Import models directly:

```kotlin
import com.ivangarzab.kluvs.model.Club
import com.ivangarzab.kluvs.model.Member
```
