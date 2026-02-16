---
name: sync-api-contract
description: >
  Guides the process of applying backend API contract changes to the mobile
  codebase. Auto-loads when discussing API changes, new endpoints, modified
  response fields, renamed fields, removed fields, DTO updates, openapi spec
  changes, or Supabase schema changes that affect the mobile data layer.
allowed-tools: Read, Write, Edit, Grep, Glob
---

# Sync API Contract Skill

When the backend changes its API — new fields, removed fields, new endpoints,
renamed properties — this skill walks through every layer that needs to change
and does it in the right order.

---

## Accepting Input

This skill accepts whatever context you have. You don't need all three — any one is enough to start:

- **OpenAPI/Swagger spec** — provide the file path (e.g. `/Users/.../kluvs-api/openapi.yml`) or paste the relevant excerpt
- **Backend DB migration SQL** — provide the `.sql` file path or paste the migration content
- **Plain English** — describe what changed (e.g. "the book endpoint now returns a `rating` field")

Multiple inputs can be combined for higher confidence.

---

## Step 1 — Read the current state

Before analyzing anything, read these files:

- `core/data/src/commonMain/kotlin/com/ivangarzab/kluvs/data/remote/dtos/Dtos.kt` — all DTOs
- `core/data/src/commonMain/kotlin/com/ivangarzab/kluvs/data/remote/mappers/` — all mappers
- `core/model/src/commonMain/kotlin/com/ivangarzab/kluvs/model/` — all domain models
- The relevant Service file(s) in `core/data/src/commonMain/kotlin/com/ivangarzab/kluvs/data/remote/api/`

If a spec or migration file was provided, read that too.

---

## Step 2 — Analyze and diff

Compare the incoming change against the current state. For each affected entity, determine:

| Change type | Layers affected |
|---|---|
| New field added to response | DTO → mapper → domain model |
| Field removed from response | DTO → mapper → domain model → check Room entity |
| Field renamed | DTO → mapper |
| New endpoint added | Service → RemoteDataSource → Repository → Koin registration |
| Endpoint removed | Service → RemoteDataSource → Repository → Koin cleanup |
| Request body changed | Request DTO → Service → RemoteDataSource |
| Response type changed | Response DTO → mapper → domain model |

---

## Step 3 — Present impact report

Before touching any file, present a clear impact summary:

```
## API Change Impact

### Changed: Book endpoint
- Added field: `rating` (Float, nullable)

### Files to update:
1. BookDto — add `val rating: Float? = null`
2. BookMappers.kt — map `rating` to domain
3. Book.kt (domain model) — add `val rating: Float? = null`

### Room cache — flagged:
BookEntity caches book data. If `rating` should be cached locally,
a Room migration is required (see migrate-room-db skill).
Do you want to cache this field? [yes / no / skip for now]
```

Wait for user confirmation before writing any files.

---

## Step 4 — Execute in this order

Always apply changes in this sequence — each layer depends on the one before it:

1. **Domain model** (`core/model`) — the target shape everything maps to
2. **DTO** (`Dtos.kt`) — match the new API contract
3. **Mapper** (`mappers/`) — bridge the updated DTO to the updated domain model
4. **Service** (`remote/api/`) — only if endpoint signature changed
5. **RemoteDataSource** (`remote/source/`) — only if a new method is needed
6. **Repository** (`repositories/`) — only if new capability is exposed
7. **Koin** (`CoreDataModule.kt`) — only if new classes were added

---

## Step 5 — Room cache handoff

After all data layer changes are applied, evaluate whether any changed field
is stored in a Room entity (`core/database/src/commonMain/.../entities/`).

If yes, state clearly:
> "The `{field}` field on `{Entity}` needs a Room migration.
> Refer to the **migrate-room-db** skill to complete this step."

Provide the migration context: what changed, old schema, new schema.
Do not write migration files in this skill — that belongs to `migrate-room-db`.

---

## Conventions to follow

**DTOs** — all in `Dtos.kt`, snake_case field names, always `@Serializable`:
```kotlin
@Serializable
data class BookDto(
    val id: String,
    val title: String,
    val rating: Float? = null  // new nullable fields always default to null
)
```

**Mappers** — extension functions, `toDomain()` pattern:
```kotlin
fun BookDto.toDomain(): Book = Book(
    id = id,
    title = title,
    rating = rating
)
```

**Domain models** — camelCase, no serialization annotations, use `kotlinx.datetime` types for dates:
```kotlin
data class Book(
    val id: String,
    val title: String,
    val rating: Float? = null
)
```

**Never** add serialization annotations (`@Serializable`, `@SerialName`) to domain models.
**Never** add business logic to DTOs.
**Never** skip the mapper — domain models must not reference DTO types.