# Firebase + Gemini AI Integration Plan

## Overview
Integrating Firebase (Auth, Firestore, FCM) and Gemini AI into the Do It Now Motivation Tracker app.

## What You Need To Do (Setup)
1. **Firebase Project**: Go to [Firebase Console](https://console.firebase.google.com), create a project, add Android app with package `com.karan.do_it_now_motivation_tracker`, download `google-services.json` → place in `app/` folder
2. **Enable Auth**: In Firebase Console → Authentication → Enable Email/Password sign-in
3. **Enable Firestore**: Firebase Console → Firestore Database → Create database
4. **Enable FCM**: Firebase Console → Cloud Messaging → Enable
5. **Gemini API Key**: Go to [AI Studio](https://aistudio.google.com/apikey), create free key, add to `local.properties`: `GEMINI_API_KEY=your_key_here`

## Features Being Added

### 🔐 Firebase Auth (Login/Signup)
- Email/Password authentication
- Auth state persistence
- Login → Signup flow before dashboard

### 🔄 Firestore Sync
- Goals synced to cloud when logged in
- User stats backed up
- Cross-device data access

### 🔔 FCM Notifications
- Push notification service
- Notification channel setup
- Goal reminder notifications

### 🤖 Gemini AI (All Features)
- **AI Motivation Coach**: Chat screen with personalized advice
- **Smart Goal Suggestions**: AI suggests goals based on user patterns
- **Daily AI Insights**: Dashboard card with AI-generated insights
- **Goal Breakdown**: AI breaks goals into actionable steps

## Files Modified
| File | Change |
|------|--------|
| `libs.versions.toml` | Add Firebase, Gemini, WorkManager versions |
| `build.gradle.kts` (root) | Add google-services plugin |
| `app/build.gradle.kts` | Add dependencies, buildConfig |
| `AndroidManifest.xml` | Permissions, FCM service |
| `AppNavigation.kt` | New routes (login, signup, aiCoach) |
| `DashboardScreen.kt` | AI insights card |
| `ProfileScreen.kt` | Logout button, user info |
| `MainActivity.kt` | Firebase init |
| `AppDatabase.kt` | Version bump |

## Files Created
| File | Purpose |
|------|---------|
| `screens/LoginScreen.kt` | Login UI |
| `screens/SignUpScreen.kt` | Signup UI |
| `screens/AiCoachScreen.kt` | AI chat interface |
| `viewmodel/AuthViewModel.kt` | Auth state management |
| `viewmodel/AiViewModel.kt` | Gemini AI logic |
| `data/FirestoreRepository.kt` | Firestore CRUD |
| `services/DoItNowMessagingService.kt` | FCM handler |
