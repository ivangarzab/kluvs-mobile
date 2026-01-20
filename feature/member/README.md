# :feature:member

Member profile and statistics UI feature module.

## Purpose

This module contains the presentation layer for the user's profile screen ("Me" tab). It shows the current user's profile, reading statistics, and currently reading books across all their clubs.

## Key Components

### MeViewModel

Manages:
- Loading user profile by auth user ID
- Fetching reading statistics
- Fetching currently reading books across all clubs
- Parallel data loading with error aggregation
- Sign out functionality

### UseCases

| UseCase | Description |
|---------|-------------|
| `GetCurrentUserProfileUseCase` | Gets member profile with formatted join date |
| `GetCurrentlyReadingBooksUseCase` | Gets active books from all user's clubs |
| `GetUserStatisticsUseCase` | Gets points and books read count |

### UI Models

- `UserProfile` - Display name, avatar, join date
- `CurrentlyReadingBook` - Book with club name and due date
- `UserStatistics` - Points and books read

## Dependencies

- `:core:model` - Domain models
- `:core:data` - MemberRepository, ClubRepository
- `:core:auth` - AuthRepository for sign out
- `:core:presentation` - FormatDateTimeUseCase

## Usage

```kotlin
@Composable
fun MeScreen(viewModel: MeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    MeContent(
        profile = state.profile,
        statistics = state.statistics,
        currentlyReading = state.currentlyReadingBooks,
        onSignOut = viewModel::signOut
    )
}
```
