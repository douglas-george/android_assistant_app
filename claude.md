# Sweetbriar Android App

## Project Overview

Android app for Doug's personal AI assistant system. Communicates with a Mobile API on a home server (gmktek) to receive push notifications from AI advisors and send back simple responses.

**Package:** `com.sweetbriarai.mobile`
**Language:** Kotlin
**UI:** Jetpack Compose (Material 3)
**Min SDK:** 30 (Android 11)
**Target SDK:** 35

## Architecture

```
Mobile API (mobile.sweetbriarai.com)  ←→  This App
         ↕
Firebase Cloud Messaging (push notifications)
```

The app talks exclusively to the Mobile API. It never connects to the assistant-service, database, or any other backend service directly.

## Backend Reference

The Mobile API is a separate FastAPI service deployed on the home server. It's built and maintained via the `home_hosting` repo — NOT this repo.

**API base URL:** `https://mobile.sweetbriarai.com`
**Auth:** Bearer token in `Authorization` header
**Endpoints the app uses:**
- `POST /devices/register` — register/update FCM token
- `GET /messages` — list messages (query params: status, limit, since)
- `GET /messages/{id}` — single message detail
- `POST /messages/{id}/respond` — submit response
- `POST /messages/{id}/read` — mark as read
- `GET /health` — health check (no auth)

## Key Concepts

**Message types:**
- `info` — informational notification, no response needed
- `yes_no` — notification with Yes/No action buttons
- `choice` — multiple choice (2-4 options), rendered as action buttons (≤3) or in-app UI (4)

**Notification behavior:**
- Yes/No responses are handled via `BroadcastReceiver` — user responds from notification without opening app
- Choice responses with ≤3 options use notification action buttons
- All notifications bridge automatically to Pixel Watch via FCM (no Wear OS code needed)

**Auth storage:** Bearer token stored in `EncryptedSharedPreferences`

## Conventions

- **Changelog:** Append to `CHANGELOG.md` in the repo root after every session. Copy to the Cowork exchange folder at `C:\Users\jay_d\Desktop\Cowork\advisors\tech_stack_advisor\claude_code_android_app\CHANGELOG.md`
- **Briefings:** Read implementation specs from `C:\Users\jay_d\Desktop\Cowork\advisors\tech_stack_advisor\claude_code_android_app\briefings\`
- **No secrets in git:** Firebase credentials (`google-services.json`) are gitignored but needed locally. Bearer tokens are entered at runtime.

## Who's Who

- **Doug** — owner, makes design decisions
- **Cowork (Tech Stack Advisor)** — architect, writes specs and briefings
- **Claude Code (this session)** — builds the Android app
- **Claude Code (home_hosting)** — separate session, builds the server-side Mobile API

## Dependencies

See `app/build.gradle.kts` for full list. Key libraries:
- Jetpack Compose + Material 3
- Retrofit + OkHttp (networking)
- Firebase Cloud Messaging (push)
- AndroidX Security (encrypted storage)
- WorkManager (background sync)
