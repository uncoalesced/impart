<!-- Engineered by uncoalesced -->
# File Structure & State

---

## 1. Current State Legend

- `[BUILT]` — exists and is fully functional per the current codebase state.
- `[TODO]` — placeholder; scaffolded by this document, implemented in a later roadmap phase.

## 2. Directory Tree

```
Impart/
├── build.gradle.kts                                [BUILT] — Compose, Hilt, Firebase BoM, FCM, Auth, Firestore, Room
├── settings.gradle.kts
├── AndroidManifest.xml                              [BUILT] — USE_FULL_SCREEN_INTENT, POST_NOTIFICATIONS,
│                                                                WAKE_LOCK, VIBRATE, lock-screen bypass activity flags
│
└── app/src/main/java/com/uncoalesced/impart/
    │
    ├── ImpartApplication.kt                         [BUILT] — @HiltAndroidApp
    ├── MainActivity.kt                               [BUILT] — Compose control panel, FCM token fetch,
    │                                                            notification permission handling,
    │                                                            "Simulate Alert" / "Initiate Panic" actions
    │
    ├── core/                                         # Cross-cutting concerns, framework glue
    │   ├── services/
    │   │   └── ImpartMessagingService.kt             [BUILT] — FirebaseMessagingService override,
    │   │                                                        WakeLock + FullScreenIntent dispatch
    │   ├── di/
    │   │   ├── AppModule.kt                          [TODO]  — FirebaseMessaging, FirebaseAuth,
    │   │   │                                                    PowerManager, KeyguardManager singletons
    │   │   ├── DatabaseModule.kt                     [TODO]  — Encrypted Room database + DAO providers
    │   │   ├── CryptoModule.kt                       [TODO]  — Keystore-backed key manager, AES-GCM cipher
    │   │   └── NetworkModule.kt                      [TODO]  — Cloud Functions callable client
    │   ├── security/
    │   │   ├── CryptoManager.kt                      [TODO]  — encrypt()/decrypt() wrapper, AES-256-GCM
    │   │   ├── KeystoreHelper.kt                     [TODO]  — Android Keystore key generation/retrieval
    │   │   └── QrPayloadCodec.kt                      [TODO]  — serialize/deserialize handshake QR payloads
    │   └── util/
    │       ├── WakeLockExtensions.kt                 [TODO]  — scoped wake-lock acquire/release helpers
    │       └── Constants.kt                          [TODO]  — channel IDs, timeout constants, FCM keys
    │
    ├── domain/                                       # Pure Kotlin. No Android framework imports.
    │   ├── model/
    │   │   ├── Contact.kt                            [TODO]  — UUID, publicKey, fcmToken, nickname, addedAt
    │   │   ├── PanicSignal.kt                        [TODO]  — decrypted signal representation
    │   │   └── HandshakePayload.kt                   [TODO]  — QR-exchanged payload model
    │   ├── repository/
    │   │   ├── ContactRepository.kt                  [TODO]  — interface only
    │   │   └── SignalRepository.kt                   [TODO]  — interface only
    │   └── usecase/
    │       ├── SendPanicSignalUseCase.kt              [TODO]
    │       ├── AddContactUseCase.kt                   [TODO]
    │       └── RevokeContactUseCase.kt                [TODO]
    │
    ├── data/                                         # Implementations of domain interfaces
    │   ├── local/
    │   │   ├── db/
    │   │   │   ├── ImpartDatabase.kt                  [TODO]  — Room + SQLCipher SupportFactory
    │   │   │   ├── ContactDao.kt                      [TODO]
    │   │   │   └── entity/ContactEntity.kt            [TODO]
    │   │   └── prefs/
    │   │       └── SecurePrefsManager.kt              [TODO]  — EncryptedSharedPreferences wrapper
    │   ├── remote/
    │   │   └── CloudFunctionsClient.kt                [TODO]  — "initiatePanic" callable wrapper
    │   └── repository/
    │       ├── ContactRepositoryImpl.kt               [TODO]
    │       └── SignalRepositoryImpl.kt                [TODO]
    │
    └── presentation/                                  # Compose UI + ViewModels
        ├── theme/
        │   ├── Color.kt                                [TODO]  — ImpartTomato, ImpartInkBlack, ImpartCream,
        │   │                                                      ImpartOlive, ImpartBlue
        │   ├── Type.kt                                 [TODO]
        │   └── Theme.kt                                [TODO]
        ├── dashboard/
        │   ├── DashboardScreen.kt                      [TODO]  — refactor target for current MainActivity content
        │   └── DashboardViewModel.kt                    [TODO]
        ├── handshake/
        │   ├── QrGeneratorScreen.kt                     [TODO]  — "My QR" — displays own handshake payload
        │   ├── QrScannerScreen.kt                       [TODO]  — CameraX + ML Kit barcode scanning
        │   └── HandshakeViewModel.kt                    [TODO]
        ├── contacts/
        │   ├── ContactMatrixScreen.kt                   [TODO]  — list/manage trusted contacts
        │   └── ContactMatrixViewModel.kt                [TODO]
        └── intrusion/
            ├── IntrusionActivity.kt                     [BUILT] — keyguard bypass, ImpartTomato palette
            └── IntrusionScreen.kt                        [TODO]  — extracted Composable content, refactor target

functions/                                              # Firebase Cloud Functions (separate Node.js project)
├── package.json
├── tsconfig.json
└── src/
    ├── index.ts                                        [TODO]  — export point for all callables
    ├── initiatePanic.ts                                 [TODO]  — onCall: validate → resolve token → dispatch FCM
    └── lib/
        └── fcmDispatcher.ts                              [TODO]  — Admin SDK send() wrapper, high-priority config
```

