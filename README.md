# PMU (Push-ups Masters Ultimate) for Android

## Project Description

This project is our end-of-year project for the `INFO4` program at `Polytech Grenoble`.

`PMU` is a mobile game for Android that let you play quickly and easily a variant of the PMU game where you can interact with the the by doing pushups. The game is playable with two players or more, but the more players there are, the more fun it is. 

## How to play
1. Download the app on your Android phone.
2. Start the App.
3. `Host` (only one player should host) or `Join` a game, then wait to connect and for the other players to join.
4. Once all players are connected, the host should start the game.
5. Bet a number of pushups on a Card symbol.
6. The host will draw a card to make this symbol go forward. If you don't want this symbol to go forward, you can do pushups to make it go back.
7. The game ends when a symbol reaches the end of the board.

## Technical Choices
- We worked with an MVVM design pattern for the overall architecture.
- `Android Studio` and `Kotlin` to implement the application.
- `XML` for the visual part of the application.
- `Google Nearby Connections API` to manage the proximity connections.

To see more details about the technical aspect of the project, you can check the **[Technical Review](docs/TECHNICAL_REVIEW.md)**.

## Authors
- [Guillaume Bellone](https://github.com/ricoupat)
- [Axel Cazorla](https://github.com/Ethazio)
- [Oriane Doudet](https://github.com/oriane-9505)
- [Noah Kohrs](https://github.com/noahkohrs)

## Credits
- [Google Nearby Connections API](https://developers.google.com/nearby/connections/overview) for the api to manage the proximity connections that we used in our project.
- [Fernando Sproviero](https://github.com/fernandospr) for providing [a simple example](https://github.com/fernandospr/android-nearby-tictactoe/) that helped us to understand how to use the Google Nearby Connections API.