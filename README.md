# Learning Dashboard (Android · Kotlin · Jetpack Compose)

Login → Course Dashboard → Course Details, with lesson completion and offline support.

Demo login: `test@gmail.com` / `123456`

## Project structure

```
app/src/main/java/com/example/learning_app
├── LearningApp.kt / AppContainer.kt   Application + manual dependency injection
├── MainActivity.kt
├── navigation/AppNavigation.kt        Login, Dashboard, CourseDetails routes
├── data
│   ├── model/Course.kt                Course, Lesson (progress is derived from lessons)
│   ├── remote/                        CourseApi + MockCourseApi (assets/courses.json)
│   ├── local/                         Room database, entities, DAO
│   └── repository/                    OfflineFirstCourseRepository, FakeAuthRepository
└── presentation
    ├── login/                         LoginScreen + LoginViewModel
    ├── dashboard/                     DashboardScreen + DashboardViewModel
    └── details/                       CourseDetailsScreen + CourseDetailsViewModel
└── ui
    ├── components/AppTopBar.kt        Shared dark-blue, centre-aligned app bar
    └── theme/                         Colors, Theme, Typography, AppStyle (text styles + spacing)
```
All user-facing text lives in `res/values/strings.xml`.

Run tests: `./gradlew testDebugUnitTest` · Build APK: `./gradlew assembleDebug`

## 1. Architecture
MVVM with a repository layer: **Compose UI → ViewModel (StateFlow UI state) → Repository → API / Room**.
Screens are stateless renderers of a single immutable `UiState`. ViewModels hold presentation logic and
depend only on repository interfaces, so they are easy to test and replace. The repository is the single
source of truth for data. A domain/use-case layer and Hilt were left out on purpose, because they would add
ceremony without adding value at this size. Both can be added without changing the UI.

## 2. Offline support
Offline-first. The UI always observes Room (`Flow`). The API result is written into Room, and Room pushes
updates to every screen. If a refresh fails (the mock API throws when there is no network), the cached
courses stay on screen with a "showing saved courses" banner. Lesson completion is written straight to Room,
so the dashboard and details progress update instantly and work offline. Refreshing never overwrites a
lesson the user has already completed locally.

## 3. Security
Store auth and refresh tokens in **EncryptedSharedPreferences / DataStore encrypted with an Android
Keystore key**. Never keep them in plain prefs, logs or the Room DB. Use short-lived access tokens with
refresh, HTTPS with certificate pinning, and R8 obfuscation for release builds.

## 4. Scale (1M users, hundreds of courses)
1. Real REST backend with Retrofit/OkHttp, paginated course APIs (Paging 3 + RemoteMediator), and HTTP caching/ETags.
2. Sync lesson completion through a WorkManager queue of pending changes, with retry and conflict resolution.
3. Hilt for DI, and a multi-module setup (`core`, `data`, `feature-*`) for faster builds and team ownership.
4. Crashlytics, analytics and performance monitoring, plus feature flags and staged rollouts.
5. CI with unit, UI and screenshot tests, Baseline Profiles for startup, and Room schema migrations.

## 5. Second platform (iOS / macOS)
SwiftUI views with an `ObservableObject`/`@Observable` ViewModel per screen that publishes a UI state.
A `CourseRepository` protocol with an async/await `URLSession` API client and a SwiftData (or Core Data)
cache as the source of truth. `NavigationStack` for routing, and the Keychain for tokens. The architecture,
layers and tests (XCTest with a fake repository) map one-to-one to this Android app.