## 3. Layer Responsibilities

- **`core/`** — Anything that touches the Android framework directly but isn't UI: services, DI wiring, security primitives, and small utility extensions. Nothing here should contain business rules about *what* Impart does — only *how* Android-specific mechanisms are invoked.
- **`domain/`** — Pure Kotlin, framework-agnostic. Models, repository interfaces, and use cases live here. This layer should compile without any `android.*` import and should be trivially unit-testable without an emulator.
- **`data/`** — Concrete implementations of the `domain/repository` interfaces, split into `local/` (Room + encrypted prefs) and `remote/` (Cloud Functions client). This is the only layer allowed to know that Room or Firebase exist.
- **`presentation/`** — Compose screens and their ViewModels, one subdirectory per feature (`dashboard`, `handshake`, `contacts`, `intrusion`). ViewModels depend on `domain/usecase`, never directly on `data/repository` implementations.

## 4. Hilt Module Map

| Module | Component Scope | Provides |
|---|---|---|
| `AppModule` | `SingletonComponent` | `FirebaseMessaging`, `FirebaseAuth`, `PowerManager`, `KeyguardManager` |
| `DatabaseModule` | `SingletonComponent` | `ImpartDatabase`, `ContactDao` |
| `CryptoModule` | `SingletonComponent` | `KeystoreHelper`, `CryptoManager` |
| `NetworkModule` | `SingletonComponent` | `CloudFunctionsClient` |

## 5. State Management Conventions

Every screen follows the same unidirectional pattern: a `ViewModel` exposes a single `StateFlow<UiState>`, Composables collect it via `collectAsStateWithLifecycle()`, and user actions are dispatched as one-shot function calls into the ViewModel rather than direct state mutation from the UI layer.

`IntrusionScreen.kt` is the one exception worth calling out explicitly: because it must render reliably even in a cold-start-from-push scenario, its ViewModel should avoid any dependency chain that could suspend on a slow I/O call before first paint — the alarm and the visible screen must appear before anything non-essential (e.g. contact nickname lookup) resolves.
