---
name: migrate-room-db
description: >
  Guide for creating a Room database migration in this project.
  Auto-loads when discussing schema changes, adding/removing columns,
  adding new entities, or any topic that requires a Room database migration.
  Covers the full workflow: interview, MigrationXToY.kt, entity update,
  DAO update, version bump, and migration registration.
allowed-tools: Read, Write, Edit, Grep, Glob
---

# Room Database Migration Skill

You are helping the user migrate the Room database in the `core/database` module of this KMP project.

## Current Database State

Dynamically load context before proceeding:

- **Database class:** `core/database/src/commonMain/kotlin/com/ivangarzab/kluvs/database/KluvsDatabase.kt`
- **Entities:** `core/database/src/commonMain/kotlin/com/ivangarzab/kluvs/database/entities/`
- **DAOs:** `core/database/src/commonMain/kotlin/com/ivangarzab/kluvs/database/dao/`
- **Existing migrations:** `core/database/src/commonMain/kotlin/com/ivangarzab/kluvs/database/migrations/`
- **Migration registration:** `core/database/src/commonMain/kotlin/com/ivangarzab/kluvs/database/GetKluvsDatabase.kt`
- **Exported schemas:** `core/database/schemas/`

Read all of the above before doing anything else.

## When Invoked Explicitly (`/migrate-room-db`)

### Step 1 — Interview the user

Ask in a single message:
1. What schema change is needed? (plain English is fine)
2. Are there any related domain model changes to be aware of?

Do NOT proceed to write files until the user has answered.

---

### Step 2 — Plan, then confirm

Based on the user's answer and the current DB state, draft a short plan:
- New version number (current + 1)
- Migration type (ADD COLUMN / DROP COLUMN / ADD TABLE / RENAME / etc.)
- Which files will be touched and how

Present the plan to the user. Proceed only after they confirm.

---

### Step 3 — Execute (in this order)

#### 3a. Create `MigrationXToY.kt`

File location: `core/database/src/commonMain/kotlin/com/ivangarzab/kluvs/database/migrations/`

Follow the pattern from the existing migration. Key rules:
- SQLite does **not** support `DROP COLUMN` directly. Use the copy-rename pattern:
  1. `CREATE TABLE <name>_new (...)`
  2. `INSERT INTO <name>_new SELECT ... FROM <name>`
  3. `DROP TABLE <name>`
  4. `ALTER TABLE <name>_new RENAME TO <name>`
  5. Recreate any indices that were on the original table
- For adding columns: use `ALTER TABLE <table> ADD COLUMN <col> <type>` with a safe default
- Nullable columns can default to `NULL`; non-nullable columns need a sensible default value

#### 3b. Update the `@Entity` class

Modify the entity in `entities/` to match the new schema. Keep field order consistent with the SQL column order.

#### 3c. Update the `@Dao` if needed

Only if new columns require new queries or existing queries need adjusting.

#### 3d. Bump the version in `KluvsDatabase.kt`

Increment `version = N` by 1.

#### 3e. Register the migration in `GetKluvsDatabase.kt`

Add the new migration constant to the `.addMigrations(...)` call.

---

### Step 4 — Remind the user

After writing all files, remind the user to:
1. Build the project so Room's KSP generates the updated code
2. Verify the exported schema file was updated in `core/database/schemas/`
3. Run the database entity tests in `core/database/src/commonTest/`

---

## When Auto-Invoked (Planning Context)

Use this skill's knowledge passively — inform plans and recommendations without writing files:
- Reference the correct files that will need to change
- Cite the SQLite `DROP COLUMN` workaround when a column removal is discussed
- Note that version bumps and migration registration are easy to forget — flag them in the plan
- Suggest the migration be its own focused commit/PR step