# Mikey Minigames

## Plugin Installation

1. Download plugin (TODO: add link)
2. Put in plugins folder
3. Reload all plugins or restart game

## Game Creation

1. Use `/games add <GameName>` to add a new game, replacing `<GameName>` with the name of your game (case insensitive)
2. Use `/games tool <GameName>` to get the editing tool (See the [Editing Tool](#editing-tool) section below)
3. When the game has been setup correctly, use `/games enable <GameName>` to enable your game
4. Your game should manually save when editing, but you can use `/games save` to save all games
5. On load, the plugin should load in all of the games, but you can use `/games load` to load all games from the save files

## Playing Games

1. The game needs to be enabled to be played
2. Use `/games join <GameName>` to join a game
3. If the game has a lobby, then it will add you to the lobby
    1. If a lobby is already created, then it will add you
    2. Or it will create a new lobby
4. Once the countdown finishes, the game will automatically teleport you to the game arena
5. Once the winning condition is met, all players will be teleported to the exit location and the game will end
6. You can use `/games quit` to leave a game you are currently in or in the lobby of

## Editing Tool

(WIP)
