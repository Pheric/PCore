# PCore Game API Home
## Making a Game
This process has been updated to be more powerful and at the same time easier. To make your plugin a game, you'll need to make a ```Game``` instance. This will handle many basic functions such as handling starting the game when enough players have joined and kicking or forcing spectate mode on those who join late. It also tracks data for each player (like points) and saves that data according to your own custom function.
```java
ConnectionManager connMan = new ConnectionManager();
connMan.initializeDatabasePool("myPlugin_main", RDBMS.POSTGRESQL, "username", "password", "127.0.0.1", 5432, "prod", 2);

UserRetriever uRet = new UserRetriever(connMan, "myPlugin_main");

Game gameInstance = new Game(this, uRet, 16, 18, (u -> {
    // Save u here
    return successful;
}));
```

## Making the Game playable
Your game will be controlled and interacted with by a series of events. You can put code in event listeners to run things depending on the current state of the game, like putting armor on players when the game starts, launching fireworks when the game ends, etc. These events are kept in the game.events package. When you need the game to skip to the next state (as you'll need to do so sometimes, like when the game is stopped manually by an admin or to simply end the game when someone wins), you will need to use the ```Game#setState()``` function. This will automatically move the game to the specified state and call the associated event.

## Sending Messages
The GamePlayerAdministrator tracks players and provides a few functions for interacting with them. An easy way to message all players is to simply broadcast a message with a prefix from GameChatFormat.

## Events
* GameJoinWaitEvent- (auto called when the server starts) First event fired after a game has (re)started. Players can join at this stage.
* GameJoinClosedEvent- (auto called) All players have joined. 20s countdown (handled); maybe broadcast info about the game. Currently unsupported: ranked users may join here, past the normal player limit.
* GameStartWaitEvent- (auto called) Teleport players to their spawn points (unhandled) and count down 5s to game start (unhandled)
* GameStartEvent- (auto called) The game has started! Do whatever here.
* GameEndWaitEvent- A winner has been chosen (unhandled). The game is over, everyone's still in the world, set as spectators (handled). Maybe broadcast the winner and play fireworks, whatever (unhandled).
* GameEndEvent- Teleport players to the lobby (unhandled), etc. Call GameJoinWaitEvent (unhandled).
* GameHaltedEvent- Something went terribly wrong. Restart the server, or send players to the lobby (unhandled).
* TeamScoreEvent- Called when a team scores (calling handled, scoring is not).