# Graph Report - ladybug  (2026-11-18)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1429 nodes · 3596 edges · 75 communities (32 shown, 43 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 516 edges (avg confidence: 0.85)
- Token cost: 30,623 input · 1,046 output

## Graph Freshness
- Built from commit: `f456c22a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Ladybug Status & Timed Actions
- Central View & Game Model
- Ghost Eyes Rendering
- Game Sound Events
- Level Structure & Settings
- Keyboard Controller Interface
- Screen Blocks & Movement
- Ghost Body Movement
- Timers & Super Power
- Core Constants & Bodies
- Maven Release Workflow
- Ghost Accessory Images
- Image Resources & Views
- Main Game Model
- Program Status Checks
- Xbox Controller Conditions
- Fruits & Lombok Entities
- Ghost Types & Status
- Ghosts Group Management
- Localized Game Messaging
- Level Tiles 492
- Level Tiles 584
- Red Lines Level Tiles
- Xbox Stick Input
- Ladybug Eating Events
- Audio, Fonts & Logging
- First Generation Levels
- Maze Block Rendering
- Dijkstra Pathfinding
- Game Status Enum
- Gameplay Loop Logic
- Swing Page Views
- Second Generation Level Files
- Game Properties & Settings
- Sound Requests
- Main Frame Setup
- Swing Graphic Structure
- Ladybug Dying Animation
- Ghost Speed Settings
- Fruit Management
- Ladybug Sprite Images
- Level Persistence & Start
- Collections & Ghost Behaviour
- Level Rendering Modes
- Modernisation Python Scripts
- Ladybug Death Handling
- Fun Messages
- Scrolling Text Panel
- Vertical Slider Demo
- Ghost Body Images
- Block Border Checks
- Status Bar View
- Simple Timers
- Ladybug Image Rotation
- Spring Boot Launcher
- Second Gen Level 1
- Second Gen Level 2
- Second Gen Level 3
- Second Gen Level 4
- Second Generation Level Base
- Lit Lamp Mode
- Second Gen Level 5
- Second Gen Level 6
- Second Gen Level 7
- Second Gen Level 8
- Common Game Images
- Ghost Behaviour Enum
- Move Style Enum
- Speed Function
- Backup Script
- Maven Project Artifact

## God Nodes (most connected - your core abstractions)
1. `ScreenData` - 89 edges
2. `ScreenBlock` - 60 edges
3. `GameModel` - 52 edges
4. `CurrentProgramStatus` - 52 edges
5. `Ghost` - 50 edges
6. `AbstratGameModel` - 47 edges
7. `Ladybug` - 43 edges
8. `GhostsGroup` - 42 edges
9. `IGameModelAction` - 39 edges
10. `CentralView` - 36 edges

## Surprising Connections (you probably didn't know these)
- `Engine` --references--> `GameModel`  [EXTRACTED]
  src/main/java/com/kycox/game/engine/Engine.java → src/main/java/com/kycox/game/engine/model/GameModel.java
- `GameModel` --references--> `GhostsGroup`  [EXTRACTED]
  src/main/java/com/kycox/game/engine/model/GameModel.java → src/main/java/com/kycox/game/body/GhostsGroup.java
- `GameModel` --references--> `Ladybug`  [EXTRACTED]
  src/main/java/com/kycox/game/engine/model/GameModel.java → src/main/java/com/kycox/game/body/ladybug/Ladybug.java
- `GameModel` --references--> `LadybugDying`  [EXTRACTED]
  src/main/java/com/kycox/game/engine/model/GameModel.java → src/main/java/com/kycox/game/body/ladybug/LadybugDying.java
- `GameModel` --implements--> `GameModelForController`  [EXTRACTED]
  src/main/java/com/kycox/game/engine/model/GameModel.java → src/main/java/com/kycox/game/constant/contract/GameModelForController.java

## Import Cycles
- None detected.

## Communities (75 total, 43 thin omitted)

### Community 0 - "Ladybug Status & Timed Actions"
Cohesion: 0.05
Nodes (25): DoActionAfterTimer, LadybugStatus, DEAD, DYING, NORMAL, AbstratGameModel, GameModelGameAskForceEndGame, GameModelGameIsEnding (+17 more)

### Community 1 - "Central View & Game Model"
Cohesion: 0.06
Nodes (7): GameModelForViews, GameStatusForGameView, GroupGhostForGameView, LadybugForGameView, EventGameModel, CentralView, PageLeftView

### Community 2 - "Ghost Eyes Rendering"
Cohesion: 0.05
Nodes (19): GhostForView, GhostEyesImages, GHOST_DOWN_EYES, GHOST_LEFT_EYES, GHOST_RIGHT_EYES, GHOST_UP_EYES, GhostsColorImages, GHOST_CAMOUFLAGE_COLOR (+11 more)

### Community 3 - "Game Sound Events"
Cohesion: 0.05
Nodes (21): GameModelForSounds, NewSoundsForGameSounds, Sounds, COMMON_TELEPORT, GAME_BEGIN_LEVEL, GAME_SIREN_0, GAME_SIREN_1, GAME_SIREN_2 (+13 more)

### Community 4 - "Level Structure & Settings"
Cohesion: 0.08
Nodes (4): LevelStructure, ManageLevel, ScreenData, CheckLevelMap

### Community 5 - "Keyboard Controller Interface"
Cohesion: 0.09
Nodes (3): GameCommon, GameModelForController, KeyboardController

### Community 8 - "Timers & Super Power"
Cohesion: 0.08
Nodes (12): TimerStatus, RUN, STOP, STOPPING, Message, MessageType, POINT, STRING (+4 more)

### Community 10 - "Maven Release Workflow"
Cohesion: 0.06
Nodes (30): description, required, env, GITHUB_TOKEN, INPUT_DEV_VERSION, INPUT_RELEASE_VERSION, devVersion, releaseVersion (+22 more)

### Community 11 - "Ghost Accessory Images"
Cohesion: 0.11
Nodes (9): GhostHatImages, GHOST_HAT, GhostHeadbandImages, HEADBAND_DOWN, HEADBAND_LEFT, HEADBAND_RIGHT, HEADBAND_UP, BodyImg (+1 more)

### Community 12 - "Image Resources & Views"
Cohesion: 0.14
Nodes (6): DisplayRendering, LinesRendering, Rendering, TilesRendering492, TilesRendering584, TilesRenderingRedLines

### Community 15 - "Xbox Controller Conditions"
Cohesion: 0.14
Nodes (5): GhostForController, XboxOneConstants, XBoxOneControllerManager, XboxOneController, WindowsHost

### Community 16 - "Fruits & Lombok Entities"
Cohesion: 0.14
Nodes (12): Fruit, FruitsImages, APPLE, BANANA, CHERRY, HEART, ORANGE, PEAR (+4 more)

### Community 17 - "Ghost Types & Status"
Cohesion: 0.11
Nodes (9): Blinky, GhostStatus, AUTOGENERATED, DYING, FLASH, NORMAL, REGENERATED, SCARED (+1 more)

### Community 20 - "Level Tiles 492"
Cohesion: 0.09
Nodes (19): LevelImages492, GOLD, MEGA_GOLD, NO_RIGHT_LEFT_NO_UP_DOWN, NO_RIGHT_LEFT_NO_UP_NO_DOWN, NO_RIGHT_LEFT_UP_DOWN, NO_RIGHT_LEFT_UP_NO_DOWN, NO_RIGHT_NO_LEFT_NO_UP_DOWN (+11 more)

### Community 21 - "Level Tiles 584"
Cohesion: 0.09
Nodes (19): LevelImages584, GOLD, MEGA_GOLD, NO_RIGHT_LEFT_NO_UP_DOWN, NO_RIGHT_LEFT_NO_UP_NO_DOWN, NO_RIGHT_LEFT_UP_DOWN, NO_RIGHT_LEFT_UP_NO_DOWN, NO_RIGHT_NO_LEFT_NO_UP_DOWN (+11 more)

### Community 22 - "Red Lines Level Tiles"
Cohesion: 0.09
Nodes (19): LevelImagesRedLines, GOLD, MEGA_GOLD, NO_RIGHT_LEFT_NO_UP_DOWN, NO_RIGHT_LEFT_NO_UP_NO_DOWN, NO_RIGHT_LEFT_UP_DOWN, NO_RIGHT_LEFT_UP_NO_DOWN, NO_RIGHT_NO_LEFT_NO_UP_DOWN (+11 more)

### Community 24 - "Ladybug Eating Events"
Cohesion: 0.21
Nodes (3): Ladybug, GroupMessages, Score

### Community 25 - "Audio, Fonts & Logging"
Cohesion: 0.15
Nodes (3): GameFont, PACFONT, ListenSound

### Community 26 - "First Generation Levels"
Cohesion: 0.14
Nodes (3): FirstGenerationLevel1, FirstGenerationLevel3, FirstGenerationLevel

### Community 28 - "Dijkstra Pathfinding"
Cohesion: 0.18
Nodes (3): LadybugForController, Dijkstra, UnitDijkstra

### Community 29 - "Game Status Enum"
Cohesion: 0.11
Nodes (17): GameStatus, ASK_FORCE_END_GAME, GAME_END, GAME_ENDING, GAME_START, GAME_STARTING, IN_GAME, LEVEL_END (+9 more)

### Community 31 - "Swing Page Views"
Cohesion: 0.16
Nodes (3): PageEndView, GhostView, LadybugView

### Community 32 - "Second Generation Level Files"
Cohesion: 0.39
Nodes (3): LevelConstruct, B, N

### Community 35 - "Main Frame Setup"
Cohesion: 0.20
Nodes (3): MainFrame, MainLadybug, Screen

### Community 38 - "Ghost Speed Settings"
Cohesion: 0.18
Nodes (3): Clyde, Inky, Pinky

### Community 40 - "Ladybug Sprite Images"
Cohesion: 0.14
Nodes (11): LadybugImages, LADYBUG_UP_1, LADYBUG_UP_2, LADYBUG_UP_3, LADYBUG_UP_4, LADYBUG_UP_5, LADYBUG_UP_6, LADYBUG_UP_7 (+3 more)

### Community 43 - "Level Rendering Modes"
Cohesion: 0.18
Nodes (6): LevelRendering, LINES_RENDERING, RandomEnum, TILES_RENDERING_492, TILES_RENDERING_584, TILES_RENDERING_REDLINES

### Community 46 - "Fun Messages"
Cohesion: 0.18
Nodes (9): GameFunMessages, MSG1, MSG2, MSG3, MSG4, MSG5, MSG6, MSG7 (+1 more)

### Community 49 - "Ghost Body Images"
Cohesion: 0.22
Nodes (5): GhostsBodyImages, CYAN, ORANGE, PINK, RED

### Community 65 - "Common Game Images"
Cohesion: 0.33
Nodes (4): GameImages, EMPTY, LADYBUG_PLUGIN_OX_UP, TELEPORTATION

### Community 66 - "Ghost Behaviour Enum"
Cohesion: 0.33
Nodes (4): GhostBehaviour, AGGRESSIVE, CALM, SMART

### Community 68 - "Move Style Enum"
Cohesion: 0.40
Nodes (5): MoveStyle, MOVE_DEFAULT, MOVE_REGENERATING, MOVE_SCARED, MOVE_TO

## Knowledge Gaps
- **161 isolated node(s):** `DEAD`, `DYING`, `NORMAL`, `GHOST_HAT`, `HEADBAND_DOWN` (+156 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 367 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **43 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ScreenData` connect `Level Structure & Settings` to `Ladybug Status & Timed Actions`, `Central View & Game Model`, `Screen Blocks & Movement`, `Ghost Body Movement`, `Core Constants & Bodies`, `Collections & Ghost Behaviour`, `Level Rendering Modes`, `Image Resources & Views`, `Main Game Model`, `Ladybug Death Handling`, `Fruits & Lombok Entities`, `Ghost Body Images`, `Ghosts Group Management`, `Block Border Checks`, `Ladybug Eating Events`, `Maze Block Rendering`, `Dijkstra Pathfinding`?**
  _High betweenness centrality (0.108) - this node is a cross-community bridge._
