# :core:network

Network configuration and utilities for Supabase communication.

## Purpose

This module provides the foundational networking layer, including Supabase client configuration, custom serializers for API compatibility, and build configuration (API keys, URLs).

## Key Components

### BuildKonfig

Generated configuration object containing:
- `SUPABASE_URL` / `SUPABASE_KEY` - Production credentials
- `TEST_SUPABASE_URL` / `TEST_SUPABASE_KEY` - Test credentials

### Custom Serializers

Handle conversion between JSON numbers and Kotlin Strings for database IDs that are auto-increment integers but need to be treated as strings in the app.

## Dependencies

- `ktor-client` - HTTP client
- `supabase-kt` - Supabase client SDK
- `buildkonfig` - Build-time configuration

## Usage

```kotlin
import com.ivangarzab.kluvs.network.BuildKonfig

val url = BuildKonfig.SUPABASE_URL
```
