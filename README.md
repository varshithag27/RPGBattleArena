# RPGBattleArena

RPGBattleArena is a Java mini-project that combines a Spring Boot REST backend with a browser-based turn-based battle game.

## What this project contains

- A Spring Boot application written for Java 17
- REST endpoints for starting a game, attacking, using inventory items, and reading leaderboard data
- A static web UI served from `src/main/resources/static` using HTML, CSS, and JavaScript
- Game logic for three player classes: Warrior, Mage, and Archer
- Enemy encounters, health tracking, and item usage

## Run the web game

### Option 1: Use the batch file
1. Double-click `RUN-SERVER.bat`
2. Wait until the server logs show `Started RpgBattleArenaApplication`
3. Open your browser to `http://localhost:8080`
4. Enter a player name, choose a class, and click `Start Adventure`
5. Use `Attack` and `Use Item` in the browser to fight enemies

### Option 2: Use Maven
1. Open a terminal in the project root
2. Run `mvn spring-boot:run`
3. Open `http://localhost:8080` in your browser

## Quick commands

- `RUN-SERVER.bat` — launch the Spring Boot server and open the web UI
- `RUN-DEMO.bat` — run the console-only demo version
- `RUN-GAME.bat` — launch the main game logic (if configured for local use)
- `HOW-TO-RUN.txt` — detailed usage instructions

## REST API endpoints

- `POST /api/start-game` — start a new game session with JSON payload `{ "name": "Player", "characterClass": "Warrior" }`
- `POST /api/attack` — execute one fight turn against the current enemy
- `GET /api/inventory` — retrieve the player’s current inventory items
- `POST /api/inventory/use` — use one item with JSON payload `{ "itemName": "Health Potion" }`
- `GET /api/leaderboard` — fetch the top players and scores
- `GET /api/health` — check application health

## Project structure

- `src/main/java/com/example/rpgbattlearena/controller` — REST controllers and request handlers
- `src/main/java/com/example/rpgbattlearena/service` — game flow, session management, and battle logic
- `src/main/java/com/example/rpgbattlearena/model` — domain objects, player classes, enemy classes, items, and response models
- `src/main/resources/static` — web UI assets: `index.html`, `css/game.css`, `js/game.js`

## Requirements

- Java 17 or newer
- Maven 3.x

## Build

To package the application:

```bash
mvn clean package
```

Then run with:

```bash
mvn spring-boot:run
```

## Notes

- The browser UI is served automatically from the Spring Boot app on `http://localhost:8080`
- The game state is managed per session through the backend service
- Use `HOW-TO-RUN.txt` for a full step-by-step walkthrough

