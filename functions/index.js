const QUESTIONS_PER_GAME = 3

const functions = require('firebase-functions');


// The Firebase Admin SDK to access the Firebase Realtime Database.
const {Firestore} = require('@google-cloud/firestore');
// Create a new client
const firestore = new Firestore();



function shuffle(a) {
  for (let i = a.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

function getRandomInt(max) {
  return Math.floor(Math.random() * Math.floor(max));
}

class Game {
  constructor(id1, id2) {
    this.id1 = id1
    this.id2 = id2
    this.status = "pending"
  }
}

const createNewGame = (player1, player2) => {
  console.log("Creating a new game")
  firestore
    .collection("questions")
    .get()
    .then( snapshot => {
      let questions = snapshot.docs
      if (questions.length >= QUESTIONS_PER_GAME) {
        shuffle(questions)
        const selectedQuestions = questions.slice(0, QUESTIONS_PER_GAME)
        let qs = []
        selectedQuestions.forEach(element => {
          qs.push(element.data())
        });
        const players = [
          {
            "id": player1.id,
            "name": player1.playerName,
            "points": 0
          },
          {
            "id": player2.id,
            "name": player2.playerName,
            "points": 0
          }
        ]
        const playerOnTurn = getRandomInt(players.length -1)
        firestore.collection("games").add(
          {
            "players": players,
            "status": "waiting",
            "questions": qs,
            "playerOnTurnIndex": playerOnTurn
          }
        )
      }
      else{
        // NON CI SONO ABBASTANZA DOMANDE
      }
      return 0
    })
    .catch(err => {
      console.log('Error retrieveing questions', err)
    })
}

const deleteChallenges = (id1, id2) => {
  console.log(`Deleting challenges ${id1} and ${id2}`)
  firestore.collection("challenges").doc(id1).delete()
  firestore.collection("challenges").doc(id2).delete()
}


exports.matchChallenges = functions
  .region('europe-west1')
  .firestore
  .document('challenges/{id}')
  .onCreate((createdSnapshot, context) => {
    console.log("New challenge created!")
    console.log(createdSnapshot.data())
    // Match two compatible challenges
    // We need to create a game object and delete the two challenges
    firestore
      .collection("challenges")
      .orderBy("createdAt")
      .limit(2)
      .get()
      .then(snapshot => {
        console.log("I retrieved the following challenges:")
        const challenges = snapshot.docs
        console.log(challenges)
        let newChallenge = createdSnapshot.data()
        let player2 = {
          "id": newChallenge.playerID,
          "playerName": newChallenge.playerName,
        }
        if ( challenges.length > 0) {
          if (challenges[0].get("playerID") !== context.params.id) {
            let player1 = {
              "id": challenges[0].get("playerID"),
              "playerName": challenges[0].get("playerName")
            }
            createNewGame(player1, player2)
            deleteChallenges(challenges[0].get("playerID"), context.params.id)
          }
          else {
            if (challenges.length > 1) {
              let player1 = {
                "id": challenges[1].get("playerID"),
                "playerName": challenges[1].get("playerName")
              }
              createNewGame(player1, player2)
              deleteChallenges(challenges[1].get("playerID"), context.params.id)
            }
          }
        }
      return 0
    })
    .catch(err => {
      console.log('Error getting challenges', err)
    })
})
