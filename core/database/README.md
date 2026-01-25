# Core: Database Module

This module provides the Room database layer for local data persistence and caching in the Kluvs app.

## Purpose

The database module enables:
- **Persistent storage** - Data survives app restarts
- **Offline support** - App can function without network connectivity
- **Performance** - Reduces remote API calls via TTL-based caching
- **Scalability** - Supports complex queries, search, and large datasets

## Architecture

This module uses Room 2.7+ for Kotlin Multiplatform, providing a shared database layer that works on both Android and iOS.

### Components

- **Entities** - Room entity classes representing database tables
- **DAOs** - Data Access Objects for CRUD operations
- **Database** - Main database class that ties everything together
- **DatabaseBuilder** - Platform-specific database initialization (expect/actual)

## Entities

| Entity | Table Name | Purpose | TTL |
|--------|-----------|---------|-----|
| `ServerEntity` | servers | Discord servers | 7 days |
| `ClubEntity` | clubs | Book clubs | 24 hours |
| `MemberEntity` | members | Club members | 24 hours |
| `SessionEntity` | sessions | Reading sessions | 6 hours |
| `BookEntity` | books | Book metadata | 7 days |
| `ClubMemberCrossRef` | club_members | Club-Member relationships | N/A |

All entities include a `lastFetchedAt` timestamp field for TTL-based cache invalidation.

## DAOs

Each entity has a corresponding DAO interface:

- `ServerDao` - Server CRUD operations
- `ClubDao` - Club CRUD operations and server relationships
- `MemberDao` - Member CRUD operations and club relationships
- `SessionDao` - Session CRUD operations and club relationships
- `BookDao` - Book CRUD operations

All DAOs provide:
- `get*()` - Retrieve single or multiple entities
- `insert*()` - Insert or update entities
- `delete*()` - Delete entities
- `getLastFetchedAt()` - Get timestamp for cache validation
- `deleteAll()` - Clear all data (for sign-out)

## Database Initialization

The database uses platform-specific builder functions following the official Room KMP pattern:

### Android

```kotlin
// In your DI setup or Application class
val database = getRoomDatabase(
    getDatabaseBuilder(context)
)
```

### iOS

```kotlin
// In your DI setup
val database = getRoomDatabase(
    getDatabaseBuilder()
)
```

The `getRoomDatabase()` function configures:
- `BundledSQLiteDriver()` for cross-platform SQLite support
- `Dispatchers.IO` for query coroutines

## Usage

This module is used by the `core:data` module to implement local data sources. It should not be used directly by feature modules.

See `core:data` for the local data source implementations that use this database.

## Testing

Unit tests verify:
- Entity creation and equality
- Data class copy operations
- Null handling

Integration tests (in `core:data`) verify:
- DAO operations work correctly
- Database transactions
- Cache TTL behavior

## Dependencies

- `androidx.room:room-runtime` - Room database runtime
- `kotlinx-datetime` - Timestamp handling for TTL
- `kotlinx-coroutines-core` - Suspend functions

## Future Enhancements

- Migration strategies for schema changes
- Database encryption for sensitive data
- Full-text search capabilities
- Database observers for reactive updates
