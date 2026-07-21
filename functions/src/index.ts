// Engineered by uncoalesced
import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

// Initialize the Firebase Admin SDK
admin.initializeApp();

/**
 * Blind Router for Impart.
 * Receives targetFcmToken and an encrypted envelope from Node A.
 * Dispatches a High-Priority Data message to Node B.
 * 
 * This function NEVER inspects or parses the content of the envelope.
 */
export const initiatePanic = functions.https.onCall(async (data, context) => {
    // 1. Validate Authentication
    if (!context.auth) {
        throw new functions.https.HttpsError(
            'unauthenticated',
            'The function must be called while authenticated.'
        );
    }

    const { targetFcmToken, envelope } = data;

    if (!targetFcmToken || typeof targetFcmToken !== 'string') {
        throw new functions.https.HttpsError(
            'invalid-argument', 
            'targetFcmToken is required and must be a string.'
        );
    }

    if (!envelope || typeof envelope !== 'object') {
        throw new functions.https.HttpsError(
            'invalid-argument', 
            'envelope is required and must be an object.'
        );
    }

    // 2. Prepare FCM Data Payload
    // FCM data payloads strictly require string-to-string key-value pairs.
    const stringifiedEnvelope: Record<string, string> = {};
    for (const key of Object.keys(envelope)) {
        stringifiedEnvelope[key] = String(envelope[key]);
    }

    // 3. Construct High-Priority Message
    const message: admin.messaging.Message = {
        token: targetFcmToken,
        data: stringifiedEnvelope,
        android: {
            // Mandates the OS to wake the device, bypassing Doze mode maintenance windows
            priority: 'high'
        }
    };

    // 4. Blind Dispatch
    try {
        await admin.messaging().send(message);
        console.log('Successfully routed encrypted panic payload.');
        return { success: true };
    } catch (error) {
        console.error('Error routing FCM message:', error);
        throw new functions.https.HttpsError(
            'internal', 
            'An internal error occurred during blind routing.'
        );
    }
});
