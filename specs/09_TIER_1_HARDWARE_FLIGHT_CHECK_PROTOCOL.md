# PROJECT IMPART: TIER 1 HARDWARE FLIGHT CHECK PROTOCOL

// Engineered by uncoalesced

This document details the physical test protocol for validating Tier 1 capabilities: Multi-Node Broadcast, Encrypted GPS Telemetry, and Two-Way ACK Handshakes across physical Android hardware.

## Pre-Flight Checklist

### 1. Requirements

*   **Device A (Sender Node)**: Physical Android device with SIM / active internet connection.
*   **Device B (Receiver Node)**: Physical Android device running on battery (unplugged from USB/Power).
*   **Location Services**: GPS / Location enabled on both devices.
*   **Firebase Backend**: `initiatePanic` Cloud Function deployed and accessible.

## Execution Protocol

### Phase 1: Permission & Matrix Handshake

1.  Install the APK on Device A and Device B.
2.  Launch Impart on both devices.
3.  Grant required permissions when prompted:
    *   **Notification Permission**: Required for background signaling and alert popups.
    *   **Location Permission (Fine / High Accuracy)**: Required on Device A to capture single-shot GPS coordinates during panic dispatch.
4.  Open the "My QR" tab on Device B.
5.  Open the "Scan QR" tab on Device A and scan Device B's QR code.
6.  Verify Device B appears in Device A's Contact Matrix screen.
7.  Repeat the process in reverse (Device B scans Device A) so both nodes are paired.

### Phase 2: Live GPS Telemetry & Intrusion Verification

1.  On Device A, open the Dashboard and select Device B from the contact list.
2.  Ensure Device B's screen is ON and unlocked.
3.  On Device A, tap "INITIATE PANIC".

**Expected Result on Device A:**
*   Device A requests a single-shot GPS fix.
*   Encrypts the payload containing latitude, longitude, and timestamp using Tink E2EE.
*   Status updates to "DISPATCHED".

**Expected Result on Device B:**
*   Within 1–2 seconds, `IntrusionActivity` fires full-screen with alert crimson theme (#EF233C).
*   Decrypted GPS coordinates (e.g., Lat: xx.xxxx, Lng: yy.yyyy) are displayed on the alert screen.
*   Alarm tone sounds.

### Phase 3: Two-Way Acknowledgment (ACK) Loop Test

1.  With `IntrusionActivity` active on Device B, tap "ACKNOWLEDGE & DISMISS".

**Expected Result on Device B:**
*   `IntrusionActivity` terminates.
*   A silent reverse dispatch (TYPE_ACK) is fired back to Device A's FCM token.

**Expected Result on Device A:**
*   Device A receives the silent high-priority ACK payload.
*   `ImpartMessagingService` intercepts the payload without launching the full-screen alert.
*   Surfaces a high-priority system notification: "PANIC ACKNOWLEDGED BY NODE [Device B ID]".

### Phase 4: Multi-Node Broadcast ("ALL-HANDS") Test

1.  Pair Device A with at least two target nodes (Device B and Device C).
2.  On Device A, tap "BROADCAST PANIC (ALL NODES)".

**Expected Result:**
*   Device A concurrently encrypts individual payloads for each stored contact key and dispatches batch FCM calls via `async`/`awaitAll`.
*   Both Device B and Device C receive high-priority alerts simultaneously.

### Phase 5: Deep Sleep / Doze Mode Test (The "Brutal" Exam)

This test proves if Impart can successfully wake a target device out of deep sleep mode.

**Prepare Device B:**
1.  Unplug Device B from power/USB charging (Doze mode triggers faster on battery).
2.  Swiping away/force closing Impart from the recent apps list.
3.  Lock the screen and place Device B on a table.
4.  Wait 15–20 minutes without touching Device B to force Android into deep Doze mode / standby bucket.

**Dispatch Alert from Device A:**
1.  Tap "INITIATE PANIC" targeting Device B.

**Target Result:**
*   Within 1–3 seconds, Device B's display violently wakes up.
*   `IntrusionActivity` overrides the lock screen with full red UI (#EF233C), displaying decrypted location coordinates and sounding the emergency alarm.

---

## OEM Battery Optimization Overrides

If Phase 5 fails on specific manufacturer hardware, apply these OEM-specific adjustments on the receiver node:

*   **Samsung (One UI)**: Settings -> Apps -> Impart -> Battery -> Set to Unrestricted.
*   **Xiaomi / Poco (MIUI/HyperOS)**: App Info -> Permissions -> Enable "Display pop-up windows while running in the background". Set Battery Saver to No Restrictions.
*   **OnePlus / Oppo (ColorOS)**: App Info -> Battery Usage -> Enable "Allow background activity" and "Allow auto-launch".
