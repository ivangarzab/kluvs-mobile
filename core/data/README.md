# :core:data

Data layer for fetching and managing club, member, session, and server data.

## Purpose

This module implements the repository pattern, providing a clean abstraction over remote data sources. It handles API communication via Supabase Edge Functions, DTO-to-domain mapping, and exposes repository interfaces for the rest of the app.

## Architecture

```
Repository → RemoteDataSource → Service → Supabase Edge Functions
                ↓
            Mappers (DTO → Domain)
```

## Key Components

### Repositories

- `ClubRepository` - CRUD operations for clubs
- `MemberRepository` - Member profiles and stats
- `SessionRepository` - Reading sessions and discussions
- `ServerRepository` - Discord server data

### Services

Direct communication with Supabase Edge Functions. Each service corresponds to an edge function endpoint.

## Dependencies

- `:core:model` - Domain models
- `:core:network` - Supabase client, serializers
- `:core:auth` - For authenticated requests

## Usage

```kotlin
class MyViewModel(private val clubRepo: ClubRepository) {
    fun loadClub(id: String) {
        viewModelScope.launch {
            clubRepo.getClub(id)
                .onSuccess { club -> /* handle */ }
                .onFailure { error -> /* handle */ }
        }
    }
}
```