- **What connects `DEAD`, `DYING`, `NORMAL` to the rest of the system?**
  _161 weakly-connected nodes found - possible missing edges._
- **Should `Ladybug Status & Timed Actions` be split into smaller, more focused modules?**
  _Cohesion score 0.05352968676951847 - nodes in this community are weakly interconnected._
- **Why does `ScreenBlock` connect `Screen Blocks & Movement` to `Second Generation Level Files`, `Level Structure & Settings`, `Ghost Body Movement`, `Maze Block Rendering`, `Core Constants & Bodies`, `Collections & Ghost Behaviour`, `Image Resources & Views`, `Fruits & Lombok Entities`, `Block Border Checks`, `Audio, Fonts & Logging`, `First Generation Levels`, `Second Generation Level Base`, `Dijkstra Pathfinding`?**
  _High betweenness centrality (0.080) - this node is a cross-community bridge._
- **Should `Central View & Game Model` be split into smaller, more focused modules?**
  _Cohesion score 0.05822784810126582 - nodes in this community are weakly interconnected._
- **Why does `GameModel` connect `Main Game Model` to `Ladybug Status & Timed Actions`, `Central View & Game Model`, `Sound Requests`, `Game Sound Events`, `Level Structure & Settings`, `Keyboard Controller Interface`, `Ghost Camouflage`, `Ladybug Death Handling`, `Program Status Checks`, `Ghost Body Images`, `Ghosts Group Management`, `Localized Game Messaging`, `Ladybug Eating Events`, `Audio, Fonts & Logging`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._
- **Should `Ghost Eyes Rendering` be split into smaller, more focused modules?**
  _Cohesion score 0.05325814536340852 - nodes in this community are weakly interconnected._