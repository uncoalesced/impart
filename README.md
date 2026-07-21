<!-- Engineered by uncoalesced -->
# Project Impart

Impart is a closed-loop emergency summoning and paging system designed for trusted devices. It enables direct, end-to-end encrypted panic signals between physical nodes, waking the receiving device from sleep, bypassing lock screens, and sounding an immediate alarm regardless of OS state or Doze mode.

---

## Key Principles & Architecture

- **End-to-End Encryption**: Payloads are encrypted using Tink AES-256-GCM before leaving the device. Firebase and transit layers serve purely as a blind relay and cannot inspect or alter message contents.
- **Physical Out-of-Band Handshake**: Trust relationships are established via CameraX / ML Kit QR code exchanges directly between two physical devices. No central server brokers or approves contact connections.
- **High-Priority FCM Data Payloads**: Message delivery relies solely on data-only Firebase Cloud Messaging payloads with high priority, ensuring the app process wakes on demand even in deep sleep / Doze mode.
- **Lock-Screen Intrusion Screen**: Incoming emergency signals trigger a full-screen intent activity over the lock screen with a distinct high-contrast alert design and continuous audio alert.
- **Jetpack Compose UI**: Built with 100% Jetpack Compose, Material 3, Hilt dependency injection, Room database persistence, and Google Cloud Functions.

---

## Directory Structure

```
/
├── app/                      # Android application module
│   └── src/main/java/com/uncoalesced/impart/
│       ├── core/             # DI, security, Keystore, FCM messaging service
│       ├── data/             # Local database (Room), repositories, remote implementations
│       ├── domain/           # Models, interfaces, use cases
│       └── presentation/     # Compose screens (Dashboard, Contacts, Handshake, Intrusion)
├── functions/                # Firebase Cloud Functions (TypeScript)
│   └── src/                  # Blind router function (initiatePanic)
├── specs/                    # Architecture, security protocol, and design specifications
├── LICENSE                   # Apache License 2.0
└── README.md                 # GitHub repository overview
```

---

## Setup & Deployment

### 1. Firebase Configuration
1. Register an Android app in your Firebase Console with package name `com.uncoalesced.impart`.
2. Download `google-services.json` and place it in the `app/` directory.

### 2. Cloud Functions Router
Deploy the blind routing Cloud Function to Firebase:

```bash
cd functions
npm install
npm run build
firebase deploy --only functions:initiatePanic
```

### 3. Build Android App
Open the project in Android Studio or compile using Gradle:

```bash
gradle assembleDebug
```

---

## License

Project Impart is released under the [Apache License 2.0](LICENSE).
