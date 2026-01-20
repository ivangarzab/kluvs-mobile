# :feature:clubs

Club details and reading session UI feature module.

## Purpose

This module contains the presentation layer for viewing club details, active reading sessions, discussion timelines, and member lists. It provides ViewModels and UseCases for the club-related screens.

## Key Components

### ClubDetailsViewModel

Manages:
- Loading club details, active session, and members in parallel
- Club switching for users with multiple clubs
- Refresh functionality
- Error aggregation from multiple data sources

### UseCases

| UseCase | Description |
|---------|-------------|
| `GetClubDetailsUseCase` | Transforms Club → ClubDetails (UI model) |
| `GetActiveSessionUseCase` | Gets current session with discussion timeline |
| `GetClubMembersUseCase` | Gets members sorted by points |
| `GetMemberClubsUseCase` | Gets list of clubs for a user |

### UI Models

- `ClubDetails` - Club overview with member count, current book
- `ActiveSessionDetails` - Session with book info and discussion timeline
- `ClubMemberInfo` - Member display info (name, points, books read)
- `DiscussionTimelineItemInfo` - Discussion with status (past/next/future)

## Dependencies

- `:core:model` - Domain models
- `:core:data` - Repositories
- `:core:auth` - For user context
- `:core:presentation` - FormatDateTimeUseCase

## Usage

```kotlin
@Composable
fun ClubScreen(viewModel: ClubDetailsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUserClubs(userId)
    }

    ClubContent(
        clubDetails = state.currentClubDetails,
        activeSession = state.activeSession,
        members = state.members
    )
}
```
