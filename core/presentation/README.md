# :core:presentation

Shared presentation utilities and base components.

## Purpose

This module provides common presentation-layer utilities that are shared across feature modules. It includes state models, formatting utilities, and base classes for ViewModels.

## Key Components

### FormatDateTimeUseCase

Formats `LocalDateTime` into human-readable strings with various formats:
- `FULL` - "February 12, 2025 at 7:00 PM"
- `DATE_ONLY` - "February 12, 2025"
- `TIME_ONLY` - "7:00 PM"
- `YEAR_ONLY` - "2025"

### DateTimeFormat

Enum defining available formatting options for dates and times.

## Dependencies

- `:core:model` - Domain models
- `androidx.lifecycle.viewmodel` - ViewModel base class
- `kotlinx-datetime` - Date/time handling

## Usage

```kotlin
class MyViewModel(
    private val formatDateTime: FormatDateTimeUseCase
) : ViewModel() {

    fun formatDate(date: LocalDateTime): String {
        return formatDateTime(date, DateTimeFormat.FULL)
    }
}
```
