<!-- Engineered by uncoalesced -->
# Project Impart V2: Alpha Flight Check

**Status:** Pre-Flight

The codebase is complete. This document defines the physical testing protocol to verify the "Resurrection" architecture in real-world conditions.

## Phase 1: Backend Deployment
Before touching the Android devices, the Cloud Function must be live.
1. Open your terminal in the Cloud Functions directory (where `index.ts` lives).
2. Ensure you are authenticated with the Firebase CLI: `firebase login`
3. Deploy the router: `firebase deploy --only functions`
4. Verify in the Google Cloud/Firebase Console that `initiatePanic` is active and requires Authentication.

## Phase 2: Physical Device Setup
> [!WARNING]
> DO NOT USE EMULATORS FOR THIS TEST. Emulators do not accurately simulate Android Doze Mode, App Standby Buckets, or OEM-specific task killers.

1. Install the APK on Device A (Sender) and Device B (Receiver).
2. Ensure both devices grant Notification permissions (Android 13+).
3. Ensure both devices have an active internet connection (Mix it up: Device A on WiFi, Device B on 5G).

## Phase 3: The Handshake Verification
1. Open Impart on both devices.
2. Generate the QR Code on Device B.
3. Use Device A to scan Device B's QR Code.
4. Verify that Device B's Node ID appears in Device A's Contact Matrix.
   *(Behind the scenes: Tink public keys have been exchanged and stored in SQLCipher).*

## Phase 4: The "Brutal" Test (The Final Exam)
This test proves if Project Impart actually works as an emergency tool.

### The Setup (Device B - Receiver):
1. Unplug Device B from power (Doze mode triggers faster on battery).
2. Force close the Impart app (swipe it away from the recent apps list).
3. Turn off the screen and lock the device.
4. Wait exactly 15 minutes. Do not touch Device B. This ensures Android drops the device into deep sleep/Doze mode.

### The Execution (Device A - Sender):
1. Open Impart.
2. Select Device B from the Contact Matrix.
3. Tap **INITIATE PANIC**.

### The Expected Result:
Within 1 to 3 seconds, Device B's screen should violently turn on, displaying the ImpartTomato red screen over the lock screen, and the alarm tone should sound.

## Phase 5: Troubleshooting OEM Killers
If the Brutal Test fails, it is almost certainly due to the manufacturer of Device B. Samsung, Xiaomi, and OnePlus notoriously violate standard Android background rules.

- **Failure:** The notification arrives, but the screen doesn't turn on (black screen with sound).
  - **Fix:** Verify `IntrusionActivity` has `setShowWhenLocked(true)` and the Manifest has `USE_FULL_SCREEN_INTENT`.
- **Failure:** Nothing happens until you unlock Device B, then the alert suddenly pops up.
  - **Fix:** The OS is suppressing FCM data payloads. On Device B, go to App Info -> Battery -> set Impart to Unrestricted.
- **Failure:** Cloud Function returns a 500 error.
  - **Fix:** Check Firebase Functions logs. Usually means the payload was formatted incorrectly or the `targetFcmToken` was null.
