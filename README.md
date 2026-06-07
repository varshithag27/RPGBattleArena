# RPG Battle Arena

Java mini project: **Spring Boot REST API** + **browser game UI**.

## Run the game (web UI)

1. Double-click **`RUN-SERVER.bat`**
2. Wait for `Started RpgBattleArenaApplication`
3. Open browser: **http://localhost:8080**
4. Play: choose class → Start Adventure → Attack → use items

See **`HOW-TO-RUN.txt`** for full step-by-step instructions.

## Architecture (for project report)

| Layer | Technology |
|-------|------------|
| Server | Java 17, Spring Boot 3 |
| API | REST (`/api/*`) |
| UI | HTML, CSS, JavaScript |
| Build | Maven |

## REST API endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/start-game` | Start session `{ "name", "characterClass" }` |
| POST | `/api/attack` | One battle turn |
| GET | `/api/inventory` | List items |
| POST | `/api/inventory/use` | Use item `{ "itemName" }` |
| GET | `/api/leaderboard` | Top players |
| GET | `/api/health` | Server status |

## Project structure

```
src/main/java/.../controller/   REST controllers
src/main/java/.../service/       Game logic
src/main/java/.../model/         OOP classes (Warrior, Mage, Archer, Enemy, Item)
src/main/resources/static/       Web UI (index.html, css, js)
```

## Requirements

- Java 17
- Maven

## Optional console demo

`RUN-DEMO.bat` — text-only demo without browser.
