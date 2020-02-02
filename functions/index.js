const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// The Firebase Admin SDK to access the Firebase Realtime Database.
const {Firestore} = require('@google-cloud/firestore');
// Create a new client
const firestore = new Firestore();


exports.createuserProfile = functions.auth.user().onCreate((user) => {
    const document = firestore.doc("midichallengeusers/" + user.uid);

    // Enter new data into the document.
    document.set({
      friends: []
    });
    console.log('Entered new data into the document');
  });