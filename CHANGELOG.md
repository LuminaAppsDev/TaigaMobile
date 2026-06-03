# Changelog

All notable changes to this project are documented in this file.

## Unreleased

### Added

### Fixed

- Project selector screen rendering the search bar centered on the whole screen instead of inside the app bar (latent layout bug exposed by newer Compose versions)

### Changed

- Compose BOM 2024.12.01 → 2026.05.01; migrate the swipeable tab pager to SecondaryTabRow / SecondaryScrollableTabRow

### Removed

## 2.2.0 - 2026-05-07

### Added

- Pre-fill the server URL on the login screen with the previously used address
- Show a "session expired" message and redirect to login automatically when the refresh token is rejected by the server
- Encrypted on-disk storage for auth tokens, refresh tokens, and the server URL, backed by the Android Keystore (one-time migration of existing session data on first launch after upgrade)
- Backup-exclusion rules so session credentials and on-disk log files are excluded from cloud backup and device-transfer

### Fixed

- Stuck on dashboard with opaque errors after a long period of inactivity instead of being routed back to login
- Cascading empty-bearer retries when multiple requests received 401 simultaneously after a failed refresh
- Refresh response body not closed on every code path (potential connection-pool leak)

### Changed

- Disable HTTP request/response body logging in release builds to keep tokens out of on-disk log files
- Target Android 17 (compileSdk and targetSdk 37); library upgrades for core-ktx, activity-compose, navigation-compose, paging-compose, gson

## 2.1.0 - 2026-03-05

### Fixed

- Corrected api refresh call
- Avoid broken release build caused by too rigid minify
- Crash when opening the "Team" screen, due to legacy call in Material2 library
- Logout button not working

### Changed

- Update to gradle build system version 9
- Migration from Material2 to Material3
- Migration from Coil1 to Coil2
- Migration from paging-compose 1-alpha to 3-stable
- Libraries updates
- Migration of abandoned compose-material-dialogs library
- Migrate to DatePicker in Material3

## 2.0.0 - 2025-12-30

### Changed

- Release 2.0 forked from original project
