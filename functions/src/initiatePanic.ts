// Engineered by uncoalesced

import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import { dispatchHighPriorityData } from './lib/fcmDispatcher';

export const initiatePanic = functions.https.onCall(async (data, context) => {
    // 1. Validate Authentication
    if (!context.auth) {
        throw new functions.https.HttpsError(
            'unauthenticated',
            'The function must be called while authenticated.'
        );
    }

    const { targetUuid, envelope } = data;

    if (!targetUuid || typeof targetUuid !== 'string') {
        throw new functions.https.HttpsError('invalid-argument', 'targetUuid is required and must be a string.');
    }

    if (!envelope || typeof envelope !== 'object') {
        throw new functions.https.HttpsError('invalid-argument', 'envelope is required and must be an object.');
    }

    try {
        // 2. Resolve targetUuid -> FCM token
        // Assumes there's a 'users' collection where doc ID is the UUID and it contains { fcmToken: '...' }
        const userDoc = await admin.firestore().collection('users').doc(targetUuid).get();
        if (!userDoc.exists) {
            throw new functions.https.HttpsError('not-found', 'Target UUID not found.');
        }

        const userData = userDoc.data();
        const fcmToken = userData?.fcmToken;

        if (!fcmToken || typeof fcmToken !== 'string') {
            throw new functions.https.HttpsError('failed-precondition', 'Target UUID does not have a valid FCM token.');
        }

        // Convert all envelope values to strings for FCM data payload
        const stringifiedEnvelope: Record<string, string> = {};
        for (const key of Object.keys(envelope)) {
            stringifiedEnvelope[key] = String(envelope[key]);
        }

        // 3. Dispatch via FCM
        await dispatchHighPriorityData(fcmToken, stringifiedEnvelope);

        return { success: true };
    } catch (error) {
        console.error('Error in initiatePanic:', error);
        if (error instanceof functions.https.HttpsError) {
            throw error;
        }
        throw new functions.https.HttpsError('internal', 'An internal error occurred during panic initiation.');
    }
});
