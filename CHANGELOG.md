# Sweetbriar Android App — Changelog

## 2026-04-01 — Project Bootstrap

**Initial project setup:**
- Android project created: Kotlin + Jetpack Compose + Material 3
- Retrofit API client for Mobile API (device registration, messages, responses)
- Firebase Cloud Messaging service for push notifications
- Notification system with action buttons (yes/no, multiple choice)
- BroadcastReceiver for responding from notifications without opening app
- EncryptedSharedPreferences for auth token storage
- Settings screen for first-launch configuration
- Message list and detail screens
- WorkManager periodic sync (15-min fallback for missed FCM)
- Automatic Wear OS notification bridging (no watch-specific code)