<div align="center">

# ⚔️ DO IT NOW

### *Your goals. Your grind. Your legend.*

**A gamified productivity tracker for Android — where discipline meets dopamine.**

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com)
[![Gemini AI](https://img.shields.io/badge/Gemini%20AI-8E75B2?style=for-the-badge&logo=google&logoColor=white)](https://deepmind.google/technologies/gemini/)
[![Room](https://img.shields.io/badge/Room%20DB-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/androidx/releases/room)
[![Status](https://img.shields.io/badge/Status-Active%20Development-brightgreen?style=for-the-badge)]()

<br/>

> *"Most productivity apps track tasks. Do It Now tracks Warriors."*

</div>

---

## 🧠 What is Do It Now?

**Do It Now** is not your average to-do list.

It's a **role-playing productivity system** built for people who are serious about achieving their goals. Every mission you complete earns you XP. Every streak you maintain levels up your character. Fail a mission — and your warrior feels it.

And when you're lost, confused, or need someone to light the fire under you?

**Meet SENSEI** — your AI-powered personal coach, built right into the app.

---

## 🤖 Meet SENSEI — Your AI Productivity Coach

<div align="center">

```
┌─────────────────────────────────────────────┐
│                   SENSEI                    │
│         ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓         │
│  "I see you have 3 active missions and      │
│   47 XP. You're 53 XP away from Level 2.   │
│   Complete your BOSS mission now. I'll      │
│   wait. I always do."                       │
│                              — SENSEI       │
└─────────────────────────────────────────────┘
```

</div>

SENSEI is powered by **Google's Gemini AI** and is deeply integrated with your personal data:

- 🎯 Knows your **active goals**, their deadlines, and difficulty levels
- 📊 Reads your **current XP**, level, and title in real time
- 🔥 Tracks your **streak** and pushes you before it breaks
- 💬 Maintains full **multi-session chat history** — every conversation is saved and resumable
- ⌨️ Replies with a signature **typewriter animation** — because wisdom deserves to be read slowly

SENSEI doesn't just answer questions. SENSEI knows your situation. SENSEI holds you accountable.

---

## ✨ Features

### 🏆 Gamification Engine
- **XP System** — earn points for completing missions based on difficulty (Easy → Boss)
- **5 Level Tiers** — Rookie → Apprentice → Challenger → Expert → Legend
- **Pixel Character Sprite** — your on-screen warrior evolves visually with every level, drawn on canvas using 8×16 pixel grids
- **Skill Tree** — perks unlock as you level up (Daily Quests, Streak Guard, Boss Missions, Legend Status)
- **Freeze Tokens** — earned through consistency, spendable to protect your streak on a missed day

### 📋 Mission Management
- Create goals with **title, deadline, difficulty, category, notes, and recurrence type**
- Set **timed missions** — a real-time countdown timer appears on the dashboard, ticking every second
- Track **manual progress percentage** on long-running goals
- Goals auto-categorise: Mission, Health, Study, Finance, Creative, and more with custom **pixel category icons**
- Full **edit and fail** flows — fail a mission and it's logged with a timestamp

### 📊 Analytics & Insights
- **Category Analytics Screen** — breakdown of completed/failed missions by category
- **Weekly Report** — grade (S/A/B/C/D), XP earned, active days, streak, and performance score
- **Dashboard stats grid** — live streak, active goals, weekly focus %, monthly active days, failed count, freeze tokens
- **Animated donut ring** — visualizes overall goal completion with a pixel-style stroke animation

### 🌐 Real-Time Global Leaderboard
- Synced to **Firebase Realtime Database** — rankings update live across all users
- Ranked by **Discipline Score** (a composite of XP + streak + level)
- Top 50 warriors visible globally

### 🔔 Smart Notification Pipeline (WorkManager)
Five background workers keep you accountable around the clock:

| Worker | Trigger | Purpose |
|--------|---------|---------|
| `DailyReminderWorker` | Scheduled daily | Morning check-in push notification |
| `StreakGuardWorker` | End of day check | Warns if streak is about to break |
| `HourlyContextualWorker` | Every hour | Reminds you of your most urgent active mission |
| `GoalTimerWorker` | On timer start | Fires a live countdown notification |
| `GoalTimerEndWorker` | On timer expiry | "Did you complete it?" prompt |

All notifications survive app kills and device restarts.

### 📤 Share Your Stats
- One-tap **stat card sharing** — generates a pixel-art styled 800×400 PNG card using Android Canvas
- Card includes: username, level, streak, total XP, and missions completed
- Shareable to any app via Android Share Sheet

### 🔒 Authentication & Cross-Device Sync
- **Google Sign-In** via Firebase Auth — one tap, zero passwords
- Full **offline-first architecture**: Room DB serves as the source of truth locally
- On login, app checks Firebase RTDB for cloud data and syncs automatically
- Goals, stats, daily logs, and weekly reports all sync bidirectionally

---

## 🏗️ Architecture

```
┌────────────────────────────────────────────────────────────┐
│                        UI Layer                            │
│   Jetpack Compose Screens  ·  MVVM  ·  StateFlow/Flow      │
├────────────────────────────────────────────────────────────┤
│                     ViewModel Layer                         │
│        GoalViewModel  ·  AiChatViewModel                   │
├──────────────────┬─────────────────────────────────────────┤
│   Local Storage  │          Remote Storage                  │
│   Room DB v7     │   Firebase RTDB  ·  Firebase Auth        │
│   (offline-first)│   FCM  ·  Gemini AI API                  │
├──────────────────┴─────────────────────────────────────────┤
│                   Background Layer                          │
│        WorkManager  ·  5 Workers  ·  FCMService             │
└────────────────────────────────────────────────────────────┘
```

**Pattern:** MVVM · Repository-free single-ViewModel design · Kotlin Coroutines + Flow throughout

---

## 🗃️ Database Schema (Room v7)

| Entity | Key Fields |
|--------|-----------|
| `Goal` | id, title, startDate, endDate, difficulty, category, isCompleted, isFailed, progressPercent, recurrenceType, hasTime |
| `DailyLog` | date, goalsCompletedCount, goalsFailedCount |
| `UserStats` | totalXp, level, levelTitle, freezeTokens |
| `DailyQuest` | description, targetValue, currentValue, xpReward, isCompleted |
| `WeeklyReport` | weekStart, goalsCompleted, activeDays, xpEarned, longestStreakInWeek, grade |
| `AiChatSession` | id, title, timestamp |
| `AiChatMessage` | id, sessionId, text, isUser, isError, timestamp |

Schema versioned from v1 → v7 with zero-data-loss migrations.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI** | Jetpack Compose, Canvas API, Material 3 |
| **Architecture** | MVVM, StateFlow, Coroutines |
| **Local DB** | Room (with DAO pattern, 7 migrations) |
| **Remote DB** | Firebase Realtime Database |
| **Auth** | Firebase Authentication (Google Sign-In) |
| **AI** | Google Gemini AI API |
| **Push Notifications** | Firebase Cloud Messaging (FCM) |
| **Background Tasks** | WorkManager |
| **Home Screen Widget** | Glance API (AppWidget) |
| **Build** | Gradle KTS |

---

## 📱 Screens

| Screen | Description |
|--------|-------------|
| `SplashScreen` | Animated entry with logo |
| `OnboardingScreen` | First-time user setup & name input |
| `LoginScreen` | Google Sign-In via Firebase Auth |
| `DashboardScreen` | Main HQ — stats, active missions, daily quests, timers |
| `GoalListScreen` | Full mission list with filter support |
| `AddGoalScreen` | Create new mission with full config |
| `EditGoalScreen` | Modify existing mission |
| `CharacterScreen` | Pixel warrior, XP bar, skill tree, freeze tokens |
| `AiChatScreen` | Chat with SENSEI — your AI accountability coach |
| `LeaderboardScreen` | Global top-50 discipline rankings |
| `CategoryAnalyticsScreen` | Per-category goal breakdown |
| `WeeklyReportScreen` | Weekly grade, XP summary, performance stats |
| `ProfileScreen` | User info, streak history, share stats |
| `SettingsScreen` | Notification toggles, sync options, sign-out |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 26+
- A Firebase project with Realtime Database + Authentication + FCM enabled
- A Google Gemini API key

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Karaniniiitd/warrior-os-android.git
   cd warrior-os-android
   ```

2. **Add Firebase config**  
   Place your `google-services.json` in `app/`

3. **Add Gemini API Key**  
   In `local.properties`:
   ```
   GEMINI_API_KEY=your_api_key_here
   ```

4. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or hit ▶ Run in Android Studio.

---

## 📁 Project Structure

```
app/src/main/java/com/karan/do_it_now_motivation_tracker/
├── data/
│   └── AppDatabase.kt          # Room DB with migrations
├── model/                      # Entities + DAOs + XP logic
├── screens/                    # 14 Compose screens
├── ui/
│   ├── components/             # Pixel icons, particle system, reusable UI
│   └── theme/                  # Material3 theme + pixel font
├── util/
│   ├── FirebaseManager.kt      # Google Sign-In + Firebase Auth
│   ├── RealtimeDatabaseSync.kt # All RTDB read/write operations
│   ├── NotificationHelper.kt   # 4 notification channels + builders
│   ├── FCMService.kt           # Firebase Cloud Messaging handler
│   ├── ShareManager.kt         # Stats card renderer + share intent
│   └── SoundManager.kt         # In-app sound effects
├── viewmodel/
│   ├── GoalViewModel.kt        # Core app logic + state
│   └── AiChatViewModel.kt      # Gemini AI session management
├── widget/
│   └── DoItNowWidget.kt        # Home screen widget (Glance)
├── workers/                    # 5 WorkManager background workers
└── MainActivity.kt
```

---

## 🗺️ Roadmap

- [ ] Social challenges — challenge a friend to a mission
- [ ] Habit streaks — recurring daily habits separate from goals
- [ ] SENSEI voice mode — Gemini audio responses
- [ ] Dark / Light theme toggle
- [ ] iOS version

---

## 👤 Author

**Karan**  
[github.com/Karaniniiitd](https://github.com/Karaniniiitd)

> *Built with discipline. Designed for warriors.*

---

<div align="center">

**⭐ Star this repo if it motivated you even slightly.**

*Do It Now. Not later. Now.*

</div>
