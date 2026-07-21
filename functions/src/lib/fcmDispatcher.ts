// Engineered by uncoalesced

import * as admin from 'firebase-admin';

/**
 * Dispatches an FCM data payload with high priority to wake the device.
 * Never uses a notification payload to ensure the OS does not intercept it.
 */
export async function dispatchHighPriorityData(fcmToken: string, encryptedEnvelope: Record<string, string>): Promise<void> {
    const message: admin.messaging.Message = {
        token: fcmToken,
        data: encryptedEnvelope,
        android: {
            priority: 'high'
        }
    };

    try {
        await admin.messaging().send(message);
        console.log(`Successfully dispatched high-priority data payload.`);
    } catch (error) {
        console.error('Error dispatching FCM message:', error);
        throw error;
    }
}
